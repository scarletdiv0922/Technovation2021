package com.example.technovation2021;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.Collections;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Integer.max;

public class DailyEvents extends AppCompatActivity {

    ArrayList<Event> evList;
    private static final String LOG_TAG = DailyEvents.class.getSimpleName();

    RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    LocalDate fromDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_events);



        evList = new ArrayList<Event>();
        String showEventsForDay = getIntent().getStringExtra("EventsForDay");
        setTitle("Events for " + showEventsForDay);
        fromDate = LocalDate.parse(showEventsForDay);
        mAuth = FirebaseAuth.getInstance();
        //evList = (ArrayList<Event>) getIntent().getSerializableExtra("EventList");

        //Bundle args = getIntent().getBundleExtra("BUNDLE");
        //evList = (ArrayList<Event>) args.getSerializable("EventList");

        //evList=this.getIntent().getExtras().getParcelableArrayList("EventList");



        Log.d(LOG_TAG, "show events:" + evList.size());

        recyclerView = findViewById(R.id.idShowDailyEvents);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        new GetEventsForDay(fromDate).execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnPreviousDayClicked(View view) {
        fromDate = fromDate.plusDays(-1);
        setTitle("Events on " + fromDate.toString());
        Log.d(LOG_TAG, "show events for previous day:" + fromDate.toString());
        new GetEventsForDay(fromDate).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnNextDayClicked(View view) {
        fromDate = fromDate.plusDays(1);
        Log.d(LOG_TAG, "show events for next day:" + fromDate.toString());
        setTitle("Events on " + fromDate.toString());
        new GetEventsForDay(fromDate).execute();
    }

    class GetEventsForDay extends AsyncTask<Void, Void, Integer> {
        boolean eventsFetched = false;
        LocalDate dateToFetch;
        LocalDate endDay;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public GetEventsForDay(LocalDate date) {
            dateToFetch = date;
            endDay = dateToFetch.plusDays(1);
            evList = new ArrayList<Event>();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Integer doInBackground(Void... params) {
            int res = 0;

            try {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                userId = mAuth.getCurrentUser().getUid();

                Log.d(LOG_TAG, "DailyEvents..fetch from:" + dateToFetch.toString() + " to:" + endDay.toString());
                Query eventList = mDatabase.child(userId).child("eventList").orderByChild("date").
                        startAt(dateToFetch.toString()).endAt(endDay.toString());

                //Query eventList = mDatabase.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

                eventList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            evList.add(ds.getValue(Event.class));
                        }
                        eventsFetched = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Log.d(LOG_TAG, "Wait for events data fetch");
                while ((eventsFetched == false))
                    Thread.yield();
                Log.d(LOG_TAG, " data fetch done");
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
            if ( result == 0 ) {
                /* show events fetched */
                Log.d(LOG_TAG, "got " + evList.size());

                DailyEventsAdapter dea = new DailyEventsAdapter(DailyEvents.this, evList );
                recyclerView.setAdapter(dea);
            }
        }
    }

}