package alertmed.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Used in templates.
 */
@Data
@AllArgsConstructor
public class ThymeleafDateFormatter {

    private final DateTimeFormatter formatter;

    public static ThymeleafDateFormatter create() {
        return new ThymeleafDateFormatter(DateTimeFormatter.ofPattern("DD-MM-YYY HH:mm"));
    }

    public static ThymeleafDateFormatter create(@NonNull String format) {
        return new ThymeleafDateFormatter(DateTimeFormatter.ofPattern(format));
    }

    public String format(TemporalAccessor zonedDateTime) {
        if (zonedDateTime == null) {
            return "";
        } else return formatter.format(zonedDateTime);
    }

    public String parse(long time) {
        if (time == 0) {
            return "";
        } else {
            return formatter.format(ZonedDateTime.ofInstant(new Date(time).toInstant(), ZoneId.systemDefault()));
        }
    }
}
