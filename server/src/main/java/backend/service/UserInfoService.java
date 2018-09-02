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
import org.springframework.data.domain.Sort;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import backend.repo.UserInfoRepository;
import backend.util.Debug;
import backend.util.StaticInfo;
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
            return true;
        }
        return false;
    }

    public List<UserInfo> getAllUserOpenid() {
        Query query = new Query();
        query.fields().include("openid");
        List<UserInfo> users = mongoTemplate.find(query, UserInfo.class);
        return users;
    }

    public UserInfo getAllRecordByOpenid(String openid) {
        Query query = new Query(Criteria.where("openid").is(openid));
        query.fields().include("record");
        query.fields().include("point");
        query.with(new Sort(Sort.Direction.ASC, "rehearsalId")); // sort from early to late
        UserInfo users = mongoTemplate.findOne(query, UserInfo.class);
        return users;
    }

    public Boolean getRecordAttendStatus(String openid, Long rehearsalId) {
        Criteria criteria = Criteria.where("record").elemMatch(Criteria.where("rehearsalId").is(rehearsalId).and("punchTime").exists(true)).and("openid").is(openid);
        Query query = new Query(criteria);
        UserInfo user = mongoTemplate.findOne(query, UserInfo.class);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean modifyRecord(String openid, Long rehearsalId, JSONObject obj) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("openid").is(openid);
        criteria = criteria.and("record.rehearsalId").in(rehearsalId);
        Query query = new Query(criteria);
        Update update = new Update();
        Iterator<String> it = obj.keys();
        //Debug.Log(obj);
        while (it.hasNext()) {
            String key = it.next();
            Object value = obj.get(key);
            update.set("record.$." + key, value);
        }
        Debug.Log(update);
        //UserInfo info = mongoTemplate.findAndModify(query, update, UserInfo.class);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserInfo.class);
        if (result.getMatchedCount() == 0) {
            Debug.Log("modifyRecord object not found.");
            return true;
        }
        if (result.getModifiedCount() == 0) {
            Debug.Log("modifyRecord update not performed.");
            return true;
        }
        return false;
        //Update update = new Update().set("record.$.role", newRole);
    }

    public Boolean insertNewRehearsalRecord(String openid, Long rehearsalId, String rehearsalDate) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("openid").is(openid);
        Query query = new Query(criteria);

        // Check user existance
        UserInfo user = mongoTemplate.findOne(query, UserInfo.class);
        int startPoint = user.getPoint();

        criteria = criteria.and("record.rehearsalId").in(rehearsalId);
        query = new Query(criteria);
        // Query record existance first
        user = mongoTemplate.findOne(query, UserInfo.class);
        if (user != null) {
            // already exists
            return true;
        }

        UserInfo.RehearsalRecord record = new UserInfo().new RehearsalRecord();
        record.setRehearsalId(rehearsalId);
        record.setRehearsalDate(rehearsalDate);
        record.setAttendance(UserInfo.ATTEND.ABSENCE); // default absense
        record.setStartPoint(startPoint);
        record.setRemainPoint(StaticInfo.DEFAULT_REMAIN_POINT);
        record.setProcessed(false);
        // update remain point set after processed
        Debug.Log(record);

        Update update = new Update();
        update.addToSet("record", record);

        // if u want to do upsert 
        //UserInfo info = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true), UserInfo.class);
        query = new Query(Criteria.where("openid").is(openid));
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserInfo.class);
        if (result.getMatchedCount() == 0) {
            Debug.Log("insertNewRehearsalRecord object not found.");
            return true;
        }
        if (result.getModifiedCount() == 0) {
            Debug.Log("insertNewRehearsalRecord update not performed.");
            return true;
        }
        return false;
    }
}