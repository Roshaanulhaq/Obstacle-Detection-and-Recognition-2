package com.example.fyp1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class Login extends AppCompatActivity {

    private TextToSpeech myTTS;
    private SpeechRecognizer mySR;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    EditText email;
    EditText pass;
    Button b1;
    TextView signUp;
    FirebaseAuth mAuth;
    Query query;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        email = findViewById(R.id.editText2);
        pass = findViewById(R.id.editText);
        b1 = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            Log.e("email of logged in user",firebaseUser.getEmail());
            Intent i =new Intent(this,MainActivity.class);
            i.putExtra("userid",firebaseUser.getEmail());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }
        else{
            initializeTTS();
        }


        //Sign up View btn
        signUp = (TextView) findViewById(R.id.textView2);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak("Redirecting you to Signup Page");
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(i);
            }
        });
        //Login button
        b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }
    public void check()
    {
       // b1.setOnClickListener(new View.OnClickListener() {
         //   @Override
           // public void onClick(View v) {
                final String emailS;
                final String passS;
                emailS = email.getText().toString().trim();
                passS = pass.getText().toString().trim();
                if (emailS.isEmpty()) {
                    email.setError("email is required");
                    speak("Email fields must be filled");
                    email.requestFocus();
                    return;
                }
                if (passS.isEmpty()) {
                    pass.setError("Password is required");
                    speak("Password fields must be filled");
                    pass.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailS).matches()) {
                    email.setError("Please enter a valid email");
                    speak("Please enter a valid email address");
                    email.requestFocus();
                    return;
                }
                if (passS.length() < 6) {
                    pass.setError("minimum length of password should be 6");
                    speak("Incorrect Password");
                    pass.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(emailS, passS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            query=FirebaseDatabase.getInstance().getReference("User").orderByChild("email").equalTo(emailS);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                                        User user=userSnapshot.getValue(User.class);
                                        Singleton.userid=user.getId();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            speak("Login Successfull.. Redirecting you to Home Page");
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Email Or Password Incorrect", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
      //  });
   // }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1) {


                    }

                    // Right to left swipe action
                    else {

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Tap on screen", Toast.LENGTH_SHORT).show();


                    if (ContextCompat.checkSelfPermission(Login.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                                Manifest.permission.RECORD_AUDIO)) {

                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(Login.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        }
                    } else {
                        // Permission has already been granted
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySR.startListening(intent);
                        // startActivityForResult(intent,10);

                    }
                    // initializeTTS();
                    initializeSpeech();
                }

        }

        return super.onTouchEvent(event);
    }

    //    public void startService(View view){
//
//        Intent i = new Intent(this,MyService.class);
//        speak("I will be giving you updates for few minutes");
//        startService(i);
//    }
///////////

    ArrayList<String> res;
    ArrayList<String> check;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (checkName == true && data != null) {
            EditText tx = (EditText) findViewById(R.id.editText2);
            res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String input = res.get(0).trim();

            input = input.replace(" ", "");
            Log.e("name", input);
            tx.setText(input);
        } else if (checkPass == true && data != null) {
            EditText tx2 = (EditText) findViewById(R.id.editText);
            res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String input2 = res.get(0).trim();
            //String  input2 = res.get(0).toString();
            input2 = input2.replace(" ", "");
            tx2.setText(input2);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initializeSpeech() {

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            mySR = SpeechRecognizer.createSpeechRecognizer(this);
            mySR.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    try {

                        List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        processResult(result_arr.get(0));  // is ka method bne ga
                        // tx.setText(res.get(0));
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySR.startListening(intent);

                        sleep(3000);
                        if(checkSignUp!=true)
                            startActivityForResult(intent, 10);


                    }catch (Throwable throwable)
                    {}
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

        }
    }

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    //processing
    Intent data;
    boolean checkName=false;
    boolean checkPass=false;
    boolean checkSignUp=false;
    private void processResult(String result_message) {

        //result_message = result_message.toLowerCase();


        // if(result_message.indexOf("what") != -1){
        if (result_message.indexOf("name") != -1 || result_message.indexOf("email") != -1) {
            checkName = true;
            checkPass=false;
            checkSignUp=false;
            speak("Email field clicked.Please enter your Email");
            EditText tx = (EditText) findViewById(R.id.editText2);
            tx.performClick();

        }
        else if (result_message.indexOf("password") != -1) {
                checkName=false;
                checkPass=true;
                checkSignUp=false;
                EditText tx2 = (EditText) findViewById(R.id.editText);
                tx2.performClick();
                speak("Password field clicked.Please enter your password ");
            }
        else if (result_message.indexOf("sign up")!= -1){
            checkName=false;
            checkPass=false;
            checkSignUp=true;
            TextView tx4 = (TextView) findViewById(R.id.textView2);
            tx4.performClick();
            speak("Redirecting you to SignUp page");
            Intent i = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(i);

        }
        else if (result_message.indexOf("login")!= -1 || result_message.indexOf("sign in")!= -1) {
            checkName = false;
            checkPass = false;
            checkSignUp = true;
            Button bp = (Button) findViewById(R.id.button);
            bp.performClick();

            check();

        }

        else if (result_message.indexOf("time") != -1) {
            String time_now = DateUtils.formatDateTime(this, new Date().getTime(), DateUtils.FORMAT_SHOW_TIME);
            speak("The time is now: " + time_now);
        }

        else{
        speak("Command not found. Try Again");
        }


//        } else if (result_message.indexOf("camera") != -1){
//            speak("Opening a camera for you.");
//           // final ImageView imageView=(ImageView)findViewById(R.id.imageView12);
//
//            //Intent i = new Intent(getApplicationContext(), ClassifierActivity.class);
//            //startActivity(i);
//            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            //startActivityForResult(cameraIntent, CAMERA_REQUEST);
//
//        };
    }

    public void initializeTTS() {

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size()==0)   // checking if any engine found
                {
                    Toast.makeText(getApplicationContext(),"There is no TTS engine",Toast.LENGTH_SHORT).show();
                    speak("Try Again");
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    if(checkName==true)
                        speak("Your UserName is Entered as : ..." + " "+ res);
                    else if(checkPass==true)
                        speak("Your Password is Entered : ..." + " "+ res);
                    else
                        speak("Hello This is Login Page ! Please Single tap On Screen to Enter your UserName and Password");
                }
            }
        });
    }

    public void speak(String message) {

        if(Build.VERSION.SDK_INT >= 21){
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            myTTS.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }

    }
    @Override
    protected  void onPause()
    {
        super.onPause();
        myTTS.shutdown();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeSpeech();
        initializeTTS();
    }

}
