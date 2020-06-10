package com.example.fyp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import static java.lang.Thread.sleep;

public class view_name_frag extends Fragment {
    private TextToSpeech myTTS;
    private EditText editText;
    private Button button;
    itemdatabase itemdatabase;
    DatabaseReference reff;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_name_frag, container, false);
        itemdatabase=new itemdatabase();
        reff= FirebaseDatabase.getInstance().getReference().child("itemdatabase");

        initializeTTS();
        editText=view.findViewById(R.id.editTextTextPersonName);
        button=view.findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editText.getText().toString().trim();
                itemdatabase.setItemname(name);
                itemdatabase.setUserid(Singleton.userid);
                itemdatabase.setId(reff.push().getKey());
                Singleton.itemid=itemdatabase.getId();
                reff.push().setValue(itemdatabase);
                myTTS.setLanguage(Locale.US);
                speak("You will need to capture atleast 10 images. Opening camera in 3. 2. 1");
                try{
                    sleep(5000);
                    Intent i=new Intent(getContext(),AddBelongingCamera.class);
                    i.putExtra("itemname",name);
                    Singleton.itemname=name;
                    startActivity(i);
                }
                catch (Exception ex)
                {

                }
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

                        speak("Please the belonging Name ");
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
