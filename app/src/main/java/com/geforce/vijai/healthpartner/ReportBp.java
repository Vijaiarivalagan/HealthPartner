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

public class ReportBp extends AppCompatActivity {

    private LineChart systolLineChart,diastolLineChart;
    private int systolnormal,diastolnormal;
    LineDataSet systolLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> systolILineDataSet=new ArrayList<>();
    LineData systolLineData;

    LineDataSet diastolLineDataset=new LineDataSet(null,null);
    ArrayList<ILineDataSet> diastolILineDataSet=new ArrayList<>();
    LineData diastolLineData;

    private SharedPreferences pref;
    private String email;
    private FirebaseFirestore db;
    SimpleDateFormat sdf=new SimpleDateFormat("dd:MM");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bp);

        pref= this.getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        systolnormal=83;
        diastolnormal=103;
        db=FirebaseFirestore.getInstance();

        systolLineChart=(LineChart)findViewById(R.id.systolgraph);
        diastolLineChart=(LineChart)findViewById(R.id.diastolgraph);
        getDataFromFirebase();

    }

    private void getDataFromFirebase() {
        Log.d("bef firebase","--------ok");
        db.collection("reports_others").document(email).collection("reportbp").orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<ModelreportBp> ModelreportBps = task.getResult().toObjects(ModelreportBp.class);
                            ArrayList<Entry> systolEntry=new ArrayList<>();
                            ArrayList<Entry> diastolEntry=new ArrayList<>();

                            for(ModelreportBp cg: ModelreportBps){
                                System.out.println("from model date: "+cg.getDate()+"value: "+cg.getValue()+"type: "+cg.getType());
                                if(cg.getType().equalsIgnoreCase("systol")){
                                    systolEntry.add(new Entry(cg.getDate(),cg.value));
                                }
                                else if(cg.getType().equalsIgnoreCase("diastol")){
                                    diastolEntry.add(new Entry(cg.getDate(),cg.value));
                                }
                            }
                            showSystolChart(systolEntry);
                            showDiastolChart(diastolEntry);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    private void showSystolChart(ArrayList<Entry> systolEntry) {

        ValueFormatter dateFormater = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return sdf.format(new Date((long)value));
            }
        };

        LimitLine upper_limit = new LimitLine(systolnormal, "Normal Limit");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);



        XAxis systolXAxis=systolLineChart.getXAxis();
        systolXAxis.setTextSize(10);
        systolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        systolXAxis.setGranularity(1f);
        systolXAxis.setValueFormatter(dateFormater);

        YAxis systolYAxisRight = systolLineChart.getAxisRight();
        systolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = systolLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);


        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        systolLineDataset.setValues(systolEntry);
        systolLineDataset.setColor(Color.BLUE);
        systolLineDataset.setLineWidth(2f);
        systolLineDataset.setLabel("systol levels");
        systolLineDataset.setValueTextSize(15);

        systolLineDataset.setCircleRadius(6);
        systolLineDataset.setCircleColor(Color.RED);
        systolILineDataSet.clear();

        systolILineDataSet.add(systolLineDataset);
        systolLineData=new LineData(systolILineDataSet);
        systolLineChart.clear();
        systolLineChart.setData(systolLineData);
        systolLineChart.animateX(500);
        systolLineChart.getDescription().setText("Systol Bp ");
        systolLineChart.setNoDataText("No data or Loading");
        systolLineChart.invalidate();

    }

    private void showDiastolChart(ArrayList<Entry> diastolEntry) {
        ValueFormatter dateFormater = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return sdf.format(new Date((long)value));
            }
        };

        LimitLine upper_limit = new LimitLine(diastolnormal, "Normal Limit");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(20f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        XAxis diastolXAxis=diastolLineChart.getXAxis();
        diastolXAxis.setTextSize(10);
        diastolXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        diastolXAxis.setGranularity(1f);
        diastolXAxis.setValueFormatter(dateFormater);

        YAxis diastolYAxisRight = diastolLineChart.getAxisRight();
        diastolYAxisRight.setEnabled(false);

        YAxis yAxisLeft = diastolLineChart.getAxisLeft();
        yAxisLeft.setTextSize(10);
        yAxisLeft.setGranularity(1f);

        yAxisLeft.removeAllLimitLines();
        yAxisLeft.addLimitLine(upper_limit);
        yAxisLeft.enableGridDashedLine(10f, 10f, 0f);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLimitLinesBehindData(true);


        diastolLineDataset.setValues(diastolEntry);
        diastolLineDataset.setColor(Color.BLUE);
        diastolLineDataset.setLineWidth(2f);
        diastolLineDataset.setLabel("diastol levels");
        diastolLineDataset.setValueTextSize(15);

        diastolLineDataset.setCircleRadius(6);
        diastolLineDataset.setCircleColor(Color.RED);
        diastolILineDataSet.clear();

        diastolILineDataSet.add(diastolLineDataset);
        diastolLineData=new LineData(diastolILineDataSet);
        diastolLineChart.clear();
        diastolLineChart.setData(diastolLineData);
        diastolLineChart.animateX(500);

        diastolLineChart.getDescription().setText("Diastol Bp ");
        diastolLineChart.setNoDataText("No data or Loading");
        diastolLineChart.invalidate();


    }

}
