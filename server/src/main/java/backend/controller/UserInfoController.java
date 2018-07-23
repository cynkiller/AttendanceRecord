package backend.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import backend.service.UserInfoService;
import backend.service.SessionService;
import backend.util.Debug;
import backend.util.StaticInfo;
import backend.util.Utility;
import backend.model.UserInfo;
import java.util.List;

@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/queryUserInfo", method = RequestMethod.POST, produces = "application/json")
    public List<UserInfo> queryUserInfo(@RequestParam(value="user", defaultValue="") String name) {
        if (! name.isEmpty()) {
            return null;
        } else {
            return null;
        }
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
        if (nickname != null) info.put("nickName", nickname);
        if (realname != null) info.put("realName", realname);
        info.put("voicePart", UserInfo.VOICEPART.values()[voicepart]);
        info.put("state", UserInfo.STATE.values()[status]);

        // modify userinfo and findAndModify
        boolean rc = userInfoService.modifyUserInfo(openid, info);

        // Exception check
        if (!rc) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_NO_USER);
        }

        return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.GENERAL_OK);   
    }
}
