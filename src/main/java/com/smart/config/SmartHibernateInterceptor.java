package com.smart.config;

import org.springframework.security.core.context.SecurityContextHolder;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.model.base.BaseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import java.io.Serializable;
import java.util.Iterator;

@Component
public class SmartHibernateInterceptor extends EmptyInterceptor {

    private String crudtype = "";
    private static final long serialVersionUID = -1;
    private static final Logger LOGGER = LogManager.getLogger("jsonLogger");

    @Override
    public String onPrepareStatement(String sql) {
        crudtype = "";
        if (sql.startsWith("update")) {
            crudtype = "UPDATE";
        } else if (sql.startsWith("insert")) {
            crudtype = "INSERT";
        }
        return super.onPrepareStatement(sql);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator entities) {
        super.postFlush(entities);
        while (entities.hasNext()) {
            Object entityObj = entities.next();
            if (crudtype.equals("INSERT") || crudtype.equals("UPDATE")) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                LOGGER.info(asJsonString(entityObj), kv("app", "Sbo-Service"), kv("type", crudtype), kv("className", entityObj.getClass().getSimpleName()), kv("classPackageName", entityObj.getClass().getName()), kv("id", BaseEntity.class.cast(entityObj).getId().toString()), kv("user", authentication == null ? "" : authentication.getName()));
            }
        }
    }

    @Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info(asJsonString(entity), kv("app", "Sbo-Service"), kv("type", "DELETE"), kv("className", entity.getClass().getSimpleName()), kv("classPackageName", entity.getClass().getName()), kv("id", id), kv("user", authentication == null ? "" : authentication.getName()));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}