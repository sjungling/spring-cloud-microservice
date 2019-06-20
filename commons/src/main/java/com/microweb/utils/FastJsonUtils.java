package com.microweb.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FastJsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parseToObject(String json, Class<T> toClass) {
        try {
            return (T) objectMapper.readValue(json, toClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseToGenericObject(String json, TypeReference<T> typeReference) {
        try {
            if (json == null || "".equals(json)) {
                return null;
            }
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String parseToString(Object obj) {
        try {
            if (obj == null) {
                return null;
            }

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseToObject(InputStream is, Class<T> toClass) {
        try {
            return (T) objectMapper.readValue(is, toClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * ValueType: JavaType
     */
    public static <T> T parseToObject(byte[] bytes, int offset, int len, Class<T> valueType) {
        try {
            return (T) objectMapper.readValue(bytes, offset, len, valueType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}