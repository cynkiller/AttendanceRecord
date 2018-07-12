/**
 * Change History:
 * 
 */
package backend.service;

import java.util.List;
import java.util.HashMap;
import java.util.UUID;
import lombok.Data;

import org.springframework.context.annotation.Configuration;

import backend.model.SessionData;
import backend.util.Debug;

@Configuration
public class SessionService {
    private HashMap<String, SessionData> session = new HashMap<String, SessionData>();
    private HashMap<String, String> idKeyMap = new HashMap<String, String>();

    private String generateSessionKey() {
        String uuid = UUID.randomUUID().toString();
        Debug.Log("New session key " + uuid + "generated.");
        return uuid;
    }

    private Boolean sessionExpired(String openid) {
        if ( !sessionExist(openid)) {
            return true;
        }
        SessionData sd = session.get(openid);
        long expiretime = sd.getExpiretime();
        long currenttime = Debug.getTimestamp();
        if (currenttime >= expiretime) {
            return true;
        }
        return false;
    }

    /**
     * Assumption: openid not registered
     */
    private SessionData getThirdSession(String openid, SessionData sd) {
        // Assumption: openid not registered
        assert sessionValid(openid) == false;

        // Initialise
        SessionData newsd = new SessionData();
        newsd.setServerData(sd.getServerData());
        newsd.setClientData(sd.getClientData());
        newsd.setOpenGId(sd.getOpenGId());

        // Generate 3rd session key
        String thirdSessionKey = generateSessionKey();
        newsd.setThirdSession(thirdSessionKey);

        // Calculate expire time
        long expire = sd.getServerData().getExpires_in();
        newsd.setExpiretime(Debug.getTimestamp() + expire * 1000); // milisecond need to multiply with 1000
        return newsd;
    }

    public boolean sessionExist(String openid) {
        if (session.get(openid) != null)
            return true;
        return false;
    }

    public boolean sessionValid(String openid) {
        if (sessionExist(openid) && !sessionExpired(openid))
            return true;
        return false;
    }

    public String getNewSession(String openid, SessionData sd) {
        Debug.Log("Current timestamp: " + Debug.getTimestamp());
        Debug.Log("Current time: " + Debug.getDatetime());
        Debug.Log("openid: " + openid);
        String thirdSession;
        thirdSession = getSession(openid);
        if (thirdSession != null) return thirdSession;

        SessionData newsd = getThirdSession(openid, sd);
        session.put(openid, newsd);
        idKeyMap.put(newsd.getThirdSession(), openid);
        Debug.Log(session);
        thirdSession = session.get(openid).getThirdSession();
        return thirdSession;
    }

    public String getSession(String openid) {
        if (sessionValid(openid)) {
            Debug.Log("Valid openid " + openid + " found!", "INFO");
            return session.get(openid).getThirdSession();
        } else {
            return null;
        }
    }
}
