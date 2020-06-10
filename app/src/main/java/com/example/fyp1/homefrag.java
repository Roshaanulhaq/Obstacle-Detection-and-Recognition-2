package com.example.fyp1;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.google.android.material.tabs.TabItem;

import java.util.Locale;

public class homefrag extends Fragment {

    private TextToSpeech myTTS;
    public boolean check1;
    public boolean check2;
    static  int touch=0;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private Thread thread;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
  //  TextView textView5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        check1=false;
        check2=false;
        View view = inflater.inflate(R.layout.home_activity, container, false);
        initializeTTS();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
//        textView5 = Typeface.createFromAsset(this.getAssets(),"font/Pacifico.ttf");
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

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
                                firebaseAuth.signOut();
                                Intent i = new Intent(getActivity(),Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                getActivity().finish();

                            }

                            // Right to left swipe action
                            else
                            {
                                Fragment selectedFragment = null;
                                selectedFragment = new belongingfrag();

                                Toast.makeText(getContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        selectedFragment).addToBackStack(null).commit();


                               // View view2 = inflater.inflate(R.layout.activity_main, container, false);
//                                TabItem t= (TabItem) view2.findViewById(R.id.tab2);
//                                t.performClick();
                            }

                        }
                        else {
                            Toast.makeText(getContext(), "Tap on screen", Toast.LENGTH_SHORT).show();
                            touch++;
                            Log.e("No of touch", String.valueOf(touch));
                            check1 = false;
                            check2 = false;
                            if (touch == 1) {
                                thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            synchronized (this) {
                                                wait(2000);
                                                if(touch==1){
                                                check1 = true;
                                                touch = 0;
                                                if (check2 == false) {
                                                    Log.e("start day mode", "1");
                                                    Singleton.mode="day";
                                                    Intent i = new Intent(getContext(), DetectorActivity.class);
                                                    startActivity(i);
                                                }
                                                }
                                            }
                                        } catch (InterruptedException ex) {
                                        }

                                        // TODO
                                    }
                                };

                                thread.start();
                            }
                            if (touch == 2 && check1 == false) {
                                try{
                                    synchronized (this){
                                        wait(2000);
                                        if(touch==2) {
                                            check2 = true;
                                            Singleton.mode="night";
                                            Log.e("Start night mode", "1");
                                            Intent i = new Intent(getContext(), DetectorActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                }
                                catch (Exception ex){

                                }

                            }
                            if(touch>2)
                            {
                                touch=0;
                                break;
                            }

                            break;
                        }
                }

                return true;
            }
        });

        return  view;
    }
    public void initializeTTS() {

        myTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (myTTS.getEngines().size() == 0)   // checking if any engine found
                {
                    Toast.makeText(getContext(), "There is no TTS engine", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        myTTS.setLanguage(Locale.US);

                        speak("Welcome to Home Page. Tap once to open day mode. Tap twice to open night mode. Swipe Left to belonging mode. Swipe Right to Logout");

                    }
                    catch (Exception ex)
                    {

                    }
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
    public void onResume() {
        super.onResume();
        initializeTTS();
    }

    @Override
    public void onPause() {
        super.onPause();
        myTTS.shutdown();
    }
}
