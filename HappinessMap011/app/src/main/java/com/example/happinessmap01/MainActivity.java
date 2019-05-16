package com.example.happinessmap01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void check(View view) {
        Toast.makeText(this, "YAY", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

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
                Toast.makeText(this, "100%", Toast.LENGTH_LONG).show();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    public void launchMap (View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }



}
