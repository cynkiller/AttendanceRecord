package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class Rehearsal {
    public enum STATE {
        PASSED,
        ONGOING,
        CANCELLED
    }

    private long startTimestamp;
    private long endTimestamp;
    private String date;        // xxxx-xx-xx
    private long addrId;
    private String event;
    private STATE state;
}
