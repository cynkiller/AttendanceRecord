package backend.service;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import backend.repo.UserInfoRepository;
import backend.model.UserInfo;

@Configuration
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Boolean openidExists(String openid) {
        long count = userInfoRepository.countByOpenid(openid);
        if (count > 0) return true;
        return false;
    }

    public List<UserInfo> getUserInfoByOpenid(String openid) {
        List<UserInfo> userInfo = userInfoRepository.findByOpenid(openid);
        return userInfo;
    }

    public Boolean insertNewUser(String openid, String nickName, String avatarUrl) {
        UserInfo user = new UserInfo(openid, nickName, avatarUrl);
        mongoTemplate.save(user);
        return true;
    }
}