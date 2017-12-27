package com.example.nick.buzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HistoryActivity extends AppCompatActivity {

    ListView listview = null;
    //BroadcastReceiver getTimeStampReceiver;
    TimeStamp[] timeStamps;
    String userName = "";
    String uniqueId = "";

    class MyCustomAdapter extends ArrayAdapter<TimeStamp> {
        public MyCustomAdapter(Context context, TimeStamp[] timeStamps) {
            super(context, -1, timeStamps);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Return the view that needs to be shown on position <position>
            View view = null;
            if (convertView != null){
                view = convertView;
            }
            else {
                view = getLayoutInflater().inflate(R.layout.timestamp_list_item, parent, false);
            }
            TimeStamp timeStamp = getItem(position);
            TextView dateTv = (TextView) view.findViewById(R.id.timeStamp_date);
            TextView timeTv = (TextView) view.findViewById((R.id.timeStamp_time));
            dateTv.setText(timeStamp.getFirstName());
            timeTv.setText((timeStamp.getTimeStamp()));
            /*if(useDarkTheme)
                view.findViewById(R.id.img).setBackgroundResource(R.drawable.simple_home_controller_white);
            else
                view.findViewById(R.id.img).setBackgroundResource(R.drawable.simple_home_controller);
            */return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.small_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        /*getTimeStampReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                response = intent.getExtras().get("response");
                HandleResponse();
                setTitle("Logged in as " + userName);
            }
        }
        */

        uniqueId = getIntent().getStringExtra("UniqueId");
        //view!!
        listview = (ListView) findViewById(R.id.listview);
        //data!!
        //new HtmlPostRequest("http://michielserver.com/AP_valley/Gettimestamps.php","AAA000",HistoryActivity.this).execute();
        new HistoryActivity.RetrieveTimeStampsTask(uniqueId).execute();
        //registerReceiver(getTimeStampReceiver, new IntentFilter("responseIntent"));
        Button logoutBtn = (Button) findViewById(R.id.LogoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, LoginActivity.class));
            }
        });
    }

    private void HandleResponse(String response){
        try {
            JSONArray json = (JSONArray) new JSONTokener(response).nextValue();
            Gson gson = new Gson();
            //timeStamps = gson.fromJson(json.toString(), TimeStamp[].class);
            timeStamps = gson.fromJson(ReverseList(json).toString(), TimeStamp[].class);
            //adapter!!
            listview.setAdapter(new MyCustomAdapter(HistoryActivity.this, timeStamps));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    JSONArray ReverseList(JSONArray jsonArray) {
        JSONArray toReturn = new JSONArray();
        int length = jsonArray.length() - 1;
        for (int i = length; i >= 0; i--) {
            try {
                toReturn.put(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return toReturn;
    }
    public void onStart() {
        super.onStart();
        //registerReceiver(getTimeStampReceiver, new IntentFilter("responseIntent"));
        Log.d("History", "onStart");
    }
    public void onStop() {
        super.onStop();
        //unregisterReceiver(getTimeStampReceiver);
        Log.d("History", "onStop");
    }


    class RetrieveTimeStampsTask extends AsyncTask<Void, Void, String> {
        private String uniqueId;
        public RetrieveTimeStampsTask(String uniqueId_) {
            uniqueId = uniqueId_;
        }
        protected void onPreExecute() {

        }
        protected String doInBackground(Void... urls) {
            HttpURLConnection urlConnection = null;
            InputStream iStream = null;
            JSONObject jsonParams = new JSONObject();
            try {
                URL url = new URL("http://michielserver.com/AP_valley/Gettimestamps.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-type", "x-www-form-urlencoded");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    if (uniqueId != null) {
                        urlConnection.setRequestProperty("userid", "" + uniqueId);
                        jsonParams.put("userid", uniqueId);
                    }
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    writer.write(jsonParams.toString());
                    writer.flush();
                    writer.close();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        iStream = urlConnection.getInputStream();
                    } else {
                        iStream = urlConnection.getErrorStream();
                    }
                    BufferedReader bufferedReader;
                    bufferedReader = new BufferedReader(new InputStreamReader(iStream, "UTF-8"), 8);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            HandleResponse(response);
            userName = timeStamps[0].getFirstName();
            //setTitle("Logged in as " + userName);
        }
    }
}