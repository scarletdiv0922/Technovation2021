//Month Calendar
package com.example.technovation2021;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static java.time.temporal.ChronoUnit.MINUTES;

public class CalendarActivity extends AppCompatActivity {
    private static Handler mHandler = new Handler();
    ArrayList<Event> evList = new ArrayList<Event>();
    boolean eventsFetched = false;
    CustomCalendar cv;
    String currentDate= LocalDate.now().toString();

    private static final String LOG_TAG = CalendarActivity.class.getSimpleName();

    class SlThread implements Runnable {
        public LocalTime hwFetchedAt = LocalTime.now();

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean over2Hours() {
            if ( MINUTES.between(hwFetchedAt, LocalTime.now()) >= 120 )
                return true;
            return false;
        }

        public void fetchTodaysEvents() {

        }

        public void playAudioBeforeEvent() {

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();
            Log.d(LOG_TAG, "now " + now.toString());

            // Fetch homework every 2 hours between 8am - 6pm on weekdays
            if ( today.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    today.getDayOfWeek() != DayOfWeek.SATURDAY && over2Hours()) {
                if (LocalTime.parse("08:00:00").isBefore(now) && now.isBefore(LocalTime.parse("18:00:00"))) {
                    Log.d(LOG_TAG, "call getschoolloophomework");
                    getSchoolLoopHomework();
                    hwFetchedAt = LocalTime.now();
                    fetchTodaysEvents();
                }
            }

            playAudioBeforeEvent();

            mHandler.postDelayed( this, 5*60*1000);
            /*
            getSchoolLoopHomework();
            mHandler.postDelayed( this, 30*1000);
             */
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getSchoolLoopHomework() {
        Log.d(LOG_TAG, "get school loop homework");
        SharedPreferences sharedPref = getSharedPreferences(
                this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        String sluser = sharedPref.getString("sluser", "invalid");
        String slpswd = sharedPref.getString("slpswd", "invalid");
        String subdomain = sharedPref.getString("slsubdomain", "hjh-fusd-ca");
        if (sluser.equals("invalid") == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new SchoolLoopHomeworkGrabber(sluser, slpswd, subdomain);
            }
        }
        cv.updateCalendar(LocalDate.now());
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(CalendarActivity.this,"Long press on a day to open its events. ",Toast.LENGTH_LONG).show();
        return;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        cv = findViewById(R.id.custom_calendar);

        new SlThread().run();

        Intent notifIntent = new Intent (CalendarActivity.this, EventNotificationBroadcast.class);
        PendingIntent notifPendingIntent = PendingIntent.getBroadcast(CalendarActivity.this, 0, notifIntent, 0);

        AlarmManager notifAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTime = System.currentTimeMillis();
        long fiveSeconds = 1000*5;

        notifAlarmManager.set(AlarmManager.RTC_WAKEUP,
                currentTime+fiveSeconds,
                notifPendingIntent);

        //cv.setContext(CalendarActivity.this);
        Log.d("CalendarXX", "call custom calendar");
        /* Below handler works but may not be needed.
        cv.setEventHandler(new CustomCalendar.EventHandler()
        {
            @Override
            public void onDayLongPress(LocalDate date)
            {
                // Expand events of day.
                Log.d(LOG_TAG, "show all events for:" + date.toString());
            }
        });
         */
        cv.updateCalendar(LocalDate.now());


        //Query (find) all events for today
        FirebaseAuth mAuthEv= FirebaseAuth.getInstance();
        Log.d(LOG_TAG, " 1 before try");

        try {
            DatabaseReference mDatabaseEv = FirebaseDatabase.getInstance().getReference();
            String userId = mAuthEv.getCurrentUser().getUid();
            Log.d(LOG_TAG, " 2 after getting user id");

            Log.d(LOG_TAG, "userId= "+userId);


           /* Query eventList = mDatabaseEv.child(userId).child("eventList").orderByChild("date").
                    startAt(currentDate).endAt("2021-04-10");
           */

            Query eventList1 = mDatabaseEv.child(userId).child("eventList").orderByChild("date").
                    startAt("2021-04-08").endAt("2021-04-10");

            Log.d(LOG_TAG, " 3 after query");

            //Query eventList1 = mDatabaseEv.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

            eventList1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        evList.add(ds.getValue(Event.class));
                        Log.d("onDataChange", "evList size= "+ evList.size());
                    }
                    eventsFetched = true;
                    Log.d(LOG_TAG, "I fetched events");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Log.d(LOG_TAG, " data fetch done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        //Loop through the events for the day and set a notification for 10 minutes before the notification start time
        //Log.d("I am here and this is the evList size", "evList= " + evList.size());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cal_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.settingMenu:
                //Toast.makeText(CalendarActivity.this, "Go to settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CalendarActivity.this, UserSettings.class);
                startActivity(intent);
                return true;

            case R.id.studentActivity:
                Intent intent2 = new Intent( CalendarActivity.this, StudentActivity.class);
                startActivity(intent2);
                return true;

            case R.id.addTask:
                Intent intent3 = new Intent( CalendarActivity.this, AddTask.class);
                startActivity(intent3);
                return true;

            /*case R.id.notifications:
                Intent intent4 = new Intent( CalendarActivity.this, StudentNotification.class);
                startActivity(intent4);
                return true;
*/
            case R.id.aboutPage:
                Intent intent5 = new Intent( CalendarActivity.this, About.class);
                startActivity(intent5);
                return true;

            case R.id.privacyScreen:
                //Toast.makeText(CalendarActivity.this, "Go to settings", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(CalendarActivity.this, Privacy.class);
                startActivity(intent6);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}

/*
11:59
12:00am
00:00am
00:01am
00:59
01:00
11:59am
12:00pm
12:01pm
12:59
01:00
11:59pm
 */