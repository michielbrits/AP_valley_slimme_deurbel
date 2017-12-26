package com.example.nick.buzz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
import java.sql.Time;
import java.util.ArrayList;

public class HtmlPostRequest extends AsyncTask<String, String, String> {

    private String urlString;
    private String uniqueId;
    private String token;
    private String name;
    private String password;
    private String passwordAgain;
    private String email;
    private String description;
    private int controllerId;
    private int sensorId;
    private int userId;
    private int id;
    private int state;
    private Context context;

    //constructors
    // 1: REGISTER
    public HtmlPostRequest(String url_, String name_, String password_, String passwordAgain_, String email_, String token_, Context context_){
        urlString = url_;
        name = name_;
        password = password_;
        passwordAgain = passwordAgain_;
        email = email_;
        token = token_;
        context = context_;
    }
    // 2: Login
    public HtmlPostRequest(String url_, String name_, String password_, String token_, Context context_){
        urlString = url_;
        name = name_;
        password = password_;
        token = token_;
        context = context_;
    }
    public HtmlPostRequest(String url_, String uniqueId_,Context context_){
        urlString = url_;
        uniqueId = uniqueId_;
        context = context_;
    }
    // 6: LOGOUT
    public HtmlPostRequest(String url_, int id_, String token_, Context context_){
        urlString = url_;
        id = id_;
        token = token_;
        context = context_;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpURLConnection urlConnection = null;
        InputStream iStream = null;
        JSONObject jsonParams = new JSONObject();
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-type", "x-www-form-urlencoded");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            if (uniqueId != null)
                urlConnection.setRequestProperty("userid", "" + uniqueId);
            if (password != null)
                jsonParams.put("password", password);
            if (uniqueId != null)
                jsonParams.put("userid", uniqueId);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            writer.write(jsonParams.toString());
            writer.flush();
            writer.close();


            try{
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    iStream = urlConnection.getInputStream();
                } else {
                    iStream = urlConnection.getErrorStream();
                }

                // Create an InputStream in order to extract the response object
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(iStream, "UTF-8"), 8);

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally {
                urlConnection.disconnect();
            }
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        Log.i("INFO", response);
        //Do anything with response..
      //  if(response.length() > );
        JSONArray json = null;
        try {
            json = (JSONArray) new JSONTokener(response.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }/*
        Gson gson = new Gson();
        TimeStamp[] timestamps = gson.fromJson(json.toString(), TimeStamp[].class);
        Log.d("timeSLength", "" + timestamps.length);
        Log.d("timeSLength", "" + timestamps.length/5);
        response = "[";
        for(int i =0; i < (int)timestamps.length/5; i++) {
            Log.d("ts", gson.toJson(timestamps[i]));
            response += gson.toJson(timestamps[i]);
            if(i + 1 != timestamps.length/5)
                response += ",";
        }
        response += "]";
        //response = response.substring(0, (response.length()/100));
        Log.d("resp",response);
        */
        Intent intent = new Intent("responseIntent");

        intent.putExtra("response", response);
        context.sendBroadcast(intent);
    }
}
