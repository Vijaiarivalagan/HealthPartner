package com.geforce.vijai.healthpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddFoodDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView foodName,qtytextview,errText,calorieExcess,foodInfo;
    private SeekBar qty;
    private Spinner sessionspinner;
    private Button addFoodToList;
    private ImageView foodimg;
    private int  session,dailyCalorie,totalCalorie;
    private ProgressBar loadingbar;
    private TableLayout tableLayout;
    private  TextView tbcalorie,tbfat,tbfiber,tbprotein,tbcarbohydrate,tbcholestoral,tbtotal;
    private LinearLayout goneLinear;

    private float calorieMultiFactor,carboMultiFactor,proteinMultiFactor,fatMultiFactor,fiberMultiFactor,cholestorelMultiFactor;
    private float cyclingMultiFactor=6f,runningMultiFactor=8.7f,walkingMultiFactor=2.66f,cleaningMultiFactor=3.5f;
    private float fcalorieValue,ffatValue,ffiberValue,fproteinValue,fcarbohydratesValue,fcholestoralValue,ftotalValue;
    private int fprogressValue;
    private String fservingValue;

    private List<String> sessionArray = Arrays.asList("BreakFast","Lunch","Dinner");


    String path=Environment.getExternalStorageDirectory()
            +"/HealthPartner/Photos/savedpic.jpg",sessionStringValue,email;
    private String grams=" g",pressureRange,diabetesRange;
    FirebaseFirestore db;
    SharedPreferences pref;
    Date date;
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    Bitmap bitmap;

    String ServerImageUploadPath;//="https://health-partner-c4302.appspot.com/predict" ;

    List<String> highBpFood,lowBpFood,lowSugarFood,highSugarFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_details);

        pref= getSharedPreferences("user", MODE_PRIVATE);
        email=pref.getString("email",null);
        dailyCalorie=pref.getInt("dailyCalorie",0);
        totalCalorie=pref.getInt("calorie",0);
        db = FirebaseFirestore.getInstance();
        bitmap = BitmapFactory.decodeFile(path);


        foodName=(TextView)findViewById(R.id.foodnameid);
        qty=(SeekBar) findViewById(R.id.qty);
        addFoodToList=(Button)findViewById(R.id.addfoodtolistid);
        foodimg=(ImageView)findViewById(R.id.takenimgid);
        qtytextview=(TextView)findViewById(R.id.qtytextview);
        sessionspinner=(Spinner)findViewById(R.id.sessionspinner);
        loadingbar=(ProgressBar)findViewById(R.id.loadingprogress);
        tableLayout=(TableLayout)findViewById(R.id.table);
        tbcalorie=(TextView)findViewById(R.id.calorie_value);
        tbprotein=(TextView)findViewById(R.id.protein_value);
        tbfat=(TextView)findViewById(R.id.fat_value);
        tbfiber=(TextView)findViewById(R.id.fiber_value);
        tbcarbohydrate=(TextView)findViewById(R.id.carbohydrate_value);
        tbcholestoral=(TextView)findViewById(R.id.cholesterol_value);
        tbtotal=(TextView)findViewById(R.id.totalCalorieValue);
        errText=(TextView)findViewById(R.id.fooderrText);
        goneLinear=(LinearLayout)findViewById(R.id.goneLinearLayout);
        calorieExcess=(TextView)findViewById(R.id.excesscalorietext);
        foodInfo=(TextView)findViewById(R.id.foodinfo);
        //send image for prediction
        foodimg.setImageBitmap(bitmap);
        ServerImageUploadPath = pref.getString("modelurl","");
        checkgcpwithimage();

        pressureRange=pref.getString("pressureRange"," ");
        diabetesRange=pref.getString("diabetesRange"," ");

        getPressureFoods(pressureRange);


        // set fields
        //only one field to set - session spinner
        sessionspinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sessionArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionspinner.setAdapter(aa);

        //session=getsession();
        session = sessionArray.indexOf(getIntent().getStringExtra("session"));
        System.out.println("sesso" + session);
        sessionspinner.setSelection(session);


        // perform seek bar change listener event used for getting the progress value
        qty.setProgress(fprogressValue);
        qtytextview.setText("0"+grams);
        qty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qtytextview.setText(progress+grams);

                fcalorieValue=calorieMultiFactor*progress;
                ffatValue=fatMultiFactor*progress;
                ffiberValue=fiberMultiFactor*progress;
                fproteinValue=proteinMultiFactor*progress;
                fcarbohydratesValue=carboMultiFactor*progress;
                fcholestoralValue=cholestorelMultiFactor*progress;
                int diff=totalCalorie-dailyCalorie;
                if(diff<=fcalorieValue){
                    addFoodToList.setBackgroundColor(getResources().getColor(R.color.rangelow_high));
                    calorieExcess.setText("Calorie excess by "+(fcalorieValue-diff));
                }
                else{
                    addFoodToList.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                tbcalorie.setText(String.format("%.2f",fcalorieValue)+grams);
                tbfat.setText(String.format("%.2f",ffatValue)+grams);
                tbfiber.setText(String.format("%.2f",ffiberValue)+grams);
                tbcarbohydrate.setText(String.format("%.2f",fcarbohydratesValue)+grams);
                tbprotein.setText(String.format("%.2f",fproteinValue)+grams);
                tbcholestoral.setText(String.format("%.2f",fcholestoralValue)+grams);
                tbtotal.setText(String.format("%.2f",fcalorieValue)+grams);




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

        /*sessionspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sessionStringValue=sessionspinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionspinner.setPrompt("Select appropriate one!!");
            }
        });*/

        //submit button
        addFoodToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // call upload to db , if condition true show alertdialog else directly call uploadtodb method
                int diff=totalCalorie-dailyCalorie;
                if(diff<=fcalorieValue){
                    //addFoodToList.setBackgroundColor(getResources().getColor(R.color.rangelow_high));
                    //

                    LayoutInflater li = LayoutInflater.from(AddFoodDetails.this);
                    View promptsView = li.inflate(R.layout.exercise_dialog, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            AddFoodDetails.this);
                    alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder.setCancelable(false);

                    final Button ok=(Button)promptsView.findViewById(R.id.ok);
                    final TextView running=(TextView)promptsView.findViewById(R.id.running_value);
                    final TextView cycling=(TextView)promptsView.findViewById(R.id.cycling_value);
                    final TextView cleaning=(TextView)promptsView.findViewById(R.id.cleaning_value);
                    final TextView walking=(TextView)promptsView.findViewById(R.id.walking_value);
                    final TextView heading=(TextView)promptsView.findViewById(R.id.dia_heading);

                    float excess=fcalorieValue-diff;

                    heading.setText("To burn "+String.format("%.2f",excess)+" excess calories you need to do");
                    running.setText(String.format("%.2f",excess/runningMultiFactor));
                    cycling.setText(String.format("%.2f",excess/cyclingMultiFactor));
                    cleaning.setText(String.format("%.2f",excess/cleaningMultiFactor));
                    walking.setText(String.format("%.2f",excess/walkingMultiFactor));



                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            loadingbar.setVisibility(View.VISIBLE);
                            String foodnametosend=foodName.getText().toString();
                            String sessiontosend=sessionStringValue;

                            //update to db
                            uploadfoodtodb(foodnametosend,(int)fcalorieValue,sessiontosend);

                            File file = new File(path);
                            //File myFile = new File(file,"savedpic.jpg");
                            file.delete();

                        }
                    });
                }
                    else {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loadingbar.setVisibility(View.VISIBLE);
                    String foodnametosend = foodName.getText().toString();
                    String sessiontosend = sessionStringValue;

                    //update to db
                    uploadfoodtodb(foodnametosend, (int) fcalorieValue, sessiontosend);

                    File file = new File(path);
                    //File myFile = new File(file,"savedpic.jpg");
                    file.delete();

                }
            }


        });
    }

    private void getPressureFoods(String pressureRange) {
        String foodpath="";
        if(pressureRange.equalsIgnoreCase("normal")){

        }
        else if(pressureRange.equalsIgnoreCase("low")){

        }
        else if(pressureRange.equalsIgnoreCase("high")){

        }
        else
            return ;
    }


    //send image for classification---- start
    private void checkgcpwithimage() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingbar.setVisibility(View.VISIBLE);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();
        postRequest(ServerImageUploadPath, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        //OkHttpClient client = new OkHttpClient();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingbar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        errText.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingbar.setVisibility(View.GONE);
                        goneLinear.setVisibility(View.VISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        try {
                            JSONObject reader = new JSONObject(response.body().string());
                            String foodname=reader.getString("image_class");
                            foodName.setText(" "+foodname);
                            getCalorieFromDb(foodname.toLowerCase());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //Get calories from db---- start
    private void getCalorieFromDb(String foodname) {
        db.collection("foodcalories").document(foodname.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document=task.getResult();
                            if(document !=null){

                                fcalorieValue=document.getDouble("calorie").floatValue();
                                ffatValue=document.getDouble("fat").floatValue();
                                ffiberValue=document.getDouble("fiber").floatValue();
                                fcarbohydratesValue=document.getDouble("carbohydrates").floatValue();
                                fproteinValue=document.getDouble("protein").floatValue();
                                fcholestoralValue=document.getDouble("cholesterol").floatValue();
                                fprogressValue=document.getLong("progressvalue").intValue();
                                fservingValue=document.getString("servingsize");

                                calorieMultiFactor=fcalorieValue/100;
                                fatMultiFactor=ffatValue/100;
                                fiberMultiFactor=ffiberValue/100;
                                carboMultiFactor=fcarbohydratesValue/100;
                                proteinMultiFactor=fproteinValue/100;
                                cholestorelMultiFactor=fcholestoralValue/100;

                                System.out.println("multifactors"+carboMultiFactor+" "+fatMultiFactor+" "+fiberMultiFactor+" "+carboMultiFactor+" "+cholestorelMultiFactor+" "+proteinMultiFactor);

                                tbcalorie.setText(String.format("%.2f",fcalorieValue)+grams);
                                tbfat.setText(String.format("%.2f",ffatValue)+grams);
                                tbfiber.setText(String.format("%.2f",ffiberValue)+grams);
                                tbcarbohydrate.setText(String.format("%.2f",fcarbohydratesValue)+grams);
                                tbprotein.setText(String.format("%.2f",fproteinValue)+grams);
                                tbcholestoral.setText(String.format("%.2f",fcholestoralValue)+grams);
                                tbtotal.setText(String.format("%.2f",fcalorieValue)+grams);
                                foodInfo.setText(fservingValue);

                            }

                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }





    //upload details to firebase
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
                        loadingbar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Food Added",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingbar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });

    }

    //get breakfast,lunch,dinner by time of the day
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sessionStringValue=sessionspinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        sessionspinner.setPrompt("Select appropriate one!!");
    }
}









//=========================================================================================================
//model start

//FirebaseCustomRemoteModel remoteModel;
//FirebaseCustomLocalModel localModel;
//FirebaseModelInterpreter interpreter;

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

//send for prediction first

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


