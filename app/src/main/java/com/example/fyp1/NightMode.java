package com.example.fyp1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class NightMode extends AppCompatActivity {
    Camera camera;
    private  Camera.Parameters parameters;
    private CameraManager mCameraManager;
    private String mCameraId;
    FrameLayout cameraPreviewLayout;
    CameraPreview cameraPreview;
    private static final int PERMISSIONS_REQUEST = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.night_mode_activity);
            cameraPreviewLayout=findViewById(R.id.container);
        boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            showNoFlashError();
        }

    }
    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }
    @Override
    protected void onResume() {
        super.onResume();


        if (hasPermission()) {
            setFragment();
        } else {
            Log.e("reqyes","1");
            requestPermission();
        }




    }
    public void setFragment()
    {
        camera = getCameraInstance();

        if(camera != null) {
            try {
                parameters=camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
            }
            catch (Exception ex)
            {
                Log.e("inside exception","1");
            }
            setupCamera();

        }
        else {

            /**
             * Delay is necessary for cases when you go back from the ScanReceiptBarcodeActivity
             * and need to wait a bit before Camera is released
             */
            cameraPreviewLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    camera = getCameraInstance();
                    if(camera != null) {
                        setupCamera();
                    }
                }
            }, 300);

        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions,
                                           final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setFragment();
                } else {
                    requestPermission();
                }
            }
        }
    }
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(NightMode.this,
                        "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        }
    }
    void setupCamera() {
        cameraPreview = new CameraPreview(NightMode.this, camera);

        cameraPreviewLayout.removeAllViews();
        cameraPreviewLayout.addView(cameraPreview);

    }
    public static Camera getCameraInstance() {
        Camera c = null;
        try {

            c = Camera.open(); // attempt to get a Camera instance


            Log.e("inside camera","1");
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e("inside camera","0");
        }
        return c; // returns null if camera is unavailable
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(camera!=null)
        {
            camera.release();
            camera=null;
            parameters=null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(camera!=null)
        {
            camera.release();
            camera=null;
            parameters=null;
        }
    }
}

