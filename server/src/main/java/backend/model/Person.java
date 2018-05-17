package backend.model;

import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
public class Person {

	@Id private String id;

	private String firstName;
	private String lastName;

	@Override
	public String toString() {
		return String.format("Person [id=%s, firstName=%s, lastName=%s]", id, firstName, lastName);
	}
}
