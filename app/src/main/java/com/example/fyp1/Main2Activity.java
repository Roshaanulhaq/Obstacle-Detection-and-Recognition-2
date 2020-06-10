package com.example.fyp1;

import android.Manifest;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;


public class Main2Activity extends AppCompatActivity {

    DatabaseReference reff;
    FirebaseAuth mAuth;
    User user;
    //TextView textInputEmail;
    TextView userName;
    TextView password;
    TextView email;

    TextView phone;
    Button btn1;
    TextView login;
    private TextToSpeech myTTS;
    private SpeechRecognizer mySR;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

//    DatabaseReference usersDb;
//    List<DemoItem>userslist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        userName = (TextView) findViewById(R.id.editText2);
        password = (TextView) findViewById(R.id.editText);
        email = (TextView) findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("User");
        user = new User();

        //Login View btn
        login = (TextView) findViewById(R.id.textView2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak("Redirecting you to Login Page");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        //SignUp button
        btn1 = (Button) findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check();
            }
        });

        ////////////////////////////////////////
        initializeTTS(); //
    }
    public void check()
    {
      //  btn1.setOnClickListener(new View.OnClickListener() {

        //    @Override
          //  public void onClick(View v) {
                final String User;
                final String mail;
                final String pass;
                User=userName.getText().toString().trim();
                mail=email.getText().toString().trim();
                pass=password.getText().toString().trim();


                if(User.isEmpty()){
                    userName.setError("Username is required");
                    userName.requestFocus();
                    speak("Username field must be filled");
                    return;
                }
                if(mail.isEmpty()){
                    email.setError("Mail is required");
                    email.requestFocus();
                    speak("Email field must be filled");
                    return;
                }
                if(pass.isEmpty()){
                    password.setError("Password is required");
                    password.requestFocus();
                    speak("Password field must be filled");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    email.setError("Please enter a valid email");
                    speak("Please enter a valid email address");
                    email.requestFocus();

                    return;
                }

                if(pass.length()<6)
                {
                    speak("Password too short ");
                    password.setError("Minimum length of password should be 6");
                    password.requestFocus();
                    return;
                }
            mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try {

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfull", Toast.LENGTH_SHORT).show();
                            speak("Registration Successfull.. Redirecting you to Login Page");
                            sleep(1000);
                            user.setName(User);
                            user.setPassword(pass);
                            user.setEmail(mail);
                            user.setId(reff.push().getKey());
                            reff.push().setValue(user);
                            startActivity(new Intent(Main2Activity.this, Login.class));
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Email already present", Toast.LENGTH_SHORT).show();
                                speak("Sorry ! Email already present");
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }catch (Throwable throwable){}
                }
            });
           }

        //});
    //}

    ArrayList<String> res;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(checkName==true) {
            EditText tx = (EditText) findViewById(R.id.editText2);
            res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String input =res.get(0).trim();
            input=input.replace(" ","");
            tx.setText(input);
        }
        else if (checkEmail==true){
            EditText tx3 = (EditText) findViewById(R.id.email);
            res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String input2 =res.get(0).trim();
            input2=input2.replace(" ","");
            tx3.setText(input2);
        }
        else if (checkPass==true){
            EditText tx2 = (EditText) findViewById(R.id.editText);
            res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String input2 =res.get(0).trim();
            input2=input2.replace(" ","");
            tx2.setText(input2);
        }
//        else if(checkLogin==true){
//
//            login = (TextView) findViewById(R.id.textView2);
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//            }
//        });
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {


                    }

                    // Right to left swipe action
                    else
                    {

                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Tap on screen", Toast.LENGTH_SHORT).show();


                    if (ContextCompat.checkSelfPermission(Main2Activity.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this,
                                Manifest.permission.RECORD_AUDIO)) {

                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(Main2Activity.this,
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

//                  ArrayList<String> res= results.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
                        processResult(result_arr.get(0));  // is ka method bne ga
                        // tx.setText(res.get(0));
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySR.startListening(intent);
                        sleep(3000);
                        if(checkLogin!=true && btnSignUp!=true)
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
    boolean checkEmail=false;
    boolean checkLogin=false;
    boolean btnSignUp=false;
    private void processResult(String result_message) {

        //result_message = result_message.toLowerCase();


        // if(result_message.indexOf("what") != -1){
        if (result_message.indexOf("name") != -1) {
            checkName = true;
            checkPass=false;
            checkEmail=false;
            checkLogin=false;
            btnSignUp=false;
            speak("Username field clicked.Please enter your UserName");
            EditText tx = (EditText) findViewById(R.id.editText2);
            tx.performClick();

        }
        else if (result_message.indexOf("password") != -1) {
            checkName=false;
            checkPass=true;
            checkLogin=false;
            checkEmail=false;
            btnSignUp=false;
            EditText tx2 = (EditText) findViewById(R.id.editText);
            tx2.performClick();
            speak("Password field clicked.Please enter your password ");
        }
        else if(result_message.indexOf("email") != -1){
            checkName=false;
            checkPass=false;
            checkEmail=true;
            checkLogin=false;
            btnSignUp=false;
            EditText tx2 = (EditText) findViewById(R.id.email);
            tx2.performClick();
            speak("Email field clicked.Please enter your Email ");
        }
        else if (result_message.indexOf("login")!= -1){
            checkName=false;
            checkPass=false;
            checkEmail=false;
            checkLogin=true;
            btnSignUp=false;
            TextView tx4 = (TextView) findViewById(R.id.textView2);
            speak("Redirecting you to Login Page");
            tx4.performClick();
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }
        else if (result_message.indexOf("sign up")!= -1){
            checkName=false;
            checkPass=false;
            checkEmail=false;
            checkLogin=true;
            btnSignUp=true;
            Button btn = (Button) findViewById(R.id.button);
//            speak("Registration Successfull.. Redirecting you to Login Page");
            btn.performClick();
            check();
          //  Intent i = new Intent(getApplicationContext(), MainActivity.class);
           // startActivity(i);
        }

        else if (result_message.indexOf("time") != -1) {
            String time_now = DateUtils.formatDateTime(this, new Date().getTime(), DateUtils.FORMAT_SHOW_TIME);
            speak("The time is now: " + time_now);
        }
    }


    public void initializeTTS() {

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size()==0)   // checking if any engine found
                {
                    Toast.makeText(getApplicationContext(),"There is no TTS engine",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    if(checkName==true)
                        speak("Your UserName is Entered as : ..." + " "+ res);
                    else if(checkPass==true)
                        speak("Your Password is Entered as : ..." + " "+ res);
                    else if(checkEmail==true)
                        speak("Your Email is Entered as : ..." + " "+ res);
                    else
                        speak("Welcome to Sign Up page ! Please Single tap On Screen to Register");
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