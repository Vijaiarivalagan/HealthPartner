package com.geforce.vijai.healthpartner.ui.exercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geforce.vijai.healthpartner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;
public class ExerciseFragment extends Fragment {


    FirebaseFirestore db;
    String gender;
    RecyclerView recyclerView;
    SharedPreferences pref;

    WebView webView;
    List<Object> group;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        pref= this.getActivity().getSharedPreferences("user", MODE_PRIVATE);
        gender=pref.getString("genderValue",null);

        View root = inflater.inflate(R.layout.fragment_exercise, container, false);

        webView=(WebView)root.findViewById(R.id.videoWebView);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));


        db.collection("exercise").document(gender).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                group = (List<Object>) document.get("url");
                List<YoutubeVideos> youtubeVideos = new ArrayList<YoutubeVideos>();
                for(Object g:group){
                    //https://m.youtube.com/watch?feature=youtu.be&v=T76BHrT8wBg;
                    //https://www.youtube.com/embed/eWEF1Zrmdow\
                    //https://youtu.be/embed/593wX9Cr0nk

                    //ss.replace("")
                    String ss="https://www.youtube.com/embed/"+(String)g+"\\";
                    Log.d("ss",ss);
                    String main="<iframe width=\"100%\" height=\"100%\" src=\""+ss+"\" frameborder=\"0\" allowfullscreen></iframe>";
                    Log.d("main",main);
                    youtubeVideos.add( new YoutubeVideos(main) );

                }
                Log.d("url from store",group.toString());
                call(youtubeVideos);
            }
        });


       /* youtubeVideos.add( new YoutubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/eWEF1Zrmdow\" frameborder=\"0\" allowfullscreen></iframe>") );
        youtubeVideos.add( new YoutubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/KyJ71G2UxTQ\" frameborder=\"0\" allowfullscreen></iframe>") );
        youtubeVideos.add( new YoutubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/y8Rr39jKFKU\" frameborder=\"0\" allowfullscreen></iframe>") );
        youtubeVideos.add( new YoutubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/8Hg1tqIwIfI\" frameborder=\"0\" allowfullscreen></iframe>") );
  */
       //youtubeVideos.add( new YoutubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/KyJ71G2UxTQ\" frameborder=\"0\" allowfullscreen></iframe>") );
       //youtubeVideos.add( new YoutubeVideos(s) );


        return root;
    }

    public void call(List<YoutubeVideos> youtubeVideos1){
        Log.d("froem call","-----------ca");
        VideoAdapter videoAdapter = new VideoAdapter(youtubeVideos1);

        recyclerView.setAdapter(videoAdapter);


    }}