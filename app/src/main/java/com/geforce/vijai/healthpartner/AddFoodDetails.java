package com.geforce.vijai.healthpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddFoodDetails extends AppCompatActivity {
    private TextView foodName;
    private EditText qty;
    private Button addFoodToList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startActivity(new Intent(this, CameraActivity.class));

        setContentView(R.layout.activity_add_food_details);

        Toast.makeText(getApplicationContext(),getIntent().getStringExtra("filename"),Toast.LENGTH_SHORT).show();
        foodName=(TextView)findViewById(R.id.foodnameid);
        qty=(EditText)findViewById(R.id.qty);
        addFoodToList=(Button)findViewById(R.id.addfoodtolistid);

        addFoodToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private String getsession() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return "BreakFast";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return "Lunch";
        }else if(timeOfDay >= 17 && timeOfDay < 24){
            return "Dinner";
        }
        return null;
    }
}
