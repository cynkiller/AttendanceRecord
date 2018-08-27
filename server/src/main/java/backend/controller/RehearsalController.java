package backend.controller;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;

import backend.service.SessionService;
import backend.service.AddressService;
import backend.service.RehearsalService;
import backend.service.UserInfoService;
import backend.util.Debug;
import backend.util.StaticInfo;
import backend.util.Utility;
import backend.model.Rehearsal;
import backend.model.Address;
import backend.model.UserInfo;
import java.util.List;

@RestController
public class RehearsalController {
    @Autowired
    private RehearsalService rehearsalService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private SessionService sessionService;

    
	@Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/setRehearsalAddress", method = RequestMethod.POST, produces = "application/json")
    public String setRehearsalAddress(
        @RequestHeader("thirdSessionKey") String sessionKey,
        @RequestParam(value = "addressUid") String addressUid)
    {
        Debug.Log("Enter setRehearsalAddress");

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

        // Change rehearsal addressUid
        Rehearsal lastRehearsal = rehearsalService.getLastRehearsal();
        String date = lastRehearsal.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date r_date = sdf.parse(date);
            Date c_date = sdf.parse(sdf.format(new Date()));
            if (c_date.after(r_date)) {
                // Rehearsal not updated
                Rehearsal nextRehearsal = rehearsalService.genNextDefaultRehearsal();
                // insert into database
                lastRehearsal = rehearsalService.addNewRehearsal(nextRehearsal);
                if (lastRehearsal != null) {
                    Debug.Log("Insert new rehearsal success.");
                } else {
                    Debug.Log("Failed to insert new rehearsal.");
                    return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_REHEARSAL_UPDATE_FAIL);
                }
            }
        } catch ( java.text.ParseException e) {
            e.printStackTrace();
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }

        JSONObject info = new JSONObject();
        info.put("addrId", addressUid);
        Boolean rc = rehearsalService.findAndModifyRehearsal(lastRehearsal.getId(), info);

        // Exception check
        if (!rc) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_REHEARSAL_UPDATE_FAIL);
        }

        return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.GENERAL_OK);
    }

    @RequestMapping(value = "/getRehearsalInfo", method = RequestMethod.GET, produces = "application/json")
    public String getRehearsalInfo(
        @RequestHeader("thirdSessionKey") String sessionKey)
    {
        Debug.Log("Enter getRehearsalInfo");

        // Check if session is valid, Get openid from session
        String openid = sessionService.getValidOpenid(sessionKey);
        if (openid == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
        }

        Rehearsal rehearsal = rehearsalService.getLastRehearsal();
        Address address = addressService.getAddressById(rehearsal.getAddrId());
        ObjectMapper mapper = new ObjectMapper();
        try {
            String rhsl = mapper.writeValueAsString(rehearsal);
            String adrs = mapper.writeValueAsString(address);
            return Utility.retmsg("{ status: %s, data: %s, address: %s }", StaticInfo.StatusCode.GENERAL_OK, rhsl, adrs);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Utility.retmsg("{ status: %s, data: %s }", StaticInfo.StatusCode.SERVER_INTERNAL_ERROR);
        }
    }

    @RequestMapping(value = "/setRehearsalInfo", method = RequestMethod.POST, produces = "application/json")
    public String setRehearsalInfo(
        @RequestHeader("thirdSessionKey") String sessionKey,
        @RequestParam(value = "date") String date,
        @RequestParam(value = "startTimestamp") long start,
        @RequestParam(value = "endTimestamp") long end,
        @RequestParam(value = "isHoliday") Boolean isHoliday,
        @RequestParam(value = "event") String event,
        @RequestParam(value = "addrId") long addrId)
    {
        Debug.Log("Enter setRehearsalInfo");
        Debug.Log(String.format("%s %s %d %d %b %d", date, event, start, end, isHoliday, addrId));

        // Check if session is valid, Get openid from session
        String openid = sessionService.getValidOpenid(sessionKey);
        if (openid == null) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.SERVER_SESSION_EXPIRED);
        }

        // Check authority
        UserInfo userinfo = userInfoService.getUserInfoByOpenid(openid);
        UserInfo.AUTH auth = userinfo.getAuthority();
        if (!auth.equals(UserInfo.AUTH.ADMIN) && !auth.equals(UserInfo.AUTH.SUPERADMIN)) {
            return Utility.retmsg(StaticInfo.FORMAT_STATUS, StaticInfo.StatusCode.CLIENT_NOT_AUTHORIZED);
        }

        // TBD
        return null;
    }
}
