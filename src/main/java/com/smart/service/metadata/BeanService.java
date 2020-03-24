package com.smart.service.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import com.smart.annotation.Metadata;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
public class BeanService {

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    @Autowired
    EntityManager entityManager;

    public List<Map<String, String>> getAllControllers() {
        List<String> controllerList = new ArrayList<>();

        controllerList.addAll(getRestControllerAnnotatedList().stream().filter(c -> !controllerList.contains(c))
                .map(c -> c.concat(":menu")).collect(Collectors.toList()));
        controllerList.addAll(getRestControllerAnnotatedList().stream().filter(c -> !controllerList.contains(c))
                .map(c -> c.concat(":*")).collect(Collectors.toList()));
        controllerList.addAll(getRepositoryRestControllerAnnotatedList().stream()
                .filter(c -> !controllerList.contains(c)).map(c -> c.concat(":menu")).collect(Collectors.toList()));
        controllerList.addAll(getRepositoryRestControllerAnnotatedList().stream()
                .filter(c -> !controllerList.contains(c)).map(c -> c.concat(":*")).collect(Collectors.toList()));
        controllerList.addAll(getRepositoryRestResourceAnnotatedList().stream().filter(c -> !controllerList.contains(c))
                .map(c -> c.concat(":menu")).collect(Collectors.toList()));
        controllerList.addAll(getRepositoryRestResourceAnnotatedList().stream().filter(c -> !controllerList.contains(c))
                .map(c -> c.concat(":*")).collect(Collectors.toList()));
        return controllerList.stream().map(c -> Map.of("name", c)).collect(Collectors.toList());
    }

    public List<String> getRestControllerAnnotatedList() {
        return listableBeanFactory.getBeansWithAnnotation(RestController.class).entrySet().stream()
                .map(c -> getControllerAnnotationValue(c.getValue().toString().split("@")[0]))
                .filter(c -> !c.equals("")).map(c -> c.replaceAll("/", "")).collect(Collectors.toList());
    }

    public List<String> getRepositoryRestControllerAnnotatedList() {
        return listableBeanFactory.getBeansWithAnnotation(RepositoryRestController.class).entrySet().stream()
                .map(c -> getControllerAnnotationValue(c.getValue().toString().split("@")[0]))
                .filter(c -> !c.equals("")).map(c -> c.replaceAll("/", "")).collect(Collectors.toList());
    }

    public List<String> getRepositoryRestResourceAnnotatedList() {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().isAnnotationPresent(Metadata.class))
                .map(entity -> entity.getJavaType().getAnnotation(Metadata.class).baseUrl())
                .collect(Collectors.toList());
    }

    public String getControllerAnnotationValue(String className) {
        try {
            if (Class.forName(className).isAnnotationPresent(RequestMapping.class))
                return Class.forName(className).getAnnotation(RequestMapping.class).value()[0];
        } catch (ClassNotFoundException e) {
        }
        return "";
    }
}