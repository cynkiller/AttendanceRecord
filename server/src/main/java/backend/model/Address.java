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
}