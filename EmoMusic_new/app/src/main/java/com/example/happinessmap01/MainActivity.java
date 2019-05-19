package com.example.happinessmap01;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.happinessmap01.Database.DateTypeConverter;
import com.example.happinessmap01.Database.Emos;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private EmosModel emosModel;
    private Random rand;
    private static final int  HTTP_OK  = 200;
    private static final int NO_DATA = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rand = new Random();
        emosModel = ViewModelProviders.of(this).get(EmosModel.class);

        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {
                        GetMeasurments task = new GetMeasurments();
                        task.execute(getString(R.string.performance_query) + "/EPOCPLUS-3b9ae818");
                    }
                },
                0,      // run first occurrence immediately
                10000);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {

            Log.v("NETWORK","The device is connected");
            DatabaseUpdate databaseUpdate = new DatabaseUpdate();
            databaseUpdate.execute(getString(R.string.synchronize_query));
        }



    }




    public void check(View view) {

        //  Calendar calendar = Calendar.getInstance();
        //String currentDate = DateFormat.getDateInstance(DateFormat("yyyy-mm-dd hh:mm:ss")).format(calendar.getTime());

        Date date = Calendar.getInstance().getTime();
        // Date d = new Date(date.getTime());


        // DateTypeConverter.toString(date)


        //emosModel.insert(new Emos(rand.nextFloat() * 100, 34f, 87f, 65f, 23f, 0f,DateTypeConverter.toString(date),55f, 25f));


        List<String> dates = getDates("2019-02-01 00:00:00", "2019-05-13 00:00:00");


            for (int i = 0; i < 200; i++) {
                emosModel.insert(new Emos(rand.nextFloat() * 100, rand.nextFloat() * 100, rand.nextFloat() * 100,
                        rand.nextFloat() * 100, rand.nextFloat() * 100, rand.nextFloat() * 100,
                        dates.get(i),getRandom(52.0, 56.0), getRandom(21.0, 26.0)));
            }


            List<Emos> emos = emosModel.getEmos();
            for (Emos e : emos) {
                Log.v("db_check", e.recoDate);
                Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
            }

            Log.v("checkdate",dates.toString());

            //emosModel.deleteAll();
        }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

/*
        switch (id) {

            case R.id.action_settings: {

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                //EditText editText = (EditText) findViewById(R.id.editText);
                //String message = editText.getText().toString();
                //intent.putExtra("LocationName", message);
                startActivity(intent);
            }
            case R.id.battery_level: {
                Toast.makeText(this, "90%", Toast.LENGTH_LONG).show();
            }
            case R.id.connect: {
                //Toast.makeText(this, "100%", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, MusicplayerActivity.class);
                startActivity(intent);

            }


        }
*/

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

    public void launchSpotify (View view) {

        Intent intent = new Intent(this, SpotifyActivity.class);
        startActivity(intent);

    }

    public static double getRandom(Double min, Double max){
        Double num = ((Double)(Math.random()*((max-min)+1))+min);
        return num;
    }

    private static List<String> getDates(String dateString1, String dateString2)
    {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            String now = DateTypeConverter.toString(cal1.getTime());
            dates.add(now);
            cal1.add(Calendar.HOUR, 12);
        }
        return dates;
    }

    public void viewPlot (View view) {

        Intent intent = new Intent(this, PlotActivity.class);
        startActivity(intent);

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
                URL url = new URL(_url);

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
                        emosModel.deleteAll();
                        List<Emos> emos = parseJson(items);
                        emosModel.insertAll(emos);
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


                URL url = new URL(_url + "/EPOCPLUS-3b9ae818" + "/" + beginDate + "/" + endDate);


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
                String recoDate = DateTypeConverter.fromIsoToStandart(item.getString("recorded_time"));
                Double latitude = item.getDouble("latitude");
                Double longitude = item.getDouble("longitude");

                emos.add(new Emos (interest, stress, relaxation,
                        excitement, engagement, focus,
                        recoDate ,latitude, longitude));

            }

            return emos;
        }

    }

}



