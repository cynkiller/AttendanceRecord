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

    private String generateSessionKey() {
        String uuid = UUID.randomUUID().toString();
        Debug.Log("New session key " + uuid + "generated.");
        return uuid;
    }
    public boolean sessionExist(String openid) {
        if (session.get(openid) != null)
            return true;
        return false;
    }

    public String getThirdSession(String openid) {
        SessionData sd = session.get(openid);
        if (sd != null) {
            // Check if 3rdsession exists
            String thirdSessionKey = sd.getThirdSession();
            if ( thirdSessionKey != null) {
                return thirdSessionKey;
            }
            //generate 3rdsession
            thirdSessionKey = generateSessionKey();
            //Store in session
            sd.setThirdSession(thirdSessionKey);
            session.replace(openid, sd);
            return thirdSessionKey;
        }
        return null;
    }

    public boolean addNewSession(String openid, SessionData sd) {
        Debug.Log("Current timestamp: " + Debug.getTimestamp());
        Debug.Log("Current time: " + Debug.getDatetime());
        if (sessionExist(openid)) {
            Debug.Log(openid + "already exists!", "ERROR");
            return true;
        }
        session.put(openid, sd);
        return false;
    }
}
