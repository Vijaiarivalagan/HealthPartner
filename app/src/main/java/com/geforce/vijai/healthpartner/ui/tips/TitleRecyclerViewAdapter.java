package com.geforce.vijai.healthpartner.ui.tips;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.geforce.vijai.healthpartner.R;
import com.geforce.vijai.healthpartner.ShowTips;


import java.util.ArrayList;


public class TitleRecyclerViewAdapter extends RecyclerView.Adapter<TitleRecyclerViewAdapter.TitleRVViewHolder> {

    Context context;
    ArrayList<TitleDataModel> arrayList;
    public TitleRecyclerViewAdapter(Context context, ArrayList<TitleDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }



    @NonNull
    @Override
    public TitleRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tips_title,parent,false);
        return new TitleRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TitleRecyclerViewAdapter.TitleRVViewHolder holder, int position) {
        final TitleDataModel Model=arrayList.get(position);

        holder.title.setText(Model.getText());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ShowTips.class);
                i.putExtra("tipspath",Model.getText());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TitleRVViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;

        public TitleRVViewHolder(@NonNull View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.titleTipsId);

        }
    }
}

