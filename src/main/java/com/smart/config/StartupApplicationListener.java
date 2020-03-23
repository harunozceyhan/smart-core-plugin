package com.smart.config;

import com.smart.service.init.InitializationService;
import com.smart.service.realm.RealmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    RealmService realmService;

    @Autowired
    InitializationService initializationService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        realmService.setPublicKey();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        initializationService.initializeApplication();
    }
}