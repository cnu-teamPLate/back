package com.cnu.teamProj.teamProj.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateToStringMapping {

    private static final Logger logger = LoggerFactory.getLogger(DateToStringMapping.class);
    public ZonedDateTime stringToDateMapper(String dateStr) {
        ZonedDateTime date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.X");
        try{
            date = ZonedDateTime.parse(dateStr, formatter);
            return date;
        } catch (DateTimeParseException e) {
            logger.error(e.getParsedString());
            return null;
        }
    }
}
