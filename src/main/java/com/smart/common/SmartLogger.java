package com.smart.common;

import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class SmartLogger {

    private static final Logger INF_LOGGER = LogManager.getLogger("infoLogger");
    private static final Logger ERR_LOGGER = LogManager.getLogger("errorLogger");

    public static void logInfo(String appName, String entityObj, String crudType, String className, String classPackageName, String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        INF_LOGGER.info(entityObj, kv("app", appName), kv("type", crudType), kv("className", className), kv("classPackageName", classPackageName), kv("id", id), kv("user", authentication == null ? "" : authentication.getName()));
    }

    public static void logError(String appName, String status, String message, String debugMessage, String stackTrace) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ERR_LOGGER.error(message, kv("app", appName), kv("status", status), kv("debugMessage", debugMessage), kv("stackTrace", stackTrace), kv("id", UUID.randomUUID().toString()), kv("user", authentication == null ? "" : authentication.getName()));
    }

}