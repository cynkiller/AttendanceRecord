package backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import backend.repo.PersonRepository;
import backend.model.Person;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PersonController {
	@Autowired
	private PersonRepository repository;
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public List<Person> queryPerson(@RequestParam(value="name", defaultValue="") String name) {
		if (! name.isEmpty()) {
			return repository.findByLastName(name);
		} else {
			return repository.findAll();
		}
	}
}
