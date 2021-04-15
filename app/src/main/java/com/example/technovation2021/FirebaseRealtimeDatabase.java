package com.example.technovation2021;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

enum EventFrequency {
    EVENT_FREQUENCY_EVERYDAY,
    EVENT_FREQUENCY_WEEKDAY,
    EVENT_FREQUENCY_WEEKEND
}

/*
class UserSettings implements Serializable {
    String passwordHint;

    public String getPasswordHint() {
        return passwordHint;
    }

    public UserSettings(String pswdHint) {
        this.passwordHint = pswdHint;
    }
}
 */

public class FirebaseRealtimeDatabase {
    private static final String LOG_TAG = FirebaseRealtimeDatabase.class.getSimpleName();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    ArrayList<GenericActivity> arrList;
    ArrayList<Event> evtList;
    ArrayList<CalInterval> intList;


    public FirebaseRealtimeDatabase() {
        mAuth = FirebaseAuth.getInstance();
        arrList = new ArrayList<GenericActivity>();
        evtList = new ArrayList<Event>();
    }

    public void getEvents(LocalDate fromDate, LocalDate toDate, Object caller) {
        Log.d(LOG_TAG, "FRD::getEvents:" + fromDate.toString() + " to:" + toDate.toString());
        new GetEventsForRange(fromDate, toDate, caller).execute();
    }

    public int clearEvents(Event task) {
        return 0;
    }

    public void getTaskList(LocalDate fromDate, LocalDate toDate, Object caller) {
        Log.d(LOG_TAG, "FRD::getEvents:" + fromDate.toString() + " to:" + toDate.toString());
        new GetTasksForRange(fromDate, toDate, caller).execute();
    }

    public void printIntList() {
        Iterator localIter = intList.iterator();
        while (localIter.hasNext()) {
            CalInterval e = (CalInterval) localIter.next();
            Log.d(LOG_TAG, "Interval: " + e.t1.toString() + " duration: " + e.duration);
        }
    }

    /*
    public int savePasswordHint(String hint) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth.getCurrentUser().
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("userSettings");
    }

     */

    public int saveActivity(GenericActivity e) {
        Log.d(LOG_TAG, "saveActivity " + e.getDesc());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("activityList");
        DatabaseReference newPostRef = newref.push();
        // TODO: check for success/failure.
        newPostRef.setValue(e);
        return 0;
    }

