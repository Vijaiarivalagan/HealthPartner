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

public class ReportCalorie extends AppCompatActivity {
    private LineChart calorieLineChart;
    private int calorieNormal;
    LineDataSet calorieLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> calorieILineDataSet=new ArrayList<>();
    LineData calorieLineData;

    private SharedPreferences pref;
    private String email;
    private FirebaseFirestore db;

    //List<ModelreportCalorie> calorieArrayList;
    SimpleDateFormat sdf=new SimpleDateFormat("dd:MM");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_calorie);

        calorieLineChart=(LineChart)findViewById(R.id.caloriegraph);
        Log.d("bef open","--------ok");
        pref= this.getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        calorieNormal=1800;
        db=FirebaseFirestore.getInstance();
        getDataFromFirebase();


    }

    private void getDataFromFirebase() {
        Log.d("bef firebase","--------ok");
        db.collection("reports_others").document(email).collection("reportcalorie").orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<ModelreportCalorie> calorieArrayList = task.getResult().toObjects(ModelreportCalorie.class);
                            ArrayList<Entry> calorieEntry=new ArrayList<>();
                             for(ModelreportCalorie cg:calorieArrayList){
                                 System.out.println("calorie date: "+cg.getDate()+"=="+cg.getCalorie());
                                 calorieEntry.add(new Entry(cg.getDate(),cg.getCalorie()));
                            }
                            showCalorieChart(calorieEntry);
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

        LimitLine upper_limit = new LimitLine(calorieNormal, "Normal Limit");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);



        XAxis systolXAxis=calorieLineChart.getXAxis();
        systolXAxis.setTextSize(10);
        systolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        systolXAxis.setGranularity(1f);
        systolXAxis.setValueFormatter(dateFormater);

        YAxis systolYAxisRight = calorieLineChart.getAxisRight();
        systolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = calorieLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);


        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        calorieLineDataset.setValues(calorieEntry);
        calorieLineDataset.setColor(Color.BLUE);
        calorieLineDataset.setLineWidth(2f);
        calorieLineDataset.setLabel("Past Calories");
        calorieLineDataset.setValueTextSize(10);

        calorieLineDataset.setCircleRadius(6);
        calorieLineDataset.setCircleColor(Color.RED);
        calorieILineDataSet.clear();

        calorieILineDataSet.add(calorieLineDataset);
        calorieLineData=new LineData(calorieILineDataSet);
        calorieLineChart.clear();
        calorieLineChart.setData(calorieLineData);
        calorieLineChart.animateX(500);
        calorieLineChart.getDescription().setText("Calorie Report");
        calorieLineChart.setNoDataText("No calorie values OR Loading");
        calorieLineChart.invalidate();



    }


}
