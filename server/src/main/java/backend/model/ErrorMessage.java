package backend.model;

import lombok.Data;

@Data
public class ErrorMessage {
    private long errcode;
    private String errmsg;
}
