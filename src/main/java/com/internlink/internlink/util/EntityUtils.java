package com.internlink.internlink.util;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;

public final class EntityUtils {

    private EntityUtils() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target,
                Arrays.stream(source.getClass().getDeclaredFields())
                        .filter(field -> {
                            try {
                                field.setAccessible(true);
                                return field.get(source) == null;
                            } catch (IllegalAccessException e) {
                                return true;
                            }
                        })
                        .map(Field::getName)
                        .toArray(String[]::new));
    }

}
