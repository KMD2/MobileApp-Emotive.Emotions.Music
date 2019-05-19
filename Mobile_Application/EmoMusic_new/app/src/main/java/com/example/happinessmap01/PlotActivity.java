package com.example.happinessmap01;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.happinessmap01.Database.DateTypeConverter;
import com.example.happinessmap01.Database.Emos;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import static java.lang.Math.toIntExact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class PlotActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart;
    private EmosModel emosModel;
    String metric;
    private static final int  HTTP_OK  = 200;
    private static final int NO_DATA = 400;
    private static int reference_timestamp = 1451660400;
    ArrayList<Entry> entries = new ArrayList<Entry>();
    ArrayList<String> xValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To make full screen layout


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_plot);

        metric = getIntent().getExtras().getString("metrics_name");

        emosModel = ViewModelProviders.of(this).get(EmosModel.class);

        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("last 30 days");
        mChart.setNoDataTextDescription("Please wait ...");

        mChart.getXAxis().enableGridDashedLine(10f, 10f, 0f);
        mChart.getAxisLeft().enableGridDashedLine(10f, 10f, 0f);
        mChart.getAxisRight().enableGridDashedLine(10f, 10f, 0f);



        // add data
        setData();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        mChart.animateX(1500, Easing.EasingOption.EaseInOutQuart);
    }


    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex()
                + ", high: " + mChart.getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin()
                + ", xmax: " + mChart.getXChartMax()
                + ", ymin: " + mChart.getYChartMin()
                + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }




    private void setData() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        LineDataSet lineDataSet;


        // If we are connected we take data from the server
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            Log.v("DATASET","Approaching server");

            RequestStatistics requestStatistics = new RequestStatistics();
            requestStatistics.execute(getString(R.string.synchronize_query));

        }

        // else retirieve from DB
        else {

            List<Emos> emotions = emosModel.getEmos();

            for(int i = 0; i < emotions.size(); i++) {
                xValues.add(emotions.get(i).recoDate);

                Float yVal = emotions.get(i).excitement;

                Log.v("metric", metric);

                switch (metric) {
                    case "interest":
                        yVal = emotions.get(i).interest;
                        break;
                    case "foc":
                        yVal = emotions.get(i).focus;
                        break;
                    case "eng":
                        yVal = emotions.get(i).engagement;
                        break;
                    case "rel":
                        yVal = emotions.get(i).relaxation;
                        break;
                    case "str":
                        yVal = emotions.get(i).stress;
                        break;
                }

                entries.add(new Entry(yVal, i));

            }


            LineDataSet dataSet = new LineDataSet(entries, "performance levels");
            setDasetAttributes(dataSet);

            List<ILineDataSet> sets = new ArrayList<>();
            sets.add(dataSet);


            LineData lineData =  new LineData(xValues,sets);


            mChart.setData(lineData);
            Log.v("DATASET","retrieving from DB");
        }


    }

    private void setDasetAttributes(LineDataSet lineDataSet) {
        lineDataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fadeblue);
        drawable.setAlpha(150);
        lineDataSet.setFillDrawable(drawable);

        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setDrawFilled(true);

        lineDataSet.setFillAlpha(200);
    }





    /**************************
     Class DatabaseUpdate sends get request to ec2 server to syncronize database, it deletes all existing records and inserts the received records
     **************************/


    private class RequestStatistics extends AsyncTask {


        // arguments are given by execute() method call (defined in the parent): params[0] is the url.
        protected Object doInBackground(Object... urls) {
            try {
                return downloadUrl((String) urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("NETWORK", e.getMessage());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Object result) {
            try {

                JSONObject jsonObj = new JSONObject((String) result);
                int code = (Integer) jsonObj.get("code");

                Log.v("HTTP response", "synchronizing database " + code + "");

                if (code == HTTP_OK) {

                    JSONObject data = jsonObj.getJSONObject("data");
                    JSONArray items = data.getJSONArray("items");

                    if (items != null && items.length() > 0) {
                        parseJson(items);
                        for(Entry e: entries) {
                            Log.v("entry", e.getXIndex() + " " + e.getVal());
                        }


                        LineDataSet dataSet = new LineDataSet(entries, "performance levels");
                        setDasetAttributes(dataSet);

                        List<ILineDataSet> sets = new ArrayList<>();
                        sets.add(dataSet);


                        LineData lineData =  new LineData(xValues,sets);


                        mChart.setData(lineData);


                    }


                } else if (code == NO_DATA) {
                    Log.v("SERVER ERROR", "no data");
                    //Toast.makeText(this,"You do not have any records. If you are connected first time. the values will be updated within 20 - 30 seconds.", Toast.LENGTH_LONG).show();
                } else {
                    Log.v("SERVER ERROR", (String) jsonObj.get("message"));
                }

            } catch (final JSONException e) {
                Log.d("JSON PARSING ERROR", e.getMessage());
                e.printStackTrace();
            }
        }

        private String downloadUrl(String _url) throws IOException {
            InputStream inputStream = null;

            try {

                Calendar cal = Calendar.getInstance();
                String endDate = DateTypeConverter.toISOformat(cal.getTime());
                cal.add(Calendar.MONTH, -4);
                String beginDate = DateTypeConverter.toISOformat(cal.getTime());

                String userID = SaveSharedPreferences.getUserId(PlotActivity.this);


                URL url = new URL(_url + "/"  + userID + "/" + beginDate + "/" + endDate);


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


                Log.v("RESPONSE", contentAsString);

                return contentAsString;
            } finally {
                if (inputStream != null) inputStream.close();
            }
        }

        private void parseJson(JSONArray items) throws JSONException {


            for (int i = 0; i < items.length(); i++) {

                JSONObject item = items.getJSONObject(i);

                Float yValue = (float) item.getDouble(metric) * 100;
                xValues.add(item.getString("recorded_time"));

                entries.add(new Entry(yValue, i));

            }
        }
    }

}
