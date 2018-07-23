package backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import backend.repo.UserInfoRepository;
import backend.util.Debug;
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

    public UserInfo getUserInfoByOpenid(String openid) {
        UserInfo userInfo = userInfoRepository.findFirstByOpenid(openid);
        return userInfo;
    }

    public Boolean insertNewUser(String openid, String nickName, String avatarUrl) {
        UserInfo user = new UserInfo(openid, nickName, avatarUrl);
        mongoTemplate.save(user);
        return true;
    }

    public Boolean modifyUserInfo(String openid, JSONObject obj) {
        Query query = new Query(Criteria.where("openid").is(openid));
        Update update = new Update();
        Iterator<String> it = obj.keys();
        Debug.Log(obj);
        while (it.hasNext()) {
            String key = it.next();
            Object value = obj.get(key);
            update.set(key, value);
        }
        Debug.Log(update);
        UserInfo info = mongoTemplate.findAndModify(query, update, UserInfo.class);
        if (info == null) {
            // old object not found
            return false;
        }
        return true;
    }
}