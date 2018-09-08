package backend.task;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import backend.service.RehearsalService;
import backend.service.UserInfoService;
import backend.model.Rehearsal;
import backend.model.UserInfo;
import backend.util.Debug;
import backend.util.StaticInfo;

@Component
@Scope("prototype")
public class RehearsalTask implements Runnable {

    private String threadName;

    private RehearsalService rehearsalService;

    private UserInfoService userInfoService;

    public RehearsalTask(String name, RehearsalService r, UserInfoService u) {
        threadName = name;
        rehearsalService = r;
        userInfoService = u;
        Debug.Log("rehearsalService: ", r.toString());
        Debug.Log("userInfoService: ", u.toString());
    }

    /**
     * Check each user recent record, perform point tunning accroding to attendance status
     */
    private void checkPassedRehearsal() {
        // get all users
        List<UserInfo> users = userInfoService.getAllUserOpenid();
        if (users == null || users.isEmpty()) return;

        Rehearsal lastRehearsal = rehearsalService.getLastRehearsal();
        Long rehearsalId = lastRehearsal.getId();

        for ( UserInfo user: users) {
            String openid = user.getOpenid();
            user = userInfoService.getAllRecordByOpenid( openid );
            List<UserInfo.RehearsalRecord> records = user.getRecord();

            for ( UserInfo.RehearsalRecord record: records) {
                // check all missed handled records
                if (record.getProcessed() == false) {
                    // check related rehearsal has finished or not
                    Rehearsal l_rehearsal = rehearsalService.getRehearsalById(record.getRehearsalId());
                    if (l_rehearsal.getState() != Rehearsal.STATE.PASSED)
                        // either cancelled or ongoing
                        continue;
                    if (user.getPoint() != record.getStartPoint()) {
                        Debug.Log("[ERROR] user remaining point not aligned with rehearsal record Should not happen!");
                        continue;
                    }
                    Long l_rehearsalId = record.getRehearsalId();
                    int tunningPoint = StaticInfo.strategy.get(record.getAttendance());
                    int remainingPoint = record.getStartPoint() + tunningPoint;

                    JSONObject update = new JSONObject();
                    update.put("point", remainingPoint);
                    Boolean rc = userInfoService.modifyUserInfo(openid, update);
                    if (rc) {
                        Debug.Log("Failed to update user point.");
                        break;
                    } else {
                        Debug.Log(openid + " user point updated.");
                    }

                    update = new JSONObject();
                    update.put("remainPoint", remainingPoint);
                    update.put("processed", true);
                    rc = userInfoService.modifyRecord(openid, l_rehearsalId, update);
                    if (rc) {
                        Debug.Log("Failed to update user record.");
                        break;
                    } else {
                        Debug.Log(openid + " user record updated.");
                    }
                }
            }
        }

    }

    @Override
    public void run() {
        Debug.Log("Enter BackgroundService.run()");
        Debug.Log("Thread " + threadName);

        checkPassedRehearsal();
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
            } else if (recentRehearsal.getState() == Rehearsal.STATE.ONGOING) {
                // update rehearsal status
                JSONObject update = new JSONObject();
                update.put("state", Rehearsal.STATE.PASSED);
                Boolean rc = rehearsalService.findAndModifyRehearsal(recentRehearsal.getId(), update);
                if (rc) {
                    Debug.Log("failed to update rehearsal state.");
                } else {
                    Debug.Log(recentRehearsal.getDate() + "Rehearsal state updated to PASSED.");
                }
            }
        }

        // create new rehearsal entry
        Boolean rc = rehearsalService.insertNextDefaultRehearsal();
        if (rc) {
            Debug.Log("Insert next default rehearsal failed.");
            return;
        } else {
            Debug.Log("Next default rehearsal inserted.");
        }

        recentRehearsal = rehearsalService.getNewInsertedRehearsal();
        Long rehearsalId = recentRehearsal.getId();

        // get all users
        List<UserInfo> users = userInfoService.getAllUserOpenid();

        // insert new record for each user
        for (UserInfo user: users) {
            String openid = user.getOpenid();
            rc = userInfoService.insertNewRehearsalRecord(openid, rehearsalId);
            if (rc) {
                Debug.Log(openid + " record not inserted.");
            } else {
                Debug.Log(openid + " record inserted.");
            }
        }
    }
}