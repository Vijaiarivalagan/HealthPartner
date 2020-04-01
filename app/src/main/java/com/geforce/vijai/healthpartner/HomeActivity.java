package com.geforce.vijai.healthpartner;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements SensorEventListener,StepListener  {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private AppBarConfiguration mAppBarConfiguration;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    TextView TvSteps,TvsGoal;
    FirebaseFirestore db;
    String email;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_exerciseid, R.id.nav_tips,
                R.id.nav_report, R.id.nav_profile,R.id.nav_logout, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        pref=getSharedPreferences("user",MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        TvSteps=(TextView)findViewById(R.id.tvstep);
        TvsGoal=(TextView)findViewById(R.id.tvgoal);
        TvsGoal.setText("Goal: "+"10000");

        email=pref.getString("email",null);
        numSteps = 0;

        //for sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(HomeActivity.this);

        numSteps=pref.getInt("dailyStepcount",0);
        TvSteps.setText(String.valueOf(numSteps));


        //save daily calorie and steps

        String oldDateString=pref.getString("beforedate",null);
        Date oldDate= null;
        int daysbetween=0;
        try {
            oldDate = sdf.parse(oldDateString);
            long diff=(new Date().getTime())-(oldDate.getTime());
            daysbetween=(int)(diff / (1000*60*60*24));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(daysbetween>=1){

            saveStepsAndCalorieToDb();
            //after send to db reset the values to zero
            editor = pref.edit();
            editor.putInt("dailyCalorie",0);
            editor.putInt("dailyStepcount",0);
            editor.putString("beforedate",sdf.format(new Date()));
            editor.commit();
            numSteps=0;
        }



        sensorManager.registerListener(HomeActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        System.out.println("mail "+email);
        System.out.println("name "+pref.getString("name",null));
        System.out.println("height "+pref.getFloat("heightValue",0.0f));
        System.out.println("weight "+pref.getFloat("weightValue",0.0f));
        System.out.println("age "+pref.getInt("ageValue",0));
        System.out.println("gender "+pref.getString("genderValue",null));
        System.out.println("systolValue "+pref.getFloat("systolValue",0.0f));
        System.out.println("diastolValue "+pref.getFloat("diastolValue",0.0f));
        System.out.println("befMealValue "+pref.getFloat("befMealValue",0.0f));
        System.out.println("aftMealValue "+pref.getFloat("aftMealValue",0.0f));
        System.out.println("exer "+pref.getString("exercise",null));
        System.out.println("cal "+pref.getInt("dailyCalorie",0));
        System.out.println("cal "+pref.getInt("dailyStepcount",0));


    }

    // save daily calorie & steps to firestore
    private void saveStepsAndCalorieToDb() {

        //save calories to calorie reports
        Map<String,Object> calorieReport=new HashMap<>();
        calorieReport.put("calorie",pref.getInt("dailyCalorie",0));
        calorieReport.put("date",new Date().getTime());
        System.out.println("calorie today"+pref.getInt("dailyCalorie",0));

        String calorieid=db.collection("reports_others").document(email).collection("reportcalorie").document().getId();
        db.collection("reports_others").document(email).collection("reportcalorie").document(calorieid).set(calorieReport).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("calorie report","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                        Log.i("calorie report","failed");
                    }
        });

        //save steps to steps reports
        Map<String,Object> stepsReport=new HashMap<>();
        stepsReport.put("steps",numSteps);
        stepsReport.put("date",new Date().getTime());
        System.out.println("step today"+numSteps);

        String stepsid=db.collection("reports_others").document(email).collection("steps").document().getId();
        db.collection("reports_others").document(email).collection("steps").document(stepsid).set(stepsReport).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("steps report","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("steps report","failed");
            }
        });

    }

    //navigation view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    //step counter
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText("Today: "+numSteps);

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = pref.edit();
        System.out.println(numSteps);
        editor.putInt("dailyStepcount",numSteps);
        editor.commit();

    }
}
