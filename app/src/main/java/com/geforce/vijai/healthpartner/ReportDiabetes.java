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

public class ReportDiabetes extends AppCompatActivity {

    private LineChart beforeMealLineChart,afterMealLineChart;
    private int beforeMealNormal,afterMealNormal;
    LineDataSet beforeMealLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> beforeMealILineDataSet=new ArrayList<>();
    LineData beforeMealLineData;

    LineDataSet afterMealLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> afterMealILineDataSet=new ArrayList<>();
    LineData afterMealLineData;
    private SharedPreferences pref;
    private String email;
    private FirebaseFirestore db;
    SimpleDateFormat sdf=new SimpleDateFormat("dd:MM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_diabetes);

        beforeMealLineChart=(LineChart) findViewById(R.id.beforemealgraph);
        afterMealLineChart=(LineChart) findViewById(R.id.aftermealgraph);

        pref= this.getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        beforeMealNormal=102;
        afterMealNormal=151;
        db= FirebaseFirestore.getInstance();
        getDataFromFirebase();

    }
    private void getDataFromFirebase() {
        Log.d("bef firebase","--------ok");
        db.collection("reports_others").document(email).collection("reportdiabetes").orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<ModelreportDiabetes> ModelreportDiabetes = task.getResult().toObjects(ModelreportDiabetes.class);
                            ArrayList<Entry> beforeMealEntry=new ArrayList<>();
                            ArrayList<Entry> afterMealEntry=new ArrayList<>();

                            for(ModelreportDiabetes cg: ModelreportDiabetes){
                                System.out.println("from model date: "+cg.getDate()+"value: "+cg.getValue()+"type: "+cg.getType());
                                if(cg.getType().equalsIgnoreCase("beforemeal")){
                                    beforeMealEntry.add(new Entry(cg.getDate(),cg.value));
                                }
                                else if(cg.getType().equalsIgnoreCase("aftermeal")){
                                    afterMealEntry.add(new Entry(cg.getDate(),cg.value));
                                }
                            }
                            showBeforeMealChart(beforeMealEntry);
                            showAfterMealChart(afterMealEntry);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    private void showBeforeMealChart(ArrayList<Entry> beforeMealEntry) {
        ValueFormatter dateFormater = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return sdf.format(new Date((long)value));
            }
        };

        LimitLine upper_limit = new LimitLine(beforeMealNormal, "Normal Limit");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);



        XAxis systolXAxis=beforeMealLineChart.getXAxis();
        systolXAxis.setTextSize(10);
        systolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        systolXAxis.setGranularity(1f);
        systolXAxis.setValueFormatter(dateFormater);

        YAxis systolYAxisRight = beforeMealLineChart.getAxisRight();
        systolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = beforeMealLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);


        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        beforeMealLineDataset.setValues(beforeMealEntry);
        beforeMealLineDataset.setColor(Color.BLUE);
        beforeMealLineDataset.setLineWidth(2f);
        beforeMealLineDataset.setLabel("Before Meal data");
        beforeMealLineDataset.setValueTextSize(15);

        beforeMealLineDataset.setCircleRadius(6);
        beforeMealLineDataset.setCircleColor(Color.RED);
        beforeMealILineDataSet.clear();

        beforeMealILineDataSet.add(beforeMealLineDataset);
        beforeMealLineData=new LineData(beforeMealILineDataSet);
        beforeMealLineChart.clear();
        beforeMealLineChart.setData(beforeMealLineData);
        beforeMealLineChart.animateX(500);
        beforeMealLineChart.getDescription().setText("Before Meal");
        beforeMealLineChart.setNoDataText("No data or Loading");
        beforeMealLineChart.invalidate();


    }
    private void showAfterMealChart(ArrayList<Entry> afterMealEntry) {
        ValueFormatter dateFormater = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return sdf.format(new Date((long)value));
            }
        };

        LimitLine upper_limit = new LimitLine(afterMealNormal, "Normal Limit");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        XAxis diastolXAxis=afterMealLineChart.getXAxis();
        diastolXAxis.setTextSize(10);
        diastolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        diastolXAxis.setGranularity(1f);
        diastolXAxis.setValueFormatter(dateFormater);

        YAxis diastolYAxisRight = afterMealLineChart.getAxisRight();
        diastolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = afterMealLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);

        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        afterMealLineDataset.setValues(afterMealEntry);
        afterMealLineDataset.setColor(Color.BLUE);
        afterMealLineDataset.setLineWidth(2f);
        afterMealLineDataset.setLabel("After Meal data");
        afterMealLineDataset.setValueTextSize(15);

        afterMealLineDataset.setCircleRadius(6);
        afterMealLineDataset.setCircleColor(Color.RED);
        afterMealILineDataSet.clear();

        afterMealILineDataSet.add(afterMealLineDataset);
        afterMealLineData=new LineData(afterMealILineDataSet);
        afterMealLineChart.clear();
        afterMealLineChart.setData(afterMealLineData);
        afterMealLineChart.animateX(500);
        afterMealLineChart.getDescription().setText("After Meal ");
        afterMealLineChart.setNoDataText("No data or Loading");
        afterMealLineChart.invalidate();




    }


}
