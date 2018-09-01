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
        SERVER_NO_USER,
        SERVER_NO_SECRETWORD,
        SERVER_INSERT_NEWUSER_FAILED,
        SERVER_SESSION_EXPIRED,
        SERVER_ADDRESS_EXIST,
        SERVER_ADDRESS_NOT_EXIST,
        SERVER_REHEARSAL_UPDATE_FAIL,
        SERVER_INTERNAL_ERROR
    }
    public static String FORMAT_STATUS = "{ status: %s }";
    public static int DEFAULT_POINT = 30;
    public static UserInfo.STATE DEFAULT_STATE = UserInfo.STATE.MEMBER;
    public static UserInfo.AUTH DEFAULT_AUTH = UserInfo.AUTH.MEMBER;
    public static Long DEFAULT_ADDR_ID = 3l; // TBD
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
}