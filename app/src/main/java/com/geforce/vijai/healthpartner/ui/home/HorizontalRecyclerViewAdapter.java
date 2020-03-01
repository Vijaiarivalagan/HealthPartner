package com.geforce.vijai.healthpartner.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.HomeActivity;
import com.geforce.vijai.healthpartner.R;

import java.util.ArrayList;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorintalRVViewHolder> {

    Context context;
    ArrayList<HorizontalModel> arrayList;
    public HorizontalRecyclerViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }



    @NonNull
    @Override
    public HorintalRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal,parent,false);
        return new HorintalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorintalRVViewHolder holder, int position) {
        HorizontalModel horizontalModel=arrayList.get(position);

            holder.foodname.setText(horizontalModel.getFood());
            holder.calorievalue.setText(String.valueOf(horizontalModel.getCalorie()));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class HorintalRVViewHolder extends RecyclerView.ViewHolder
    {
        TextView foodname,calorievalue;


    public HorintalRVViewHolder(@NonNull View itemView) {
        super(itemView);
        foodname=(TextView)itemView.findViewById(R.id.foodnameid);
        calorievalue=(TextView)itemView.findViewById(R.id.calorievalueid);

    }
}
}
