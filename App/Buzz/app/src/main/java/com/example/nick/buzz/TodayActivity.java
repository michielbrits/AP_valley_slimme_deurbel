package com.example.nick.buzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class TodayActivity extends AppCompatActivity {

    ListView listview = null;
    BroadcastReceiver getTimeStampReceiver;
    TimeStamp[] timeStamps;
    Object response = null;
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

        getTimeStampReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                response = intent.getExtras().get("response");
                HandleResponse();
                setTitle("Logged in as " + userName);
            }
        };

        uniqueId = getIntent().getStringExtra("UniqueId");
        //view!!
        listview = (ListView) findViewById(R.id.listview);
        //data!!
        new HtmlPostRequest("http://michielserver.com/AP_valley/Gettimestamps24.php", uniqueId,TodayActivity.this).execute();
        registerReceiver(getTimeStampReceiver, new IntentFilter("responseIntent"));
        Button logoutBtn = (Button) findViewById(R.id.LogoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodayActivity.this, LoginActivity.class));
            }
        });
    }

    private void HandleResponse(){
        try {
            JSONArray json = (JSONArray) new JSONTokener(response.toString()).nextValue();
            Gson gson = new Gson();
            //timeStamps = gson.fromJson(json.toString(), TimeStamp[].class);
            timeStamps = gson.fromJson(ReverseList(json).toString(), TimeStamp[].class);
            userName = timeStamps[0].getFirstName();
            //adapter!!
            listview.setAdapter(new MyCustomAdapter(TodayActivity.this, timeStamps));
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
        registerReceiver(getTimeStampReceiver, new IntentFilter("responseIntent"));
        Log.d("Today", "onStart");
    }
    public void onStop() {
        super.onStop();
        unregisterReceiver(getTimeStampReceiver);
        Log.d("Today", "onStop");
    }
}