package com.thorson.adam.pitchhoner;

import static com.thorson.adam.pitchhoner.PitchManagerConstants.*;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements PitchManager.PitchManagerCallback{
    private static final String TAG = MainActivity.class.getSimpleName();


    private PitchManager pitchManager;

    private Button startButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, String.format("Thread %d", Thread.currentThread().getId()));

        initPitchManager();
        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPitchManager(){
        pitchManager = new PitchManager(this);
    }

    private void setupUI(){
        startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitchManager.start(PITCHMANAGER_TYPE_FREQUENCY);
            }
        });
    }

    private void togglePlay(){

    }

    @Override
    public void onPitchManagerStatus(int code, int typeId) {
        Log.d(TAG, "PitchManager " + code + " " + typeId);
    }
}
