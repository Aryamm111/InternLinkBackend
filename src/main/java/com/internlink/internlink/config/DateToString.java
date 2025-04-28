package com.internlink.internlink.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DateToString implements Converter<Date, String> {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String convert(Date date) {
        return sdf.format(date);
    }
}
