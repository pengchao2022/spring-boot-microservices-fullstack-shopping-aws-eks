package com.ecommerce.payment.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.app-id:2021006103655907}")
    private String appId;

    @Value("${alipay.private-key:}")
    private String privateKeyBase64;

    @Value("${alipay.public-key:}")
    private String publicKeyBase64;

    @Value("${alipay.gateway:https://openapi.alipay.com/gateway.do}")
    private String gateway;

    @Value("${alipay.notify-url:http://localhost:8083/api/payments/alipay/notify}")
    private String notifyUrl;

    @Value("${alipay.return-url:http://localhost:3000/payment/success}")
    private String returnUrl;

    @Value("${alipay.sign-type:RSA2}")
    private String signType;

    @Value("${alipay.charset:UTF-8}")
    private String charset;

    private AlipayClient alipayClient;

    private String decodeBase64Key(String base64Key) {
        if (base64Key == null || base64Key.trim().isEmpty()) {
            throw new RuntimeException("支付宝密钥为空");
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Key);
            return new String(decodedBytes);
        } catch (Exception e) {
            log.error("Failed to decode Base64 key", e);
            throw new RuntimeException("支付宝密钥解码失败: " + e.getMessage());
        }
    }

    private AlipayClient getAlipayClient() {
        if (alipayClient == null) {
            try {
                // 解码Base64编码的密钥
                String privateKey = decodeBase64Key(privateKeyBase64);
                String publicKey = decodeBase64Key(publicKeyBase64);
                
                alipayClient = new DefaultAlipayClient(
                    gateway,
                    appId,
                    privateKey,
                    "json",
                    charset,
                    publicKey,
                    signType
                );
                log.info("Alipay client initialized successfully - AppId: {}", appId);
            } catch (Exception e) {
                log.error("Failed to initialize Alipay client", e);
                throw new RuntimeException("支付宝客户端初始化失败: " + e.getMessage());
            }
        }
        return alipayClient;
    }

    @Override
    public String createPayment(String orderNumber, BigDecimal amount, String subject) {
        try {
            AlipayClient client = getAlipayClient();
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            
            // 设置异步通知地址
            request.setNotifyUrl(notifyUrl);
            // 设置同步跳转地址
            request.setReturnUrl(returnUrl);

            // 创建业务参数
            String bizContent = "{" +
                "\"out_trade_no\":\"" + orderNumber + "\"," +
                "\"total_amount\":\"" + amount.toString() + "\"," +
                "\"subject\":\"" + subject + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "\"timeout_express\":\"30m\"" +
                "}";

            request.setBizContent(bizContent);

            AlipayTradePagePayResponse response = client.pageExecute(request, "POST");
            
            if (response.isSuccess()) {
                log.info("Alipay payment created successfully - Order: {}, Amount: {}", orderNumber, amount);
                return response.getBody();
            } else {
                log.error("Alipay payment creation failed - Order: {}, Error: {}", orderNumber, response.getSubMsg());
                throw new RuntimeException("支付宝支付创建失败: " + response.getSubMsg());
            }
        } catch (Exception e) {
            log.error("Alipay payment creation error - Order: {}", orderNumber, e);
            throw new RuntimeException("支付宝支付创建异常: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(Map<String, String> params) {
        try {
            // 解码公钥
            String publicKey = decodeBase64Key(publicKeyBase64);
            
            // 移除签名类型参数，因为支付宝SDK会自动处理
            Map<String, String> verifyParams = new HashMap<>(params);
            verifyParams.remove("sign_type");
            
            // 使用支付宝SDK进行签名验证
            boolean isValid = AlipaySignature.rsaCheckV1(
                verifyParams,
                publicKey,
                charset,
                signType
            );
            
            log.info("Alipay payment signature verification - Order: {}, Result: {}", 
                    params.get("out_trade_no"), isValid ? "PASSED" : "FAILED");
            return isValid;
        } catch (Exception e) {
            log.error("Alipay payment signature verification error - Order: {}", params.get("out_trade_no"), e);
            return false;
        }
    }

    @Override
    public Map<String, String> queryPaymentStatus(String orderNumber) {
        try {
            AlipayClient client = getAlipayClient();
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            
            String bizContent = "{" +
                "\"out_trade_no\":\"" + orderNumber + "\"" +
                "}";
            
            request.setBizContent(bizContent);

            AlipayTradeQueryResponse response = client.execute(request);
            
            Map<String, String> result = new HashMap<>();
            if (response.isSuccess()) {
                result.put("trade_no", response.getTradeNo());
                result.put("trade_status", response.getTradeStatus());
                result.put("total_amount", response.getTotalAmount());
                result.put("buyer_user_id", response.getBuyerUserId());
                result.put("buyer_logon_id", response.getBuyerLogonId());
                log.info("Alipay payment status queried successfully - Order: {}, Status: {}", orderNumber, response.getTradeStatus());
            } else {
                log.error("Alipay payment status query failed - Order: {}, Error: {}", orderNumber, response.getSubMsg());
                result.put("trade_status", "QUERY_FAILED");
                result.put("error_msg", response.getSubMsg());
            }
            
            return result;
        } catch (Exception e) {
            log.error("Alipay payment status query error - Order: {}", orderNumber, e);
            Map<String, String> result = new HashMap<>();
            result.put("trade_status", "QUERY_ERROR");
            result.put("error_msg", e.getMessage());
            return result;
        }
    }

    @Override
    public String createRefund(String alipayTradeNo, BigDecimal amount, String reason) {
        try {
            AlipayClient client = getAlipayClient();
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            
            String bizContent = "{" +
                "\"trade_no\":\"" + alipayTradeNo + "\"," +
                "\"refund_amount\":\"" + amount.toString() + "\"," +
                "\"refund_reason\":\"" + (reason != null ? reason : "正常退款") + "\"" +
                "}";
            
            request.setBizContent(bizContent);

            AlipayTradeRefundResponse response = client.execute(request);
            
            if (response.isSuccess()) {
                log.info("Alipay refund created successfully - Trade: {}, Amount: {}, RefundNo: {}", 
                        alipayTradeNo, amount, response.getRefundFee());
                return response.getTradeNo();
            } else {
                log.error("Alipay refund creation failed - Trade: {}, Error: {}", alipayTradeNo, response.getSubMsg());
                throw new RuntimeException("支付宝退款创建失败: " + response.getSubMsg());
            }
        } catch (Exception e) {
            log.error("Alipay refund creation error - Trade: {}", alipayTradeNo, e);
            throw new RuntimeException("支付宝退款创建异常: " + e.getMessage());
        }
    }
}