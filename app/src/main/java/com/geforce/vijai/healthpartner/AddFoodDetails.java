package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geforce.vijai.healthpartner.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddFoodDetails extends AppCompatActivity {
    private TextView foodName;
    private SeekBar qty;
    private TextView qtytextview;
    private Button addFoodToList;
    private ImageView foodimg;
    private int qtyeditvalue,qtysetvalue, progressChangedValue = 100;;
    private float calPerGram;
    String path=Environment.getExternalStorageDirectory()
            +"/HealthPartner/Photos",sessionStringValue,email;
    int session;
    private Spinner sessionspinner;
    FirebaseFirestore db;
    SharedPreferences pref;
    Date date;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

    FirebaseCustomRemoteModel remoteModel;
    FirebaseCustomLocalModel localModel;
    FirebaseModelInterpreter interpreter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_details);

        //ml part
        /*remoteModel = new FirebaseCustomRemoteModel.Builder("food_classifier").build();
        localModel= new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("food_classifier.tflite")
                .build();

        try {
            runInference();
        } catch (FirebaseMLException e) {
            System.out.println("error in runintference"+e.toString());
            e.printStackTrace();
        }*/
        //end ml part

        foodName=(TextView)findViewById(R.id.foodnameid);
        qty=(SeekBar) findViewById(R.id.qty);
        addFoodToList=(Button)findViewById(R.id.addfoodtolistid);
        foodimg=(ImageView)findViewById(R.id.takenimgid);
        qtytextview=(TextView)findViewById(R.id.qtytextview);
        sessionspinner=(Spinner)findViewById(R.id.sessionspinner);
        db = FirebaseFirestore.getInstance();


        Bitmap bitmap = BitmapFactory.decodeFile(path+"/"+"savedpic.jpg");
        foodimg.setImageBitmap(bitmap);

        pref= getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);

        session=getsession();
        sessionspinner.setSelection(session);

        // perform seek bar change listener event used for getting the progress value
        qty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                                           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                               progressChangedValue = progress;
                                               qtytextview.setText(String.valueOf(progress)+"grams.");
                                               //qtynumber.setText(progressChangedValue);
                                           }

                                           public void onStartTrackingTouch(SeekBar seekBar) {
                                               // TODO Auto-generated method stub
                                           }

                                           public void onStopTrackingTouch(SeekBar seekBar) {
                                               //qtysetvalue=progressChangedValue;
                                               //qtynumber.setText(progressChangedValue);
                                               //Toast.makeText(AddFoodDetails.this,""+progressChangedValue,Toast.LENGTH_SHORT).show();
                                           }
        });

        sessionspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sessionStringValue=sessionspinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionspinner.setPrompt("Select appropriate one!!");
            }
        });

        //submit button
        addFoodToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String foodnametosend=foodName.getText().toString();
                int calorievalue=progressChangedValue;
                String sessiontosend=sessionStringValue;

                //update to db
                uploadfoodtodb(foodnametosend,calorievalue,sessiontosend);



                File file = new File(path);
                File myFile = new File(file,"savedpic.jpg");
                myFile.delete();

            }


        });
    }
//=========================================================================================================
    //model start
/*
// get the available model interpreter
private FirebaseModelInterpreter getModelInterpreter(
        final FirebaseCustomRemoteModel remoteModel,
        final FirebaseCustomLocalModel localModel) {
    // [START mlkit_check_download_status]
    FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
            .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean isDownloaded) {
                    FirebaseModelInterpreterOptions options;
                    if (isDownloaded) {
                        options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                    } else {
                        options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                    }
                    try {
                        interpreter = FirebaseModelInterpreter.getInstance(options);

                    } catch (FirebaseMLException e) {

                    }
                }
            });
    return interpreter;
}
// [END mlkit_check_download_status]

    //main method fr model, starting point...............
private void runInference() throws FirebaseMLException {

    FirebaseModelInterpreter interpreter;
        FirebaseModelInterpreterOptions options =
                new FirebaseModelInterpreterOptions.Builder(localModel).build();
        interpreter = FirebaseModelInterpreter.getInstance(options);


    FirebaseModelInterpreter firebaseInterpreter = interpreter;
    float[][][][] input = bitmapToInputArray();
    FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();

    // [START mlkit_run_inference]
    FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
            .add(input)  // add() as many input arrays as your model requires
            .build();
    firebaseInterpreter.run(inputs, inputOutputOptions)
            .addOnSuccessListener(
                    new OnSuccessListener<FirebaseModelOutputs>() {
                        @Override
                        public void onSuccess(FirebaseModelOutputs result) {
                            // [START_EXCLUDE]
                            // [START mlkit_read_result]
                            Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_SHORT);
                            System.out.println("result is: "+result+"result cls: "+result.getClass()+"res out: "+result.getOutput(0));
                            float[][] output = result.getOutput(0);
                            float[] probabilities = output[0];
                            System.out.println("output is:"+output+"ans is : "+probabilities);
                            Toast.makeText(getApplicationContext(),probabilities.toString(),Toast.LENGTH_SHORT).show();
                            foodName.setText(probabilities.toString());
                            // [END mlkit_read_result]
                            // [END_EXCLUDE]
                        }
                    })
            .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"error in tf interpreter",Toast.LENGTH_SHORT).show();

                        }
                    });
    // [END mlkit_run_inference]
    }

    private float[][][][] bitmapToInputArray() {
        // [START mlkit_bitmap_input]
        Bitmap bitmap = BitmapFactory.decodeFile(path+"/"+"savedpic.jpg");
        bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true);

        int batchNum = 0;
        float[][][][] input = new float[1][64][64][3];
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
        // [END mlkit_bitmap_input]

        return input;
    }

    private Bitmap getYourInputImage() {
        // This method is just for show
        return Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8);
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
        // [START mlkit_create_io_options]
        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 64, 64, 3})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 4})
                        .build();
        // [END mlkit_create_io_options]

        return inputOutputOptions;
    }*/
    //========================================================================================================================


    private void uploadfoodtodb(String foodnametosend, int calorievalue, String sessiontosend) {
        Map<String, Object> foodcalories = new HashMap<>();
        foodcalories.put("calorie", calorievalue);
        foodcalories.put("food",foodnametosend);
        foodcalories.put("food_session", sessiontosend);

        date= new Date();
        String datestring=sdf.format(date);
        String id=db.collection("calories").document(email).collection(datestring).document().getId();
        db.collection("calories").document(email).collection(datestring).document(id).set(foodcalories)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Food Added",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });

    }

    private int getsession() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            //return "BreakFast";
            return 0;
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            //return "Lunch";
            return 1;
        }else if(timeOfDay >= 17 && timeOfDay < 24){
            //return "Dinner";
            return 2;
        }
        return 0;
    }
}
