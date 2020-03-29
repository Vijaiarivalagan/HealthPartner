package com.geforce.vijai.healthpartner.ui.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.geforce.vijai.healthpartner.GetDetailsTwo;
import com.geforce.vijai.healthpartner.HomeActivity;
import com.geforce.vijai.healthpartner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private EditText nname,nage,nemail,nheight,nweight;
    private Button saveChanges;
    private Spinner nexer,ngender;
    private String newName,newEmail,newExerstring,newGenderString;
    private int newAge,newCaloriePerDay;
    private float newHeight,newWeight;
    private List<String> exerList= Arrays.asList("sedentary","lightly","moderately","veryactive","superactive");
    //private List<String> genderList=Arrays.asList("Male","Female");
    private List<Float> cpdList=Arrays.asList(1.2f,1.375f,1.55f,1.725f,1.9f);
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private FirebaseFirestore db;

    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";
    private static final String HEIGHT_KEY = "height";
    private static final String WEIGHT_KEY="weight";
    private static final String AGE_KEY="age";
    private static final String GENDER_KEY="gender";
    private static final String CALORIE_KEY="calorie";
    private static final String EXERCISE_KEY="exercise";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        db=FirebaseFirestore.getInstance();
        pref= getActivity().getSharedPreferences("user", MODE_PRIVATE);

        nname=(EditText)root.findViewById(R.id.nusername);
        nemail=(EditText)root.findViewById(R.id.nemail);
        nage=(EditText)root.findViewById(R.id.nage);
        nheight=(EditText)root.findViewById(R.id.nheight);
        nweight=(EditText)root.findViewById(R.id.nweight);
        nexer=(Spinner)root.findViewById(R.id.nexer);
        ngender=(Spinner)root.findViewById(R.id.ngender);
        saveChanges=(Button)root.findViewById(R.id.save_values);


        //set old values geted from sharedpref
        setValues();

        //get new values & send to db
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newName=nname.getText().toString();
                //newEmail=nemail.getText().toString();
                newAge=Integer.parseInt(nage.getText().toString());
                newHeight=Float.parseFloat(nheight.getText().toString());
                newWeight=Float.parseFloat(nweight.getText().toString());


                nexer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        newExerstring=nexer.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        nexer.setPrompt("Select appropriate one!!");
                    }
                });

                ngender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        newGenderString=ngender.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        ngender.setPrompt("Select appropriate one!!");
                    }
                });

                if(newExerstring.equalsIgnoreCase(getString(R.string.sedentary)))
                    newExerstring="sedentary";
                else if(newExerstring.equalsIgnoreCase(getString(R.string.lightly)))
                    newExerstring="lightly";
                else if(newExerstring.equalsIgnoreCase(getString(R.string.moderately)))
                    newExerstring="moderately";
                else if(newExerstring.equalsIgnoreCase(getString(R.string.veryactive)))
                    newExerstring="veryactive";
                else if(newExerstring.equalsIgnoreCase(getString(R.string.superactive)))
                    newExerstring="superactive";

                newCaloriePerDay=new GetDetailsTwo().calcualteRequiredCalorie(newHeight,newWeight,newAge,newGenderString,newExerstring);

                editor = pref.edit();
                editor.putFloat("heightValue",newHeight);
                editor.putFloat("weightValue",newWeight);
                editor.putInt("ageValue",newAge);
                editor.putString("genderValue",newGenderString);
                editor.putString("exercise",newExerstring);
                editor.putString("name",newName);
                editor.commit();
                //to sent to firebase db
                updateUser(newEmail,newName,newHeight,newWeight,newAge,newGenderString,newCaloriePerDay,newExerstring);


            }
        });
        return root;
    }


    private void setValues() {

        newEmail=pref.getString("email","email");
        String g=pref.getString("genderValue",null);
        if (g.equalsIgnoreCase("male"))
            ngender.setSelection(0);
        else
            ngender.setSelection(1);
        String oldExer=pref.getString("exercise",null);
        nexer.setSelection(exerList.indexOf("exercise"));

        nname.setText(pref.getString("name",null));
        nemail.setText(newEmail);
        nheight.setText(String.valueOf(pref.getFloat("heightValue",0.0f)));
        nweight.setText(String.valueOf(pref.getFloat("weightValue",0.0f)));
        nage.setText(String.valueOf(pref.getInt("ageValue",0)));

        newGenderString=g;
        newExerstring=oldExer;

    }

    private void updateUser(String email,String name,float height,float weight,int age,String gender,int calorie,String exer) {
        DocumentReference users = db.collection("users").document(email);

        Map<String, Object> user = new HashMap<>();
        user.put(EMAIL_KEY, email);
        user.put(NAME_KEY,name);
        user.put(HEIGHT_KEY, height);
        user.put(WEIGHT_KEY, weight);
        user.put(AGE_KEY, age);
        user.put(GENDER_KEY, gender);
        user.put(CALORIE_KEY,calorie);
        user.put(EXERCISE_KEY,exer);
        System.out.println("profile email: "+email+" name:"+name+" height: "+height+" weight: "+weight+" age: "+age+" gender: "+gender+" calorie: "+calorie+" exer: "+exer);
        users.update(user).addOnSuccessListener(new OnSuccessListener < Void > () {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Profiel update success",
                        Toast.LENGTH_SHORT).show();
                Log.d("Profile update","success");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Profiel update failed",
                                Toast.LENGTH_SHORT).show();
                        Log.d("Profile update","failed");
                    }
                });
           }

}