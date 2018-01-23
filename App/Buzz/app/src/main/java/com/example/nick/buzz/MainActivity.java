package com.example.nick.buzz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    String uniqueId = "";

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.Theme_AppCompat_BuzzDarkTheme);
        }
        super.onCreate(savedInstanceState);
        Log.d("Main", "onCreate");
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.small_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        uniqueId = getIntent().getStringExtra("UniqueId");

        Button logoutBtn = (Button) findViewById(R.id.LogoutButton);
        ImageButton historyBtn = (ImageButton) findViewById(R.id.HistoryButton);
        ImageButton todayBtn = (ImageButton)findViewById(R.id.TodayButton);
        ImageButton graphBtn = (ImageButton)findViewById(R.id.GraphButton);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
