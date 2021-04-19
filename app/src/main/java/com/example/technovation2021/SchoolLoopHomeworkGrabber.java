package com.example.technovation2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;

class HomeworkFromSchoolloop {
    String sub;
    String hw_desc;
    String duedate;
    String hash;

    public HomeworkFromSchoolloop(String subj, String hwdesc, String ddate, String hashval) {
        sub = subj;
        hw_desc = hwdesc;
        duedate = ddate;
        hash = hashval;
    }
}

public class SchoolLoopHomeworkGrabber {
    private static Handler mHandler = new Handler();
    private static String LOG_TAG = "SchoolLoopHomeworkGrabber";
    String userName;
    String userPassword;
    String slDomain;
    SchoolLoopConnector slc = null;
    ArrayList<HomeworkFromSchoolloop> unscheduledHwList;
    ArrayList<GenericTask> masterTaskList;

    public boolean hwTaskAlreadyScheduled(HomeworkFromSchoolloop h, ArrayList<GenericTask> taskList) {
        for ( int i = 0; i < taskList.size(); i++ ) {
            if ( h.hash.equals(taskList.get(i).hash)) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void scheduleNextHomework(int index) {
        FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
        for ( int i = index; i < unscheduledHwList.size(); i++ ) {
            HomeworkFromSchoolloop h = unscheduledHwList.get(i);
            if ( hwTaskAlreadyScheduled(h, masterTaskList) == false ) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate dueDate = LocalDate.parse(h.duedate, formatter);
                GenericTask newtask = new GenericTask(String.valueOf(System.currentTimeMillis()),
                        LocalDate.now().toString(),
                        LocalDate.parse(h.duedate, formatter).toString(),
                        h.sub, h.hw_desc, h.hash, 2, 120);
                // If dueDate is already today, not much to schedule
                if ( LocalDate.now().isBefore(dueDate) == true ) {
                    // Split the task into events, save them to firebase. then save task itself
                    // to firebase. when its done, callback scheduleNextHomework with index+1
                    frd.saveHwTask(newtask, this, i + 1);
                    return;
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    // This is the callback from FirebaseRealtimeDatabase.getTaskList()
    public void onTaskListFetchDone(ArrayList<GenericTask> tskList) {
        masterTaskList = new ArrayList<GenericTask>();
        masterTaskList = tskList;
        Log.d(LOG_TAG, "master taskList count: " + tskList.size());
        scheduleNextHomework(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SchoolLoopHomeworkGrabber(String slUser, String slPswd, String subDomain) {
        userName = slUser;
        userPassword = slPswd;
        slDomain = subDomain;
        slc = new SchoolLoopConnector(userName, userPassword, slDomain, this);
        slc.execute();
    }

    public void stopTimer() {
        Log.d(LOG_TAG, "cancel callbacks");
        //mHandler.removeCallbacks(slt);
        if (slc != null)
            slc.cancel(true);
    }


    class SchoolLoopConnector extends AsyncTask<Void, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        String uname, password, subdomain;
        Object slHwGrabberObj;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public SchoolLoopConnector(String username, String pswd, String sub_dmn, Object caller) {
            uname = username;
            password = pswd;
            subdomain = sub_dmn;
            slHwGrabberObj = caller;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(LOG_TAG, "SchoolLoopConnect async task is stopped");
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
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;

                URL url = new URL("https://easternwinds.biz/cgi-bin/school.py?");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                Log.d("async task", "before url conn2");

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
                if ( isCancelled() )
                    res = "";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return res;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ( isCancelled() ) {
                Log.d(LOG_TAG, "school loop homework fetcher task is cancelled");
                return;
            }

            try {

                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("hwlist");

                if ( array.length()  > 0)
                    unscheduledHwList = new ArrayList<HomeworkFromSchoolloop>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String sub = jsonObject.getString("subject");
                    String hw_desc = jsonObject.getString("hw_desc");
                    String duedate = jsonObject.getString("duedate");
                    String hash = jsonObject.getString("hash");
                    unscheduledHwList.add(new HomeworkFromSchoolloop(sub, hw_desc, duedate, hash));
                }
                /* Now start the task of getting "master" taskList from firebase */
                FirebaseRealtimeDatabase frb = new FirebaseRealtimeDatabase();
                frb.getTaskList(LocalDate.now().plusDays(-10), LocalDate.now().plusDays(10), slHwGrabberObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
