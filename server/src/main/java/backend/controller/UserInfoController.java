package backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import backend.repo.UserInfoRepository;
import backend.model.UserInfo;
import java.util.List;

@RestController
@RequestMapping("/userinfo")
public class UserInfoController {
    @Autowired
    private UserInfoRepository repository;
    
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public List<UserInfo> queryUserInfo(@RequestParam(value="user", defaultValue="") String name) {
        if (! name.isEmpty()) {
            return null;
        } else {
            return null;
        }
    }

}
