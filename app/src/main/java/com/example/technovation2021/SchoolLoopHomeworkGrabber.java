package com.example.technovation2021;

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
    private Handler mHandler = new Handler();
    private static String LOG_TAG = "SchoolLoopHomeworkGrabber";
    String userName;
    String userPassword;
    String slDomain;
    SlThread slt;
    SchoolLoopConnector slc;
    ArrayList<HomeworkFromSchoolloop> unscheduledHwList;
    ArrayList<GenericTask> masterTaskList;

    class SlThread implements Runnable {

        /*
        String user, pswd, dmn;
        public SlThread(String u, String p, String d) {
            user = u;
            pswd = p;
            dmn = d;
        }

         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();
            Log.d(LOG_TAG, "now " + now.toString());
            getSchoolLoopHomework();
            /*
            if ( today.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    today.getDayOfWeek() != DayOfWeek.SATURDAY ) {
                if (LocalTime.parse("08:00:00").isBefore(now) && now.isBefore(LocalTime.parse("15:00:00"))) {
                    Log.d(LOG_TAG, "call getschoolloophomework");
                    getSchoolLoopHomework();
                }
            }
            mHandler.postDelayed( this, 60*1000);

             */
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getSchoolLoopHomework() {
        Log.d(LOG_TAG, "get school loop homework");
        slc = new SchoolLoopConnector(userName, userPassword, slDomain, this);
        slc.execute();
    }

    public boolean hwTaskAlreadyScheduled(HomeworkFromSchoolloop h, ArrayList<GenericTask> taskList) {
        for ( int i = 0; i < taskList.size(); i++ ) {
            if ( h.hash.equals(taskList.get(i).hash)) {
                Log.d(LOG_TAG, "hw " + h.sub +  " desc " + h.hw_desc + " is already scheduled");
                return true;
            }
        }
        return false;
    }

    public void scheduleNextHomework(int index) {
        FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
        for ( int i = index; i < unscheduledHwList.size(); i++ ) {
            Log.d(LOG_TAG, "Trying to schedule homework at index: " + index);
            HomeworkFromSchoolloop h = unscheduledHwList.get(i);
            if ( hwTaskAlreadyScheduled(h, masterTaskList) == false ) {
                /*
                    public GenericTask(String taskId,
                       String startDate,
                       String dueDate,
                       String desc,
                       String notes,
                       String hash,
                       Integer status,
                       Integer timeNeeded // in minutes
                 */
                Log.d(LOG_TAG, "hw at index: " + index + " hw:" + h.sub);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                GenericTask newtask = new GenericTask(LocalTime.now().toString(),
                        LocalDate.now().toString(),
                        LocalDate.parse(h.duedate, formatter).toString(),
                        h.sub, h.hw_desc, h.hash, 2, 120);
                // Split the task into events, save them to firebase. then save task itself
                // to firebase. when its done, callback scheduleNextHomework with index+1
                frd.saveHwTask(newtask, this, index+1);
                return;
            }
            //Log.d(LOG_TAG, "desc " + h.sub + " desc:" + h.hw_desc + " is already scheduled");
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
        //mHandler.removeCallbacks(slThread);
        slt = new SlThread();//slUser, slPswd, subDomain);
        slt.run();
    }

    public void stopTimer() {
        Log.d(LOG_TAG, "cancel callbacks");
        mHandler.removeCallbacks(slt);
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

            Log.d("async task", result);
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
                    Log.d("post Exec", sub + " " + hw_desc + " " + duedate + "hash=>" + hash);
                    unscheduledHwList.add(new HomeworkFromSchoolloop(sub, hw_desc, duedate, hash));

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
                /* Now start the task of getting "master" taskList from firebase */
                FirebaseRealtimeDatabase frb = new FirebaseRealtimeDatabase();
                // TODO: from and to dates below show be based on the home work posted date and due dates
                frb.getTaskList(LocalDate.now().plusDays(-10), LocalDate.now().plusDays(10), slHwGrabberObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
