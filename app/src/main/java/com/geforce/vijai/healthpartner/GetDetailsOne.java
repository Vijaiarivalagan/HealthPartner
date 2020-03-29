package com.geforce.vijai.healthpartner;
// GET height, weight, age, gender
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class GetDetailsOne extends AppCompatActivity {

    //private FirebaseAuth auth;
    SharedPreferences pref;
    EditText height,weight,age;
    Spinner gender;
    Button next;
    String genderValue;
    float heightValue,weightValue;
    byte ageValue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_details_one);

        //Get Firebase auth instance
        //auth = FirebaseAuth.getInstance();
        pref= getSharedPreferences("user", MODE_PRIVATE);

        height=(EditText)findViewById(R.id.gheight);
        weight=(EditText)findViewById(R.id.gweight);
        age=(EditText)findViewById(R.id.gage);
        gender=(Spinner) findViewById(R.id.ggender);
        next=(Button) findViewById(R.id.gnext);


        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                genderValue=gender.getSelectedItem().toString();
                //male =0  , female =1
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
             gender.setPrompt("Gender value is needed!!");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightValue=Float.valueOf(height.getText().toString());
                weightValue=Float.valueOf(weight.getText().toString());
                ageValue=Byte.valueOf(age.getText().toString());
                if (genderValue.equalsIgnoreCase("male") || genderValue.equalsIgnoreCase("femal"))
                    if (heightValue > 0.0f && weightValue > 0.0f && ageValue > 0) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putFloat("heightValue",heightValue);
                        editor.putFloat("weightValue",weightValue);
                        editor.putInt("ageValue",ageValue);
                        editor.putString("genderValue",genderValue);
                        editor.commit();

                        Log.i("getdetail---1--------",heightValue+" "+weightValue+" "+ageValue+" "+genderValue);
                        Intent i = new Intent(GetDetailsOne.this, GetDetailsExtra.class);
                        startActivity(i);
                        finish();
                    }
                if(heightValue<=0.0f)
                    height.setError("Invalid height");
                if(weightValue<=0.0f)
                    weight.setError("Invalid height");
                if(ageValue<=0)
                    age.setError("Invalid height");

            }
        });


    }
}
