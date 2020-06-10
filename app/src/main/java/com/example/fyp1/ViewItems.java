package com.example.fyp1;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;


public class ViewItems extends AppCompatActivity {
    private TextToSpeech myTTS;
    Query query;
    List<picturedatabase> picturedatabasesList;
    List<picturedatabase> getPicturedatabasesList2;
    RecyclerView recyclerViewDemo;
    boolean check;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check=false;
        setContentView(R.layout.view_recycle_activity);
        picturedatabasesList = new ArrayList<>();
        getPicturedatabasesList2 = new ArrayList<>();



    }

    @Override
    protected void onStart() {
        super.onStart();
        check=false;
        query= FirebaseDatabase.getInstance().getReference("picturedatabase").orderByChild("userid").equalTo(Singleton.userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                picturedatabasesList.clear();
                getPicturedatabasesList2.clear();
                for(DataSnapshot picturesnapshot: dataSnapshot.getChildren())
                {
                    picturedatabase picturedatabase=picturesnapshot.getValue(picturedatabase.class);
                    picturedatabasesList.add(picturedatabase);
                    check=true;
                }

                if(check==true) {
                    String itemid = picturedatabasesList.get(0).getItemid();
                    getPicturedatabasesList2.add(picturedatabasesList.get(0));

                    for (int i = 0; i < picturedatabasesList.size(); i++) {
                        if (itemid.equals(picturedatabasesList.get(i).getItemid())) {
                            //do nothing
                        } else {
                            itemid = picturedatabasesList.get(i).getItemid();
                            getPicturedatabasesList2.add(picturedatabasesList.get(i));
                        }
                    }
                    recyclerViewDemo = findViewById(R.id.recycler_view);
                    recyclerViewDemo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewDemo.setAdapter(new viewAdapter(getPicturedatabasesList2, getApplicationContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
