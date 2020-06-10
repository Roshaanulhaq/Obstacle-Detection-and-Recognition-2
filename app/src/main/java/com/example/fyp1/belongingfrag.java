package com.example.fyp1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class belongingfrag extends Fragment {
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private TextToSpeech myTTS;
    private Button add;
    private Button Search;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.belongingactivity, container, false);
        add=view.findViewById(R.id.button);
        Search=view.findViewById(R.id.button2);
        initializeTTS();

        add.setOnTouchListener(new View.OnTouchListener() {
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
                                Log.e("swipe right","1");
                                Fragment selectedFragment = null;
                                selectedFragment = new homefrag();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        selectedFragment).addToBackStack(null).commit();

                            }

                            // Right to left swipe action
                            else
                            {

                                Log.e("swipe left","1");
                                Toast.makeText(getContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();



                            }

                        }
                        else {

                            Log.e("tapped","1");
                            Fragment selectedFragment = null;
                            selectedFragment = new AddBelongingFrag();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment).addToBackStack(null).commit();
                        }
                }
                return true;
            }
        });

        Search.setOnTouchListener(new View.OnTouchListener() {
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
                                Log.e("swipe right","1");
                                Fragment selectedFragment = null;
                                selectedFragment = new homefrag();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        selectedFragment).addToBackStack(null).commit();

                            }

                            // Right to left swipe action
                            else
                            {

                                Log.e("swipe left","1");
                                Toast.makeText(getContext(), "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();



                            }

                        }
                        else {
                            Toast.makeText(getContext(), "Tap on screen", Toast.LENGTH_SHORT).show();
                            Log.e("tapped","1");
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
    public void onStart() {
        super.onStart();

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

                        speak("Welcome to Belonging mode. Tap on Upper half to add new item. Tap on lower half to open camera. Swipe Right to Go Back.");

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
