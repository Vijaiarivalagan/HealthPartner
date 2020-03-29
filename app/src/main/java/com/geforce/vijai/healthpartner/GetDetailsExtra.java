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
private TextView skip,bpHead,diaHead;
private Button next;
private float systolValue=0.0f,diastolValue=0.0f,befMealValue=0.0f,aftMealValue=0.0f;
    SharedPreferences pref;
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

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();

                editor.putFloat("systolValue",systolValue);
                editor.putFloat("diastolValue",diastolValue);
                editor.putFloat("befMealValue",befMealValue);
                editor.putFloat("aftMealValue",aftMealValue);
                editor.commit();
                Intent i = new Intent(GetDetailsExtra.this, GetDetailsTwo.class);
                startActivity(i);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systolValue=Float.parseFloat(systol.getText().toString());
                diastolValue=Float.parseFloat(diastol.getText().toString());
                befMealValue=Float.parseFloat(befMeal.getText().toString());
                aftMealValue=Float.parseFloat(aftMeal.getText().toString());

                if(systolValue==0.0f || diastolValue==0.0f)
                    bpHead.setError("Either bp values can't be zero");

                if(systolValue==0.0f || diastolValue==0.0f)
                    diaHead.setError("Either diabetes values can't be zero");
                SharedPreferences.Editor editor = pref.edit();
                editor.putFloat("systolValue",systolValue);
                editor.putFloat("diastolValue",diastolValue);
                editor.putFloat("befMealValue",befMealValue);
                editor.putFloat("aftMealValue",aftMealValue);
                editor.commit();

                Intent i = new Intent(GetDetailsExtra.this, GetDetailsTwo.class);
                startActivity(i);
            }
        });


    }
}
