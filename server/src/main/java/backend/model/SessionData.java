package backend.model;

import lombok.Data;
import java.util.List;
import backend.model.ServerData;
import backend.model.ClientData;

@Data
public class SessionData {
    List<ServerData> serverData;
    List<ClientData> clientData;
}
