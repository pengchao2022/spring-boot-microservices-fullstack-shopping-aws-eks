package com.ecommerce.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {

    @Value("${alipay.app-id:2021006103655907}")
    private String appId;

    @Value("${alipay.private-key:}")
    private String privateKey;

    @Value("${alipay.public-key:}")
    private String publicKey;

    @Value("${alipay.gateway:https://openapi.alipay.com/gateway.do}")
    private String gateway;

    @Value("${alipay.notify-url:}")
    private String notifyUrl;

    @Value("${alipay.return-url:}")
    private String returnUrl;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
            gateway,
            appId,
            privateKey,
            "json",
            "UTF-8",
            publicKey,
            "RSA2"
        );
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}