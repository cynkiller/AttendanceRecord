package backend.util;

import java.util.HashMap;
import backend.model.UserInfo;

public class StaticInfo {
    public enum StatusCode {
        GENERAL_OK,
        GENERAL_BAD,
        CLIENT_BAD_DATA,
        CLIENT_BAD_SECRETWORD,
        CLIENT_NOT_AUTHORIZED,
        CLIENT_ASKLEAVE_TOOLATE,
        SERVER_NO_USER,
        SERVER_NO_SECRETWORD,
        SERVER_INSERT_NEWUSER_FAILED,
        SERVER_SESSION_EXPIRED,
        SERVER_ADDRESS_EXIST,
        SERVER_ADDRESS_NOT_EXIST,
        SERVER_REHEARSAL_UPDATE_FAIL,
        SERVER_REHEARSAL_NOT_STARTED,
        SERVER_PUNCHIN_TIME_PASSED,
        SERVER_UPDATE_REHEARSAL_STATUS_FAILED,
        SERVER_INTERNAL_ERROR
    }
    public static String FORMAT_STATUS = "{ status: %s }";
    public static int DEFAULT_POINT = 30;
    public static UserInfo.STATE DEFAULT_STATE = UserInfo.STATE.MEMBER;
    public static UserInfo.AUTH DEFAULT_AUTH = UserInfo.AUTH.MEMBER;
    public static Long DEFAULT_ADDR_ID = 1l; // TBD
    public static String DEFAULT_EVENT = "Rehearsal";
    public static HashMap<UserInfo.ATTEND, Integer> strategy = new HashMap<UserInfo.ATTEND, Integer>() {
        {
            put(UserInfo.ATTEND.ABSENCE, -12);
            put(UserInfo.ATTEND.ASK_LEAVE, -6);
            put(UserInfo.ATTEND.LATE, -3);
            put(UserInfo.ATTEND.ON_TIME, 0);
            put(UserInfo.ATTEND.WELFARE, 10);  // should be changed
        }
    };
    public static Integer DEFAULT_REMAIN_POINT = 99999;
    public static int DEFAULT_PUNCHIN_TIME = 30 * 60 * 1000; // punch in started 30 minutes before rehearsal started

    public static String DEFAULT_ADDRESS_LOCATION = "location";
    public static String DEFAULT_ADDRESS_ADDRESS = "address";
    public static Double DEFAULT_ADDRESS_LONGTITUDE = 121.0;
    public static Double DEFAULT_ADDRESS_LATITUDE = 31.0;

}