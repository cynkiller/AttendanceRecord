package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Data;

import backend.util.StaticInfo;

@Data
public class UserInfo {
    public enum STATE {
        MEMBER,
        DEPART
    }

    public enum VOICEPART {
        FH1,
        FH2,
        FM1,
        FM2,
        MH1,
        MH2,
        ML1,
        ML2,
        INVALID
    }

    public enum LEADER {
        FH,
        FM,
        MH,
        ML
    }

    public enum AUTH {
        MEMBER,
        ADMIN,
        SUPERADMIN
    }

    public enum ATTEND {
        ABSENCE,
        ON_TIME,
        ASK_LEAVE,
        LATE,
        WELFARE
    }

    @Data
    public class RehearsalRecord {
        private Long rehearsalId;
        private ATTEND attendance;
        private Long punchTime;
        private int startPoint;
        private int remainPoint;
        private Boolean processed;
        private Long tunningPoint; // tunning point, only used in WELFARE situation
    }

    @Data
    public class Message {
        private String event;
        private long timestamp;
        private String content;
        private boolean read;
    }

    @Id private String id;
    private String openid;              // must
    private String nickName;            // must
    private String realName;
    private VOICEPART voicePart;
    private String avatarUrl;           // must
    private List<String> groups;
    private STATE state;                // must
    private int point;                  // must
    private AUTH authority;             // must
    private LEADER leader;              // administrator set?
    private List<RehearsalRecord> record;
    private List<Message> message;

    public UserInfo() {}
    public UserInfo(String openid, String nickName, String avatarUrl) {
        this.openid = openid;
        this.nickName = nickName;
        this.avatarUrl = avatarUrl;
        this.state = StaticInfo.DEFAULT_STATE;
        this.point = StaticInfo.DEFAULT_POINT;
        this.authority = StaticInfo.DEFAULT_AUTH;
        this.voicePart = VOICEPART.INVALID;
    }

    @Override
    public String toString() {
        String format = "{id: %s, nickName: %s, realName: %s, state: %d, point: %d, authority: %d, voicePart: %d}";
        String info = String.format(format, this.id, this.nickName, this.realName, this.state.ordinal(), this.point, this.authority.ordinal(), this.voicePart.ordinal());
        return info;
    }
}
