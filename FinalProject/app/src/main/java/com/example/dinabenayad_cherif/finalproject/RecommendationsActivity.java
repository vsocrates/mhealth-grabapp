package com.example.dinabenayad_cherif.finalproject;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.Manifest;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import android.util.Log;

import java.util.ArrayList;

public class RecommendationsActivity extends ListActivity implements GoogleApiClient.OnConnectionFailedListener{

    public static ArrayList<Integer> prgmImages = new ArrayList<Integer>();
    public static ArrayList<String> prgmNameList= new ArrayList<String>();
    //{"It's sunny outside, let's bike!","You've been driving a lot, maybe take a walk","It's probably raining, drive to work!"};
    private GoogleApiClient mGoogleApiClient;

    private int MY_PERMISSION_LOCATION = 1;

    private float temperature;

    private boolean clearconditions = false;
    private boolean rainyconditions = false;
    private boolean snowyconditions = false;

    private double walkingSeconds;
    private double runningSeconds;
    private double drivingSeconds;
    private double bikingSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        walkingSeconds = i.getExtras().getDouble("walking");
        runningSeconds = i.getExtras().getDouble("running");
        bikingSeconds = i.getExtras().getDouble("biking");
        drivingSeconds = i.getExtras().getDouble("driving");
        int k = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        Log.d("GOOGLE AWARE", "" + k + "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        mGoogleApiClient = new GoogleApiClient.Builder(RecommendationsActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        getRecommendations();

        //the adapter takes two lists, one of recommendation strings and the other of icons ids, in that order
        RecommendationAdapter adapter = new RecommendationAdapter(this,
                prgmNameList, prgmImages);

        setListAdapter(adapter);

    }

    private void getRecommendations(){
        if (ActivityCompat.checkSelfPermission(
                RecommendationsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    RecommendationsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
        }



        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            //tv.setText("weather failed");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        float temp = weather.getTemperature(Weather.FAHRENHEIT);
                        temperature = temp;
                        int[] conditions = weather.getConditions();
                        for(int cond : conditions) {
                            if (cond == 1) {
                                clearconditions = true;
                            }
                            if (cond == 6) {
                                rainyconditions = true;
                            }
                            if (cond == 7) {
                                snowyconditions = true;

                            }
                        }

                        //tv.setText(weather + "");
                    }
                });

        if (walkingSeconds > drivingSeconds && temperature < 50) {
            prgmNameList.add("You've been walking a lot, you must be tired!");
            prgmImages.add(R.drawable.ic_time_to_leave_black_36dp);
        }

        if (clearconditions) {
            prgmNameList.add("It's nice out, you should go biking!");
            prgmImages.add(R.drawable.ic_directions_bike_black_36dp);
        }

        if (drivingSeconds > bikingSeconds && clearconditions && temperature > 40) {
            prgmNameList.add("You have been driving a lot, it's nice out, try biking.");
            prgmImages.add(R.drawable.ic_directions_bike_black_36dp);
        }
        if (rainyconditions) {
            prgmNameList.add("It's raining today, drive to work.");
            prgmImages.add(R.drawable.ic_time_to_leave_black_36dp);
        }
        else {
            prgmNameList.add("It's sunny outside, let's bike!");
            prgmImages.add(R.drawable.ic_directions_bike_black_36dp);
            prgmNameList.add("You've been driving a lot, maybe take a walk");
            prgmImages.add(R.drawable.ic_directions_walk_black_36dp);
            prgmNameList.add("It's probably raining, drive to work!");
            prgmImages.add(R.drawable.ic_time_to_leave_black_36dp);

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Checking if failed", result.toString());

    }

    public void continueClick(View v){
        //close activity
        finish();
    }
}
