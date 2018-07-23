package backend.repo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import backend.model.UserInfo;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    List<UserInfo> findByOpenid(String openid);
    UserInfo findFirstByOpenid(String openid);
    List<UserInfo> findAll();
    long countByOpenid(String openid);
    //UserInfo insert(UserInfo userInfo);
}
