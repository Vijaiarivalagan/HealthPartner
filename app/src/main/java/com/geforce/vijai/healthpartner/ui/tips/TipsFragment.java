package com.geforce.vijai.healthpartner.ui.tips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.MainActivity;
import com.geforce.vijai.healthpartner.R;
import com.geforce.vijai.healthpartner.ui.home.VerticleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class TipsFragment extends Fragment {

    TitleRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    private  ArrayList<TitleDataModel> titleDataModelArrayList;

    ArrayList personNames = new ArrayList<>(Arrays.asList("Person 1", "Person 2", "Person 3", "Person 4", "Person 5", "Person 6", "Person 7","Person 8", "Person 9", "Person 10", "Person 11", "Person 12", "Person 13", "Person 14"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tips, container, false);


        titleDataModelArrayList=new ArrayList<>();

        recyclerView= (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false));
        adapter=new TitleRecyclerViewAdapter(getActivity(),titleDataModelArrayList);
        recyclerView.setAdapter(adapter);

        setData();

        return root;
    }

    private void setData() {
        String[] arr={"Common","Energy count","Weight","Meal planner","Blood pressure","Blood sugar"};
        for(int i=0;i<arr.length;i++){
            titleDataModelArrayList.add(new TitleDataModel(arr[i]));
        }
        adapter.notifyDataSetChanged();
    }
}