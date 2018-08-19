package backend.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.sql.Timestamp; 

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import backend.service.RehearsalService;
import backend.model.Rehearsal;
import backend.util.Debug;
import backend.util.StaticInfo;

@Component
@Scope("prototype")
public class RehearsalTask implements Runnable {

    private String threadName;

    private RehearsalService rehearsalService;

    public RehearsalTask(String name, RehearsalService r) {
        threadName = name;
        rehearsalService = r;
        Debug.Log("rehearsalService: ", r.toString());
    }

    @Override
    public void run() {
        Debug.Log("Enter BackgroundService.run()");
        Debug.Log("Thread " + threadName);

        // find the latest rehearsal record, see if its endtimestamp is before current timestamp
        // if it is, we need to add a new entry for next rehearsal
        Rehearsal recentRehearsal = rehearsalService.getLastRehearsal();
        if ( recentRehearsal != null) {
            // check if the latest rehearsal is outdated
            Long end_ts = recentRehearsal.getEndTimestamp();
            Long curr_ts = System.currentTimeMillis();
            if ( curr_ts <= end_ts) {
                Debug.Log("Current timestamp: " + curr_ts.toString());
                Debug.Log("Last rehearsal timestamp: " + end_ts.toString());
                return;
            }
        }

        // create new rehearsal entry
        LocalDate ld = LocalDate.now();
        ld = ld.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)); // Next Saturday
        Debug.Log("Next rehearsal date: " + ld.toString());

        Rehearsal rehearsal = new Rehearsal();
        String start = ld.toString() + " 09:30:00";
        String end = ld.toString() + " 12:30:00";
        rehearsal.setDate(ld.toString());
        rehearsal.setStartTimestamp(Timestamp.valueOf(start).getTime());
        rehearsal.setEndTimestamp(Timestamp.valueOf(end).getTime());
        rehearsal.setAddrId(StaticInfo.DEFAULT_ADDR_ID);
        rehearsal.setEvent(StaticInfo.DEFAULT_EVENT);
        rehearsal.setState(Rehearsal.STATE.ONGOING);

        // insert into database
        Rehearsal newRehearsal = rehearsalService.addNewRehearsal(rehearsal);
        if (newRehearsal != null) {
            Debug.Log("Insert new rehearsal success.");
        } else {
            Debug.Log("Failed to insert new rehearsal.");
        }
    }
}