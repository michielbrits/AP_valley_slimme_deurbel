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
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends AppCompatActivity {
    String uniqueId = "";
    BroadcastReceiver getLastTimeStampReceiver;
    int lastTimeStampId = 0;
    TimeStamp[] timeStamps;
    Object response = null;
    Timer timer;
    Boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main", "onCreate");
        setContentView(R.layout.activity_main);
        getLastTimeStampReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                response = intent.getExtras().get("response");
                HandleResponse();
                TimeStamp lastTimeStamp = timeStamps[timeStamps.length - 1];
                if (lastTimeStampId == 0)
                    lastTimeStampId = lastTimeStamp.getId();
                if(lastTimeStampId != 0 && lastTimeStamp.getId() != lastTimeStampId) {
                    Intent intent2 = new Intent(context, LoginActivity.class);
                    NotificationUtils.notificatePush(context, 0, "Ticker", "Ring!!!", "Somebody at the door... " + "Quick, HODOR!", intent2);
                    lastTimeStampId = lastTimeStamp.getId();
                }
            }
        };

        uniqueId = getIntent().getStringExtra("UniqueId");

        Button logoutBtn = (Button) findViewById(R.id.LogoutButton);
        ImageButton historyBtn = (ImageButton) findViewById(R.id.HistoryButton);
        ImageButton todayBtn = (ImageButton)findViewById(R.id.TodayButton);
        ImageButton graphBtn = (ImageButton)findViewById(R.id.GraphButton);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class).putExtra("UniqueId", uniqueId));
            }
        });

        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TodayActivity.class).putExtra("UniqueId", uniqueId));
            }
        });

        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GraphActivity.class).putExtra("UniqueId", uniqueId));
            }
        });
        registerReceiver(getLastTimeStampReceiver,new IntentFilter("responseIntent"));
        //PollDatabase();
    }
    public void onStart() {
        super.onStart();
        registerReceiver(getLastTimeStampReceiver, new IntentFilter("responseIntent"));
        if (!timerStarted) {
          //  PollDatabase();
        }
        Log.d("Main", "onStart");
    }
    public void onStop() {
        super.onStop();
        unregisterReceiver(getLastTimeStampReceiver);
        if (timerStarted && timer != null) {
            timer.cancel();
            timer.purge();
            timerStarted = false;
        }
        Log.d("Main", "onStop");
    }
    public void onRestart() {
        super.onRestart();
        Log.d("Main", "onRestart");
    }
    public void onResume() {
        super.onResume();
        Log.d("Main", "onResume");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("Main", "onDestroy");
    }


    private void HandleResponse(){
        try {
            JSONArray json = (JSONArray) new JSONTokener(response.toString()).nextValue();
            Gson gson = new Gson();
            timeStamps = gson.fromJson(json.toString(), TimeStamp[].class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void PollDatabase() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new HtmlPostRequest("http://michielserver.com/AP_valley/Gettimestamps24.php",uniqueId,MainActivity.this).execute();

                            Log.i("MainPOLL","done!");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 50000 ms
        timerStarted = true;
    }
}
