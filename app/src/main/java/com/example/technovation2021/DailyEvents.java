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
    ArrayList<Event> filteredList;
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
        Log.d("event date test", "showEventsForDay= " + showEventsForDay);
        setTitle("Events for " + showEventsForDay);
        fromDate = LocalDate.parse(showEventsForDay);
        mAuth = FirebaseAuth.getInstance();
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
            endDay = dateToFetch.plusDays(0);
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

                filteredList = new ArrayList<Event>();
                for (int i =0;i < evList.size(); i++) {
                    Log.d(LOG_TAG, "ev :" + i + " desc:" + evList.get(i).eventDesc + " start:" + evList.get(i).startTime + " day:" + evList.get(i).date.toString());
                    if ( evList.get(i).type != 4 ) {
                        Log.d(LOG_TAG, "add " + evList.get(i).eventDesc + " to display list");
                        filteredList.add(evList.get(i));
                    }
                }
                DailyEventsAdapter dea = new DailyEventsAdapter(DailyEvents.this, dateToFetch, filteredList );
                recyclerView.setAdapter(dea);
            }
        }
    }

}