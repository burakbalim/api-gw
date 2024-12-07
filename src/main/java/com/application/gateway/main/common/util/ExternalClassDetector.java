package com.application.gateway.main.common.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@Slf4j
public class ExternalClassDetector<T> {

    private final ApplicationContext applicationContext;

    @PostConstruct
    void init () {
        log.info("Initializing external class detector using class loader: {}", Thread.currentThread().getContextClassLoader().getName());
    }

    public T getInstanceOfClass(String className) {
        try {
            if (className.contains("$")) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();;
                Class.forName(className.split("\\$")[0]);
                Class<?> outerClass = classLoader.loadClass(className.split("\\$")[0]);
                Object outerInstance = outerClass.newInstance();
                Class<?> innerClass = Class.forName(className);
                Constructor<?> constructor = innerClass.getDeclaredConstructor(outerClass);
                constructor.setAccessible(true);
                Object innerInstance = constructor.newInstance(outerInstance);
                return ((T) innerInstance);
            }
            else {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();;
                Class<?> loadedClass = classLoader.loadClass(className);
                Object instance = loadedClass.getDeclaredConstructor().newInstance();
                return ((T) instance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Object> getBean(String className) {
        if (!applicationContext.containsBean(className)) {
            return Optional.empty();
        }
        return Optional.of(applicationContext.getBean(className));
    }
}
