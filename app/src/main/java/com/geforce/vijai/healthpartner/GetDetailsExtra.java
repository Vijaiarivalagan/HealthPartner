package com.geforce.vijai.healthpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GetDetailsExtra extends AppCompatActivity {
private EditText systol,diastol,befMeal,aftMeal;
private TextView skip,bpHead,diaHead,pressuerrangetext,diabetesrangetext;
private Button next;
private int systolValue=0,diastolValue=0,befMealValue=0,aftMealValue=0;
private String pressureRange,diabetesRange;
private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details_extra);

        pref= getSharedPreferences("user", MODE_PRIVATE);

        bpHead=(TextView)findViewById(R.id.bp_heading);
        diaHead=(TextView)findViewById(R.id.diabetes_heading);
        skip=(TextView)findViewById(R.id.skip);
        next=(Button)findViewById(R.id.gnext);
        systol=(EditText)findViewById(R.id.gsystol);
        diastol=(EditText)findViewById(R.id.gdiastol);
        befMeal=(EditText)findViewById(R.id.gbeforeMeal);
        aftMeal=(EditText)findViewById(R.id.gaftermeal);
        pressuerrangetext=(TextView)findViewById(R.id.pressurerangetext);
        diabetesrangetext=(TextView)findViewById(R.id.diabetesrangetext);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putInt("systolValue",systolValue);
                editor.putInt("diastolValue",diastolValue);
                editor.putInt("befMealValue",befMealValue);
                editor.putInt("aftMealValue",aftMealValue);
                editor.apply();
                Intent i = new Intent(GetDetailsExtra.this, GetDetailsTwo.class);
                startActivity(i);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systolValue=Integer.parseInt(systol.getText().toString());
                diastolValue=Integer.parseInt(diastol.getText().toString());
                befMealValue=Integer.parseInt(befMeal.getText().toString());
                aftMealValue=Integer.parseInt(aftMeal.getText().toString());

                if(systolValue==0 || diastolValue==0)
                {
                    bpHead.setError("Either bp values can't be zero");
                    return;
                }


                if(befMealValue==0 || aftMealValue==0)
                {
                    diaHead.setError("Either diabetes values can't be zero");
                    return;
                }

                //Pressure range calculator
                pressureRange=getPressureRange(systolValue,diastolValue);
                showPressureRange(pressureRange);


                diabetesRange=getDiabetesRange(befMealValue,aftMealValue);
                showDiabetesRange(diabetesRange);

                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("systolValue",systolValue);
                editor.putInt("diastolValue",diastolValue);
                editor.putString("pressureRange",pressureRange);

                editor.putInt("befMealValue",befMealValue);
                editor.putInt("aftMealValue",aftMealValue);
                editor.putString("diabetesRange",diabetesRange);
                editor.apply();

                Intent i = new Intent(GetDetailsExtra.this, GetDetailsTwo.class);
                startActivity(i);
            }
        });


    }

    // diabetes get and show ranges
    private String getDiabetesRange(int befMealValue, int aftMealValue) {
        String range="";

        if(befMealValue<=69 && aftMealValue <= 100){
            range="low";
        }
        else if((befMealValue>=70 && befMealValue<=130) && (aftMealValue <=180))
        {
            range="normal";
        }
        else if(befMealValue>=131 && aftMealValue>=181){
            range="high";
        }
        return range;
    }
    private void showDiabetesRange(String range) {
        if(range.equalsIgnoreCase("normal")){
            diabetesrangetext.setText(getResources().getString(R.string.normal_db));
            diabetesrangetext.setTextColor(getResources().getColor(R.color.rangenormal));
        }
        else if(range.equalsIgnoreCase("low") ){
            diabetesrangetext.setText(getResources().getString(R.string.low_db));
            diabetesrangetext.setTextColor(getResources().getColor(R.color.rangelow_high));
        }
        else if(range.equalsIgnoreCase("High") ){
            diabetesrangetext.setText(getResources().getString(R.string.high_db));
            diabetesrangetext.setTextColor(getResources().getColor(R.color.rangelow_high));
        }
    }




    //pressures get and show ranges
    public String getPressureRange(int systolValue,int diastolValue) {
        String range="";
         if ((systolValue >= 60 && systolValue <= 109) && (diastolValue >= 40 && diastolValue <= 74)) {
            // blood pressure Low level
            range = "low";
        }
        else if ((systolValue >= 110 && systolValue <= 135) && (diastolValue >= 75 && diastolValue <= 85)) {
            // blood pressure Normal level
             range = "normal";
        }  else if ((systolValue >= 136 && systolValue <= 210) && (diastolValue >= 86 && diastolValue <= 120)) {
            // blood pressure High level
             range = "high";
        }
        return range;
    }
    public void showPressureRange(String  range){
        if(range.equalsIgnoreCase("normal")){
            pressuerrangetext.setText(getResources().getString(R.string.normal_bp));
            pressuerrangetext.setTextColor(getResources().getColor(R.color.rangenormal));
        }
        else if(range.equalsIgnoreCase("low") ){
            pressuerrangetext.setText(getResources().getString(R.string.low_bp));
            pressuerrangetext.setTextColor(getResources().getColor(R.color.rangelow_high));
        }
        else if(range.equalsIgnoreCase("High") ){
            pressuerrangetext.setText(getResources().getString(R.string.high_bp));
            pressuerrangetext.setTextColor(getResources().getColor(R.color.rangelow_high));
        }
    }





}
