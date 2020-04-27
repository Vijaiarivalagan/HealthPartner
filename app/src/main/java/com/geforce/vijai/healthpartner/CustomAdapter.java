package com.geforce.vijai.healthpartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import java.util.List;


public class CustomAdapter extends BaseAdapter {
    Context context;
    List<ModelEnergyCount> energyCountList;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, List<ModelEnergyCount> energyCountList) {
        this.context = context;
        this.energyCountList = energyCountList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return energyCountList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_tips_listview, null);
        TextView country = (TextView)convertView.findViewById(R.id.textView);
        country.setText(energyCountList.get(position).getWork()+" "+energyCountList.get(position).getBurn());
        return convertView;
    }
}
