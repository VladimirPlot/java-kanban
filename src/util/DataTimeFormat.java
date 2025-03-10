package util;

import java.time.format.DateTimeFormatter;

public class DataTimeFormat {
    public static DateTimeFormatter getDataTimeFormat() {
        return DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy");
    }
}