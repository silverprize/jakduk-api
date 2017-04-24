package com.jakduk.core.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * Created by pyohwanjang on 2017. 3. 14..
 */
public class DateUtils {

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return Objects.isNull(date) ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date LocalDateTimeToDate(LocalDateTime localDateTime) {
        return Objects.isNull(localDateTime) ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static ZonedDateTime dateToZonedDateTime(Date date) {
        return Objects.isNull(date) ? null : ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
