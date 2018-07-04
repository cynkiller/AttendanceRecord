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
    private String openid;
    private String nickName;
    private String realName;
    private String voicePart;
    private String avatarUrl;
    private List<String> groups;
    private STATE state;
    private int point;
    private AUTH authority;
    private List<RehearsalRecord> record;
    private List<Message> message;
}
