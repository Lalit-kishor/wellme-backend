package com.ultimate.wellme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.cashfree.Cashfree;

import jakarta.annotation.PostConstruct;

@Configuration
public class CashfreeConfig {

    @Value("${cashfree.app.id}")
    private String appId;

    @Value("${cashfree.secret.key}")
    private String secretKey;

    @Value("${cashfree.environment}")
    private String env;

    @PostConstruct
    public void init() {
        // Initialize Cashfree globally

        Cashfree.XClientId = appId;
        Cashfree.XClientSecret = secretKey;

        Cashfree.XEnvironment = "SANDBOX".equalsIgnoreCase(env) ? Cashfree.SANDBOX : Cashfree.PRODUCTION;   
    }
    
}
