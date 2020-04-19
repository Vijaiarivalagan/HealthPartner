package com.geforce.vijai.healthpartner.ui.home;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.GetDetailsTwo;
import com.geforce.vijai.healthpartner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private SharedPreferences pref;

    SharedPreferences.Editor editor;
    private RecyclerView verticleRecyclerView;
    private VerticleRecyclerViewAdapter adapter;
    private ArrayList<VerticleModel> arrayListVerticle;
    private List<HorizontalModel> horilist;

    private List<String> exerList= Arrays.asList("sedentary","lightly","moderately","veryactive","superactive");
    private List<Float> cpdList=Arrays.asList(1.2f,1.375f,1.55f,1.725f,1.9f);

    private String email;
    private TextView totaltv;
    private ImageButton addexer,updateBp,updateDiabetes,updateHeight,updateweight;
    private ProgressBar Pb;
    Date date;
    int calorie,c;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    private static final String BP_KEY="reportbp";
    private static final String DIABETES_KEY="reportdiabetes";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);



        db=FirebaseFirestore.getInstance();
        pref= this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        calorie=(int)pref.getFloat("calorie",0);

        Pb=(ProgressBar)root.findViewById(R.id.breakfastpb);
        addexer=(ImageButton)root.findViewById(R.id.exerId);
        updateBp=(ImageButton)root.findViewById(R.id.updatebp);
        updateDiabetes=(ImageButton)root.findViewById(R.id.updatesugar);
        updateHeight=(ImageButton)root.findViewById(R.id.updateheight);
        totaltv=(TextView)root.findViewById(R.id.totaltvid);

        totaltv.setText(c+"/"+calorie);

        Pb.setMax(calorie);



        arrayListVerticle=new ArrayList<>();

        verticleRecyclerView= (RecyclerView) root.findViewById(R.id.recyclerView);
        verticleRecyclerView.setHasFixedSize(true);
        verticleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        adapter=new VerticleRecyclerViewAdapter(getActivity(),arrayListVerticle);

        verticleRecyclerView.setAdapter(adapter);


        date= new Date();
        String datestring=sdf.format(date);
        db.collection("calories").document(email).collection(datestring)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Convert the whole snapshot to a POJO list
                            Log.d("error",task.toString());

                            horilist = task.getResult().toObjects(HorizontalModel.class);
                            setData(horilist);
                            setProgress(horilist);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });

        updateBp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBpValue();
            }
        });
        updateDiabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDiabetesValue(); }});
        updateHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHeightWeightValue();
            }
        });

        /*addexer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min=Integer.parseInt(getexer.getText().toString());
                tvexer.setText(String.valueOf(min*2));
            }
        });*/
        /*for(HorizontalModel hh:horilist){
            c+=hh.getCalorie();
            if(hh.getFood_session().equalsIgnoreCase("BreakFast")){
                breakfast+=hh.getCalorie();
            }
            else if(hh.getFood_session().equalsIgnoreCase("Lunch")){
                lunch+=hh.getCalorie();
            }
            else if(hh.getFood_session().equalsIgnoreCase("Dinner")){
                dinner+=hh.getCalorie();
            }
        }
        Pb.setProgress(c);
        totaltv.setText(c+"/"+calorie);
*/

        return root;
    }

    public void setProgress(List<HorizontalModel> horilist1){
        for(HorizontalModel hh:horilist) {
            c += hh.getCalorie();
        }
        Pb.setProgress(c);
        totaltv.setText(c+"/"+calorie);
        editor = pref.edit();
        editor.putInt("dailyCalorie",c);
        editor.commit();
        c=0;
    }

    public void setData(List<HorizontalModel> horilist1){
        for(int i=1;i<=3;i++){
            String[] ar={"BreakFast","Lunch","Dinner"};
            VerticleModel verticleModel =new VerticleModel();
            verticleModel.setSessionTitle(ar[i-1]);
            ArrayList<HorizontalModel> arrayList=new ArrayList<>();

            for(HorizontalModel h:horilist1){

                arrayList.add(h);
            }

            verticleModel.setArrayList(arrayList);
            arrayListVerticle.add(verticleModel);
        }
        adapter.notifyDataSetChanged();
    }



    private void updateBpValue() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.two_et_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setIcon(R.drawable.redi_bp);
        alertDialogBuilder.setTitle("Update Bp value");
        final EditText systolET = (EditText)promptsView.findViewById(R.id.twoEtDiaEt1);
        final EditText diastolET = (EditText)promptsView.findViewById(R.id.twoEtDiaEt2);
        final Button cancel=(Button)promptsView.findViewById(R.id.twoEtDiaCancel);
        final Button ok=(Button)promptsView.findViewById(R.id.twoEtDiaOk);
        final TextView errText=(TextView)promptsView.findViewById(R.id.twoEtDiaerrText);
        systolET.setHint("Systol Bp.");
        diastolET.setHint("Diastol Bp.");

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float v1=0.0f,v2=0.0f;
                try {
                    v1=Float.valueOf(systolET.getText().toString());
                    v2=Float.valueOf(diastolET.getText().toString());
                }catch (NumberFormatException e){
                    errText.setText(getString(R.string.bpExcdigit4));
                    return;
                }

                if(systolET.getText().length()<=3 && diastolET.getText().length()<=3){
                    sendToDb(v1,v2,BP_KEY);
                        editor = pref.edit();
                        editor.putFloat("systolValue", v1);
                        editor.putFloat("diastolValue", v2);
                        editor.commit();
                        updateUserProfile("systol",v1, "diastol", v2);
                        alertDialog.cancel();

                }
                else{
                    errText.setText(getString(R.string.bpErrdigit4));
                    return;
                }

            }
        });


    }


    private void updateDiabetesValue(){

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.two_et_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setIcon(R.drawable.redi_sugar);
        alertDialogBuilder.setTitle("Update Diabetes value");
        final EditText beforeMealEt = (EditText)promptsView.findViewById(R.id.twoEtDiaEt1);
        final EditText afterMealET = (EditText)promptsView.findViewById(R.id.twoEtDiaEt2);
        final Button cancel=(Button)promptsView.findViewById(R.id.twoEtDiaCancel);
        final Button ok=(Button)promptsView.findViewById(R.id.twoEtDiaOk);
        final TextView errText=(TextView)promptsView.findViewById(R.id.twoEtDiaerrText);

        beforeMealEt.setHint("Before taking meals");
        afterMealET.setHint("After taking meals");

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float v1=0.0f,v2=0.0f;
                try {
                    v1 = Float.parseFloat(beforeMealEt.getText().toString());
                    v2 = Float.parseFloat(afterMealET.getText().toString());
                }catch (NumberFormatException e){
                    errText.setText(getString(R.string.bpExcdigit4));
                    return;
                }

                if(beforeMealEt.getText().length()<=3 && afterMealET.getText().length()<=3){
                    sendToDb(v1,v2,DIABETES_KEY);
                    editor = pref.edit();
                    editor.putFloat("befMealValue", v1);
                    editor.putFloat("aftMealValue", v2);
                    editor.commit();
                    updateUserProfile("beforeMeal",v1, "afterMeal", v2);
                    alertDialog.cancel();
                }
                else{
                    errText.setText(getString(R.string.dbErrdigit4));
                    return;
                }

            }
        });
    }


    private void updateHeightWeightValue(){

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.two_et_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setIcon(R.drawable.user_prof);
        alertDialogBuilder.setTitle("Update Height & Weight");
        final EditText heightET = (EditText)promptsView.findViewById(R.id.twoEtDiaEt1);
        final EditText weightET = (EditText)promptsView.findViewById(R.id.twoEtDiaEt2);
        final Button cancel=(Button)promptsView.findViewById(R.id.twoEtDiaCancel);
        final Button ok=(Button)promptsView.findViewById(R.id.twoEtDiaOk);
        final TextView errText=(TextView)promptsView.findViewById(R.id.twoEtDiaerrText);


        heightET.setHint("Height");
        weightET.setHint("Weight");

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float v1=0.0f,v2=0.0f;
                try {
                    v1=Float.parseFloat(heightET.getText().toString());
                    v2=Float.parseFloat(weightET.getText().toString());
                }catch (NumberFormatException e){
                    errText.setText(getString(R.string.bpExcdigit4));
                    return;
                }
                if(heightET.getText().length()<=3 && weightET.getText().length()<=3){

                    int ageValue=pref.getInt("ageValue",0);
                    String genderValue=pref.getString("genderValue",null);
                    String exerciseValue=pref.getString("exercise",null);

                    int caloriePerDay=new GetDetailsTwo().calcualteRequiredCalorie(v1,v2,ageValue,genderValue,exerciseValue);

                    updateCalorieProfile("height",v1, "weight", v2,"calorie",caloriePerDay);

                    editor = pref.edit();
                    editor.putFloat("heightValue", v1);
                    editor.putFloat("weightValue", v2);
                    editor.putFloat("calorie",caloriePerDay);
                    editor.commit();

                    alertDialog.cancel();
                }
                else{
                    errText.setText(getString(R.string.HeiWeierrdigit4));
                    return;
                }


            }
        });
    }


    private void sendToDb(float value1,float value2,String path){


        Date date=new Date();
        long d=date.getTime();
        if(path.equalsIgnoreCase(BP_KEY)){

            Map<String, Object> systolReport = new HashMap<>();
            systolReport.put("date", d);
            systolReport.put("type","systol");
            systolReport.put("value", value1);

            Map<String, Object> diastolReport = new HashMap<>();
            diastolReport.put("date", d);
            diastolReport.put("type","diastol");
            diastolReport.put("value", value2);

            new GetDetailsTwo().addNewReport(email,systolReport,BP_KEY);
            new GetDetailsTwo().addNewReport(email,diastolReport,BP_KEY);
        }
        if(path.equalsIgnoreCase(DIABETES_KEY)){
            Map<String, Object> befMealReport = new HashMap<>();
            befMealReport.put("date", d);
            befMealReport.put("type","beforemeal");
            befMealReport.put("value", value1);

            Map<String, Object> aftMealReport = new HashMap<>();
            aftMealReport.put("date", d);
            aftMealReport.put("type","aftermeal");
            aftMealReport.put("value", value2);

            new GetDetailsTwo().addNewReport(email,befMealReport,DIABETES_KEY);
            new GetDetailsTwo().addNewReport(email,aftMealReport,DIABETES_KEY);
        }
    }


    private void updateUserProfile(String key1,float value1, String key2, float value2){

            DocumentReference user = db.collection("users").document(email);
            user.update(key1, value1);
            user.update(key2, value2)
                    .addOnSuccessListener(new OnSuccessListener < Void > () {
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

    private void updateCalorieProfile(String key1,float value1, String key2, float value2, String key3, int value3){

        DocumentReference user = db.collection("users").document(email);
        user.update(key1, value1);
        user.update(key2, value2);
        user.update(key3, value3)
                .addOnSuccessListener(new OnSuccessListener < Void > () {
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