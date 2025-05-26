package pt.scml.fin.batch.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pt.scml.fin.batch.core.exceptions.DateParseException;

@Slf4j
public class DateUtils {

    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDD_CAPS_LOCK = "YYYYMMDD";
    public static final String YYMM = "yyMM";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
    }

    public static LocalDateTime getLocalDateTimeFromString(String date, String format) {

        if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(date, formatter).atTime(LocalTime.MIN);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new DateParseException(e);
        }

    }

    public static LocalDate getLocalDateFromString(String date, String format) {

        if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new DateParseException(e);
        }
    }

    public static String format(Object date, String format) {
        if (date == null || StringUtils.isBlank(format)) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return switch (date) {
                case LocalDate localDate -> localDate.format(formatter);
                case LocalDateTime localDateTime -> localDateTime.format(formatter);
                default -> throw new IllegalArgumentException(
                    "Unsupported date type: " + date.getClass().getName());
            };
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            throw new DateParseException(e);
        }
    }

    public static String getStringFromLocalDate(LocalDate date, String format) {
        return format(date, format);
    }

    public static String getStringFromLocalDateTime(LocalDateTime date, String format) {
        return format(date, format);
    }

    public static boolean isDateBeforeToday(String date) {
        LocalDate today = LocalDate.now();
        LocalDate localDateFromString = getLocalDateFromString(date, DateUtils.YYYYMMDD);
        return Objects.requireNonNull(localDateFromString).isBefore(today);
    }

}