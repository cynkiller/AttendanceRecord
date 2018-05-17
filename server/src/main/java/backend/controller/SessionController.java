package backend.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import backend.model.ClientData;
import backend.service.LoginService;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public String getCode(@RequestParam(value="encryptedData", required = false) String encryptedData) {
        if ( encryptedData != null) {
            System.out.println(encryptedData);
            return encryptedData;
        } else {
            return "Invalid Code!";
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    //@ResponseBody
    //public String postCode( @RequestBody ClientData data) {
    //public String postCode( HttpServletRequest request) throws IOException, JSONException {
    //    String code = request.getParameter("code");
    public String postCode( @ModelAttribute ClientData data ) {
    /*public String postCode(
            @RequestParam(value="encryptedData", required = false) String encryptedData,
            @RequestParam(value="code", required = false) String code,
            @RequestParam(value="iv", required = false) String iv) {*/
        String code = data.getCode();
        System.out.println("data:" + data + " Code: " + code);
        String result = loginService.getSessionKeyOropenid(code);
        System.out.println(result);
        return loginService.getSessionKeyOropenid(code);
    }
}
