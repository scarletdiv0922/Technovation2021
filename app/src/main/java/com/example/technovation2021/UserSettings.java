package com.example.technovation2021;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

@IgnoreExtraProperties
class SchoolLoopDetails implements Serializable {
    public String loginName;
    public String password;

    public SchoolLoopDetails() {
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public SchoolLoopDetails(String login, String pswd) {
        this.loginName = login;
        this.password = pswd;
    }
};

public class UserSettings extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    public SchoolLoopDetails sld;
    public String userId;
    private static final String userKey = "schoolLoopCredentials";
    public static final int minIdLen = 5;
    ProgressBar pbar;

    //Button doneBtn = findViewById(R.id.idConfigDone);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        setContentView(R.layout.activity_user_settings);
        pbar = findViewById(R.id.idConfigProgressbar);
        pbar.setVisibility(View.GONE);
        userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).child(userKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    SchoolLoopDetails s = task.getResult().getValue(SchoolLoopDetails.class);
                    //Log.d("firebaseXX", String.valueOf(task.getResult().getValue()));
                    Log.d("firebaseYY", s.loginName + " " + s.password );
                    EditText slId = findViewById(R.id.schoolloopId);
                    EditText slPswd = findViewById(R.id.schoolloopPswd);
                    slId.setText(s.loginName);
                    slPswd.setText(s.password);
                }
            }
        });
    }

    private boolean errorsInActivity() {
        EditText slId = findViewById(R.id.schoolloopId);
        if ( slId.getText().toString().length() < minIdLen ) {
            //Toast.makeText(UserSettings.this, "Login id is too short", Toast.LENGTH_SHORT).show();
            slId.setError("Login id is too short");
            return true;
        }

        EditText slPswd = findViewById(R.id.schoolloopPswd);
        if ( slPswd.getText().toString().length() < minIdLen ) {
            //Toast.makeText(UserSettings.this, "Password is too short", Toast.LENGTH_SHORT).show();
            slPswd.setError("Password is too short");
            return true;
        }
        return false;
    }

    public void btnConfigDoneClicked(View view) {
        //Toast.makeText(UserSettings.this, "done", Toast.LENGTH_SHORT).show();
        //if ( errorsInActivity() )
          //  return;

        pbar.setVisibility(View.VISIBLE);

        EditText slId = findViewById(R.id.schoolloopId);
        EditText slPswd = findViewById(R.id.schoolloopPswd);

        sld = new SchoolLoopDetails(slId.getText().toString(), slPswd.getText().toString());
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        // https://missiontech2021-default-rtdb.firebaseio.com/
        //Toast.makeText(this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
        //Log.d("user settings", currUser);

        // TODO: Save only if login name or password changed.
        mDatabase.child(userId).child(userKey).setValue(sld);
        //mDatabase.child(currUser).setValue("test");

        pbar.setVisibility(View.GONE);
        this.finish();
    }

    public void cancelConfigTouched(View view) {
        this.finish();
    }
}