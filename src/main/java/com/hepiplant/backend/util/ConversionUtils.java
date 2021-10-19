package com.hepiplant.backend.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ConversionUtils {

    public static LocalDateTime convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
