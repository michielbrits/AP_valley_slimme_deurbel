package com.example.nick.buzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TimeStamp[] timeStamps;
    GraphView graph = null;
    ProgressBar pBar = null;
    String uniqueId;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    boolean useDarkTheme = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.Theme_AppCompat_BuzzDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.small_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.GONE);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);
        uniqueId = getIntent().getStringExtra("UniqueId");
        Button logoutBtn = (Button) findViewById(R.id.LogoutButton);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //get the spinner from the xml.
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        //if(useDarkTheme)
          //  dropdown.setBackgroundColor(Color.GRAY);
        //create a list of items for the spinner.
        String[] items = new String[]{"1", "2", "3","4", "5", "6","7", "8", "9", "10"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(spinnerAdapter);
        dropdown.setSelection(6);
        dropdown.setOnItemSelectedListener(this);
        new GraphActivity.RetrieveTimeStampsTask(uniqueId).execute();
    }

    public void HandleResponse(String response){
        try {
            Log.d("response",response);
            JSONArray json = (JSONArray) new JSONTokener(response).nextValue();
            Gson gson = new Gson();
            //timeStamps = gson.fromJson(ReverseList(json).toString(), TimeStamp[].class);
            timeStamps = gson.fromJson(json.toString(), TimeStamp[].class);
            DrawGraph(7);
            graph.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    /*
    private JSONArray ReverseList(JSONArray jsonArray) {
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
    */
    private void DrawGraph(final int totalDays) throws ParseException {
        graph.removeAllSeries();
        DataPoint[] datapoints = new DataPoint[totalDays];
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //String dateRaw = "2017-11-25";
        //Date date = format.parse(dateRaw);
        int[] ringsPerDay = new int[totalDays];
        final int oneDay = 86400000;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date lastMidnight = cal.getTime();
        for(int i = 0; i < ringsPerDay.length; i++) {
            for(int j = 0; j < timeStamps.length; j++) {
                Date d = format.parse(timeStamps[j].getTimeStamp());
                if(d.getTime() > lastMidnight.getTime() - oneDay*i && d.getTime() < (lastMidnight.getTime() - oneDay*i) + oneDay)
                {
                    ringsPerDay[i]++;
                }
            }
        }
        int maxRings = 0;
        for(int i = 0; i < ringsPerDay.length; i++) {
            datapoints[i] = new DataPoint(totalDays-i, ringsPerDay[i]);
            Log.d("ringsPerDay", ""+ringsPerDay[i]);
            if(ringsPerDay[i] > maxRings)
                maxRings = ringsPerDay[i];
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(datapoints);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    DateFormat format = new SimpleDateFormat("E dd/MM");  //add E \n to string to add day
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(new Date().getTime() - (oneDay*(totalDays-(long)value)));
                    Date xValue = c.getTime();
                    return format.format(xValue);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setLabelHorizontalHeight(175);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(50);
        graph.getGridLabelRenderer().setNumHorizontalLabels(totalDays+2);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(totalDays + 1);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(maxRings + 5);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
        graph.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb(255,121, 0);
            }
        });

        series.setSpacing(10);

        // draw values on top
        series.setDrawValuesOnTop(true);
        if(useDarkTheme)
            series.setValuesOnTopColor(Color.WHITE);
        else
            series.setValuesOnTopColor(Color.BLACK);
        //series.setValuesOnTopSize(50);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        graph.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
        parent.getItemAtPosition(position);
        int scope = Integer.parseInt(parent.getItemAtPosition(position).toString());
        try {
            if(timeStamps != null) {
                graph.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);
                DrawGraph(scope);
                graph.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
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
            //setTitle("Logged in as " + userName);
        }
    }
}

