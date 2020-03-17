package com.smart.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.smart.annotation.MetaColumn;
import com.smart.annotation.MetaTab;
import com.smart.annotation.Metadata;
import org.springframework.stereotype.Service;
import org.hibernate.validator.constraints.Length;
import com.smart.service.interfc.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@Service
public class MetaDataServiceImpl implements MetaDataService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public Map<String, Object> getMetaDataOfClass(String name) {
        Map<String, Object> responseMap = new HashMap<String, Object>();

        EntityType<?> entityType = entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().isAnnotationPresent(Metadata.class)
                        && entity.getJavaType().getAnnotation(Metadata.class).value().equals(name))
                .findFirst().orElse(null);

        if (entityType != null) {
            String contextPath = httpServletRequest.getContextPath().replaceFirst("/", "") + "/";
            Metadata classMetadata = entityType.getJavaType().getAnnotation(Metadata.class);
            responseMap.put("value", classMetadata.value());
            responseMap.put("title", classMetadata.title());
            responseMap.put("detailTitleKey", classMetadata.detailTitleKey());
            responseMap.put("baseUrl", contextPath + classMetadata.baseUrl());
            responseMap.put("getUrl", contextPath + classMetadata.getUrl());
            responseMap.put("responseKey", classMetadata.responseKey());
            responseMap.put("columns", getColumnListOfClass(entityType));
            responseMap.put("tabs", getTabListOfClass(entityType));
        } else {
            throw new ResourceNotFoundException("MetaData Not Found");
        }

        return responseMap;
    }

    public List<Map<String, Object>> getColumnListOfClass(EntityType<?> entityType) {
        List<Map<String, Object>> columnList = new ArrayList<>();
        List<Field> fieldList = Arrays.asList(entityType.getJavaType().getDeclaredFields()).stream()
                .filter(field -> field.isAnnotationPresent(MetaColumn.class)).collect(Collectors.toList());
        fieldList.forEach(field -> columnList.add(getColumnMetaDataOfClass(field)));
        return columnList;
    }

    public List<Map<String, Object>> getTabListOfClass(EntityType<?> entityType) {
        List<Map<String, Object>> tabs = new ArrayList<>();
        List<Field> fieldList = Arrays.asList(entityType.getJavaType().getDeclaredFields()).stream()
                .filter(field -> field.isAnnotationPresent(MetaTab.class)).collect(Collectors.toList());
        fieldList.forEach(field -> {
            tabs.add(getTabMetaDataOfClass(field));
        });
        return tabs;
    }

    public Map<String, Object> getTabMetaDataOfClass(Field field) {
        String contextPath = httpServletRequest.getContextPath().replaceFirst("/", "") + "/";
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Class<?> fieldClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        EntityType<?> entityType = entityManager.getMetamodel().entity(fieldClass);
        Metadata classMetadata = fieldClass.getAnnotation(Metadata.class);
        MetaTab fielMetaTab = field.getAnnotation(MetaTab.class);
        if (entityType != null) {
            responseMap.put("tab", fielMetaTab.value());
            responseMap.put("value", classMetadata.value());
            responseMap.put("title", classMetadata.title());
            responseMap.put("detailTitleKey", classMetadata.detailTitleKey());
            responseMap.put("baseUrl", contextPath + classMetadata.baseUrl());
            responseMap.put("getUrl", contextPath + classMetadata.getUrl());
            responseMap.put("responseKey", classMetadata.responseKey());
            responseMap.put("columns", getColumnListOfClass(entityType));
        } else {
            throw new ResourceNotFoundException("MetaData Not Found");
        }

        return responseMap;
    }

    public Map<String, Object> getColumnMetaDataOfClass(Field field) {
        String contextPath = httpServletRequest.getContextPath().replaceFirst("/", "") + "/";
        Map<String, Object> columnsMap = new HashMap<String, Object>();
        MetaColumn fieldMeta = field.getAnnotation(MetaColumn.class);
        String fieldType = field.getType().getSimpleName();
        if (fieldType.equals("String")) {
            columnsMap.put("type", fieldMeta.type().equals("") ? "text" : fieldMeta.type());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "text" : fieldMeta.formType());
        } else if (fieldType.equals("Date")) {
            columnsMap.put("type", fieldMeta.type().equals("") ? "text" : fieldMeta.type());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "datepicker" : fieldMeta.formType());
        } else if (fieldType.equals("Boolean")) {
            columnsMap.put("type", fieldMeta.type().equals("") ? "boolean" : fieldMeta.type());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "checkbox" : fieldMeta.formType());
        } else if (fieldType.equals("Integer")) {
            columnsMap.put("type", fieldMeta.type().equals("") ? "integer" : fieldMeta.type());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "text" : fieldMeta.formType());
        } else if (fieldType.equals("Double") || fieldType.equals("Float")) {
            columnsMap.put("type", fieldMeta.type().equals("") ? "float" : fieldMeta.type());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "text" : fieldMeta.formType());
        } else { // Object
            columnsMap.put("type", fieldMeta.type().equals("") ? "object" : fieldMeta.type());
            columnsMap.put("filterBy", fieldMeta.filterBy());
            columnsMap.put("formType", fieldMeta.formType().equals("") ? "combobox" : fieldMeta.formType());
            columnsMap.put("metadata", field.getType().getAnnotation(Metadata.class).value());
            columnsMap.put("metadataUrl",
                    contextPath + "metadata/" + field.getType().getAnnotation(Metadata.class).value());
        }

        if (field.isAnnotationPresent(Length.class)) {
            Length fieldLengthMeta = field.getAnnotation(Length.class);
            columnsMap.put("min", fieldLengthMeta.min());
            columnsMap.put("max", fieldLengthMeta.max());
        } else {
            columnsMap.put("min", fieldMeta.min());
            columnsMap.put("max", fieldMeta.max());
        }
        columnsMap.put("required", field.isAnnotationPresent(NotNull.class));
        columnsMap.put("text", fieldMeta.text().equals("") ? field.getName() : fieldMeta.text());
        columnsMap.put("value", fieldMeta.value().equals("") ? field.getName() : fieldMeta.value());
        columnsMap.put("tableValue", fieldMeta.tableValue().equals("") ? field.getName() : fieldMeta.tableValue());
        columnsMap.put("searchKey", fieldMeta.searchKey().equals("") ? field.getName() : fieldMeta.searchKey());
        columnsMap.put("width", fieldMeta.width());
        columnsMap.put("url", contextPath + fieldMeta.url());
        columnsMap.put("responseKey", fieldMeta.responseKey());
        columnsMap.put("itemText", fieldMeta.itemText());
        columnsMap.put("sortable", fieldMeta.sortable());
        columnsMap.put("showInTable", fieldMeta.showInTable());
        columnsMap.put("searchable", fieldMeta.searchable());
        columnsMap.put("updatable", fieldMeta.updatable());

        return columnsMap;
    }
}