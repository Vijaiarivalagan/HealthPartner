package com.geforce.vijai.healthpartner.ui.Report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.geforce.vijai.healthpartner.BpReport;
import com.geforce.vijai.healthpartner.CalorieReport;
import com.geforce.vijai.healthpartner.DiabetesReport;
import com.geforce.vijai.healthpartner.R;

public class ReportFragment extends Fragment {

    private ImageView calorie,bp,diabets;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        calorie=(ImageView)root.findViewById(R.id.caloriebtn);
        bp=(ImageView)root.findViewById(R.id.bloodpressurebtn);
        diabets=(ImageView)root.findViewById(R.id.diabetsbtn);

        calorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CalorieReport.class));
            }
        });

        diabets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DiabetesReport.class));
            }
        });

        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BpReport.class));
            }
        });
        return root;
    }
}