package backend.model;

import lombok.Data;
import java.util.List;

@Data
public class SessionData {
    @Data
    public static class ServerData {
        private String openid;
        private String session_key;
        private long expires_in;
    }

    @Data
    public static class ClientData {
        private String code;
        private String encryptedData;
        private String iv;
        private String groupData;
        private String groupIv;
    }

    ServerData serverData;
    ClientData clientData;
    String thirdSession;
    String openGId;
    long expiretime;

}
