package backend.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import backend.model.AuthorizedInfo;

public class AuthorizedInfoRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    //@Query(value = "{}", fields = "{'secretWord': 1}")
    String findSecretWord() {
        Query query = new Query();
        query.fields().include("secretWord");
        AuthorizedInfo ai = mongoTemplate.findOne(query, AuthorizedInfo.class);
        if (ai != null)
            return ai.getSecretWord();
        else
            return null;
    }

}