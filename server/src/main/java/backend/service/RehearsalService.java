package backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.sql.Timestamp; 

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Sort;

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

    private Long getLargestId() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "id"));
        query.fields().include("id");
        Rehearsal rehearsal = mongoTemplate.findOne(query, Rehearsal.class);
        if (rehearsal != null)
            return rehearsal.getId();
        else
            return null;
    }

    public Rehearsal getLastRehearsal() {
        return rehearsalRepository.findFirstByOrderByStartTimestampDesc();
    }

    public Rehearsal genNextDefaultRehearsal() {
        LocalDate ld = LocalDate.now();
        ld = ld.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)); // Next Saturday
        Debug.Log("Next rehearsal date: " + ld.toString());

        Long lastid = getLargestId();
        if (lastid == null) {
            lastid = 0l;
        }
        Rehearsal rehearsal = new Rehearsal();
        String start = ld.toString() + " 09:30:00";
        String end = ld.toString() + " 12:30:00";
        rehearsal.setDate(ld.toString());
        rehearsal.setId(lastid + 1);
        rehearsal.setIsHoliday(false);
        rehearsal.setStartTimestamp(Timestamp.valueOf(start).getTime());
        rehearsal.setEndTimestamp(Timestamp.valueOf(end).getTime());
        rehearsal.setAddrId(StaticInfo.DEFAULT_ADDR_ID);
        rehearsal.setEvent(StaticInfo.DEFAULT_EVENT);
        rehearsal.setState(Rehearsal.STATE.ONGOING);
        return rehearsal;
    }

    public Boolean insertNextDefaultRehearsal() {
        Rehearsal nextRehearsal = this.genNextDefaultRehearsal();
        Rehearsal newRehearsal = this.addNewRehearsal(nextRehearsal);
        if (newRehearsal != null) {
            Debug.Log("Insert new rehearsal success.");
            return false;
        } else {
            Debug.Log("Failed to insert new rehearsal.");
            return true;
        }
    }

    public Rehearsal addNewRehearsal(long startTimestamp, long endTimestamp, String date, Boolean isHoliday, long addrId, String event, Rehearsal.STATE state) {
        Rehearsal r = new Rehearsal();
        r.setDate(date);
        r.setIsHoliday(isHoliday);
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

    public Boolean findAndModifyRehearsal(Long id, JSONObject obj) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();
        Iterator<String> it = obj.keys();
        Debug.Log(obj);
        while (it.hasNext()) {
            String key = it.next();
            Object value = obj.get(key);
            update.set(key, value);
        }
        Debug.Log(update);
        Rehearsal info = mongoTemplate.findAndModify(query, update, Rehearsal.class);
        if (info == null) {
            // old object not found
            return false;
        }
        return true;
    }
}