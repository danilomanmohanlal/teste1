package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import pt.scml.fin.batch.core.exceptions.DateParseException;
import pt.scml.fin.batch.core.utils.DateUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilsTest {

    @Test
    void testGetLocalDateTimeFromStringValid() {
        String input = "05/05/2025";
        LocalDateTime result = DateUtils.getLocalDateTimeFromString(input, DateUtils.DD_MM_YYYY);
        assertEquals(LocalDate.of(2025, 5, 5).atStartOfDay(), result);
    }

    @Test
    void testGetLocalDateTimeFromStringNullOrBlank() {
        assertNull(DateUtils.getLocalDateTimeFromString(null, DateUtils.DD_MM_YYYY));
        assertNull(DateUtils.getLocalDateTimeFromString(" ", DateUtils.DD_MM_YYYY));
        assertNull(DateUtils.getLocalDateTimeFromString("05/05/2025", ""));
    }

    @Test
    void testGetLocalDateTimeFromStringInvalidFormat() {
        assertThrows(DateParseException.class,
            () -> DateUtils.getLocalDateTimeFromString("20250505", DateUtils.DD_MM_YYYY));
    }

    @Test
    void testGetLocalDateFromStringValid() {
        String input = "05/05/2025";
        LocalDate result = DateUtils.getLocalDateFromString(input, DateUtils.DD_MM_YYYY);
        assertEquals(LocalDate.of(2025, 5, 5), result);
    }

    @Test
    void testGetLocalDateFromStringInvalidFormat() {
        assertThrows(DateParseException.class,
            () -> DateUtils.getLocalDateFromString("2025-05-05", DateUtils.DD_MM_YYYY));
    }

    @Test
    void testFormatWithLocalDate() {
        LocalDate date = LocalDate.of(2025, 5, 6);
        String result = DateUtils.format(date, DateUtils.YYYYMMDD);
        assertEquals("20250506", result);
    }

    @Test
    void testFormatWithLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 6, 15, 30, 0);
        String result = DateUtils.format(dateTime, DateUtils.DD_MM_YYYY_HH_MM_SS);
        assertEquals("06/05/2025 15:30:00", result);
    }

    @Test
    void testFormatWithInvalidTypeThrows() {
        assertThrows(DateParseException.class,
            () -> DateUtils.format("not a date", DateUtils.YYYYMMDD));
    }

    @Test
    void testFormatNullInputs() {
        assertNull(DateUtils.format(null, DateUtils.YYYYMMDD));
        assertNull(DateUtils.format(LocalDate.now(), ""));
    }

    @Test
    void testGetStringFromLocalDate() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        String formatted = DateUtils.getStringFromLocalDate(date, DateUtils.DD_MM_YYYY);
        assertEquals("01/01/2024", formatted);
    }

    @Test
    void testGetStringFromLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String formatted = DateUtils.getStringFromLocalDateTime(dateTime,
            DateUtils.DD_MM_YYYY_HH_MM_SS);
        assertEquals("01/01/2024 10:00:00", formatted);
    }

    @Test
    void testIsDateBeforeTodayTrue() {
        String yesterday = LocalDate.now().minusDays(1).format(
            DateTimeFormatter.ofPattern(DateUtils.YYYYMMDD));
        assertTrue(DateUtils.isDateBeforeToday(yesterday));
    }

    @Test
    void testIsDateBeforeTodayFalse() {
        String todayOrFuture = LocalDate.now().plusDays(1).format(
            DateTimeFormatter.ofPattern(DateUtils.YYYYMMDD));
        assertFalse(DateUtils.isDateBeforeToday(todayOrFuture));
    }
}
