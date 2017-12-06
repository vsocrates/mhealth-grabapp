package com.example.dinabenayad_cherif.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vsocrates on 12/5/17.
 */

public class RecommendationAdapter extends BaseAdapter {

    String [] recommendations;
    Context context;
    int [] images;
    private static LayoutInflater inflater=null;

    public RecommendationAdapter(RecommendationsActivity activity, String[] aRecommendations, int[] aIcons){
        recommendations = aRecommendations;
        context = activity;
        images = aIcons;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recommendations.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder = new Holder();

        View rowView;
        rowView = inflater.inflate(R.layout.recommendation_tile, null);

        holder.tv= rowView.findViewById(R.id.recommendation_text);
        holder.img= rowView.findViewById(R.id.recommendation_icon);

        holder.tv.setText(recommendations[position]);
        holder.img.setImageResource(images[position]);

        return rowView;
    }
}
