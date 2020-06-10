package com.example.fyp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class AddBelongingFrag extends Fragment {
    private TextToSpeech myTTS;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public boolean check1;
    public boolean check2;
    static  int touch=0;
    private Thread thread;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_belonging_frag, container, false);
        initializeTTS();
        check1=false;
        check2=false;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
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
                                Fragment selectedFragment = null;
                                selectedFragment = new belongingfrag();

                                Toast.makeText(getContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        selectedFragment).addToBackStack(null).commit();


                            }

                            // Right to left swipe action
                            else
                            {



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
                                                check1 = true;
                                                touch = 0;
                                                if (check2 == false) {
                                                    Log.e("start day mode", "1");
                                                    try {
//
                                                        Fragment selectedFragment = null;
                                                        selectedFragment = new view_name_frag();
                                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                                selectedFragment).addToBackStack(null).commit();
                                                    }
                                                    catch (Exception ex)
                                                    {

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
                                check2 = true;
                                Log.e("Start view  mode", "1");
                                Intent i = new Intent(getContext(), ViewItems.class);
                                startActivity(i);

                                // consider as something else - a screen tap for example
                            }
                            break;
                        }
                }

                return true;
            }
        });

        return view;

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

                        speak("Tap Once To Add new Item. Tap Twice To View Your Items. Swipe Right to go back.");
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
}
