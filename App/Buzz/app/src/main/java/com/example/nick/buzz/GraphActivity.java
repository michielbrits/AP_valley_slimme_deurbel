package com.example.nick.buzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphActivity extends AppCompatActivity {
    TimeStamp[] timeStamps;
    DataPoint[] graphValues = null;
    GraphView graph = null;
    BroadcastReceiver getTimeStampReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = (GraphView) findViewById(R.id.graph);
        new HtmlPostRequest("http://michielserver.com/AP_valley/Gettimestamps.php","AAA000",GraphActivity.this).execute();
    }

    public void HandleResponse(String response){
        try {
            Log.d("response",response);
            JSONArray json = (JSONArray) new JSONTokener(response).nextValue();
            Gson gson = new Gson();
            timeStamps = gson.fromJson(ReverseList(json).toString(), TimeStamp[].class);
            DataPoint[] datapoints = new DataPoint[timeStamps.length];
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //String dateRaw = "2017-11-25";
            //Date date = format.parse(dateRaw);
            ArrayList<Date> filteredTimeStamps=new ArrayList<>();
            for(int i = 0; i < timeStamps.length; i++) {
                Date d = format.parse(timeStamps[i].getTimeStamp());
                /*if(filteredTimeStamps.contains(d)) {
                    continue;
                }
                else{
                */    filteredTimeStamps.add(d);
                    //datapoints[i] = new DataPoint(d,i);
                    //Log.d("dp","" +datapoints[i].getX());
                //}

                //datapoints[i] = new DataPoint(format.parse(timestamps[i].getTimeStamp()), 8);
                //Log.d("ts",timestamps[i].getTimeStamp());
                //Log.d("ts",format.parse(timestamps[i].getTimeStamp()).toString());

            }
            int[] datesOccured = new int[filteredTimeStamps.size()];
            for (int i = 0; i < filteredTimeStamps.size(); i++){
                datesOccured[i] = Collections.frequency(filteredTimeStamps, filteredTimeStamps.get(i) );
            }
            for(int i = 0; i < datesOccured.length; i++) {
                Log.d("datesOccured","" + datesOccured[i]);

            }

            //DataPoint[] datapoints = new DataPoint[filteredTimeStamps.size()];
            for(int i = 0; i < filteredTimeStamps.size(); i++) {
           //         datapoints[i] = new DataPoint(filteredTimeStamps.d,i);
                    Log.d("dp","" +datapoints[i].getX());
                }


            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(datapoints);

            //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this));
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            //graph.getViewport().setMaxX(7);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(25);
            graph.getViewport().setScrollable(true);
            graph.addSeries(series);

// styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });

            series.setSpacing(50);

// draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);
//series.setValuesOnTopSize(50);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
            String userName = timeStamps[0].getFirstName();
            setTitle("Logged in as " + userName);
        }
    }
}

