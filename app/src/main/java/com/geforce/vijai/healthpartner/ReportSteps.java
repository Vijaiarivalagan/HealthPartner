package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportSteps extends AppCompatActivity {
    private LineChart stepLineChart;
    private int stepNormal;
    LineDataSet stepLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> stepILineDataSet=new ArrayList<>();
    LineData stepLineData;

    private SharedPreferences pref;
    private String email;
    private FirebaseFirestore db;

    //List<ModelreportCalorie> calorieArrayList;
    SimpleDateFormat sdf=new SimpleDateFormat("dd:MM");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_steps);

        stepLineChart=(LineChart)findViewById(R.id.stepGraph);
        Log.d("bef open","--------ok");
        pref= this.getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        stepNormal=1800;
        db=FirebaseFirestore.getInstance();
        getDataFromFirebase();

    }


    private void getDataFromFirebase() {
        Log.d("bef firebase","--------ok");
        db.collection("reports_others").document(email).collection("steps").orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<ModelreportSteps> stepsArrayList = task.getResult().toObjects(ModelreportSteps.class);
                            ArrayList<Entry> stepsEntry=new ArrayList<>();
                            for(ModelreportSteps cg:stepsArrayList){
                                System.out.println("step date: "+cg.getDate()+"=="+cg.getSteps());
                                stepsEntry.add(new Entry(cg.getDate(),cg.getSteps()));
                            }
                            showCalorieChart(stepsEntry);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    private void showCalorieChart(ArrayList<Entry> calorieEntry)
    {

        ValueFormatter dateFormater = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return sdf.format(new Date((long)value));
            }
        };

        LimitLine upper_limit = new LimitLine(stepNormal, "Req. steps");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);



        XAxis systolXAxis=stepLineChart.getXAxis();
        systolXAxis.setTextSize(10);
        systolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        systolXAxis.setGranularity(1f);
        systolXAxis.setValueFormatter(dateFormater);

        YAxis systolYAxisRight = stepLineChart.getAxisRight();
        systolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = stepLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);


        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        stepLineDataset.setValues(calorieEntry);
        stepLineDataset.setColor(Color.BLUE);
        stepLineDataset.setLineWidth(2f);
        stepLineDataset.setLabel("Past Calories");
        stepLineDataset.setValueTextSize(10);

        stepLineDataset.setCircleRadius(6);
        stepLineDataset.setCircleColor(Color.RED);
        stepILineDataSet.clear();

        stepILineDataSet.add(stepLineDataset);
        stepLineData=new LineData(stepILineDataSet);
        stepLineChart.clear();
        stepLineChart.setData(stepLineData);
        stepLineChart.animateX(500);
        stepLineChart.getDescription().setText("Steps Report");
        stepLineChart.setNoDataText("No steps OR Loading");
        stepLineChart.invalidate();



    }


}
