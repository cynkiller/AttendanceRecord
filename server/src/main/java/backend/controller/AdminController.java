package backend.controller;

import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;

import backend.util.Utility;
import backend.util.StaticInfo;
import backend.util.Debug;

import backend.repo.AuthorizedInfoRepository;
import backend.repo.UserInfoRepository;
import backend.model.AuthorizedInfo;
import backend.model.VerifyData;
import backend.model.UserInfo;
import backend.model.SessionData;

@RestController
public class AdminController {

	@Autowired
	private AuthorizedInfoRepository authorizedInfoRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@RequestMapping("/test/distance")
	public double queryPerson() {
		double distance = Utility.latlongDistance(121, 31, 122, 32);
		System.out.println(distance);
		return distance;
	}

	@RequestMapping("/admin/setSecretWord") // consider change to /admin/... for these information
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

	@RequestMapping(name = "/admin/verifyLogin", method = RequestMethod.POST, produces = "application/json")
	public String verifyLogin(@ModelAttribute VerifyData data) {
		String outString;
		String userSecretWord = data.getSecretWord();
		//SessionData.ClientData sessionData = data.getSessionData();
		String secretWord = authorizedInfoRepository.findSecretWord();
		if (secretWord == null) {
			Debug.Log("secretWord has not been set yet.");
            outString = String.format("{ status: %s}", StaticInfo.StatusCode.SERVER_NO_SECRETWORD);
            return new JSONObject(outString).toString();			
		} else if (secretWord.equals(userSecretWord)) {
			Debug.Log("Correct secretWord.");
			// Insert user info
			// TBD
			// return good status
            outString = String.format("{ status: %s}", StaticInfo.StatusCode.GENERAL_OK);
            return new JSONObject(outString).toString();				
		} else {
			Debug.Log("User secretWord is not correct.");
            outString = String.format("{ status: %s}", StaticInfo.StatusCode.CLIENT_BAD_SECRETWORD);
            return new JSONObject(outString).toString();
		}
	}
}
