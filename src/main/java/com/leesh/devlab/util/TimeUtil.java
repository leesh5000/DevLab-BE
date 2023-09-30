package com.leesh.devlab.util;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TimeUtil {

    public static LocalDateTime convertLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
