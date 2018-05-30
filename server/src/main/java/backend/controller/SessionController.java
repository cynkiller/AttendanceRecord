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

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getCode(@RequestParam(value="encryptedData", required = false) String encryptedData) {
        if ( encryptedData != null) {
            Debug.Log(encryptedData);
            return encryptedData;
        } else {
            return "Invalid Code!";
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    //@ResponseBody
    //public String postCode( @RequestBody ClientData data) {
    //public String postCode( HttpServletRequest request) throws IOException, JSONException {
    //    String code = request.getParameter("code");
    public String postCode( @ModelAttribute SessionData.ClientData data,
                            @CookieValue(value = "SESSIONID", required = false) String thirdSession)
                            throws  IOException,
                                    JSONException,
                                    JsonParseException,
                                    JsonMappingException{

    /*public String postCode(
            @RequestParam(value="encryptedData", required = false) String encryptedData,
            @RequestParam(value="code", required = false) String code,
            @RequestParam(value="iv", required = false) String iv) {*/
        SessionData sessionData = new SessionData();
        sessionData.setClientData(data);
         String code = data.getCode();
        //code = "001ZD0Z42VtzmM0NxRW42vnUY42ZD0Zs";
        Debug.Log("data:" + data + " Code: " + code);
        String result = loginService.getWeixinOpenidAndSessionkey(code);
        String outString;
        ObjectMapper mapper = new ObjectMapper();
        try {
            SessionData.ServerData serverData = mapper.readValue(result, SessionData.ServerData.class);
            sessionData.setServerData(serverData);
            Debug.Log(serverData);
            //outString = new JSONObject(out).toString();
            /**
             * Verify identity 
             * 1. Check if openid already in sessionData. Expired or not.
             *  1.1 Not expired. Return the session id directly.
             *  1.2 Not in sessionData or expired.
             *      1.2.1 Check if openid already registered (in database)
             *      1.2.2 If registered, record client and server data,
                          calculate expiretime, create new session id
                          and return the id
             *      1.2.3 If not registered, judge if the user can be authorized
             *          1.2.3.1 Cannot be authorized, return error
             *          1.2.3.2 Can be authorized, add new user into database
             */

            if (userInfoService.openidExists(serverData.getOpenid())) {
                Debug.Log("Openid exists in the database. Verification passed.");
                // find existing 3rdsessionid

            }

            /* Get user sensitive data and group info in case needed */
            String encptdt = data.getEncryptedData();
            String ivdt = data.getIv();
            String ssk = serverData.getSession_key();
            // Currently user sensitive data not needed
            if ( !Debug.emptyStringExists(ssk, encptdt, ivdt) ) {
                JSONObject userSensitiveData = loginService.getEncryptedInfo(ssk, encptdt, ivdt);
                Debug.Log(userSensitiveData.toString());
                if(loginService.isValidData(userSensitiveData)) {
                    Debug.Log("Valid user sensitive data.");
                } else {
                    Debug.Log("Invalid user sensitive data!");
                }
            }

            /* Check if openid exist in the database */
            // use UserInfoService

            /* Check openId if this the first time login */
            String groupData = data.getGroupData();
            String groupIv = data.getGroupIv();
            if (!Debug.emptyStringExists(groupData, groupIv)) {
                Debug.Log("groupData: " + groupData + " groupIv: " + groupIv);
                try {
                    JSONObject groupInfo = loginService.getEncryptedInfo(ssk, groupData, groupIv);
                    Debug.Log(groupInfo.toString());
                    if (loginService.isValidData(groupInfo)) {
                        if (groupInfo.has("openGId")) {
                            sessionData.setOpenGId(groupInfo.getString("openGId"));
                        }
                    }
                    sessionService.addNewSession(serverData.getOpenid(), sessionData);
                    Debug.Log("sessionData: " + sessionData.toString());
                } catch(BadPaddingException e) {
                    e.printStackTrace();
                    outString = "Bad encrypted data.";
                    return outString;
                }
            }
            return new JSONObject("{status: ok, openGId: " + sessionData.getOpenGId() + "}").toString();
        } catch (Exception e) {
            e.printStackTrace();
            Debug.Log(result);
            ErrorMessage out = mapper.readValue(result, ErrorMessage.class);
            Debug.Log(out);
            outString = new JSONObject(out).toString();
            return outString;
        }
    }
}
