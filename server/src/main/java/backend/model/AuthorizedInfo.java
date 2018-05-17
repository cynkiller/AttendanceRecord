package backend.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Data;

@Data
public class AuthorizedInfo {

	private String secretWord;
	private List<String> groupId;
}
