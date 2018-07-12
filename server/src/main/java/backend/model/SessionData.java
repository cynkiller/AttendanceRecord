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

        public ClientData() {}
        public ClientData(String code, String encryptedData, String iv, String groupData, String groupIv) {
            this.code = code;
            this.encryptedData = encryptedData;
            this.iv = iv;
            this.groupData = groupData;
            this.groupIv = groupIv;
        }
    }

    ServerData serverData;
    ClientData clientData;
    String openGId;
    String thirdSession;
    long expiretime;

}
