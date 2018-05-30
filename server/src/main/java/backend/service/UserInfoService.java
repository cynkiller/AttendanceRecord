package backend.service;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import backend.repo.UserInfoRepository;
import backend.model.UserInfo;

@Configuration
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    public Boolean openidExists(String openid) {
        long count = userInfoRepository.countByOpenid(openid);
        if (count > 0) return true;
        return false;
    }

    public List<UserInfo> getUserInfoByOpenid(String openid) {
        List<UserInfo> userInfo = userInfoRepository.findByOpenid(openid);
        return userInfo;
    }
}