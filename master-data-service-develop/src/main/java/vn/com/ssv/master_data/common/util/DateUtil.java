package vn.com.ssv.master_data.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil {
    public static final String DAY_MO_YEAR = "dd/MM/yyyy";

    public static String formatLocalDate(Date date, String format) {
        String formatDate = "";
        if (date != null && Strings.isNotBlank(format)) {
            formatDate = new SimpleDateFormat(format).format(date);
        }
        return formatDate;
    }

    public static String formatLocalDateTime(LocalDateTime date, String format) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.of(0, 0, 0));
    }

    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.of(23, 59, 59));
    }


    public static LocalDateTime parseToLocalDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            // ISO-8601 (2025-12-23T17:00:00.000Z)
            if (value.contains("T")) {
                return Instant.parse(value)
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toLocalDateTime();
            }
            // dd/MM/yyyy HH:mm
            if (value.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")) {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return LocalDateTime.parse(value, formatter);
            }
            // dd/MM/yyyy
            if (value.matches("\\d{2}/\\d{2}/\\d{4}")) {
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern(DAY_MO_YEAR);
                return LocalDate.parse(value, formatter).atStartOfDay();
            }

            // yyyy-MM-dd HH:mm:ss (DB)
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(value, formatter);

        } catch (Exception e) {
            return null;
        }
    }

}
