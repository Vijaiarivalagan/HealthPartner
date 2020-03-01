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

import java.util.HashMap;
import java.util.Map;

public class GetDetailsTwo extends AppCompatActivity {

    FirebaseFirestore db;
    SharedPreferences pref;
    float heightValue,weightValue;
    int ageValue,caloriePerDay;
    String name,email,genderValue;
    RadioButton l1,l2,l3,l4,l5;
    Button complete;
    float bmr,cpd;

    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";
    private static final String HEIGHT_KEY = "height";
    private static final String WEIGHT_KEY="weight";
    private static final String AGE_KEY="age";
    private static final String GENDER_KEY="gender";
    private static final String CALORIE_KEY="calorie";
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
        pref= getSharedPreferences("user", MODE_PRIVATE);

        email=pref.getString("email",null);
        name=pref.getString("name",null);
        heightValue=pref.getFloat("heightValue",0.0f);
        weightValue=pref.getFloat("weightValue",0.0f);
        ageValue=pref.getInt("ageValue",0);
        genderValue=pref.getString("genderValue",null);

        bmr=calcualteBmr(heightValue,weightValue,ageValue,genderValue);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l1.isChecked()) {
                     cpd=1.2f*bmr;
                } else if (l2.isChecked()) {
                    cpd=1.375f*bmr;
                } else if (l3.isChecked()) {
                   cpd=1.55f*bmr;
                } else if (l4.isChecked()) {
                    cpd=1.725f*bmr;
                } else if (l5.isChecked()) {
                    cpd=1.9f*bmr;
                }
                caloriePerDay=(int)cpd;
                //Toast.makeText(getApplicationContext(), cpd.toString(), Toast.LENGTH_LONG).show(); // print calorie per day(cpd)
                SharedPreferences.Editor editor = pref.edit();
                editor.putFloat("calorie",caloriePerDay);
                editor.commit();

                addNewUser(email,name,heightValue,weightValue,ageValue,genderValue,caloriePerDay);
            }
        });
    }
    public float calcualteBmr(float h,float w,int age, String gender){
        float result=0.0f;//male =0  , female =1
        if(gender.equalsIgnoreCase("male")){
            // MEN
            //BMR = (10 × weight in kg) + (6.25 × height in cm) − (5 × age in years) + 5
            result=(10*w)+(6.25f*h)-(5*age)+5;
        }
        else if(gender.equalsIgnoreCase("female")){
            // FEMALE
            //BMR = (10 × weight in kg) + (6.25 × height in cm) − (5 × age in years) − 161
            result=(10*w)+(6.25f*h)-(5*age)-161;
        }
        return result;
    }

    private void addNewUser(String e,String n,float h,float w,int a,String g,int c) {
        Map<String, Object> user = new HashMap<>();
        user.put(EMAIL_KEY, e);
        user.put(NAME_KEY,n);
        user.put(HEIGHT_KEY, h);
        user.put(WEIGHT_KEY, w);
        user.put(AGE_KEY, a);
        user.put(GENDER_KEY, g);
        user.put(CALORIE_KEY, c);

        db.collection("users").document(e).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "User Registered",
                                Toast.LENGTH_SHORT).show();
                        Log.i("getdetail---2--------",heightValue+" "+weightValue+" "+ageValue+" "+genderValue+" "+cpd);
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
}
