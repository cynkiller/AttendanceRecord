package backend.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Thread;

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
        //String func = Thread.currentThread().getStackTrace()[1].getMethodName();
        //System.out.println(getDatetime() + String.format(" [INFO\t] [%s] %s", func, message));
        System.out.println(getDatetime() + String.format(" [INFO\t] %s", message));
    }

    public static void Log(Object message, String type) {
        //String func = Thread.currentThread().getStackTrace()[1].getMethodName();
        //System.out.println(getDatetime() + String.format(" [%s\t] [%s] %s", type, func, message));
        System.out.println(getDatetime() + String.format(" [%s\t] %s", type, message));
    }

    public static void Log(String... args) {
        for (String s: args) {
            Log(s);
        }
    }
}
