package backend.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");

    public static Boolean emptyStringExists(String... args) {
        for(String s: args) {
            if (s == null || s.isEmpty()|| s.equals("null")) {
                return true;
            }
        }
        return false;
    }

    public static String byteArrayToString(byte[] bytes) {
        String out = "";
        for (byte b : bytes) {
            out += String.format("%02X", b);
        }
        return out;
    }

    public static long getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }

    public static String getDatetime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return sdf.format(timestamp);
    }

    public static void Log(Object message) {
        System.out.println(getDatetime() + " [INFO\t] " + message);
    }

    public static void Log(Object message, String type) {
        System.out.println(getDatetime() + " [" + type + "\t] " + message);
    }

    public static void Log(String... args) {
        for (String s: args) {
            Log(s);
        }
    }
}
