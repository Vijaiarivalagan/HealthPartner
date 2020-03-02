package com.geforce.vijai.healthpartner.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //Get Firebase auth instance
        db=FirebaseFirestore.getInstance();
        pref= this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        Toast.makeText(getActivity(),"email id"+email,Toast.LENGTH_SHORT);

        arrayListVerticle=new ArrayList<>();

        verticleRecyclerView= (RecyclerView) root.findViewById(R.id.recyclerView);
        verticleRecyclerView.setHasFixedSize(true);
        verticleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        adapter=new VerticleRecyclerViewAdapter(getActivity(),arrayListVerticle);

        verticleRecyclerView.setAdapter(adapter);


        db.collection("calories").document(email).collection("22-02-2020")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Convert the whole snapshot to a POJO list
                            Log.d("error",task.toString());

                            horilist = task.getResult().toObjects(HorizontalModel.class);
                            setData(horilist);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });


        return root;
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