    // split task into events. save events and task to firebase.
    // events go into "eventList" and task goes into "taskList"
    public int saveHwTask(GenericTask t, Object caller, int nextIndex) {
        Log.d(LOG_TAG, "saveTask called");

        //ArrayList<GenericActivity> activities = new ArrayList<GenericActivity>();
        //getAllActivities();
        //Log.d(LOG_TAG, "iter size " + activities.size());

        //t.timeToFinishTheTask();
        new AddTaskAndSchedule(t, caller, nextIndex).execute();
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean is2Weeks(LocalDate d1, LocalDate d2) {
        LocalDateTime b = d1.atStartOfDay();
        LocalDateTime e = d2.atStartOfDay();
        Duration dn = Duration.between(b, e);
        // days between from and to
        Log.d(LOG_TAG, "is2Weeks " + dn.toDays() + " days");
        if ((dn.toDays() % 14) == 0) {
            return true;
        }
        return false;
    }

    class CalInterval implements Comparable<CalInterval> {
        LocalTime t1;
        int duration;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public CalInterval() {
            this.t1 = LocalTime.MIDNIGHT;
            this.duration = 30;
        }

        @Override
        public int compareTo(CalInterval calInterval) {
            return this.t1.compareTo(calInterval.t1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void markTimeAsBusy(Object obj, int type) {
        // type 1: GenericActivity, 2: Event
        CalInterval i = new CalInterval();
        //if( GenericActivity.class.isInstance(obj) ) {
        if (type == 1) {
            GenericActivity a = (GenericActivity) obj;
            i.t1 = a.startTime;
            i.duration = a.duration;
            Log.d(LOG_TAG, a.desc + " busy from " + a.getStartTime());
        }
        //if( Event.class.isInstance(obj) ) {
        if (type == 2) {
            Event e = (Event) obj;
            i.t1 = e.startTime;
            i.duration = e.duration;
            Log.d(LOG_TAG, e.eventDesc + " event busy from " + e.getStartTime());
        }
        intList.add(i);
        Log.d(LOG_TAG, "added to intList " + intList.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int findFreeSlot(int timeNeeded) {
        //for(CalInterval timeSlot: intList){
        //if ( timeSlot.t1)
        //}
        Log.d(LOG_TAG, "findFreeSlot of: " + timeNeeded + " busySlots: " + intList.size());
        for (int i = 0; i < (intList.size() - 1); i++) {
            CalInterval i1 = intList.get(i);
            CalInterval i2 = intList.get(i + 1);
            Log.d(LOG_TAG, "i1.time " + i1.t1.toString() + " duration: " + i1.duration + " next slot: " + i2.t1.toString());
            if (i1.t1.plusMinutes(i1.duration + timeNeeded).compareTo(i2.t1) < 0) {
                Log.d(LOG_TAG, "free slot after: " + i1.t1.toString());
                return i;
            }
        }
        Log.d(LOG_TAG, "findFreeSlot: could not find free slot");
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int addBreakEvent(LocalTime tm, LocalDate d, String taskId) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("eventList");
        DatabaseReference newPostRef = newref.push();
        /*
            String eventDesc,
            String date,
            String startTime,
            Integer chunkNumber,
            Integer totalChunks,
            Integer duration,
            Integer type,
            String taskId,
            String notes
         */
        Log.d(LOG_TAG, "Adding break event at: " + tm.toString() + " on: " + d.toString());
        Event breakEvent = new Event("Break time", d.toString(), tm.toString(),
                1, 1, GenericTask.MIN_BREAK_TIME, CalEvent.CAL_EVENT_BREAK.ordinal(),
                        "BREAKTASK", "Relax. Play some music or read a book or play an instrument!");
        // TODO: check for success/failure.
        newPostRef.setValue(breakEvent);
        return 0; // TODO: check return value
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int addDoNotDisturbEvents(LocalDate curDate) {
        Log.d(LOG_TAG, "Add do not disturb event");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("eventList");
        DatabaseReference newPostRef = newref.push();
        Event ev1 = new Event("DND", curDate.toString(), "00:01", 1,1, 9*60, 4,
                            "DNDEVENT", "DNDEVENT");
        // TODO: check for success/failure.
        newPostRef.setValue(ev1);

        Event ev2 = new Event("DND", curDate.toString(), "21:00", 1,1, 3*60, 4,
                "DNDEVENT", "DNDEVENT");
        // TODO: check for success/failure.
        newPostRef.setValue(ev2);

        return 0; // TODO: check return value
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int addEvent(LocalTime tm, int duration, GenericTask tsk, LocalDate localDate, Integer slotNr, Integer totSlots) {
        Log.d(LOG_TAG, "Scheduling event: " + tsk.desc + " at: " + tm.toString() + " on: " + localDate.toString());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("eventList");
        DatabaseReference newPostRef = newref.push();
        Event workEvent = new Event(tsk.desc, localDate.toString(), tm.toString(), slotNr, totSlots, duration, 2,
                tsk.taskId, tsk.notes);
        // TODO: check for success/failure.
        newPostRef.setValue(workEvent);
        return 0; // TODO: check return value
    }

    // Class to return master taskList from firebase
    class GetTasksForRange extends AsyncTask<Void, Void, Integer> {

        LocalDate fDate, tDate;
        boolean evtFetchDone = false;
        ArrayList<GenericTask> dbTaskList;
        Object caller;

        // TODO: For now get all tasks from fb. In theory app could only fetch tasks that
        //       were posted from oldest homework posted date in the schoolloop to the
        //       farthest due date in homework.
        public GetTasksForRange(LocalDate from, LocalDate to, Object _caller) {
            fDate = from;
            tDate = to;
            caller =  _caller;
            dbTaskList = new ArrayList<GenericTask>();
            Log.d(LOG_TAG, "get tasks for range: " + from.toString() + " to " + to.toString());
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Integer doInBackground(Void... params) {
            int res = 0;

            try {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                userId = mAuth.getCurrentUser().getUid();
                //Query eventList = mDatabase.child(userId).child("taskList");//.orderByChild("date");
                        //.startAt(fDate.toString()).endAt(tDate.toString());

                //Query eventList = mDatabase.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

                DatabaseReference newref = mDatabase.child(userId).child("taskList");

                newref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e(LOG_TAG ,"taskCount: "+snapshot.getChildrenCount());
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Log.d(LOG_TAG, "add task to list");
                            //GenericTask gt = ds.getValue(GenericTask.class);
                            //Log.d(LOG_TAG, "print task: " + gt.print());
                            dbTaskList.add(ds.getValue(GenericTask.class));
                            //arrList.add(e);
                            //Event e = ds.getValue(Event.class);

                            //Log.d("async task", "11 arrList size " + arrList.size());
                        }
                        evtFetchDone = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Log.d(LOG_TAG, "Wait for taskk list data fetch");
                while ((evtFetchDone == false))
                    Thread.yield();
                Log.d(LOG_TAG, " task list fetch done");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return res;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            // TODO: Check result
            if ( result == 0 ) {
                Log.d(LOG_TAG, "total task list: " + dbTaskList.size());
                SchoolLoopHomeworkGrabber c = (SchoolLoopHomeworkGrabber) caller;
                c.onTaskListFetchDone(dbTaskList);
            }
        }
    }


    class GetEventsForRange extends AsyncTask<Void, Void, Integer> {

        LocalDate fDate, tDate;
        boolean evtFetchDone = false;
        ArrayList<Event> calEvntList;
        Object caller;

        public GetEventsForRange(LocalDate from, LocalDate to, Object _caller) {
            fDate = from;
            tDate = to;
            caller = _caller;
            calEvntList = new ArrayList<Event>();
            Log.d(LOG_TAG, "getevents for range: " + from.toString() + " to " + to.toString());
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Integer doInBackground(Void... params) {
            int res = 0;

            try {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                userId = mAuth.getCurrentUser().getUid();
                Query eventList = mDatabase.child(userId).child("eventList").orderByChild("date").
                        startAt(fDate.toString()).endAt(tDate.toString());

                //Query eventList = mDatabase.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

                eventList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //Log.d(LOG_TAG, "add activitiy to list");
                            //arrList.add(ds.getValue(GenericActivity.class));
                            //GenericActivity e = new GenericActivity();
                            calEvntList.add(ds.getValue(Event.class));
                            //arrList.add(e);
                            //Event e = ds.getValue(Event.class);
                            //Log.d(LOG_TAG, "Printing EventList for display: " + e.print());
                            //Log.d("async task", "11 arrList size " + arrList.size());
                        }
                        evtFetchDone = true;
                        Log.d(LOG_TAG, "true- evList size= "+calEvntList.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Log.d(LOG_TAG, "Wait for event list data fetch");
                while ((evtFetchDone == false))
                    Thread.yield();
                Log.d(LOG_TAG, " data fetch done");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return res;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            // TODO: Check result
            if (result == 0) {
                Log.d(LOG_TAG, "total event list: " + calEvntList.size());
                CustomCalendar c = (CustomCalendar) caller;
                c.onEventsFetchDone(calEvntList);
            }
        }
    }

    class AddTaskAndSchedule extends AsyncTask<Void, Void, Integer> {
        //String uname, password, subdomain;
        //boolean actFetchDone;
        boolean evtFetchDone;
        GenericTask taskToBeScheduled;
        Object caller;
        int nextIndex;

        public AddTaskAndSchedule(GenericTask t, Object _caller, int nextIdx) {
            //uname = username;
            //password = pswd;
            //subdomain = sub_dmn;
            //actFetchDone = false;
            evtFetchDone = false;
            taskToBeScheduled = t;
            caller = _caller;
            nextIndex = nextIdx;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Integer doInBackground(Void... params) {
            int res = 0;

            try {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                userId = mAuth.getCurrentUser().getUid();
                //DatabaseReference newref = mDatabase.child(userId).child("activityList");
                String taskStartDate, taskEndDate;
                taskStartDate = taskToBeScheduled.getStartDate().toString();
                taskEndDate = taskToBeScheduled.getDueDate().toString();

                Query eventList = mDatabase.child(userId).child("eventList").orderByChild("date").
                        startAt(taskStartDate).endAt(taskEndDate);

                //Query eventList = mDatabase.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

                eventList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //Log.d(LOG_TAG, "add activitiy to list");
                            //arrList.add(ds.getValue(GenericActivity.class));
                            //GenericActivity e = new GenericActivity();
                            evtList.add(ds.getValue(Event.class));
                            //arrList.add(e);
                            //Event e = ds.getValue(Event.class);
                            //Log.d(LOG_TAG, "Orderbykey " + e.print());
                            //Log.d("async task", "11 arrList size " + arrList.size());
                        }
                        evtFetchDone = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*
                newref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "getAllActivities123");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            //Log.d(LOG_TAG, "add activitiy to list");
                            arrList.add(ds.getValue(GenericActivity.class));
                            //GenericActivity e = new GenericActivity();
                            //e = (GenericActivity)ds.getValue(GenericActivity.class);
                            //arrList.add(e);
                            //Event e = ds.getValue(Event.class);
                            //Log.d(LOG_TAG, e.print());
                            //Log.d("async task", "11 arrList size " + arrList.size());
                        }
                        Log.d("async task", "AddTaskAndSchedule arrList size " + arrList.size());
                        actFetchDone = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
                Log.d(LOG_TAG, "Wait for data fetch");
                while ((actFetchDone == false) || (evtFetchDone == false))
                    Thread.yield();

                 */
                Log.d(LOG_TAG, "Wait for data fetch");
                while (evtFetchDone == false)
                    Thread.yield();

                Log.d(LOG_TAG, " data fetch done");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

            return res;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            /*
                1. Get all activities from activityList
                2. time needed to do the task.
                   x = (dueDate - startDate - 1).
                   if ( x == 1 ) totalTimeNeeded = 30m
                   if ( x == 2 ) totalTimeNeeded = 40m
                   if ( x == 3 ) 50m
                   if ( x == 4 ) 60m
                   perDayTaskTime = totalTimeNeeded / x
                   [[15min break Task + perDayTaskTime]]
                 from start_date to start_date+x {

                    // walk all activity and already scheduled events to figure out free slots
                    // then schedule an event in a slot and add this new event to the day.
                 }
             */

            int minTimeSlot = taskToBeScheduled.timeToFinishTheTask() / taskToBeScheduled.nrDaysToDue();
            int timeToFin = taskToBeScheduled.timeToFinishTheTask();
            int timeScheduled = 0;
            int daysToDueDate = taskToBeScheduled.nrDaysToDue();
            Log.d("post exec async task", Integer.toString(result) + " " + arrList.size() + " timeToFin " + timeToFin +
                    " evtListsize " + evtList.size());
            LocalDate curDate = taskToBeScheduled.startDate;
            Integer slotNr = 1;
            Integer totalSlotsNeeded = Math.max(taskToBeScheduled.timeToFinishTheTask() / minTimeSlot, 1);
            while (timeToFin > 0 && daysToDueDate > 0) {
                intList = new ArrayList<CalInterval>();
                if ((minTimeSlot % GenericTask.MIN_TASK_TIME) > 0) {
                    timeScheduled = ((minTimeSlot / GenericTask.MIN_TASK_TIME) + 1) * GenericTask.MIN_TASK_TIME;
                } else {
                    timeScheduled = minTimeSlot;
                }

                Log.d(LOG_TAG, "mintimeslot " + minTimeSlot + " timescheduled " + timeScheduled);
                Iterator itr = arrList.iterator();
                while (itr.hasNext()) {
                    GenericActivity a = (GenericActivity) itr.next();
                    Log.d(LOG_TAG, "ITERATOR " + a.getDesc());
                    if (a.getRepeats() == 2) { // once a week every week
                        if (curDate.getDayOfWeek() == a.startDate.getDayOfWeek()) {
                            markTimeAsBusy(a, 1);
                        }
                    }
                    if (a.getRepeats() == 3) { // once in 2 weeks
                        if ((curDate.getDayOfWeek() == a.startDate.getDayOfWeek()) &&
                                is2Weeks(curDate, a.startDate)) {
                            markTimeAsBusy(a, 1);
                        }
                    }
                    if ( a.getRepeats() == 5 ) { // every week day (school or dinner or sleep)
                        if ( curDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                                curDate.getDayOfWeek() != DayOfWeek.SUNDAY ) {
                            markTimeAsBusy(a, 1);
                        }
                    }
                    if ( a.getRepeats() == 7 ) { // every day of week. sleep, dinner
                        markTimeAsBusy(a, 1);
                    }
                }

                // Get all events that were already scheduled for this day and mark those
                // slots as busy.
                Iterator evIter = evtList.iterator();
                //curDate = taskToBeScheduled.startDate;
                while (evIter.hasNext()) {
                    Event e = (Event) evIter.next();
                    if (e.date.equals(curDate)) {
                        markTimeAsBusy(e, 2);
                    }
                }

                // sleep from 9am-9pm if no other events on that day.
                // TODO: we shouldn't run into this if any activity falls on weekend
                if (intList.size() == 0) {
                    CalInterval he1 = new CalInterval();
                    CalInterval he2 = new CalInterval();
                    he1.t1 = LocalTime.of(0, 1);
                    he1.duration = 9 * 60;
                    he2.t1 = LocalTime.of(21, 0);
                    he2.duration = 180;
                    intList.add(he1);
                    intList.add(he2);
                    addDoNotDisturbEvents(curDate);
                }

                // Sort the time interval.
                Collections.sort(intList);

                // 0830 - 420
                //           0830+420+45 != 1830
                //
                // 1830 - 60
                // 2000 - 480
                printIntList();
                // Find a slot thats free.
                int freeSlotIndex = findFreeSlot(GenericTask.MIN_BREAK_TIME + timeScheduled);
                if (freeSlotIndex != -1) {
                    CalInterval i = intList.get(freeSlotIndex);
                    // Schedule Break Event.
                    LocalTime breakAt = i.t1.plusMinutes(i.duration);
                    Log.d(LOG_TAG, "time of break " + i.t1.toString());
                    addBreakEvent(breakAt, curDate, taskToBeScheduled.taskId);
                    // Schedule Hw Event.
                    LocalTime hwAt = i.t1.plusMinutes(i.duration + GenericTask.MIN_BREAK_TIME);
                    Log.d(LOG_TAG, "time of hw " + i.t1.toString());
                    addEvent(hwAt, timeScheduled, taskToBeScheduled, curDate, slotNr, totalSlotsNeeded);
                    slotNr++;
                    timeToFin -= timeScheduled;
                    Log.d(LOG_TAG, "free slot found after: " + i.t1.toString() + " scheduled: " + timeScheduled + " need to schedule: " + timeToFin + " more mins");
                } else {
                    Log.d(LOG_TAG, "COULDN'T FIND A FREE SLOT ON: " + curDate.toString());
                }
                // Insert an Event for this day at that free slot.
                curDate = curDate.plusDays(1);
                daysToDueDate--;
                Log.d(LOG_TAG, "get all activities and events for " + curDate.toString());
            }
            if (timeToFin > 0) {
                Log.d(LOG_TAG, "COULD NOT SCHEDULE!!!");
            } else {
                Log.d(LOG_TAG, "Event scheduled successfully!!!");

                // Save the task in "taskList"
                pushHomeworkTask(taskToBeScheduled);

                SchoolLoopHomeworkGrabber c = (SchoolLoopHomeworkGrabber) caller;
                c.scheduleNextHomework(nextIndex);
            }
        }
    }


    // Use firebase keyword "push" as name of private function to be called from this class
    // instead of "save". callers of FirebaseRealtimeDatabase class may use "saveHomeworkTask"
    private int pushHomeworkTask(GenericTask t) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("taskList");
        DatabaseReference newPostRef = newref.push();
        // TODO: check for success/failure.
        newPostRef.setValue(t);
        Log.d(LOG_TAG, "task saved to firebase");
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalTime strToTime(String tmStr) {
        // tmStr may contain AM or PM
        // expected tmStr format "12:28 AM"
        int hr = Integer.parseInt(tmStr.split(":")[0]);
        int mn = Integer.parseInt(tmStr.split(" ")[0].split(":")[1]);
        if (tmStr.contains("AM") && hr == 12)
            hr -= 12;
        if (tmStr.contains("PM") && (hr >= 1 && hr <= 11))
            hr += 12;
        return LocalTime.of(hr, mn, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean dayIsWeekend(LocalDate dateToCheck) {
        if ( dateToCheck.getDayOfWeek() == DayOfWeek.SATURDAY ||
            dateToCheck.getDayOfWeek() == DayOfWeek.SUNDAY )
            return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean dayIsWeekday(LocalDate dateToCheck) {
        if ( false == dayIsWeekend(dateToCheck) )
            return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int createEventWithFreq(String eventStartTime, String eventEndTime, String desc, EventFrequency freq) {
        LocalDate today = LocalDate.now();
        LocalTime evStart = strToTime(eventStartTime);
        LocalTime evEnd = strToTime(eventEndTime);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("eventList");

        // TODO: change this to reasonable value. even though it works, 365 may be too large.
        for (int i = 1; i <= 365; i++) {
            if ( (freq == EventFrequency.EVENT_FREQUENCY_EVERYDAY) ||
                    ((freq == EventFrequency.EVENT_FREQUENCY_WEEKEND) && dayIsWeekend(today)) ||
                    ((freq == EventFrequency.EVENT_FREQUENCY_WEEKDAY) && dayIsWeekday(today)) ) {
                Duration duration = Duration.between(evStart, evEnd);
                Event e = new Event(desc, today.toString(), evStart.toString(), 1,1,
                        (int) (duration.getSeconds() / 60), 4, desc + "EVENT",
                        desc + "Hours");
                DatabaseReference newPostRef = newref.push();
                newPostRef.setValue(e);
            }
            today = today.plusDays(1);
        }
        return 0;
    }

    // TODO: The activity screen needs to be redesigned or made more simple
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int createExtraCurricularActivity(String startTime, String endTime, String startDate, String desc,
                                             int[] daysOfEvent, long recurs, String actNotes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate startDay = LocalDate.parse(startDate, formatter);
        LocalTime evStart = strToTime(startTime);
        LocalTime evEnd = strToTime(endTime);
        Duration duration = Duration.between(evStart, evEnd);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("eventList");

        if ( startDay.getDayOfWeek().getValue() != 1 ) {
            int dayAdjust = startDay.getDayOfWeek().getValue()-1;
            startDay = startDay.plusDays(-dayAdjust);
            Log.d(LOG_TAG, "Start scheduling in week starting:" + startDay.toString());
        }
        LocalDate scheduleWeek = startDay;
        // TODO: for now schedule all activities for an year.
        int nrWeeks;
        switch ((int) recurs) {
            case 0:
                nrWeeks = 1;
                break;
            case 1:
                nrWeeks = 52;
                break;
            case 2:
                nrWeeks = 26;
                break;
            case 3:
                nrWeeks = 12;
                break;
            default:
                nrWeeks = 24;
                break;
        }
        Log.d(LOG_TAG, "nrWeeks: " + nrWeeks);
        for ( int j = 1; j <= nrWeeks; j++ ) {
            startDay =  scheduleWeek;
            for (int i = 0; i <= 6; i++) {
                if ( daysOfEvent[i] == 1 ) {
                    Log.d(LOG_TAG, "Schedule " + desc + " on:" + startDay.toString());
                    Event e = new Event(desc, startDay.toString(), evStart.toString(), 1, 1,
                            (int) (duration.getSeconds() / 60), 3, String.valueOf(System.currentTimeMillis()),
                            actNotes);
                    DatabaseReference newPostRef = newref.push();
                    newPostRef.setValue(e);
                }
                startDay = startDay.plusDays(1);
            }
            switch ((int) recurs) {
                case 1:
                    scheduleWeek = scheduleWeek.plusDays(7);
                    Log.d(LOG_TAG, "scheduling for week starting: " + scheduleWeek.toString());
                    break;
                case 2:
                    scheduleWeek = scheduleWeek.plusDays(14);
                    Log.d(LOG_TAG, "scheduling for 2 weeks. starting: " + scheduleWeek.toString());
                    break;
                case 3:
                    scheduleWeek = scheduleWeek.plusMonths(1);
                    Log.d(LOG_TAG, "scheduling for a month starting: " + scheduleWeek.toString());
                default:
                    break;
            }
        }
        return 0;
    }
    //school
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int createWeekdayEvent(String eventStart, String eventEnd, String desc) {
        Log.d(LOG_TAG, "createWeekdayEvent: " + eventStart + " to:" + eventEnd);
        createEventWithFreq(eventStart, eventEnd, desc, EventFrequency.EVENT_FREQUENCY_WEEKDAY);
        return 0;
    }


    ///save dinner time inputs to firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int createEverydayEvent(String eventStart, String eventEnd, String desc) {
        Log.d(LOG_TAG, "createEverydayEvent: " + eventStart + " to:" + eventEnd);
        createEventWithFreq(eventStart, eventEnd, desc, EventFrequency.EVENT_FREQUENCY_EVERYDAY);
        return 0;
    }

    //save weekend free hours to firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int createWeekendEvent(String eventStart, String eventEnd, String desc) {
        Log.d(LOG_TAG, "createWeekendEvent: " + eventStart + " to:" + eventEnd);
        createEventWithFreq(eventStart, eventEnd, desc, EventFrequency.EVENT_FREQUENCY_WEEKEND);
        return 0;
    }

    public int saveCalendarEvent(String key, Object e) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child(key);
        DatabaseReference newPostRef = newref.push();
        // TODO: check for success/failure.
        newPostRef.setValue(e);
        return 0;
    }

    public int saveActivityEvent(String key, Object e) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("activityList").child(key);
        DatabaseReference newPostRef = newref.push();
        newPostRef.setValue(e);
        return 0;
    }

    public void getAllActivities() {
//            String key = ""
//        }
        /*
        Firebase ref = new Firebase(FIREBASE_URL);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            <YourClass> post = postSnapshot.getValue(<YourClass>.class);
                    Log.e("Get Data", post.<YourMethod>());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });

         */
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child("activityList");

        newref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "getAllActivities");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //arrList.add(ds.getValue(GenericActivity.class));
                    GenericActivity e = ds.getValue(GenericActivity.class);
                    //Log.d(LOG_TAG, "add" + e.getDesc() +" to list");
                    //arrList.add(e);
                    //Event e = ds.getValue(Event.class);
                    Log.d(LOG_TAG, e.print());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

}
//
//        public int deleteEvent () {
//            return 0;



