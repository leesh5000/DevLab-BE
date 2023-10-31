package com.leesh.devlab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimezoneConfig {

    protected TimezoneConfig(@Value("${custom.timezone}") String timezone) {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    }

}
