package backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.crypto.BadPaddingException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

import backend.model.SessionData;
import backend.model.ErrorMessage;
import backend.service.LoginService;
import backend.service.SessionService;
import backend.service.UserInfoService;
import backend.util.Debug;
import backend.util.StaticInfo;
import backend.util.Utility;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserInfoService userInfoService;

    //private List<SessionData> sessionData;

    /*
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getCode(@RequestParam(value="encryptedData", required = false) String encryptedData) {
        if ( encryptedData != null) {
            Debug.Log(encryptedData);
            return encryptedData;
        } else {
            return "Invalid Code!";
        }
    }
    */

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    //@ResponseBody
    //public String postCode( @RequestBody ClientData data) {
    //public String postCode( HttpServletRequest request) throws IOException, JSONException {
    //    String code = request.getParameter("code");
    public String session( @ModelAttribute SessionData.ClientData data,
                            @CookieValue(value = "SESSIONID", required = false) String thirdSession)
                            throws  IOException,
                                    JSONException,
                                    JsonParseException,
                                    JsonMappingException{

    /*public String postCode(
            @RequestParam(value="encryptedData", required = false) String encryptedData,
            @RequestParam(value="code", required = false) String code,
            @RequestParam(value="iv", required = false) String iv) {*/
                
        String outString;
        String thirdSessionKey;
        SessionData sessionData = new SessionData();
        sessionData.setClientData(data);
        SessionData.ServerData serverData;
        serverData = loginService.getSessionData(data);
        if (serverData == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_BAD_DATA);
        }
        sessionData.setServerData(serverData);
    
        //outString = new JSONObject(out).toString();
        /**
         * Verify identity 
         * 1. Check if openid exists in database. if not, return SERVER_NO_USER
         * 2. Check if valid session exists is session service. if yes, return this session key.
         * 3. Valid user and invalid session data. Get user infomation, add to new session and return to user.
         */
        String openid = serverData.getOpenid();
        if (!userInfoService.openidExists(openid)) {
            // User not exist in database
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_NO_USER);
        }
        Debug.Log("Openid exists in the database. Verification passed.");

        if ( !sessionService.sessionValid(openid)) {
            /* Get user sensitive data and group info in case needed */
            JSONObject userSensitiveData = loginService.GetUserSensitiveData(sessionData);
            if (serverData == null) {
                return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_BAD_DATA);
            }

            /* Get group info GId in case need */
            // Currently GId is not needed
            String groupData = data.getGroupData();
            String groupIv = data.getGroupIv();
            if (!Debug.emptyStringExists(groupData, groupIv)) {
                String groupId = loginService.getGId(sessionData);
                if (groupId != null) {
                    sessionData.setOpenGId(groupId);
                } else {
                    return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_BAD_DATA);
                }
            }
            //Debug.Log("sessionData: " + sessionData.toString());
            thirdSessionKey = sessionService.getNewSession(openid, sessionData);
        } else {
            thirdSessionKey = sessionService.getSession(openid);
        }
        return Utility.retmsg("{ status: %s, thirdSessionKey: %s}", StaticInfo.StatusCode.GENERAL_OK, thirdSessionKey);
    }
}
