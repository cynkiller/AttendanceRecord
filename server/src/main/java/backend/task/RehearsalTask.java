package backend.task;

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
        Boolean rc = rehearsalService.insertNextDefaultRehearsal();
        if(rc) {
            Debug.Log("Insert next default rehearsal failed.");
        }
    }
}