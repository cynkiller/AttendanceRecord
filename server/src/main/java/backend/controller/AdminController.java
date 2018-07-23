package backend.controller;

import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import backend.util.Utility;
import backend.util.StaticInfo;
import backend.util.Debug;

import backend.repo.AuthorizedInfoRepository;
import backend.service.UserInfoService;
import backend.service.LoginService;
import backend.service.SessionService;
import backend.model.VerifyData;
import backend.model.SessionData;

@RestController
public class AdminController {

	@Autowired
	private AuthorizedInfoRepository authorizedInfoRepository;

	@Autowired
    private UserInfoService userInfoService;
	
    @Autowired
    private LoginService loginService;
		
    @Autowired
    private SessionService sessionService;

	@RequestMapping("/test/distance")
	public double queryPerson() {
		double distance = Utility.latlongDistance(121, 31, 122, 32);
		System.out.println(distance);
		return distance;
	}

	@RequestMapping("/admin/setSecretWord") // consider change to /admin/... for these information
	public void setSecretWord(
		@RequestParam(value="secretWord") String secretWord,
		@RequestHeader("thirdSessionKey") String sessionKey)
	{
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

	@RequestMapping(value = "/admin/verifyLogin", method = RequestMethod.POST, produces = "application/json")
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
			SessionData sessionData = new SessionData();

			// Make client data
			SessionData.ClientData clientData = new SessionData.ClientData(
				data.getCode(), data.getEncryptedData(), data.getIv(), data.getGroupData(), data.getGroupIv()
			);
			sessionData.setClientData(clientData);

			// Get server data
			SessionData.ServerData serverData;
			serverData = loginService.getSessionData(clientData);
			if (serverData == null) {
				outString = String.format("{ status: %s}", StaticInfo.StatusCode.CLIENT_BAD_DATA);
				return new JSONObject(outString).toString();
			}
			sessionData.setServerData(serverData);

			// Get user sensitive data
			JSONObject userSensitiveData = loginService.GetUserSensitiveData(sessionData);
			if (serverData == null) {
				outString = String.format("{ status: %s}", StaticInfo.StatusCode.CLIENT_BAD_DATA);
				return new JSONObject(outString).toString();
			}

			String openid = serverData.getOpenid();
			// Add the check in test scenario, user should never go to this page again after their first login
			if (!userInfoService.openidExists(openid)) {
				// insert new user into database
				String nickName = userSensitiveData.getString("nickName");
				String avatarUrl = userSensitiveData.getString("avatarUrl");
				userInfoService.insertNewUser(openid, nickName, avatarUrl);
			}

			if (userInfoService.openidExists(openid)) {
				Debug.Log("Openid exists in the database. Verification passed.");
				// find existing 3rdsessionid
				String thirdSessionKey = sessionService.getNewSession(openid, sessionData);
				outString = String.format("{ status: %s, thirdSessionKey: %s}", StaticInfo.StatusCode.GENERAL_OK, thirdSessionKey);
				return new JSONObject(outString).toString();
			} else {
				outString = String.format("{ status: %s}", StaticInfo.StatusCode.SERVER_INSERT_NEWUSER_FAILED);
				return new JSONObject(outString).toString();
			}				
		} else {
			Debug.Log("User secretWord is not correct.");
            outString = String.format("{ status: %s}", StaticInfo.StatusCode.CLIENT_BAD_SECRETWORD);
            return new JSONObject(outString).toString();
		}
	}
}
