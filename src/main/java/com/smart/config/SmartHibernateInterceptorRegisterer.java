package com.smart.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class SmartHibernateInterceptorRegisterer implements HibernatePropertiesCustomizer {

    @Autowired
    private SmartHibernateInterceptor smartHibernateInterceptor;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        smartHibernateInterceptor.setAppName(applicationProperties.getName());
        hibernateProperties.put("hibernate.ejb.interceptor", smartHibernateInterceptor);
    }
}