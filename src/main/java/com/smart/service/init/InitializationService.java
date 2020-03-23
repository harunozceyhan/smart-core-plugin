package com.smart.service.init;

import java.util.Map;

import com.smart.config.ApplicationProperties;
import com.smart.service.metadata.BeanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InitializationService {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BeanService beanService;

    public void initializeApplication() {
        try {
            createClient();
            createClientRoles();
            createRealmRole();
            assignClientRolesToRealmRole();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createClient() {
        restTemplate.exchange("http://common-service/common/api/v1/client/" + applicationProperties.getName(),
                HttpMethod.PUT, new HttpEntity<>(null), ResponseEntity.class);
    }

    public void createClientRoles() {
        restTemplate.exchange("http://common-service/common/api/v1/role/batch/" + applicationProperties.getName(),
                HttpMethod.POST, new HttpEntity<>(beanService.getAllControllers()), ResponseEntity.class);
    }

    public void createRealmRole() {
        restTemplate.exchange("http://common-service/common/api/v1/role/", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", applicationProperties.getAdminRole())), ResponseEntity.class);
    }

    public void assignClientRolesToRealmRole() {
        restTemplate.exchange(
                "http://common-service/common/api/v1/role/composite/" + applicationProperties.getAdminRole() + "/"
                        + applicationProperties.getName(),
                HttpMethod.POST, new HttpEntity<>(null), ResponseEntity.class);
    }
}