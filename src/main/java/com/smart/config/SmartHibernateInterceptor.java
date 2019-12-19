package com.smart.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.model.base.BaseEntity;
import org.hibernate.EmptyInterceptor;
import com.smart.common.SmartLogger;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Iterator;

@Component
public class SmartHibernateInterceptor extends EmptyInterceptor {

    private String crudType = "";
    private static final long serialVersionUID = -1;

    private String appName;

    @Override
    public String onPrepareStatement(String sql) {
        crudType = sql.startsWith("update") ? "UPDATE" : (sql.startsWith("insert") ? "INSERT" : "");
        return super.onPrepareStatement(sql);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void postFlush(Iterator entities) {
        while (entities.hasNext()) {
            Object entityObj = entities.next();
            if (crudType.equals("INSERT") || crudType.equals("UPDATE")) {
                SmartLogger.logInfo(appName, asJsonString(entityObj), crudType, entityObj.getClass().getSimpleName(), entityObj.getClass().getName(), BaseEntity.class.cast(entityObj).getId().toString());
            }
        }
        super.postFlush(entities);
    }

    @Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        SmartLogger.logInfo(appName, asJsonString(entity), "DELETE", entity.getClass().getSimpleName(), entity.getClass().getName(), BaseEntity.class.cast(entity).getId().toString());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

}