package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
    public static String getCurrentTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
