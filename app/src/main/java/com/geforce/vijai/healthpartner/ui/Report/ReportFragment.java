package com.geforce.vijai.healthpartner.ui.Report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.geforce.vijai.healthpartner.ReportBp;
import com.geforce.vijai.healthpartner.ReportCalorie;
import com.geforce.vijai.healthpartner.ReportDiabetes;
import com.geforce.vijai.healthpartner.R;
import com.geforce.vijai.healthpartner.ReportSteps;

public class ReportFragment extends Fragment {

    private ImageView calorie,bp,diabets,steps;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_report, container, false);
        calorie=(ImageView)root.findViewById(R.id.caloriebtn);
        bp=(ImageView)root.findViewById(R.id.bloodpressurebtn);
        diabets=(ImageView)root.findViewById(R.id.diabetsbtn);
        steps=(ImageView)root.findViewById(R.id.stepbtn);
        calorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReportCalorie.class));
            }
        });

        diabets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReportDiabetes.class));
            }
        });

        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReportBp.class));
            }
        });
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReportSteps.class));
            }
        });
        return root;
    }
}