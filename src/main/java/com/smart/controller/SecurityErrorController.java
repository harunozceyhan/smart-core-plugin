package com.smart.controller;

import com.auth0.jwt.JWT;
import java.security.KeyFactory;
import org.springframework.stereotype.Controller;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import javax.servlet.http.HttpServletRequest;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;

@Controller
public class SecurityErrorController implements ErrorController {

    @Value("${security.signing.key}")
    private String signingKey;

    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new AccessDeniedException("Token Not Found");
        } else {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decodeBase64(signingKey));
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
            JWT.require(Algorithm.RSA256(pubKey, null)).build().verify(token.replace("Bearer ", ""));
        }
        return "";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}