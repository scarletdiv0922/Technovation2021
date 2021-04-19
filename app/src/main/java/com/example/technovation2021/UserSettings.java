//School loop Login and PW
package com.example.technovation2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Helper;

@IgnoreExtraProperties
class SchoolLoopDetails implements Serializable {
    public String loginName;
    public String password;
    public String schoolloopUrl;

    public SchoolLoopDetails() {
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public SchoolLoopDetails(String login, String pswd, String url) {
        this.loginName = login;
        this.password = pswd;
        this.schoolloopUrl = url;
    }
};

public class UserSettings extends AppCompatActivity {
    private static final String LOG_TAG = "User Settings";
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    public SchoolLoopDetails sld;
    public String userId;
    private static final String userKey = "schoolLoopCredentials";
    public static final int minIdLen = 5;
    ProgressBar pbar;

    @RequiresApi(api = Build.VERSION_CODES.O)
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


        SharedPreferences sharedPref = getSharedPreferences(
                this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        String sluser2 = sharedPref.getString("sluser", "invalid");
        String slpswd2 = sharedPref.getString("slpswd", "");
        String subdomain2 = sharedPref.getString("slsubdomain", "");

        EditText slId = findViewById(R.id.schoolloopId);
        EditText slPswd = findViewById(R.id.schoolloopPswd);
        EditText slDmn = findViewById(R.id.Subdomain);

        // user has already given their schoolloop id/password. show them.
        if ( false == sluser2.equals("invalid") ) {
            slId.setText(sluser2);
            slPswd.setText(slpswd2);
            slDmn.setText(subdomain2);
        }
    }

    private boolean errorsInActivity() {
        EditText slId = findViewById(R.id.schoolloopId);
        if ( slId.getText().toString().length() < minIdLen ) {
            Toast.makeText(UserSettings.this, "Login id is too short", Toast.LENGTH_SHORT).show();
            slId.setError("Login id is too short");
            return true;
        }

        EditText slPswd = findViewById(R.id.schoolloopPswd);
        if ( slPswd.getText().toString().length() < minIdLen ) {
            Toast.makeText(UserSettings.this, "Password is too short", Toast.LENGTH_SHORT).show();
            slPswd.setError("Password is too short");
            return true;
        }

        EditText sl_subdomain = findViewById(R.id.Subdomain);
        String subdmn = sl_subdomain.getText().toString().trim();
        if ( subdmn.isEmpty() || subdmn.length() < 5 ) {
            sl_subdomain.setError("Schoolloop suddomain is not valid. Please check.");
            Toast.makeText(UserSettings.this, "URL is too short.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void btnSaveSchoolLoopDetails(View view) {
        //Toast.makeText(UserSettings.this, "done", Toast.LENGTH_SHORT).show();
        if ( errorsInActivity() )
            return;

        pbar.setVisibility(View.VISIBLE);

        EditText slId = findViewById(R.id.schoolloopId);
        EditText slPswd = findViewById(R.id.schoolloopPswd);
        EditText slDmn = findViewById(R.id.Subdomain);

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sluser", slId.getText().toString());
        editor.putString("slpswd", slPswd.getText().toString());
        editor.putString("slsubdomain", slDmn.getText().toString());
        editor.commit();

        SchoolLoopHomeworkGrabber slg = new SchoolLoopHomeworkGrabber(slId.getText().toString(),
                                            slPswd.getText().toString(),
                                            slDmn.getText().toString());

        Timer timer = new Timer();
        TimerTask task = new MyTimerTask(this);

        timer.schedule(task, 3000);
        Log.d(LOG_TAG, "Wait for timer to finish");
        pbar.setVisibility(View.GONE);
        // TODO: make this activity wait till the homework is fetched.
        Toast.makeText(UserSettings.this, "Pulling homework from School Loop...", Toast.LENGTH_LONG).show();
        //this.finish();
    }

    class MyTimerTask extends TimerTask {
        AppCompatActivity context;
        public MyTimerTask(AppCompatActivity c) {
            context = c;
        }
        @Override
        public void run() {
            //Log.d(LOG_TAG, "inside timer handler");
            //context.pbar.setVisibility(View.GONE);
            context.finish();
        }
    }
    public void cancelConfigTouched(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
