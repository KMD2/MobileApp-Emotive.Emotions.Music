package com.example.happinessmap01.heatmap;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeterogeneousExpandableList;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.example.happinessmap01.PlotActivity;
import com.example.happinessmap01.R;
import com.example.happinessmap01.SaveSharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class HeatmapActivity extends MapsActivity {

    private static final int  HTTP_OK  = 200;
    private static final int NO_DATA = 400;
    private Map<String, String> map = new HashMap<String, String>();

    /**
     * Alternative radius for convolution
     */
    private static final int ALT_HEATMAP_RADIUS = 10;

    /**
     * Alternative opacity of heatmap overlay
     */
    private static final double ALT_HEATMAP_OPACITY = 0.4;

    /**
     * Alternative heatmap gradient (blue -> red)
     * Copied from Javascript version
     */
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 0, 255, 255),// transparent
            Color.argb(255 / 3 * 2, 0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 0, 127),
            Color.rgb(255, 0, 0)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    /**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();

    @Override
    protected int getLayoutId() {
        return R.layout.heatmap;
    }

    @Override
    protected void startHeatMap() {
        try {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.4539, 54.3773), 6));

            HeatmapDatasetGet heatmapDatasetGet = new HeatmapDatasetGet();
            heatmapDatasetGet.execute(getString(R.string.heatmap_query)).get();

            // Set up the spinner/dropdown list
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.heatmaps_datasets_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new SpinnerActivity());

        }
        catch (Exception e) {
            e.printStackTrace();
            Log.v("DATASET", e.getMessage() );
        }



        // Make the handler deal with the map
        // Input: list of WeightedLatLngs, minimum and maximum zoom levels to calculate custom
        // intensity from, and the map to draw the heatmap on
        // radius, gradient and opacity not specified, so default are used
    }

    public void changeRadius(View view) {
        if (mDefaultRadius) {
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
        }
        mOverlay.clearTileCache();
        mDefaultRadius = !mDefaultRadius;
    }

    public void changeGradient(View view) {
        if (mDefaultGradient) {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
        } else {
            mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
        }
        mOverlay.clearTileCache();
        mDefaultGradient = !mDefaultGradient;
    }

    public void changeOpacity(View view) {
        if (mDefaultOpacity) {
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
        }
        mOverlay.clearTileCache();
        mDefaultOpacity = !mDefaultOpacity;
    }

    // Dealing with spinner choices
    public class SpinnerActivity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            String dataset = parent.getItemAtPosition(pos).toString();

            TextView attribution = ((TextView) findViewById(R.id.attribution));

            if(!mLists.isEmpty() && mLists.containsKey(dataset))  {
                // Check if need to instantiate (avoid setData etc twice)
                if (mProvider == null) {
                    Log.v("check", mLists.size() + "");
                    mProvider = new HeatmapTileProvider.Builder().weightedData(
                            mLists.get(getString(R.string.my_map)).getData()).build();
                    mOverlay = getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    // Render links
                    attribution.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    mProvider.setWeightedData(mLists.get(dataset).getData());
                    mOverlay.clearTileCache();
                }
            }

            else {
                Toast.makeText(HeatmapActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }


    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<WeightedLatLng> mDataset;
        private String mUrl;

        public DataSet(ArrayList<WeightedLatLng> dataSet, String url) {
            this.mDataset = dataSet;
            this.mUrl = url;
        }

        public ArrayList<WeightedLatLng> getData() {
            return mDataset;
        }

        public String getUrl() {
            return mUrl;
        }
    }

    /**************************
     Class DatabaseUpdate sends get request to ec2 server to syncronize database, it deletes all existing records and inserts the received records
     **************************/


    private class HeatmapDatasetGet extends AsyncTask {


        // arguments are given by execute() method call (defined in the parent): params[0] is the url.
        protected Object doInBackground(Object... urls) {
            try {
                map.clear();

                String userID = SaveSharedPreferences.getUserId(HeatmapActivity.this);
                String response1 = downloadUrl((String) urls[0]);
                String response2 = downloadUrl(((String) urls[0]) + "/" + userID);
                map.put(getString(R.string.common_map), response1);
                map.put(getString(R.string.my_map), response2);

                return map;
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


                for(Map.Entry<String, String> pair : map.entrySet()) {


                    String title = pair.getKey();
                    String response = pair.getValue();

                    Log.v("title", title);

                    Log.v("response", response);

                    JSONObject jsonObj = new JSONObject(response);
                    int code = (Integer)jsonObj.get("code");

                    Log.v("HTTP response", "Heatmap database" + code + "");

                    if(code == HTTP_OK) {

                        JSONObject data  = jsonObj.getJSONObject("data");
                        JSONArray items = data.getJSONArray("items");


                        if (items != null && items.length() > 0) {
                            Log.v("mlists", "1");
                            ArrayList<WeightedLatLng> readItems = parseJson(items);
                            mLists.put(title, new DataSet(readItems, title));
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
            }
            catch (final JSONException e) {
                Log.d("JSON PARSING ERROR", e.getMessage());
                e.printStackTrace();
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

        private ArrayList<WeightedLatLng> parseJson(JSONArray items) throws JSONException {

            ArrayList<WeightedLatLng> weightedLatLngs = new ArrayList<>();

            for (int i = 0; i < items.length(); i++) {

                JSONObject item = items.getJSONObject(i);

                Float interest = (float)item.getDouble("interest");
                Float relaxation = (float)item.getDouble("rel");
                Float excitement = (float)item.getDouble("exc");
                Float engagement = (float)item.getDouble("eng");

                Double lat = item.getDouble("lat");
                Double lng = item.getDouble("lng");

                Double intensity = 0.35*relaxation + 0.35 * excitement + 0.15* interest + 0.15*engagement;
                weightedLatLngs.add(new WeightedLatLng(new LatLng(lat, lng), intensity));

            }

            return weightedLatLngs;
        }

    }

}

