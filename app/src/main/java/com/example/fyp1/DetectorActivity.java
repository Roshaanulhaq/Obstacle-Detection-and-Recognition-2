package com.example.fyp1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;


import com.example.fyp1.customview.OverlayView;
import com.example.fyp1.env.BorderedText;
import com.example.fyp1.env.ImageUtils;
import com.example.fyp1.env.Logger;
import com.example.fyp1.tracking.MultiBoxTracker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();

  // Configuration values for the prepackaged SSD model.
  private static final int TF_OD_API_INPUT_SIZE = 300;
  private static final boolean TF_OD_API_IS_QUANTIZED = true;
  private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;
  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
  private static final boolean MAINTAIN_ASPECT = false;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;
  OverlayView trackingOverlay;
  private Integer sensorOrientation;
  private TextToSpeech myTTS;
  private Classifier detector;
  ArrayList<DetectionInfo> detectionInfos;
  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private boolean computingDetection = false;
  private int semaphorecheck;
  private int secondsemaphorecheck;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    semaphorecheck=0;
  }

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;
  private int check;
  private MultiBoxTracker tracker;
  private String previoustitle=null;
  private BorderedText borderedText;
  private String positionvertical;
  private String positionhorizontal;
  @Override
  public synchronized void onStart() {
    super.onStart();
    detectionInfos=new ArrayList<>();

  }

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);
    initializeTTS();
    tracker = new MultiBoxTracker(this);

    int cropSize = TF_OD_API_INPUT_SIZE;

    try {
      detector =
          TFLiteObjectDetectionAPIModel.create(
              getAssets(),
              TF_OD_API_MODEL_FILE,
              TF_OD_API_LABELS_FILE,
              TF_OD_API_INPUT_SIZE,
              TF_OD_API_IS_QUANTIZED);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      e.printStackTrace();
      LOGGER.e(e, "Exception initializing classifier!");
      Toast toast =
          Toast.makeText(
              getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
        ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
            sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
        new OverlayView.DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            tracker.draw(canvas);
            if (isDebug()) {
              tracker.drawDebug(canvas);
            }
          }
        });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
  }

  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    runInBackground(
        new Runnable() {
          @Override
          public void run() {

              LOGGER.i("Running detection on image " + currTimestamp);
              final long startTime = SystemClock.uptimeMillis();
              final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
              lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

              cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
              final Canvas canvas = new Canvas(cropCopyBitmap);
              final Paint paint = new Paint();
              paint.setColor(Color.RED);
              paint.setStyle(Style.STROKE);
              paint.setStrokeWidth(2.0f);

              float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
              switch (MODE) {
                case TF_OD_API:
                  minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                  break;
              }

              final List<Classifier.Recognition> mappedRecognitions =
                      new LinkedList<Classifier.Recognition>();

              for (final Classifier.Recognition result : results) {
                final RectF location = result.getLocation();
                if (location != null && result.getConfidence() >= minimumConfidence) {
                  canvas.drawRect(location, paint);

                  cropToFrameTransform.mapRect(location);
//                  Log.e("Left side", String.valueOf(location.left));
//                  Log.e("Right side", String.valueOf(location.right));
                Log.e("Top side",String.valueOf(location.top));
                Log.e("Bottom side",String.valueOf(location.bottom));
                Log.e("Title",result.getTitle());
                  result.setLocation(location);
                  mappedRecognitions.add(result);


                  if (location.top > 250) {
                    positionvertical = "Bottom";
                  } else {
                    positionvertical = "Top";
                  }

                  if (location.right < 350) {
                    positionhorizontal = "Left";
                  } else {
                    positionhorizontal = "right";
                  }
                  check = 0;
                  for (DetectionInfo detection : detectionInfos) {
                    if (detection.title.equalsIgnoreCase(result.getTitle())) {
                      int index = detectionInfos.indexOf(detection);
                      check = 1;
                      Date d1, d2;
                      String detection_time = detection.duration;
                      Calendar c = Calendar.getInstance();
                      SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");
                      String datetime = dateformat.format(c.getTime());
                      try {
                        d1 = dateformat.parse(detection_time);
                        d2 = dateformat.parse(datetime);
                        long diff = d2.getTime() - d1.getTime();
                        long diffSeconds = diff / 1000 % 60;

                        long diffMinutes = diff / (60 * 1000) % 60;

                        if (diffSeconds > 5) {

                          speak(detection.title + " at " + positionvertical + " " + positionhorizontal);
                          detection.duration = datetime;
                          detection.locationvertical = positionvertical;
                          detection.locationhorizontal = positionhorizontal;
                          detectionInfos.set(index, detection);


                        }

                      } catch (ParseException e) {
                        e.printStackTrace();
                      }

                    }

                  }
                  if (check == 0) {
                    if (semaphorecheck == 0) {
                      semaphorecheck=1;
                      DetectionInfo detectionInfo = new DetectionInfo();
                      detectionInfo.title = result.getTitle();
                      Calendar c = Calendar.getInstance();
                      SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");
                      String datetime = dateformat.format(c.getTime());
                      detectionInfo.duration = datetime;
                      detectionInfo.locationhorizontal = positionhorizontal;
                      detectionInfo.locationvertical = positionvertical;
                      detectionInfos.add(detectionInfo);
                      speak("There is a " + result.getTitle() + " at " + positionvertical + " " + positionhorizontal);
                      semaphorecheck = 0;

                    }
                  }
                }
              }

              tracker.trackResults(mappedRecognitions, currTimestamp);
              trackingOverlay.postInvalidate();

              computingDetection = false;

              runOnUiThread(
                      new Runnable() {
                        @Override
                        public void run() {
//                    showFrameInfo(previewWidth + "x" + previewHeight);
//                    showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
//                    showInference(lastProcessingTimeMs + "ms");
                        }
                      });
            }


        });
  }


    @Override
  protected int getLayoutId() {
    return R.layout.tfe_od_camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }

  @Override
  protected void setUseNNAPI(final boolean isChecked) {
    runInBackground(() -> detector.setUseNNAPI(isChecked));
  }

  @Override
  protected void setNumThreads(final int numThreads) {
    runInBackground(() -> detector.setNumThreads(numThreads));
  }

  public void initializeTTS() {

    myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (myTTS.getEngines().size() == 0)   // checking if any engine found
        {
          Toast.makeText(getApplicationContext(), "There is no TTS engine", Toast.LENGTH_SHORT).show();

        } else {
          try {
            myTTS.setLanguage(Locale.US);

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
