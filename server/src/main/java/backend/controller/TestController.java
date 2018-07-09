package backend.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import backend.util.Utility;
import backend.util.Debug;
import backend.repo.PersonRepository;
import backend.model.Person;
import backend.repo.AuthorizedInfoRepository;
import backend.model.AuthorizedInfo;

@RestController
public class TestController {
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private AuthorizedInfoRepository authorizedInfoRepository;
	
	@RequestMapping(value = "/people", method = RequestMethod.GET, produces = "application/json")
	public List<Person> queryPerson(@RequestParam(value="name", defaultValue="") String name) {
		if (! name.isEmpty()) {
			return personRepository.findByLastName(name);
		} else {
			return personRepository.findAll();
		}
	}

	@RequestMapping("/test/distance")
	public double queryPerson() {
		double distance = Utility.latlongDistance(121, 31, 122, 32);
		System.out.println(distance);
		return distance;
	}

	@RequestMapping("/test/setSecretWord") // consider change to /admin/... for these information
	public void setSecretWord(@RequestParam(value="secretWord") String secretWord) {
		// TBD: add identification verify. High importance
		authorizedInfoRepository.saveSecretWord(secretWord);
	}

	// TEST USE ONLY. Disable this interface in production for security consideration
	// Compare of password only happen in backend
	@RequestMapping("/test/getSecretWord")
	public String getSecretWord() {
		String secretWord = authorizedInfoRepository.findSecretWord();
		if (secretWord == null) {
			Debug.Log("secretWord has not been set yet.");
		}
		return secretWord;
	}

}
