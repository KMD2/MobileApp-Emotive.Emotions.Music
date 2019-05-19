package com.example.happinessmap01;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.happinessmap01.Database.DateTypeConverter;
import com.example.happinessmap01.Database.Emos;
import com.example.happinessmap01.heatmap.HeatmapActivity;
import com.example.happinessmap01.player.MusicplayerActivity;
import com.example.happinessmap01.player.SpotifyActivity;
import com.github.mikephil.charting.data.Entry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EmosModel emosModel;
    private Random rand;
    private String userID;
    private static final int  HTTP_OK  = 200;
    private static final int NO_DATA = 400;
    private String beginDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(SaveSharedPreferences.getUserId(MainActivity.this).length() == 0)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        else {

            setContentView(R.layout.activity_main);
            rand = new Random();
            emosModel = ViewModelProviders.of(this).get(EmosModel.class);
            userID = SaveSharedPreferences.getUserId(MainActivity.this);

            Calendar cal = Calendar.getInstance();
            beginDate = DateTypeConverter.toISOformat(cal.getTime());

            Timer t = new Timer();

            //EPOCPLUS-3b9ae818

            t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        GetMeasurments task = new GetMeasurments();
                        task.execute(getString(R.string.performance_query) + "/" + userID);
                    }
                },
                0,      // run first occurrence immediately
                10000);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


            if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {

                Log.v("NETWORK", "The device is connected");
                DatabaseUpdate databaseUpdate = new DatabaseUpdate();
                databaseUpdate.execute(getString(R.string.synchronize_query));
            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);

    }


    public void launchMap (View view) {

        Intent intent = new Intent(this, HeatmapActivity.class);
        startActivity(intent);

    }

    public void launchMusicplayer (View view) {

        Intent intent = new Intent(this, MusicplayerActivity.class);
        startActivity(intent);

    }



    /**************************
     On-click Listeners
     **************************/

    private void viewPlot(String title) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.putExtra("metrics_name", title);
        startActivity(intent);
    }

    public void viewPlotStress (View view) {
        viewPlot("str");
    }

    public void viewPlotFocus (View view) {
        viewPlot("foc");

    }

    public void viewPlotRelaxation (View view) {
        viewPlot("rel");
    }


    public void viewPlotEngagemnet (View view) {
        viewPlot("eng");
    }


    public void viewPlotInterest (View view) {
        viewPlot("interest");
    }

    public void viewPlotExcitement (View view) {
        viewPlot("exc");
    }





    /**************************
    Class GetMeasurments sends get request to ec2 server to retrieve performance metric and update main UI
     **************************/

    private class GetMeasurments extends AsyncTask {

        // arguments are given by execute() method call (defined in the parent): params[0] is the url.
        protected Object doInBackground(Object... urls) {
            try {
                return downloadUrl((String) urls[0]);
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.v("NETWORK", e.getMessage() );
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Object result) {
            try{

                JSONObject jsonObj = new JSONObject((String)result);
                int code = (Integer)jsonObj.get("code");

                Log.v("HTTP response", "getting metrics " + code + "");

                if(code == HTTP_OK) {
                    JSONObject jsonObject = (JSONObject)jsonObj.get("data");
                    ((TextView)findViewById(R.id.interest)).setText((int) (jsonObject.getDouble("interest") * 100) + "%");
                    ((TextView)findViewById(R.id.str)).setText((int) (jsonObject.getDouble("str") * 100) + "%");
                    ((TextView)findViewById(R.id.rel)).setText((int) (jsonObject.getDouble("rel") * 100) + "%");
                    ((TextView)findViewById(R.id.exc)).setText((int) (jsonObject.getDouble("exc") * 100) + "%");
                    ((TextView)findViewById(R.id.eng)).setText((int) (jsonObject.getDouble("eng") * 100) + "%");
                    ((TextView)findViewById(R.id.foc)).setText((int) (jsonObject.getDouble("foc") * 100) + "%");
                }
                else if (code == NO_DATA){
                    Log.v("SERVER ERROR", "no data");
                    //Toast.makeText(this,"You do not have any records. If you are connected first time. the values will be updated within 20 - 30 seconds.", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.v("SERVER ERROR", (String) jsonObj.get("message"));
                }

            }
            catch (final JSONException e) {
                Log.d("JSON PARSING ERROR",  e.getMessage());
            }
        }

        private String downloadUrl(String _url) throws IOException {
            InputStream inputStream = null;

            try {

                URL url = new URL(_url + "/" + beginDate);

                // Starts the query
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000); /* milliseconds */
                conn.setConnectTimeout(15000); /* milliseconds */
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();

                // log the result
                Log.d("NETWORK", "The response is: " + response);

                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String contentAsString = scanner.hasNext() ? scanner.next() : "";
                return contentAsString;
            }
            finally {
                if (inputStream != null) inputStream.close();
            }
        }

    }

    /*********************************************************************************************/




    /**************************
     Class DatabaseUpdate sends get request to ec2 server to syncronize database, it deletes all existing records and inserts the received records
     **************************/


    private class DatabaseUpdate extends AsyncTask {


        // arguments are given by execute() method call (defined in the parent): params[0] is the url.
        protected Object doInBackground(Object... urls) {
            try {
                return downloadUrl((String) urls[0]);
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.v("NETWORK", e.getMessage() );
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Object result) {
            try{

                JSONObject jsonObj = new JSONObject((String)result);
                int code = (Integer)jsonObj.get("code");

                Log.v("HTTP response", "synchronizing database " + code + "");

                if(code == HTTP_OK) {

                    JSONObject data  = jsonObj.getJSONObject("data");
                    JSONArray items = data.getJSONArray("items");

                    if (items != null && items.length() > 0){
                        List<Emos> emos = parseJson(items);
                        emosModel.deleteAll();

                        for(int i = 0; i < emos.size(); i++) {
                            emosModel.insert(emos.get(i));
                            Log.v("INSERT DB", emos.get(i).excitement + " ");
                        }

                        List<Emos> emotions = emosModel.getEmos();
                        for (Emos e : emotions)
                            Log.v("GETFROM DB", e.excitement + " ");
                    }


                }
                else if (code == NO_DATA){
                    Log.v("SERVER ERROR", "no data");
                    //Toast.makeText(this,"You do not have any records. If you are connected first time. the values will be updated within 20 - 30 seconds.", Toast.LENGTH_LONG).show();
                }
                else {
                    Log.v("SERVER ERROR", (String) jsonObj.get("message"));
                }

            }
            catch (final JSONException e) {
                Log.d("JSON PARSING ERROR",  e.getMessage());
                e.printStackTrace();
            }
        }

        private String downloadUrl(String _url) throws IOException {
            InputStream inputStream = null;

            try {

                Calendar cal = Calendar.getInstance();
                String endDate = DateTypeConverter.toISOformat(cal.getTime());
                cal.add(Calendar.MONTH, -1);
                String beginDate = DateTypeConverter.toISOformat(cal.getTime());


                URL url = new URL(_url + "/" + userID + "/" + beginDate + "/" + endDate);


                // Starts the query
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000); /* milliseconds */
                conn.setConnectTimeout(15000); /* milliseconds */
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();

                // log the result
                Log.d("NETWORK", "The response is: " + response);

                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String contentAsString = scanner.hasNext() ? scanner.next() : "";

                Log.v("content response", contentAsString);
                return contentAsString;
            }
            finally {
                if (inputStream != null) inputStream.close();
            }
        }

        private List<Emos> parseJson(JSONArray items) throws JSONException {

            List<Emos> emos = new ArrayList<>();

            for (int i = 0; i < items.length(); i++) {

                JSONObject item = items.getJSONObject(i);

                Float interest = (float)item.getDouble("interest");
                Float stress = (float)item.getDouble("str");
                Float relaxation = (float)item.getDouble("rel");
                Float excitement = (float)item.getDouble("exc");
                Float engagement = (float)item.getDouble("eng");
                Float focus = (float)item.getDouble("foc");

                emos.add(new Emos (interest, stress, relaxation,
                        excitement, engagement, focus, item.getString("recorded_time")));

            }

            return emos;
        }

    }

}



