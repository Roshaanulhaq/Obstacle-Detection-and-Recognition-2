package com.example.fyp1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AddBelongingCamera extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE=7;
    Uri FilePathUri;
    TextView textView;
    StorageReference storageReference;
    picturedatabase picturedatabase;
    Intent intent;
    DatabaseReference reff;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String itemname;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.belonging_camera);
        textView=findViewById(R.id.textView6);
        reff= FirebaseDatabase.getInstance().getReference().child("picturedatabase");

        picturedatabase=new picturedatabase();
        //  Log.e("userid in camera",Singleton.userid);
        picturedatabase.setUserid(Singleton.userid);
        picturedatabase.setItemid(Singleton.itemid);
        picturedatabase.setItemname(Singleton.itemname);
        textView.setText("3");
        storageReference = FirebaseStorage.getInstance().getReference();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},  MY_CAMERA_PERMISSION_CODE);
        }
        else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Log.e("inside camera2", "1");

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }


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
                        Log.e("swipe right","1");


                    }

                    // Right to left swipe action
                    else
                    {

                        Log.e("swipe left","1");

                    }

                }
                else {

                    Log.e("tapped","1");
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        Log.e("inside camera2","1");

                        startActivityForResult(intent, CAMERA_REQUEST_CODE);

                    }

                }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Log.e("inside camera3","1");

                    startActivityForResult(intent, CAMERA_REQUEST_CODE);

                }
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("inside onresult0","1");

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            FilePathUri = getImageUri(getApplicationContext(), photo);

            Log.e("FilePathURI",String.valueOf(FilePathUri));
            String Imageid = System.currentTimeMillis() + "." + GetFileExtension(FilePathUri);
            picturedatabase.setImageid(Imageid);

            final StorageReference storageReference2 = storageReference.child("Images").child(Imageid);
            storageReference2.putFile(FilePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return storageReference2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        Log.e("download uri",downloadUri.toString());
                        picturedatabase.setImageURI(downloadUri.toString());
                        reff.push().setValue(picturedatabase);
                        Singleton.imagecount--;
                        textView.setText(String.valueOf(Singleton.imagecount));
                        if(Singleton.imagecount==0)
                        {
                            Singleton.imagecount=3;
                            finish();
                        }

                    } else
                    {
                        Toast.makeText(AddBelongingCamera.this, "Please Select Image", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }
    }
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


//public class AddBelongingCamera extends AppCompatActivity {
//    private static final int CAMERA_REQUEST_CODE=7;
//    private static final int MY_CAMERA_PERMISSION_CODE = 100;
//    Uri FilePathUri;
//    TextView textView;
//    StorageReference storageReference;
//    picturedatabase picturedatabase;
//    Intent intent;
//    DatabaseReference reff;
//    private float x1,x2;
//    static final int MIN_DISTANCE = 150;
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.belonging_camera);
//        textView=findViewById(R.id.textView6);
//        reff= FirebaseDatabase.getInstance().getReference().child("picturedatabase");
//
//
//        picturedatabase=new picturedatabase();
//        picturedatabase.setUserid(Singleton.userid);
//        picturedatabase.setItemid(Singleton.itemid);
//        storageReference = FirebaseStorage.getInstance().getReference();
//        textView.setText("3");
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//        {
//            requestPermissions(new String[]{Manifest.permission.CAMERA},  MY_CAMERA_PERMISSION_CODE);
//        }
//        else {
//            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                Log.e("inside camera2", "1");
//
//                startActivityForResult(intent, CAMERA_REQUEST_CODE);
//            }
//        }
//
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_PERMISSION_CODE)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    Log.e("inside camera3","1");
//
//                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
////            textView.setText("9");
//                }
//            }
//            else
//            {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                float deltaX = x2 - x1;
//
//                if (Math.abs(deltaX) > MIN_DISTANCE)
//                {
//                    // Left to Right swipe action
//                    if (x2 > x1)
//                    {
//                        Log.e("swipe right","1");
//
//
//                    }
//
//                    // Right to left swipe action
//                    else
//                    {
//
//                        Log.e("swipe left","1");
//
//                    }
//
//                }
//                else {
//
//                    Log.e("tapped","1");
//                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        Log.e("inside camera2","1");
//
//                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
////                        Singleton.imagecount--;
////                        textView.setText(String.valueOf(Singleton.imagecount));
////                        if(Singleton.imagecount==0)
////                        {
////                            Singleton.imagecount=9;
////                            finish();
////                        }
//                    }
//
//                }
//        }
//
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("inside onresult0","1");
//
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            FilePathUri = getImageUri(getApplicationContext(), photo);
//
//            Log.e("FilePathURI",String.valueOf(FilePathUri));
//            String Imageid = System.currentTimeMillis() + "." + GetFileExtension(FilePathUri);
//            picturedatabase.setImageid(Imageid);
//
//            StorageReference storageReference2 = storageReference.child("Images").child(Imageid);
//            storageReference2.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Singleton.imagecount--;
//                    textView.setText(String.valueOf(Singleton.imagecount));
//                    if(Singleton.imagecount==0)
//                    {
//                        Singleton.imagecount=10;
//                        finish();
//                    }
//                    reff.push().setValue(picturedatabase);
//                    Toast.makeText(AddBelongingCamera.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
//                }
//            });
//
//        }
//    }
//    public String GetFileExtension(Uri uri) {
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
//    }
//
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//}
