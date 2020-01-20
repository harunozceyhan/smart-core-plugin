package com.smart.service.interfc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.EntityType;

public interface MetaDataService {
    Map<String, Object> getMetaDataOfClass(String name);

    Map<String, Object> getColumnMetaDataOfClass(Field field);

    List<Map<String, Object>> getColumnListOfClass(EntityType<?> entityType);

    List<Map<String, Object>> getTabListOfClass(EntityType<?> entityType);
}
