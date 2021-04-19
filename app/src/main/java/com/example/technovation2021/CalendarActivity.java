//Month Calendar
package com.example.technovation2021;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import androidx.core.app.NotificationCompat;

import static java.time.Duration.between;
import static java.time.temporal.ChronoUnit.MINUTES;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarActivity extends AppCompatActivity {
    private static Handler mHandler = new Handler();
    ArrayList<Event> evList = new ArrayList<Event>();
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    boolean eventsFetched = false;
    CustomCalendar cv;
    String currentDate= LocalDate.now().toString();

    private static final String LOG_TAG = CalendarActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    class SlThread implements Runnable {
        public LocalTime hwFetchedAt = LocalTime.now();

        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean over2Hours() {
            if ( MINUTES.between(hwFetchedAt, LocalTime.now()) >= 120 )
                return true;
            return false;
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
                if (LocalTime.parse("06:00:00").isBefore(now) && now.isBefore(LocalTime.parse("21:00:00"))) {
                    Log.d(LOG_TAG, "call getschoolloophomework");
                    getSchoolLoopHomework();
                    hwFetchedAt = LocalTime.now();
                }
            }

            scheduleNotifications();

            // Every 5 mins.
            mHandler.postDelayed( this, 5*60*1000);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getEventTimeFromNow(Event e) {
        long x = MINUTES.between(LocalTime.now(), e.startTime);
        Log.d(LOG_TAG, "event: " + e.eventDesc + " starts in:" + x + " minutes from now");
        if ( x >= 0 ) {
            return x * 60 * 1000;
        }
        return -1;
    }

    private void scheduleNotificationForEvent(Event e, long delay) {
        Intent notifIntent = new Intent (CalendarActivity.this, EventNotificationBroadcast.class);
        notifIntent.putExtra("EventDesc", e.eventDesc);
        PendingIntent notifPendingIntent = PendingIntent.getBroadcast(CalendarActivity.this, 0, notifIntent, 0);

        Log.d(LOG_TAG, "schedule notification for " + e.eventDesc);
        AlarmManager notifAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        notifAlarmManager.set(AlarmManager.RTC_WAKEUP,
                currentTime+delay,
                notifPendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void scheduleNotifications() {
        Log.d(LOG_TAG, "Scheduling notifications");

        //Query (find) all events for today
        FirebaseAuth mAuthEv= FirebaseAuth.getInstance();
        try {
            DatabaseReference mDatabaseEv = FirebaseDatabase.getInstance().getReference();
            String userId = mAuthEv.getCurrentUser().getUid();
            Query eventList1 = mDatabaseEv.child(userId).child("eventList").orderByChild("date").
                    startAt(LocalDate.now().toString()).endAt(LocalDate.now().toString());

            eventList1.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 1;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Event e = ds.getValue(Event.class);
                        Log.d(LOG_TAG, "got event:" + e.eventDesc + " type:" + e.type + " start:" + e.date.toString());
                            if ( (e.type == 2 || e.type == 3)) {
                                long delay = getEventTimeFromNow(e);
                                Log.d(LOG_TAG, "delay: " + delay);
                                if ( delay >= 0 && delay < 300000 ) {
                                    scheduleNotificationForEvent(e, delay);
                                    i++;
                                }
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getSchoolLoopHomework() {
        Log.d(LOG_TAG, "get school loop homework");
        SharedPreferences sharedPref = getSharedPreferences(
                this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        String sluser = sharedPref.getString("sluser", "invalid");
        String slpswd = sharedPref.getString("slpswd", "invalid");
        String subdomain = sharedPref.getString("slsubdomain", "invalid");
        if (sluser.equals("invalid") == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new SchoolLoopHomeworkGrabber(sluser, slpswd, subdomain);
            }
        }
        cv.updateCalendar(LocalDate.now());
    }

    @Override
    public void onBackPressed() {
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

        cv.updateCalendar(LocalDate.now());
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

            case R.id.aboutPage:
                Intent intent5 = new Intent( CalendarActivity.this, About.class);
                startActivity(intent5);
                return true;

            case R.id.privacyScreen:
                Intent intent6 = new Intent(CalendarActivity.this, Privacy.class);
                startActivity(intent6);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(LOG_TAG, "back from some activity: refresh calendar");
        cv.updateCalendar(LocalDate.now());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "back from some activity: refresh calendar onResume");
        cv.updateCalendar(LocalDate.now());
    }
}