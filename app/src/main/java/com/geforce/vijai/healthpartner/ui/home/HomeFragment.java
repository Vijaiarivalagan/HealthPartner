package com.geforce.vijai.healthpartner.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.R;
import com.geforce.vijai.healthpartner.ui.exercise.YoutubeVideos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private SharedPreferences pref;
    private RecyclerView verticleRecyclerView;
    private VerticleRecyclerViewAdapter adapter;
    private ArrayList<VerticleModel> arrayListVerticle;
    private String email;
    private List<HorizontalModel> horilist;
    private TextView tvexer,totaltv;
    private Button addexer;
    private EditText getexer;
    private ProgressBar Pb;
    Date date;
    int breakfast,lunch,dinner,calorie,c;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);



        db=FirebaseFirestore.getInstance();
        pref= this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        calorie=(int)pref.getFloat("calorie",0);

        tvexer=(TextView)root.findViewById(R.id.tvexer);
        addexer=(Button)root.findViewById(R.id.addexer);
        getexer=(EditText)root.findViewById(R.id.getexer);
        Pb=(ProgressBar)root.findViewById(R.id.breakfastpb);
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

}