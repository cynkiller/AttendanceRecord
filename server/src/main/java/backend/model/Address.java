package backend.model;

import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class Address {
    @Id @JsonIgnore private Long id;
    private String location;
    private String address;
    private double longtitude;
    private double latitude;

    @Override
    public String toString() {
        return String.format("{addrId: %d, location: %s, address: %s, longtitude: %f, latitude: %f }", this.id, this.location, this.address, this.longtitude, this.latitude);
    }
}