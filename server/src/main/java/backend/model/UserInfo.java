package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Data;

@Data
public class UserInfo {
    public enum AUTH {
        ROOT,
        ADMIN,
        MEMBER
    }

    @Id private String id;
    private String openid;
    private String nickName;
    private String avatarUrl;
    private List<String> groups;
    private String state;
    private int point;
    private AUTH authority;
}
