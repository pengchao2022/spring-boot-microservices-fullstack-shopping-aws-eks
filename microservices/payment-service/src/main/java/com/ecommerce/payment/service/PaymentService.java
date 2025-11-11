package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.request.CreatePaymentRequest;
import com.ecommerce.payment.dto.request.RefundRequest;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.RefundResponse;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.Refund;
import com.ecommerce.payment.model.enums.PaymentMethod;
import com.ecommerce.payment.model.enums.PaymentStatus;
import com.ecommerce.payment.model.enums.RefundStatus;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.repository.RefundRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final AlipayService alipayService;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository, 
                         RefundRepository refundRepository,
                         AlipayService alipayService, 
                         ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.alipayService = alipayService;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建支付 - DTO版本
     */
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        logger.info("创建支付 - 订单号: {}, 金额: {}, 商品: {}", 
                   request.getOrderNumber(), request.getAmount(), request.getSubject());
        
        String paymentData;
        
        // 只支持支付宝支付
        if (request.getPaymentMethod() == PaymentMethod.ALIPAY) {
            // 调用 AlipayService 创建支付
            paymentData = alipayService.createPayment(
                request.getOrderNumber(), 
                request.getAmount(), 
                request.getSubject()
            );
        } else {
            throw new IllegalArgumentException("不支持的支付方式: " + request.getPaymentMethod());
        }

        // 保存支付记录到数据库
        Payment payment = new Payment();
        payment.setOrderNumber(request.getOrderNumber());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setSubject(request.getSubject());
        payment.setBody(request.getBody() != null ? request.getBody() : request.getSubject());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CNY");
        payment.setCreatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("支付记录保存成功 - ID: {}, 订单号: {}", savedPayment.getId(), request.getOrderNumber());
        
        // 转换为Response
        return convertToPaymentResponse(savedPayment, paymentData);
    }

    /**
     * 处理支付宝回调
     */
    public void handleAlipayCallback(Map<String, String> params) {
        logger.info("处理支付宝回调 - 参数数量: {}", params.size());
        
        try {
            // 验证签名
            boolean isValid = alipayService.verifyPayment(params);
            
            if (isValid) {
                // 解析关键信息
                String orderNumber = params.get("out_trade_no");
                String alipayTradeNo = params.get("trade_no");
                String tradeStatus = params.get("trade_status");
                String totalAmount = params.get("total_amount");
                String buyerId = params.get("buyer_id");
                String buyerLogonId = params.get("buyer_logon_id");
                
                logger.info("支付宝回调验证成功 - 订单号: {}, 交易状态: {}, 支付宝交易号: {}", 
                           orderNumber, tradeStatus, alipayTradeNo);
                
                // 根据交易状态处理
                processTradeStatus(orderNumber, tradeStatus, alipayTradeNo, totalAmount, buyerId, buyerLogonId);
            } else {
                logger.warn("支付宝回调验证失败");
                throw new RuntimeException("支付宝回调签名验证失败");
            }
        } catch (Exception e) {
            logger.error("处理支付宝回调异常", e);
            throw new RuntimeException("处理支付宝回调失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付宝异步通知
     */
    public boolean handleAlipayNotify(String notifyData) {
        logger.info("处理支付宝异步通知 - 数据: {}", notifyData);
        
        try {
            // 解析通知参数
            Map<String, String> params = parseNotifyData(notifyData);
            
            // 验证签名
            boolean isValid = alipayService.verifyPayment(params);
            
            if (isValid) {
                // 解析关键信息
                String orderNumber = params.get("out_trade_no");
                String alipayTradeNo = params.get("trade_no");
                String tradeStatus = params.get("trade_status");
                String totalAmount = params.get("total_amount");
                String buyerId = params.get("buyer_id");
                String buyerLogonId = params.get("buyer_logon_id");
                
                logger.info("支付宝通知验证成功 - 订单号: {}, 交易状态: {}, 支付宝交易号: {}", 
                           orderNumber, tradeStatus, alipayTradeNo);
                
                // 根据交易状态处理
                return processTradeStatus(orderNumber, tradeStatus, alipayTradeNo, totalAmount, buyerId, buyerLogonId);
            } else {
                logger.warn("支付宝通知验证失败");
                return false;
            }
        } catch (Exception e) {
            logger.error("处理支付宝通知异常", e);
            return false;
        }
    }

    /**
     * 退款支付 - DTO版本
     */
    public RefundResponse refundPayment(RefundRequest request) {
        logger.info("退款支付 - 订单号: {}, 金额: {}, 原因: {}", 
                   request.getOrderNumber(), request.getAmount(), request.getReason());
        
        // 根据订单号查找支付记录
        List<Payment> payments = paymentRepository.findByOrderNumber(request.getOrderNumber());
        if (payments.isEmpty()) {
            throw new RuntimeException("未找到对应的支付记录 - 订单号: " + request.getOrderNumber());
        }
        
        Payment payment = payments.get(0);
        
        if (!payment.canBeRefunded()) {
            throw new RuntimeException("支付无法退款，当前状态: " + payment.getStatus());
        }
        
        // 验证退款金额不超过支付金额
        if (request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("退款金额不能超过支付金额");
        }
        
        // 创建退款记录
        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(RefundStatus.PENDING);
        refund.setCreatedAt(LocalDateTime.now());
        
        Refund savedRefund = refundRepository.save(refund);
        logger.info("退款记录创建成功 - 退款ID: {}, 退款单号: {}", savedRefund.getId(), savedRefund.getRefundNumber());
        
        try {
            // 执行退款
            String alipayRefundNo = alipayService.createRefund(
                payment.getAlipayTradeNo(), 
                refund.getAmount(), 
                refund.getReason()
            );
            
            // 退款成功
            refund.markAsSuccess(alipayRefundNo);
            payment.markAsRefunded();
            
            Refund updatedRefund = refundRepository.save(refund);
            paymentRepository.save(payment);
            
            logger.info("退款执行成功 - 退款ID: {}, 支付宝退款单号: {}", savedRefund.getId(), alipayRefundNo);
            
            // 转换为Response
            return convertToRefundResponse(updatedRefund);
            
        } catch (Exception e) {
            // 退款失败
            refund.markAsFailed(e.getMessage());
            Refund failedRefund = refundRepository.save(refund);
            
            logger.error("退款执行失败 - 退款ID: {}", savedRefund.getId(), e);
            throw new RuntimeException("退款执行失败: " + e.getMessage());
        }
    }

    /**
     * 根据订单号获取支付信息
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderNumber(String orderNumber) {
        logger.debug("根据订单号查询支付信息 - 订单号: {}", orderNumber);
        
        List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
        if (payments.isEmpty()) {
            throw new RuntimeException("未找到对应的支付记录 - 订单号: " + orderNumber);
        }
        
        Payment payment = payments.get(0);
        return convertToPaymentResponse(payment, null);
    }

    /**
     * 获取支付状态
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(String orderNumber) {
        logger.debug("查询支付状态 - 订单号: {}", orderNumber);
        
        List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
        if (payments.isEmpty()) {
            throw new RuntimeException("未找到对应的支付记录 - 订单号: " + orderNumber);
        }
        
        Payment payment = payments.get(0);
        
        // 创建只包含状态信息的Response
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderNumber(payment.getOrderNumber());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setCreatedAt(payment.getCreatedAt());
        response.setPaidAt(payment.getPaidAt());
        
        return response;
    }

    /**
     * 取消支付
     */
    public PaymentResponse cancelPayment(String orderNumber) {
        logger.info("取消支付 - 订单号: {}", orderNumber);
        
        List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
        if (payments.isEmpty()) {
            throw new RuntimeException("未找到对应的支付记录 - 订单号: " + orderNumber);
        }
        
        Payment payment = payments.get(0);
        
        if (!payment.canBeCancelled()) {
            throw new RuntimeException("支付无法取消，当前状态: " + payment.getStatus());
        }
        
        payment.markAsCancelled();
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("支付取消成功 - 订单号: {}", orderNumber);
        
        return convertToPaymentResponse(updatedPayment, null);
    }

    /**
     * 获取支付统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentStatistics() {
        logger.debug("获取支付统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总支付金额
        List<Payment> paidPayments = paymentRepository.findByStatus(PaymentStatus.PAID);
        BigDecimal totalAmount = paidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalAmount", totalAmount);
        
        // 支付总数
        statistics.put("totalPayments", paymentRepository.count());
        
        // 成功支付数
        long paidCount = paidPayments.size();
        statistics.put("paidCount", paidCount);
        
        // 待处理支付数
        long pendingCount = paymentRepository.findByStatus(PaymentStatus.PENDING).size();
        statistics.put("pendingCount", pendingCount);
        
        // 失败支付数
        long failedCount = paymentRepository.findByStatus(PaymentStatus.FAILED).size();
        statistics.put("failedCount", failedCount);
        
        // 取消支付数
        long cancelledCount = paymentRepository.findByStatus(PaymentStatus.CANCELLED).size();
        statistics.put("cancelledCount", cancelledCount);
        
        // 退款统计
        long totalRefunds = refundRepository.count();
        statistics.put("totalRefunds", totalRefunds);
        
        long successRefunds = refundRepository.findByStatus(RefundStatus.SUCCESS).size();
        statistics.put("successRefunds", successRefunds);
        
        long pendingRefunds = refundRepository.findByStatus(RefundStatus.PENDING).size();
        statistics.put("pendingRefunds", pendingRefunds);
        
        // 支付方式统计
        long alipayCount = paymentRepository.countByPaymentMethod(PaymentMethod.ALIPAY);
        statistics.put("alipayCount", alipayCount);
        
        // 今日支付统计
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Payment> todayPayments = paymentRepository.findByCreatedAtAfter(todayStart);
        BigDecimal todayAmount = todayPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("todayAmount", todayAmount);
        statistics.put("todayCount", todayPayments.size());
        
        logger.info("支付统计信息获取完成 - 总金额: {}, 总支付数: {}", totalAmount, paidCount);
        return statistics;
    }

    /**
     * 创建支付 - 原始版本
     */
    public String createPayment(PaymentMethod method, String orderNumber, BigDecimal amount, String subject) {
        logger.info("创建支付 - 方式: {}, 订单号: {}, 金额: {}, 商品: {}", method, orderNumber, amount, subject);
        
        String paymentData;
        
        // 只支持支付宝支付
        if (method == PaymentMethod.ALIPAY) {
            // 调用 AlipayService 创建支付
            paymentData = alipayService.createPayment(orderNumber, amount, subject);
        } else {
            throw new IllegalArgumentException("不支持的支付方式: " + method);
        }

        // 保存支付记录到数据库
        Payment payment = new Payment();
        payment.setOrderNumber(orderNumber);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setSubject(subject);
        payment.setBody(subject);
        payment.setCurrency("CNY");
        payment.setCreatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("支付记录保存成功 - ID: {}, 订单号: {}", savedPayment.getId(), orderNumber);
        
        return paymentData;
    }

    /**
     * 根据ID获取支付记录
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPayment(@NonNull Long id) {
        logger.debug("查询支付记录 - ID: {}", id);
        return paymentRepository.findById(id);
    }

    /**
     * 根据订单号获取支付记录
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByOrder(String orderNumber) {
        logger.debug("查询订单支付记录 - 订单号: {}", orderNumber);
        return paymentRepository.findByOrderNumber(orderNumber);
    }

    /**
     * 确认支付成功
     */
    public Payment confirmPayment(@NonNull Long id, String alipayTradeNo, String payerUserId, String payerEmail) {
        logger.info("确认支付成功 - ID: {}, 支付宝交易号: {}", id, alipayTradeNo);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("支付记录不存在 - ID: " + id));
        
        payment.markAsPaid(alipayTradeNo, payerUserId, payerEmail);
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("支付确认成功 - ID: {}, 订单号: {}", id, payment.getOrderNumber());
        
        return updatedPayment;
    }

    /**
     * 取消支付 - 原始版本
     */
    public Payment cancelPayment(@NonNull Long id) {
        logger.info("取消支付 - ID: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("支付记录不存在 - ID: " + id));
        
        if (!payment.canBeCancelled()) {
            throw new RuntimeException("支付无法取消，当前状态: " + payment.getStatus());
        }
        
        payment.markAsCancelled();
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("支付取消成功 - ID: {}, 订单号: {}", id, payment.getOrderNumber());
        
        return updatedPayment;
    }

    /**
     * 创建退款 - 原始版本
     */
    public Refund createRefund(@NonNull Long paymentId, BigDecimal amount, String reason) {
        logger.info("创建退款 - 支付ID: {}, 金额: {}, 原因: {}", paymentId, amount, reason);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("支付记录不存在 - ID: " + paymentId));
        
        if (!payment.canBeRefunded()) {
            throw new RuntimeException("支付无法退款，当前状态: " + payment.getStatus());
        }
        
        // 验证退款金额不超过支付金额
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("退款金额不能超过支付金额");
        }
        
        // 创建退款记录
        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setStatus(RefundStatus.PENDING);
        refund.setCreatedAt(LocalDateTime.now());
        
        Refund savedRefund = refundRepository.save(refund);
        logger.info("退款记录创建成功 - 退款ID: {}, 退款单号: {}", savedRefund.getId(), savedRefund.getRefundNumber());
        
        return savedRefund;
    }

    /**
     * 执行退款 - 原始版本
     */
    public Refund executeRefund(@NonNull Long refundId) {
        logger.info("执行退款 - 退款ID: {}", refundId);
        
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款记录不存在 - ID: " + refundId));
        
        Payment payment = refund.getPayment();
        
        try {
            // 调用支付宝退款接口
            String alipayRefundNo = alipayService.createRefund(
                payment.getAlipayTradeNo(), 
                refund.getAmount(), 
                refund.getReason()
            );
            
            // 退款成功
            refund.markAsSuccess(alipayRefundNo);
            payment.markAsRefunded();
            
            Refund updatedRefund = refundRepository.save(refund);
            // 直接保存 payment，不使用变量接收返回值
            paymentRepository.save(payment);
            
            logger.info("退款执行成功 - 退款ID: {}, 支付宝退款单号: {}", refundId, alipayRefundNo);
            return updatedRefund;
            
        } catch (Exception e) {
            // 退款失败
            refund.markAsFailed(e.getMessage());
            Refund failedRefund = refundRepository.save(refund);
            
            logger.error("退款执行失败 - 退款ID: {}", refundId, e);
            throw new RuntimeException("退款执行失败: " + e.getMessage());
        }
    }

    /**
     * 根据退款ID获取退款记录
     */
    @Transactional(readOnly = true)
    public Optional<Refund> getRefund(@NonNull Long refundId) {
        logger.debug("查询退款记录 - ID: {}", refundId);
        return refundRepository.findById(refundId);
    }

    /**
     * 根据支付ID获取退款记录
     */
    @Transactional(readOnly = true)
    public List<Refund> getRefundsByPayment(@NonNull Long paymentId) {
        logger.debug("查询支付退款记录 - 支付ID: {}", paymentId);
        return refundRepository.findByPaymentId(paymentId);
    }

    /**
     * 根据退款单号获取退款记录
     */
    @Transactional(readOnly = true)
    public Optional<Refund> getRefundByRefundNumber(String refundNumber) {
        logger.debug("查询退款记录 - 退款单号: {}", refundNumber);
        return refundRepository.findByRefundNumber(refundNumber);
    }

    /**
     * 标记支付失败
     */
    public Payment markPaymentAsFailed(@NonNull Long id, String failureReason) {
        logger.info("标记支付失败 - ID: {}, 原因: {}", id, failureReason);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("支付记录不存在 - ID: " + id));
        
        payment.markAsFailed(failureReason);
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("支付标记为失败 - ID: {}, 订单号: {}", id, payment.getOrderNumber());
        
        return updatedPayment;
    }

    /**
     * 解析通知数据
     */
    private Map<String, String> parseNotifyData(String notifyData) throws Exception {
        Map<String, String> params = new HashMap<>();
        
        if (notifyData.contains("&")) {
            // URL 参数格式
            String[] pairs = notifyData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }
        } else {
            // 可能是 JSON 格式，尝试解析
            try {
                Map<?, ?> jsonParams = objectMapper.readValue(notifyData, Map.class);
                for (Map.Entry<?, ?> entry : jsonParams.entrySet()) {
                    params.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } catch (Exception e) {
                logger.warn("无法解析通知数据格式: {}", notifyData);
                throw new RuntimeException("无法解析支付宝通知数据");
            }
        }
        
        return params;
    }

    /**
     * 处理交易状态
     */
    private boolean processTradeStatus(String orderNumber, String tradeStatus, 
                                     String alipayTradeNo, String totalAmount, 
                                     String buyerId, String buyerLogonId) {
        try {
            List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
            if (payments.isEmpty()) {
                logger.error("未找到对应的支付记录 - 订单号: {}", orderNumber);
                return false;
            }
            
            Payment payment = payments.get(0);
            BigDecimal amount = new BigDecimal(totalAmount);
            
            // 验证金额是否匹配
            if (payment.getAmount().compareTo(amount) != 0) {
                logger.error("支付金额不匹配 - 订单号: {}, 期望: {}, 实际: {}", 
                           orderNumber, payment.getAmount(), amount);
                return false;
            }
            
            switch (tradeStatus) {
                case "TRADE_SUCCESS":
                case "TRADE_FINISHED":
                    // 支付成功
                    payment.markAsPaid(alipayTradeNo, buyerId, buyerLogonId);
                    paymentRepository.save(payment);
                    logger.info("支付成功处理完成 - 订单号: {}, 支付宝交易号: {}", orderNumber, alipayTradeNo);
                    break;
                    
                case "TRADE_CLOSED":
                    // 交易关闭
                    payment.markAsCancelled();
                    paymentRepository.save(payment);
                    logger.info("支付已关闭 - 订单号: {}", orderNumber);
                    break;
                    
                case "WAIT_BUYER_PAY":
                    // 等待用户付款 - 不需要处理，保持 PENDING 状态
                    logger.info("等待用户付款 - 订单号: {}", orderNumber);
                    break;
                    
                default:
                    logger.warn("未知的交易状态: {} - 订单号: {}", tradeStatus, orderNumber);
                    return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("处理交易状态异常 - 订单号: {}", orderNumber, e);
            return false;
        }
    }

    /**
     * 同步查询支付宝支付状态
     */
    public boolean syncPaymentStatus(String orderNumber) {
        logger.info("同步查询支付状态 - 订单号: {}", orderNumber);
        
        try {
            List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
            if (payments.isEmpty()) {
                logger.error("未找到对应的支付记录 - 订单号: {}", orderNumber);
                return false;
            }
            
            // 保留 payment 变量用于后续处理
            Payment payment = payments.get(0);
            
            // 调用支付宝查询接口获取最新状态
            Map<String, String> tradeStatus = alipayService.queryPaymentStatus(orderNumber);
            
            if (tradeStatus != null && tradeStatus.containsKey("trade_status")) {
                String alipayTradeNo = tradeStatus.get("trade_no");
                String tradeStatusValue = tradeStatus.get("trade_status");
                String totalAmount = tradeStatus.get("total_amount");
                String buyerId = tradeStatus.get("buyer_user_id");
                String buyerLogonId = tradeStatus.get("buyer_logon_id");
                
                return processTradeStatus(orderNumber, tradeStatusValue, alipayTradeNo, totalAmount, buyerId, buyerLogonId);
            } else {
                logger.warn("无法获取支付宝交易状态 - 订单号: {}", orderNumber);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("同步查询支付状态异常 - 订单号: {}", orderNumber, e);
            return false;
        }
    }

    /**
     * 根据支付ID和订单号获取支付记录
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByIdAndOrderNumber(@NonNull Long id, String orderNumber) {
        logger.debug("查询支付记录 - ID: {}, 订单号: {}", id, orderNumber);
        return paymentRepository.findByIdAndOrderNumber(id, orderNumber);
    }

    /**
     * 获取待处理的支付记录
     */
    @Transactional(readOnly = true)
    public List<Payment> getPendingPayments() {
        logger.debug("查询待处理支付记录");
        return paymentRepository.findByStatus(PaymentStatus.PENDING);
    }

    /**
     * 获取已支付的支付记录
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaidPayments() {
        logger.debug("查询已支付记录");
        return paymentRepository.findByStatus(PaymentStatus.PAID);
    }

    /**
     * 根据支付宝交易号查找支付记录
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByAlipayTradeNo(String alipayTradeNo) {
        logger.debug("根据支付宝交易号查询支付记录 - 交易号: {}", alipayTradeNo);
        return paymentRepository.findByAlipayTradeNo(alipayTradeNo);
    }

    /**
     * 根据订单号更新支付状态
     */
    public Payment updatePaymentStatusByOrderNumber(String orderNumber, String alipayTradeNo, String payerUserId, String payerEmail) {
        logger.info("根据订单号更新支付状态 - 订单号: {}, 支付宝交易号: {}", orderNumber, alipayTradeNo);
        
        List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
        if (payments.isEmpty()) {
            throw new RuntimeException("未找到对应的支付记录 - 订单号: " + orderNumber);
        }
        
        // 通常一个订单只有一个支付记录，取第一个
        Payment payment = payments.get(0);
        payment.markAsPaid(alipayTradeNo, payerUserId, payerEmail);
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("支付状态更新成功 - 订单号: {}, 状态: {}", orderNumber, PaymentStatus.PAID);
        
        return updatedPayment;
    }

    /**
     * 删除支付记录（仅用于测试或管理）
     */
    public void deletePayment(@NonNull Long id) {
        logger.warn("删除支付记录 - ID: {}", id);
        
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("支付记录不存在 - ID: " + id);
        }
        
        paymentRepository.deleteById(id);
        logger.info("支付记录删除成功 - ID: {}", id);
    }

    /**
     * 检查订单是否已支付
     */
    @Transactional(readOnly = true)
    public boolean isOrderPaid(String orderNumber) {
        logger.debug("检查订单支付状态 - 订单号: {}", orderNumber);
        List<Payment> payments = paymentRepository.findByOrderNumber(orderNumber);
        return payments.stream()
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.PAID);
    }

    /**
     * 获取用户的支付记录
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByPayer(String payerUserId) {
        logger.debug("查询用户支付记录 - 用户ID: {}", payerUserId);
        return paymentRepository.findByPayerUserId(payerUserId);
    }

    /**
     * 获取最近创建的支付记录 - 使用 Pageable 方法
     */
    @Transactional(readOnly = true)
    public List<Payment> getRecentPayments(int limit) {
        logger.debug("查询最近支付记录 - 限制: {}", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return paymentRepository.findByOrderByCreatedAtDesc(pageable);
    }

    /**
     * 统计支付金额
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentAmount() {
        logger.debug("统计总支付金额");
        List<Payment> paidPayments = paymentRepository.findByStatus(PaymentStatus.PAID);
        return paidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取待处理的退款记录
     */
    @Transactional(readOnly = true)
    public List<Refund> getPendingRefunds() {
        logger.debug("查询待处理退款记录");
        return refundRepository.findByStatus(RefundStatus.PENDING);
    }

    /**
     * 获取成功的退款记录
     */
    @Transactional(readOnly = true)
    public List<Refund> getSuccessfulRefunds() {
        logger.debug("查询成功退款记录");
        return refundRepository.findByStatus(RefundStatus.SUCCESS);
    }

    /**
     * 取消退款
     */
    public Refund cancelRefund(@NonNull Long refundId) {
        logger.info("取消退款 - 退款ID: {}", refundId);
        
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("退款记录不存在 - ID: " + refundId));
        
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new RuntimeException("只能取消待处理的退款");
        }
        
        refund.setStatus(RefundStatus.CANCELLED);
        
        Refund updatedRefund = refundRepository.save(refund);
        logger.info("退款取消成功 - 退款ID: {}", refundId);
        
        return updatedRefund;
    }

    /**
     * 转换Payment为PaymentResponse
     */
    private PaymentResponse convertToPaymentResponse(Payment payment, String paymentData) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderNumber(payment.getOrderNumber());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setSubject(payment.getSubject());
        response.setBody(payment.getBody());
        response.setCurrency(payment.getCurrency());
        response.setAlipayTradeNo(payment.getAlipayTradeNo());
        response.setPayerUserId(payment.getPayerUserId());
        response.setPayerEmail(payment.getPayerEmail());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setPaidAt(payment.getPaidAt());
        response.setRefundedAt(payment.getRefundedAt());
        response.setCancelledAt(payment.getCancelledAt());
        response.setFailureReason(payment.getFailureReason());
        // 使用 paymentUrl 字段而不是 paymentData
        response.setPaymentUrl(paymentData);
        return response;
    }

    /**
     * 转换Refund为RefundResponse
     */
    private RefundResponse convertToRefundResponse(Refund refund) {
        RefundResponse response = new RefundResponse();
        response.setId(refund.getId());
        response.setRefundNumber(refund.getRefundNumber());
        response.setPaymentId(refund.getPayment().getId());
        response.setOrderNumber(refund.getPayment().getOrderNumber());
        response.setAmount(refund.getAmount());
        response.setReason(refund.getReason());
        response.setStatus(refund.getStatus());
        response.setAlipayRefundNo(refund.getAlipayRefundNo());
        response.setFailureReason(refund.getFailureReason());
        response.setCreatedAt(refund.getCreatedAt());
        response.setUpdatedAt(refund.getUpdatedAt());
        response.setRefundedAt(refund.getProcessedAt());
        return response;
    }
}