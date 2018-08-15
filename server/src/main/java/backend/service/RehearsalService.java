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

import backend.repo.RehearsalRepository;
import backend.util.Debug;
import backend.util.StaticInfo;
import backend.model.Rehearsal;

@Configuration
public class RehearsalService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RehearsalRepository rehearsalRepository;

    public Rehearsal getLastRehearsal() {
        return rehearsalRepository.findFirstByOrderByStartTimestampDesc();
    }

    public Rehearsal addNewRehearsal(long startTimestamp, long endTimestamp, String date, long addrId, String event, Rehearsal.STATE state) {
        Rehearsal r = new Rehearsal();
        r.setDate(date);
        r.setStartTimestamp(startTimestamp);
        r.setEndTimestamp(endTimestamp);
        r.setAddrId(addrId);
        r.setEvent(event);
        r.setState(state);
        return this.addNewRehearsal(r);
    }

    public Rehearsal addNewRehearsal(Rehearsal rehearsal) {
        return rehearsalRepository.save(rehearsal);
    }

}