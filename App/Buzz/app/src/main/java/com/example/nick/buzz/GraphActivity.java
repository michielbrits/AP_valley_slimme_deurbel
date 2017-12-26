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
    DataPoint[] graphValues = null;
    GraphView graph = null;
    BroadcastReceiver getTimeStampReceiver;
    Object response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        getTimeStampReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                response = intent.getExtras().get("response");
                HandleResponse();
            }
        };
        graph = (GraphView) findViewById(R.id.graph);
        new HtmlPostRequest("http://michielserver.com/AP_valley/Gettimestamps.php","AAA000",GraphActivity.this).execute();
        registerReceiver(getTimeStampReceiver, new IntentFilter("responseIntent"));
    }

    public void HandleResponse(){
        try {
            Log.d("response",""+response.toString());
            JSONArray json = (JSONArray) new JSONTokener(response.toString()).nextValue();
            Gson gson = new Gson();
            TimeStamp[] timestamps = gson.fromJson(ReverseList(json).toString(), TimeStamp[].class);
            DataPoint[] datapoints = new DataPoint[timestamps.length];
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //String dateRaw = "2017-11-25";
            //Date date = format.parse(dateRaw);
            ArrayList<Date> filteredTimeStamps=new ArrayList<>();
            for(int i = 0; i < timestamps.length; i++) {
                Date d = format.parse(timestamps[i].getTimeStamp());
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
}

