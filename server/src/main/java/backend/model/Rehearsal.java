package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class Rehearsal {
    public enum STATE {
        PASSED,
        ONGOING
    }

    private long startTimestamp;
    private long endTimestamp;
    private long addrId;
    private String event;
    private STATE state;
}
