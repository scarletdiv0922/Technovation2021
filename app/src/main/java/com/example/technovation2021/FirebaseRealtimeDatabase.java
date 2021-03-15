package com.example.technovation2021;

import android.nfc.Tag;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseRealtimeDatabase {
    private static final String LOG_TAG = FirebaseRealtimeDatabase.class.getSimpleName();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    public FirebaseRealtimeDatabase() {
        mAuth = FirebaseAuth.getInstance();
    }

    public int saveCalendarEvent(String key, Event e) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        DatabaseReference newref = mDatabase.child(userId).child(key);
        DatabaseReference newPostRef = newref.push();
        // TODO: check for success/failure.
        newPostRef.setValue(e);
        return 0;
    }

    public void getAllActivities(String key) {
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
        DatabaseReference newref = mDatabase.child(userId).child(key);
        newref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "getAllActivities");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Event e = ds.getValue(Event.class);
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

    public int deleteEvent() {
        return 0;
    }
}
