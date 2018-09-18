package backend.controller;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import backend.service.UserInfoService;
import backend.service.RehearsalService;
import backend.service.SessionService;
import backend.util.Debug;
import backend.util.StaticInfo;
import backend.util.Utility;
import backend.model.UserInfo;
import backend.model.Rehearsal;
import java.util.List;

@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RehearsalService rehearsalService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/queryUserInfo", method = RequestMethod.GET, produces = "application/json")
    public String queryUserInfo(
        @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter queryUserInfo");
        Debug.Log(sessionKey);
        // Check if session is valid, Get openid from session
        String openid = sessionService.getValidOpenid(sessionKey);
        if (openid == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
        }

        UserInfo userinfo = userInfoService.getUserInfoByOpenid(openid);
        return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, userinfo.toString());
    }

    @RequestMapping(value = "/queryAllUserInfo", method = RequestMethod.GET, produces = "application/json")
    public String queryAllUserInfo(
        @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter queryAllUserInfo");
        Debug.Log(sessionKey);
        // Check if session is valid, Get openid from session
        String openid = sessionService.getValidOpenid(sessionKey);
        if (openid == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
        }
        UserInfo userinfo = userInfoService.getUserInfoByOpenid(openid);
        UserInfo.AUTH auth = userinfo.getAuthority();
        if (!auth.equals(UserInfo.AUTH.ADMIN) && !auth.equals(UserInfo.AUTH.SUPERADMIN)) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_NOT_AUTHORIZED);
        }

        List<UserInfo> users = userInfoService.getAllUsers();
        return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, users.toString());
    }

    @RequestMapping(value = "/setUserinfo", method = RequestMethod.POST, produces = "application/json")
    public String setUserInfo(
        @RequestParam(value = "nickname", required = false) String nickname,
        @RequestParam(value = "realname", required = false) String realname,
        @RequestParam(value = "voicepart") int voicepart,
        @RequestParam(value = "status") int status,
        @RequestHeader("thirdSessionKey") String sessionKey)
    {
        Debug.Log("Enter setUserInfo");

        // Check if session is valid, Get openid from session
        String openid = sessionService.getValidOpenid(sessionKey);
        if (openid == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
        }

        // set updated userinfo
        JSONObject info = new JSONObject();
        //UserInfo info = userInfoService.getUserInfoByOpenid(openid);

        // check optional param
        if (!nickname.isEmpty()) info.put("nickName", nickname);
        if (!realname.isEmpty()) info.put("realName", realname);
        info.put("voicePart", UserInfo.VOICEPART.values()[voicepart]);
        info.put("state", UserInfo.STATE.values()[status]);

        // modify userinfo and findAndModify
        boolean rc = userInfoService.modifyUserInfo(openid, info);

        // Exception check
        if (rc) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_NO_USER);
        }

        return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.GENERAL_OK);   
    }

    private String _punchin(String openid, Long rehearsalId, UserInfo.ATTEND status, Long curr_ts)
    {
        UserInfo user = userInfoService.getAllRecordByOpenid(openid);
        List<UserInfo.RehearsalRecord> records = user.getRecord();
        if (records == null     ||
            records.isEmpty()   ||
            records.get(records.size() - 1).getRehearsalId() != rehearsalId)
        {
            if(userInfoService.insertNewRehearsalRecord(openid, rehearsalId)) {
                Debug.Log(openid + " record not inserted.");
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_UPDATE_REHEARSAL_STATUS_FAILED);
            } else {
                Debug.Log(openid + " record inserted.");
            }
        }
        JSONObject update = new JSONObject();
        update.put("attendance", status);
        update.put("punchTime", curr_ts);
        if (userInfoService.modifyRecord(openid, rehearsalId, update)) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_UPDATE_REHEARSAL_STATUS_FAILED);
        }
        return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, status.toString());
    }

    @RequestMapping(value = "/punchIn", method = RequestMethod.POST, produces = "application/json")
    public String punchIn( @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter punchIn");

        try {
            // Check if session is valid, Get openid from session
            String openid = sessionService.getValidOpenid(sessionKey);
            if (openid == null) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
            }

            Rehearsal lastRehearsal = rehearsalService.getLastRehearsal();
            Long rehearsalId = lastRehearsal.getId();
            
            // check if the latest rehearsal is outdated
            Long curr_ts = System.currentTimeMillis();
            Long start_ts = lastRehearsal.getStartTimestamp();
            Long end_ts = lastRehearsal.getEndTimestamp();
            if ( curr_ts < start_ts - StaticInfo.DEFAULT_PUNCHIN_TIME) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_REHEARSAL_NOT_STARTED);
            } else if ( curr_ts > end_ts) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_PUNCHIN_TIME_PASSED);
            } else if ( curr_ts > start_ts && curr_ts <= end_ts) {
                // late
                return _punchin(openid, rehearsalId, UserInfo.ATTEND.LATE, curr_ts);
            } else if ( curr_ts > start_ts - StaticInfo.DEFAULT_PUNCHIN_TIME && curr_ts <= start_ts ) {
                // on time
                return _punchin(openid, rehearsalId, UserInfo.ATTEND.ON_TIME, curr_ts);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }
    }

    @RequestMapping(value = "/queryPunchStatus", method = RequestMethod.GET, produces = "application/json")
    public String queryPunchStatus( @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter queryPunchStatus");

        try {
            // Check if session is valid, Get openid from session
            String openid = sessionService.getValidOpenid(sessionKey);
            if (openid == null) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
            }

            Rehearsal lastRehearsal = rehearsalService.getLastRehearsal();
            Long rehearsalId = lastRehearsal.getId();
            
            Boolean attend = userInfoService.getRecordAttendStatus(openid, rehearsalId);
            if (attend) {
                return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, "PUNCHED");
            } else {
                return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, "NOT_PUNCHED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }
    }

    @RequestMapping(value = "/askLeave", method = RequestMethod.POST, produces = "application/json")
    public String askLeave( @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter askLeave");

        try {
            // Check if session is valid, Get openid from session
            String openid = sessionService.getValidOpenid(sessionKey);
            if (openid == null) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
            }

            Rehearsal lastRehearsal = rehearsalService.getLastRehearsal();
            Long rehearsalId = lastRehearsal.getId();
            
            // ask leave need to be earlier than then rehearsal date
            String rehearsalDate = lastRehearsal.getDate();
            Long curr_ts = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            Date date = sdf.parse(rehearsalDate);
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            Long lastest_ts = cal.getTime().getTime();
            if (curr_ts > lastest_ts) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_ASKLEAVE_TOOLATE);
            }

            UserInfo user = userInfoService.getAllRecordByOpenid(openid);
            List<UserInfo.RehearsalRecord> records = user.getRecord();
            if (records == null     ||
                records.isEmpty()   ||
                records.get(records.size() - 1).getRehearsalId() != rehearsalId)
            {
                if(userInfoService.insertNewRehearsalRecord(openid, rehearsalId)) {
                    Debug.Log(openid + " record not inserted.");
                    return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_UPDATE_REHEARSAL_STATUS_FAILED);
                } else {
                    Debug.Log(openid + " record inserted.");
                }
            }

            JSONObject update = new JSONObject();
            update.put("attendance", UserInfo.ATTEND.ASK_LEAVE);
            if (userInfoService.modifyRecord(openid, rehearsalId, update)) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_UPDATE_REHEARSAL_STATUS_FAILED);
            }
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.GENERAL_OK);
        } catch ( java.text.ParseException e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }
    }

    @RequestMapping(value = "/queryRecords", method = RequestMethod.GET, produces = "application/json")
    public String queryRecords( @RequestHeader("thirdSessionKey") String sessionKey) {
        Debug.Log("Enter queryRecords");

        try {
            // Check if session is valid, Get openid from session
            String openid = sessionService.getValidOpenid(sessionKey);
            if (openid == null) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
            }
            
            List<Rehearsal> rehearsals = rehearsalService.getAllRehearsal();
            UserInfo user = userInfoService.getAllRecordByOpenid(openid);
            List<UserInfo.RehearsalRecord> records = user.getRecord();
            ObjectMapper mapper = new ObjectMapper();
            String recordstr = mapper.writeValueAsString(records);
            mapper = new ObjectMapper();
            String rehearsalstr = mapper.writeValueAsString(rehearsals);
            if (records != null) {
                return Utility.retmsg("{ status: %s, data: %s, rehearsal: %s }", StaticInfo.StatusCode.GENERAL_OK, recordstr, rehearsalstr);
            } else {
                return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.GENERAL_OK, "[]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }
    }    
}
