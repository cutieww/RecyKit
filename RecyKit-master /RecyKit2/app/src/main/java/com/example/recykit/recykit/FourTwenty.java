package com.example.recykit.recykit;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class FourTwenty extends Activity{
    CameraView cameraView;
    ImageButton btnDetect, ml_to_bar;
    AlertDialog waitingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_four_twenty);

        ml_to_bar = (ImageButton)findViewById(R.id.ml_to_bar);

        ml_to_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = savedInstanceState
                startActivity(new Intent(getApplicationContext(), App.class));
//                getApplication().startActivity(intent);
            }
        });

        cameraView = (CameraView)findViewById(R.id.camera_view2);
        btnDetect = (ImageButton)findViewById(R.id.btn_detect2);
        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                System.out.println(bitmap);

                runDetector(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void runDetector(Bitmap bitmap){

        //load the model
        FirebaseLocalModel localModel = new FirebaseLocalModel.Builder("my_local_model")
                .setAssetFilePath("finalmodel/manifest.json")
                .build();
        FirebaseModelManager.getInstance().registerLocalModel(localModel);

        //preparing input image
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        //run the image labeler
        FirebaseVisionOnDeviceAutoMLImageLabelerOptions labelerOptions =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                        .setLocalModelName("my_local_model")    // Skip to not use a local model
                        .setConfidenceThreshold(0)  // Evaluate your model in the Firebase console
                        // to determine an appropriate value.
                        .build();
        try {
            FirebaseVisionImageLabeler labeler =
                    FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions);

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {

                            processDataResultModel(labels);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("HDERROR", e.getMessage());
                        }
                    });
        } catch (FirebaseMLException e) {

            Toast.makeText(this,"Cloud Result: "+e.toString(),Toast.LENGTH_SHORT).show();
        }


    }

    private void processDataResultModel(List<FirebaseVisionImageLabel> firebaseVisionCloudLabels) {
        for (FirebaseVisionImageLabel label: firebaseVisionCloudLabels){
            if (waitingDialog.isShowing()){
                waitingDialog.dismiss();

            }
            goToResultsPage(label.getText());
//            if (label.getText().equals("metal") || label.getText().equals("plastic")) {
//                info = new StringBuilder("")
//                        .append("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t")
//                        .append("It's ")
//                        .append(label.getText())
//                        .append("\n")
//                        .append("\n")
//                        .append("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t")
//                        .append("RecyKit!")
//                        .toString();
//            }else {
//                info = new StringBuilder("")
//                        .append("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t")
//                        .append("It's ")
//                        .append(label.getText())
//                        .append("\n")
//                        .append("\n")
//                        .append("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t")
//                        .append("!RecyKit")
//                        .toString();
//            }
//            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
//            builder.setMessage(info);
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                    onResume();
//                }
//            });
//            android.support.v7.app.AlertDialog dialog = builder.create();
//            dialog.show();

//            Toast.makeText(this,"Result: "+label.getText()+label.getConfidence(),Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void goToResultsPage(String material) {
        Intent intent = new Intent(this, AfterML.class);
        Bundle bundle = new Bundle();
        bundle.putString("material", material);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }
}
