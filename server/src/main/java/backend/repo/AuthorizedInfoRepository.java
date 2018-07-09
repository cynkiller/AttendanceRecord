package backend.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import backend.model.AuthorizedInfo;

@Repository
public class AuthorizedInfoRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    //@Query(value = "{}", fields = "{'secretWord': 1}")
    public String findSecretWord() {
        Query query = new Query();
        query.fields().include("secretWord");
        AuthorizedInfo ai = mongoTemplate.findOne(query, AuthorizedInfo.class);
        if (ai != null)
            return ai.getSecretWord();
        else
            return null;
    }

    public void saveSecretWord(String secretWord) {
        Query query = new Query();
        Update update = new Update();
        update.set("secretWord", secretWord);
        AuthorizedInfo ai = mongoTemplate.findAndModify(query, update, AuthorizedInfo.class);
        if (ai == null) {
            ai = new AuthorizedInfo();
            ai.setSecretWord(secretWord);
            mongoTemplate.save(ai);
        }
    }
}