package com.example.nick.buzz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    String uniqueId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main", "onCreate");
        setContentView(R.layout.activity_main);
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
    }

    public void onStart() {
        super.onStart();
        Log.d("Main", "onStart");
    }
    public void onStop() {
        super.onStop();
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
}
