package backend.model;

import lombok.Data;

@Data
public class ClientData {

    private String code;
    private String encryptedData;
    private String iv;
}
