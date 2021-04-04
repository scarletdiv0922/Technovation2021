//First Screen: Welcome and Login/Sign Up
package com.example.technovation2021;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare objects
    Button loginButton;
    Button signUpButton;
    ProgressBar pbar;
    //ArrayList<itemModel> arrayList;
    // References (firebase signin): https://www.youtube.com/watch?v=TwHmrZxiPA8

    // TODO: Add progress bar icon
    // TODO: Change "Welcome!" string font

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // TODO: make validating email and password a method by itself
    // TODO: Make custom Toast() message using BuildConfig.DEBUG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize objects with elements from xml files
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.sign_up_button);
        pbar = findViewById(R.id.progressBar);

        pbar.setVisibility(View.GONE);
        if ( BuildConfig.DEBUG ) {
            EditText tv = findViewById(R.id.userEmail);
            EditText pswd = findViewById(R.id.userPassword);

            tv.setText("test123@test.com");
            pswd.setText("test123");
        }

        // When the Login and Sign Up buttons are clicked, take the user to CalendarActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.userEmail);
                EditText pswd = findViewById(R.id.userPassword);

                // Do sanity checks to make sure data is good!
                if (TextUtils.isEmpty(email.getText().toString()) ) {
                    //Toast.makeText(MainActivity.this, "Email cannot be empty.",
                            //Toast.LENGTH_SHORT).show();
                    email.setError("Email cannot be empty.");
                    return;
                }
                if ( !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    // TODO: Change these toast message to field.setError()
                    Toast.makeText(MainActivity.this, "Email address is not valid.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( TextUtils.isEmpty(pswd.getText().toString()) ) {
                    Toast.makeText(MainActivity.this, "Password is empty.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Make password length a constant variable
                if ( pswd.length() < 5 ) {
                    Toast.makeText(MainActivity.this, "Password is too short.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                pbar.setVisibility(View.VISIBLE);
                // TODO: disable login button if a login is already in progress? first login is taking quite a bit of time.
                // TODO: check on firebase authentication private key configuring in app.
                mAuth.signInWithEmailAndPassword(email.getText().toString(), pswd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                            startActivity(intent);
                        }
                        else {
                            //Toast.makeText(MainActivity.this, "User has not signed up.", Toast.LENGTH_SHORT).show();
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_USER_NOT_FOUND":
                                    //email.setError("User not registered!");
                                    Toast.makeText(MainActivity.this, "User is not registered.", Toast.LENGTH_LONG).show();
                                    break;
                                case "ERROR_WRONG_PASSWORD":
                                    Toast.makeText(MainActivity.this, "Incorrect password.", Toast.LENGTH_LONG).show();
                                    break;
                            }
                            pbar.setVisibility(View.GONE);
                        }
                        //TODO: Figure out if pw is wrong or no account is made
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    class HTTPReqTask extends AsyncTask<Void, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        String uname, password, subdomain;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public HTTPReqTask(String username, String pswd, String sub_dmn) {
            uname = username;
            password = pswd;
            subdomain = sub_dmn;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String res = "";

            try {
                String urlParameters;
                // TODO: escape & in the username or password
                urlParameters="user="+uname+"&pswd="+password+"&subdomain="+subdomain;
                        //"user=prasadk102&pswd=l3Cache9$";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;

                URL url = new URL("https://easternwinds.biz/cgi-bin/school.py?");
                urlConnection = (HttpURLConnection) url.openConnection();
            /*
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
             */

                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                Log.d("async task", "before url conn2");
            /*
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    out, "UTF-8"));
            writer.write(postData.toString());
            writer.flush();

             */


                try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write(postData);
                }


                int code = urlConnection.getResponseCode();
                if (code != 200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.d("http resp", line);
                    if (!line.startsWith("<")) {
                        res += line;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("async task", result);
            try {

                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("hwlist");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject jsonObject = array.getJSONObject(i);
                    String sub = jsonObject.getString("subject");
                    String hw_desc = jsonObject.getString("hw_desc");
                    String duedate = jsonObject.getString("duedate");
                    String hash = jsonObject.getString("hash");
                    Log.d("post Exec", sub + " " + hw_desc + " " + duedate + "hash=>" + hash);

                /*
                itemModel model = new itemModel();
                model.setId(id);
                model.setName(first_name + " " + last_name);
                model.setEmail(email);
                arrayList.add(model);

                2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: US History 8 Expansion notes 03/31/2021hash=>252b2c7054b80c118a4ca91c2918b8c6
2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: US History 8 Edpuzzle - The Mexican-American War 03/31/2021hash=>e19cb8308fb3997ccb6882aa1182d53e
2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: US History 8 Compromise of 1850 vocab 03/31/2021hash=>5a3a7f858d0399c9cee8012e17c359c9
2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: Common Core Math 3 & Algebra 1 Accel Wednesday 3/31 submissions #88 #90 #91 Due 11:59 PM 04/01/2021hash=>0f1090044d20c3d52c3113149f06a745
2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: Common Core Math 3 & Algebra 1 Accel #95 Presentation relationships of 2 parallel lines and a transversal line This
    was taught last year and is Review. Take any notes that YOU need to. Complete
    slide 20 "Find all the remaining angles" as a self check to make sure you
    understand the relationships. Then complete the 7 problems attached. 04/01/2021hash=>52625e5b99d142d2e3baf36c80d25ccb
2021-03-31 00:07:33.950 30671-30671/com.example.technovation2021 D/post Exec: Common Core Math 3 & Algebra 1 Accel #96 Practice Test Course 3 Unit 3 Link attached 04/02/2021hash=>cc42e5a1541eaaa170fabf557d585430
2021-03-31 00:07:33.951 30671-30671/com.example.technovation2021 D/post Exec: Common Core Math 3 & Algebra 1 Accel Unit 3 Test Course 3 04/12/2021hash=>bac10d64e0ac29bdd9d697dc501eea93

                 */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void forgotPasswordClicked(View view) {
            //Intent intent = new Intent(MainActivity.this, UserInfo.class);
            //startActivity(intent);

            /*
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.getAllActivities("activityList");
             */

            /*
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            //SharedPreferences.Editor editor = sharedPref.edit();
            String sluser = sharedPref.getString("sluser", "invalid");
            String slpswd = sharedPref.getString("slpswd", "invalid");
            String subdomain = sharedPref.getString("slsubdomain", "hjh-fusd-ca");
            //Log.d(LOG_TAG, sluser+slpswd+subdomain);
            new HTTPReqTask(sluser, slpswd, subdomain).execute();
             */
        }
}