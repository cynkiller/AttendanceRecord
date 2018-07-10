package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Data;

@Data
public class UserInfo {
    public enum STATE {
        MEMBER,
        DEPART
    }

    public enum AUTH {
        ROOT,
        SUPERADMIN,
        ADMIN,
        MEMBER
    }

    public enum ATTEND {
        ON_TIME,
        ASK_LEAVE,
        LATE,
        ABSENCE
    }

    @Data
    private class RehearsalRecord {
        private String date;
        private ATTEND attendance;
        private int remainPoint;
    }

    @Data
    private class Message {
        private String event;
        private long timestamp;
        private String content;
        private boolean read;
    }

    @Id private String id;
    private String openid;              // must
    private String nickName;            // must
    private String realName;
    private String voicePart;
    private String avatarUrl;           // must
    private List<String> groups;
    private STATE state;                // must
    private int point;                  // must
    private AUTH authority;             // must
    private List<RehearsalRecord> record;
    private List<Message> message;
}
