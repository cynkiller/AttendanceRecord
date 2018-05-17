package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Data;

@Data
public class RehearsalInfo {

    @Data
    public class Event {
        @Id private int id;
        private String topic;
    }

    @Data
    public class Address {
    	@Id private int id;
    	private String location;
    	private String address;
    	private double longtitude;
        private double latitude;
    }

    @Data
    public class Rehearsal {
        private long startTimestamp;
        private long endTimestamp;
        private int addrId;
        private int eventId; // event string?
        private String state;
    }

    private List<Event> events;
    private List<Address> addresses;
    private List<Rehearsal> rehearsal;
}
