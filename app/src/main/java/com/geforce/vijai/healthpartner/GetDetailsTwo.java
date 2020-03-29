package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDetailsTwo extends AppCompatActivity {

    FirebaseFirestore db;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private List<String> exerList= Arrays.asList("sedentary","lightly","moderately","veryactive","superactive");
    private List<Float> cpdList=Arrays.asList(1.2f,1.375f,1.55f,1.725f,1.9f);

    private float heightValue,weightValue,systolValue,diastolValue,befMealValue,aftMealValue;
    private int ageValue,caloriePerDay;
    private String name,email,genderValue,exerciseValue;
    private RadioButton l1,l2,l3,l4,l5;
    private Button complete;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");


    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";
    private static final String HEIGHT_KEY = "height";
    private static final String WEIGHT_KEY="weight";
    private static final String AGE_KEY="age";
    private static final String GENDER_KEY="gender";
    private static final String EXERCISE_KEY="exercise";
    private static final String CALORIE_KEY="calorie";
    private static final String SYSTOL_KEY="systol";
    private static final String DIASTOL_KEY="diastol";
    private static final String BEFOREMEALS_KEY="beforeMeal";
    private static final String AFTERMEALS_KEY="afterMeal";
    //TO READ DATA FOR PROFILE PAGE
    //https://stackoverflow.com/questions/46706433/firebase-firestore-get-data-from-collection

    // FIRESTORE TUTOTIALS
    // https://dzone.com/articles/cloud-firestore-read-write-update-and-delete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details_two);
        l1=(RadioButton)findViewById(R.id.level1);
        l2=(RadioButton)findViewById(R.id.level2);
        l3=(RadioButton)findViewById(R.id.level3);
        l4=(RadioButton)findViewById(R.id.level4);
        l5=(RadioButton)findViewById(R.id.level5);
        complete=(Button)findViewById(R.id.gcomplete);

        db = FirebaseFirestore.getInstance();
        pref= getSharedPreferences("user",MODE_PRIVATE);

        email=pref.getString("email",null);
        name=pref.getString("name",null);
        heightValue=pref.getFloat("heightValue",0.0f);
        weightValue=pref.getFloat("weightValue",0.0f);
        ageValue=pref.getInt("ageValue",0);
        genderValue=pref.getString("genderValue",null);
        systolValue=pref.getFloat("systolValue",0.0f);
        diastolValue=pref.getFloat("diastolValue",0.0f);
        befMealValue=pref.getFloat("befMealValue",0.0f);
        aftMealValue=pref.getFloat("aftMealValue",0.0f);




        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l1.isChecked()) {
                    exerciseValue="sedentary";
                } else if (l2.isChecked()) {
                    exerciseValue="lightly";
                } else if (l3.isChecked()) {
                    exerciseValue="moderately";
                } else if (l4.isChecked()) {
                    exerciseValue="veryactive";
                } else if (l5.isChecked()) {
                    exerciseValue="superactive";
                }
                caloriePerDay=calcualteRequiredCalorie(heightValue,weightValue,ageValue,genderValue,exerciseValue);
                //Toast.makeText(getApplicationContext(), cpd.toString(), Toast.LENGTH_LONG).show(); // print calorie per day(cpd)
                addNewUser(email,name,heightValue,weightValue,ageValue,genderValue,caloriePerDay,systolValue,diastolValue,befMealValue,aftMealValue,exerciseValue);
                editor = pref.edit();
                editor.putFloat("calorie",caloriePerDay);
                editor.putInt("stepcount",1000);
                editor.putInt("dailyCalorie",0);
                editor.putInt("dailyStepcount",0);
                editor.putString("exercise",exerciseValue);
                editor.putString("beforedate",sdf.format(new Date()));
                editor.commit();


            }
        });
    }


    public int calcualteRequiredCalorie(float height ,float weight,int age, String gender,String exercise){
        float bmr=0.0f;//male =0  , female =1
        if(gender.equalsIgnoreCase("male")){
            // MEN
            //BMR = (10 × weight in kg) + (6.25 × height in cm) − (5 × age in years) + 5
            bmr=(10*weight)+(6.25f*height)-(5*age)+5;
        }
        else if(gender.equalsIgnoreCase("female")){
            // FEMALE
            //BMR = (10 × weight in kg) + (6.25 × height in cm) − (5 × age in years) − 161
            bmr=(10*weight)+(6.25f*height)-(5*age)-161;
        }
        int indexofnewexer=exerList.indexOf(exercise);
        float cpd=cpdList.get(indexofnewexer)*bmr;
        return (int)cpd;

    }

    private void addNewUser(String e,String n,float h,float w,int a,String g,int c,float sys,float dia, float befM, float aftM,String exerciseValue) {
        Map<String, Object> user = new HashMap<>();
        user.put(EMAIL_KEY, e);
        user.put(NAME_KEY,n);
        user.put(HEIGHT_KEY, h);
        user.put(WEIGHT_KEY, w);
        user.put(AGE_KEY, a);
        user.put(GENDER_KEY, g);
        user.put(CALORIE_KEY,c);
        user.put(EXERCISE_KEY,exerciseValue);
        user.put(SYSTOL_KEY, sys);
        user.put(DIASTOL_KEY, dia);
        user.put(BEFOREMEALS_KEY, befM);
        user.put(AFTERMEALS_KEY, aftM);

        db.collection("users").document(e).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "User Registered",
                                Toast.LENGTH_SHORT).show();
                        saveToReport();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }

    private void saveToReport() {
        long d=new Date().getTime();
        if(systolValue!=0.0f && diastolValue!=0.0f){

            Map<String, Object> systolReport = new HashMap<>();
            systolReport.put("date", d);
            systolReport.put("type","systol");
            systolReport.put("value", systolValue);

            Map<String, Object> diastolReport = new HashMap<>();
            diastolReport.put("date", d);
            diastolReport.put("type","diastol");
            diastolReport.put("value", diastolValue);

            addNewReport(email,systolReport,"reportbp");
            addNewReport(email,diastolReport,"reportbp");
        }
        if(befMealValue!=0.0f && aftMealValue!=0.0f){
            Map<String, Object> befMealReport = new HashMap<>();
            befMealReport.put("date", d);
            befMealReport.put("type","beforemeal");
            befMealReport.put("value", befMealValue);

            Map<String, Object> aftMealReport = new HashMap<>();
            aftMealReport.put("date", d);
            aftMealReport.put("type","aftermeal");
            aftMealReport.put("value", aftMealValue);

            addNewReport(email,befMealReport,"reportdiabetes");
            addNewReport(email,aftMealReport,"reportdiabetes");
        }
    }

    public void addNewReport(String email,Map<String,Object> report,String path) {
        System.out.println(path+" "+email+"addnewreports keys");
        db = FirebaseFirestore.getInstance();
        String id=db.collection("reports_others").document(email).collection(path).document().getId();
        db.collection("reports_others").document(email).collection(path).document(id).set(report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Update Report",
                                Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
    }

}

