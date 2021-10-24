package com.hepiplant.backend.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {

    public static LocalDateTime convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
