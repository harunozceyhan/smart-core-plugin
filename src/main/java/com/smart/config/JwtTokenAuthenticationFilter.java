package com.smart.config;

import com.auth0.jwt.JWT;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.security.KeyFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.Base64;
import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private String appName;
    private String signingKey;
    private String contextPath;

    public JwtTokenAuthenticationFilter(String appName, String signingKey, String contextPath) {
        this.signingKey = signingKey;
        this.appName = appName;
        this.contextPath = contextPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decodeBase64(signingKey));
                RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(pubKey, null)).build()
                        .verify(token.replace("Bearer ", ""));
                String user = decodedJWT.getClaim("preferred_username").asString();
                HashMap<String, ArrayList<String>> clientMap = ((HashMap<String, ArrayList<String>>) decodedJWT
                        .getClaim("resource_access").asMap().get(appName));
                if (clientMap == null) {
                    throw new AccessDeniedException("Client Service Not Found!");
                } else {
                    String defaultMapping = request.getRequestURI().replace(contextPath + "/", "").split("/")[0];
                    Long permissionCount = clientMap.get("roles").stream()
                            .filter(permission -> permission.equals("*") || permission.equals(defaultMapping + ":*")
                                    || permission.equals(defaultMapping + ":" + request.getMethod().toLowerCase()))
                            .count();
                    if (permissionCount == 0) {
                        throw new AccessDeniedException("Permission Not Found!");
                    }
                }
                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
                return null;
            } catch (Exception e) {
                request.setAttribute("exception", e);
                SecurityContextHolder.clearContext();
            }
        }
        SecurityContextHolder.clearContext();
        return null;
    }

}