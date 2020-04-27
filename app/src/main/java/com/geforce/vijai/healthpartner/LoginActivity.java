package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.geforce.vijai.healthpartner.ui.home.HorizontalModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginActivity extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private FirebaseFirestore db;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=FirebaseFirestore.getInstance();
        pref= getSharedPreferences("user", MODE_PRIVATE);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // set the view now


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                        getUserDetail();

                                }
                            }
                        });
            }
        });
    }

    private void getUserDetail() {
        db.collection("users").document(inputEmail.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document=task.getResult();
                            if(document !=null){

                                Toast.makeText(getApplicationContext(),"Welcom back "+document.getString("name"),Toast.LENGTH_SHORT);
                                //Toast.makeText(getApplicationContext(),document.getString("gender")+" "+document.getDouble("calorie"),Toast.LENGTH_SHORT);
                                int systolValue,diastolValue,befMealValue,aftMealValue;
                                String pressureRange,diabetesRange;
                                systolValue=document.getLong("systol").intValue();
                                diastolValue=document.getLong("diastol").intValue();
                                befMealValue=document.getLong("beforeMeal").intValue();
                                aftMealValue=document.getLong("afterMeal").intValue();
                                pressureRange=getPressureRange(systolValue,diastolValue);
                                diabetesRange=getDiabetesRange(befMealValue,aftMealValue);
                                editor = pref.edit();
                                editor.putString("email",inputEmail.getText().toString());
                                editor.putString("name",document.getString("name"));
                                editor.putString("genderValue",document.getString("gender"));
                                editor.putInt("calorie",document.getLong("calorie").intValue());
                                editor.putFloat("heightValue",document.getDouble("height").floatValue());
                                editor.putFloat("weightValue",document.getDouble("weight").floatValue());
                                editor.putInt("ageValue",document.getLong("age").intValue());
                                editor.putInt("systolValue",systolValue);
                                editor.putInt("diastolValue",diastolValue);
                                editor.putInt("befMealValue",befMealValue);
                                editor.putInt("aftMealValue",aftMealValue);
                                editor.putString("beforedate",sdf.format(new Date()));
                                editor.putString("pressureRange",pressureRange);
                                editor.putString("diabetesRange",diabetesRange);

                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });
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

}

