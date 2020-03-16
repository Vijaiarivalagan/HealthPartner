package com.geforce.vijai.healthpartner.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.AddFoodDetails;
import com.geforce.vijai.healthpartner.CameraActivity;
import com.geforce.vijai.healthpartner.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

public class VerticleRecyclerViewAdapter extends RecyclerView.Adapter<VerticleRecyclerViewAdapter.VerticleRVViewHolder> {

    Context context;
    ArrayList<VerticleModel> arrayList;

    public VerticleRecyclerViewAdapter(Context context, ArrayList<VerticleModel> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public VerticleRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verticle,parent,false);
        return new VerticleRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VerticleRVViewHolder holder, int position) {
        VerticleModel verticleModel=arrayList.get(position);

        final String title=verticleModel.getSessionTitle();
        ArrayList<HorizontalModel> singleItem=verticleModel.getArrayList();

        ArrayList<HorizontalModel> si=new ArrayList<>();
        for(HorizontalModel hh:singleItem){
            if(hh.getFood_session().equalsIgnoreCase(title))
                si.add(hh);
        }
        holder.textViewTitle.setText(title);
        holder.addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CameraActivity.class);
                //intent.putExtra("session",title);
                context.startActivity(intent);
            }
        });
        HorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter=new HorizontalRecyclerViewAdapter(context,si);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        holder.recyclerView.setAdapter(horizontalRecyclerViewAdapter);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VerticleRVViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView textViewTitle;
        ImageView addFood;
        public VerticleRVViewHolder(@NonNull View itemView) {
        super(itemView);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recyclerView1);
            textViewTitle=(TextView)itemView.findViewById(R.id.titleid);
            addFood=(ImageView)itemView.findViewById(R.id.addfoods);
    }
}
}


