package backend.util;

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
        SERVER_INTERNAL_ERROR
    }
    public static String FORMAT_STATUS = "{ status: %s }";
    public static int DEFAULT_POINT = 30;
    public static UserInfo.STATE DEFAULT_STATE = UserInfo.STATE.MEMBER;
    public static UserInfo.AUTH DEFAULT_AUTH = UserInfo.AUTH.MEMBER;
}