package com.example.nick.buzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
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


public class LoginActivity extends AppCompatActivity {

    EditText uniqueIdText;
    EditText passwordText;
    ProgressBar progressBar;
    static final String API_URL = "http://michielserver.com/AP_valley/checklogin.php";
    String uniqueId = "";
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.Theme_AppCompat_BuzzDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseMessaging.getInstance().subscribeToTopic("AAA000");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.small_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        uniqueIdText = (EditText) findViewById(R.id.UniqueIdET);
        passwordText = (EditText) findViewById(R.id.PasswordET);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uniqueId = uniqueIdText.getText().toString();
                new LoginTask(uniqueId, passwordText.getText().toString()).execute();
            }
        });
        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setChecked(useDarkTheme);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });
    }

    class LoginTask extends AsyncTask<Void, Void, String> {
        private String uniqueId;
        private String password;
        public LoginTask(String uniqueId_, String password_) {
            uniqueId = uniqueId_;
            password = password_;
        }
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        protected String doInBackground(Void... urls) {
            HttpURLConnection urlConnection = null;
            InputStream iStream = null;
            try {
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("Code", uniqueId);
                    jsonParams.put("Password", password);
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
            progressBar.setVisibility(View.GONE);
            if(Objects.equals(response, new String("valid \n"))){
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("UniqueId", uniqueId);
                startActivity(i);
            }
            else {
                Toast toast2 = Toast.makeText(getApplicationContext(),"The combination of Unique ID and password is invalid",Toast.LENGTH_LONG);
                toast2.show();
            }
        }
    }
}
