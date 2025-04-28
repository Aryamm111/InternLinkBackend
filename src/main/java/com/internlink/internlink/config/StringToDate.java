package com.internlink.internlink.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class StringToDate implements Converter<String, Date> {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date convert(String source) {
        try {
            return source != null ? sdf.parse(source) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + source);
        }
    }
}
