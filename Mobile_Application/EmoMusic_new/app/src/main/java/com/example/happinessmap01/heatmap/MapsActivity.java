

package com.example.happinessmap01.heatmap;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.happinessmap01.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;



public abstract class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    protected int getLayoutId() {
        return R.layout.activity_maps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setUpMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }
        mMap = map;
        startHeatMap();

    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    protected abstract void startHeatMap();
    protected GoogleMap getMap() { return mMap; }
}

