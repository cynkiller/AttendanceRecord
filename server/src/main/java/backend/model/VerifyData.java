package backend.model;

import lombok.Data;
import java.util.List;
import backend.model.SessionData;

@Data
public class VerifyData {
    //private SessionData.ClientData sessionData;
    private String code;
    private String encryptedData;
    private String iv;
    private String groupData;
    private String groupIv;
    private String secretWord;
}
