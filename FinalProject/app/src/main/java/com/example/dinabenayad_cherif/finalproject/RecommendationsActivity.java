package com.example.dinabenayad_cherif.finalproject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;

public class RecommendationsActivity extends ListActivity {

    public static int [] prgmImages={R.drawable.ic_directions_bike_black_36dp,R.drawable.ic_directions_walk_black_36dp,R.drawable.ic_time_to_leave_black_36dp};
    public static String [] prgmNameList={"It's sunny outside, let's bike!","You've been driving a lot, maybe take a walk","It's probably raining, drive to work!"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        //the adapter takes two lists, one of recommendation strings and the other of icons ids, in that order
        RecommendationAdapter adapter = new RecommendationAdapter(this,
                prgmNameList, prgmImages);

        setListAdapter(adapter);

    }

    public void continueClick(View v){
        //close activity
        finish();
    }
}
