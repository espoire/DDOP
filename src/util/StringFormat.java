package util;

public class StringFormat {
    public static String padStringToLength(String string, int length) {
        if (string.length() > length) string = string.substring(0, length);
        while (string.length() < length) string += " ";

        return string;
    }
}
