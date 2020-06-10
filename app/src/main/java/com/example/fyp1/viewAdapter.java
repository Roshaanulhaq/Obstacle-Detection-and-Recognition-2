package com.example.fyp1;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class viewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<picturedatabase> picturedatabases;

    public viewAdapter( List<picturedatabase> picturedatabases,Context context) {
        this.context = context;
        this.picturedatabases=picturedatabases;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.image_item, parent, false);
        return new myitemsholder2(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        myitemsholder2 demoitemholder=(myitemsholder2) holder;
        demoitemholder.textView.setText(picturedatabases.get(position).getItemname());
        Picasso.get().load(picturedatabases.get(position).getImageURI()).fit().centerCrop().into(demoitemholder.imageView);

    }

    @Override
    public int getItemCount() {
        return  picturedatabases.size();

    }

    public class myitemsholder2 extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;


        public myitemsholder2(View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.text_view_name);
            imageView=itemView.findViewById(R.id.image_view_upload);


        }



        }


    }

