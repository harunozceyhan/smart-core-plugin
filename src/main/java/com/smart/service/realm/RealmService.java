package com.smart.service.realm;

import com.google.gson.JsonParser;
import com.smart.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RealmService {

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    RestTemplate publicRestTemplate;

    public void setPublicKey() {
        ApplicationProperties.signingKey = JsonParser
                .parseString(publicRestTemplate.getForObject(applicationProperties.getTokenUrl(), String.class))
                .getAsJsonObject().get("public_key").getAsString();
    }
}