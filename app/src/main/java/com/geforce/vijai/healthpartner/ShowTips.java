package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShowTips extends AppCompatActivity {

    private FirebaseFirestore db;
    private SharedPreferences pref;
    LinearLayout bpLinearLayout,sugarLinearLayout;
    private List<String> tipsDataList;
    String path;
    ListView simpleList;
    Button lowbp,highbp,lowsugar,highsugar;
    String[] simple={"SYMPTOMS:.. Early symptoms include:..  Confusion. Dizziness. Feeling shaky. Hunger. Headaches. Irritability. Pounding heart; racing pulse. Pale skin. Sweating. Trembling. Weakness. Anxiety..  Without treatment, you might get more severe symptoms, including:..  Poor coordination. Poor concentration. Numbness in mouth and tongue. Passing out. Seizures. Nightmares or bad dreams. Coma..",
            "CAUSE:.. skipping meals and snacks. not eating enough food during a meal or snack. exercising longer or harder than usual without eating some extra food. getting too much insulin. not timing the insulin doses properly with meals, snacks, and exercise.",
    " TIPS:..  When your blood sugar level drops below 70 (mg/dL), you will usually have symptoms of low blood sugar. This can develop quickly, in 10 to 15 minutes. occurs when a diabetic has not eaten enough food, or has too much insulin within his or her body. If happened you may feel tired, anxious, weak, shaky, or sweaty, and you may have a rapid heart rate. An excessive amount of exercise can also cause low blood sugar levels. If your blood sugar level continues to drop (usually below 40 mg/dL), your behavior may change, and you may feel more irritable. You may become too weak or confused to eat something with sugar to raise your blood sugar level. Anytime your blood sugar drops below 50 mg/dL, you should act whether you have symptoms or not. If your blood sugar level drops very low (usually below 20 mg/dL), you may lose consciousness or have a seizure. if your blood sugar level has been higher than 300 mg/dL for a week or so and the level drops suddenly to 100 mg/dL, you may have symptoms of low blood sugar even though your blood sugar is in the target range."};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tips);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        path = intent.getStringExtra("tipspath");
        simpleList = (ListView) findViewById(R.id.simpleListView);
        bpLinearLayout = (LinearLayout) findViewById(R.id.bpLinearLayuot);
        sugarLinearLayout = (LinearLayout) findViewById(R.id.diabetesLinearLayuot);
        lowbp = (Button) findViewById(R.id.lowbp);
        highbp = (Button) findViewById(R.id.highbp);
        lowsugar = (Button) findViewById(R.id.lowsugar);
        highsugar = (Button) findViewById(R.id.highsugar);

        if (path.equalsIgnoreCase("Common")) {

        } else if (path.equalsIgnoreCase("Energy count")) {
            getEnergyData("energycount", "calorieburn", "energytips");// energycount calorieburn

        }

        else if (path.equalsIgnoreCase("Weight")) {

        } else if (path.equalsIgnoreCase("Meal planner")) {

        } else if (path.equalsIgnoreCase("Blood pressure")) {

            simpleList.setVisibility(View.GONE);
            bpLinearLayout.setVisibility(View.VISIBLE);

            lowbp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleList.setVisibility(View.VISIBLE);
                    bpLinearLayout.setVisibility(View.GONE);
                    getTips("bloodpressure", "tips", "bptips", "lowbp");


                }
            });

            highbp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleList.setVisibility(View.VISIBLE);
                    bpLinearLayout.setVisibility(View.GONE);
                    getTips("bloodpressure", "tips", "bptips", "highbp");
                }
            });

        }
        //sugar
        else if (path.equalsIgnoreCase("Blood sugar")) {

            simpleList.setVisibility(View.GONE);
            sugarLinearLayout.setVisibility(View.VISIBLE);

            lowsugar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTips("diabetes", "tips", "sugartips", "lowsugar");
                    simpleList.setVisibility(View.VISIBLE);
                    sugarLinearLayout.setVisibility(View.GONE);

                }
            });

            highsugar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleList.setVisibility(View.VISIBLE);
                    sugarLinearLayout.setVisibility(View.GONE);
                    getTips("diabetes", "tips", "sugartips", "highsugar");
                }
            });
        }

    }

    private void getTips(String doc1, String subCol1, String subdoc1, final String field) {
        //simpleList.setVisibility(View.VISIBLE);
        db.collection("healthtips").document(doc1).collection(subCol1)
                .document(subdoc1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                tipsDataList = (List<String>) document.get(field);
                                String[] tipsArray=new String[tipsDataList.size()];
                                tipsArray=tipsDataList.toArray(tipsArray);
                                for(int i=0;i<tipsArray.length;i++){
                                    tipsArray[i]=tipsArray[i].replaceAll("/n","\n");
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowTips.this, R.layout.item_tips_listview, R.id.textView,tipsArray);
                                simpleList.setAdapter(arrayAdapter);

                            }

                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed getting documents: ", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getEnergyData(String doc1,String subCol1,String doc2) {

        db.collection("healthtips").document(doc1).collection(subCol1)
                .document(doc2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                tipsDataList = (List<String>) document.get("burnlist");
                                String[] tipsArray=new String[tipsDataList.size()];
                                tipsArray=tipsDataList.toArray(tipsArray);
                               /* for(int i=0;i<tipsArray.length;i++){
                                    tipsArray[i]=tipsArray[i].replaceAll("are","\n");
                                }*/
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowTips.this, R.layout.item_tips_listview, R.id.textView,tipsArray);
                                simpleList.setAdapter(arrayAdapter);

                            }

                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed getting documents: ", Toast.LENGTH_SHORT).show();
            }
        });


    }
}

/*
    // This used for get array values
    * db.collection("admin").document("oilstocks").collection("purchasedoilproducts")
                .document("usedoilproducts").get().
        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        brands = (List<String>) document.get("brands");
                        brands.add("others");
                        grades = (List<String>) document.get("grades");
                        grades.add("others");
                        sizes = (List<Object>) document.get("sizes");
                        sizes.add("others");

                        loadingbar.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);

                        setAdapter(brands,grades,sizes);
                    }

                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                    Toast.makeText(getApplicationContext(), "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        })
*/