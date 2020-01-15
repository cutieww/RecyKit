package com.example.recykit.recykit;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Releasable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AfterScan extends Activity {

    String barcode;
    DatabaseReference recycleDB;
    RelativeLayout linearLayout, crowd_sourcing, resultsLayout, verifiedLayout;
    ImageButton imageRecy, imageWaste;
    ImageView verifiedImage, typeImage;
    TextView verifiedText, typeText, barcodeText, textViewWaste, textViewRecy;
    ProgressBar progressBarWaste, progressBarRecy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);

        crowd_sourcing = (RelativeLayout)findViewById(R.id.crowd_sourcing);
        resultsLayout = (RelativeLayout)findViewById(R.id.resultsLayout);
        verifiedLayout = (RelativeLayout)findViewById(R.id.verifiedLayout);
        linearLayout = (RelativeLayout)findViewById(R.id.linearLayout);
        imageRecy = (ImageButton)findViewById(R.id.imageRecy);
        imageWaste = (ImageButton)findViewById(R.id.imageWaste);
        verifiedImage = (ImageView)findViewById(R.id.verifiedImage);
        typeImage = (ImageView)findViewById(R.id.typeImage);
        verifiedText = (TextView)findViewById(R.id.verifiedText);
        typeText = (TextView)findViewById(R.id.typeText);
        barcodeText = (TextView)findViewById(R.id.barcodeText);
        textViewWaste = (TextView)findViewById(R.id.textViewWaste);
        textViewRecy = (TextView)findViewById(R.id.textViewRecy);
        progressBarWaste = (ProgressBar)findViewById(R.id.vote1);
        progressBarRecy = (ProgressBar)findViewById(R.id.vote2);

        barcode = getIntent().getExtras().getString("barcode");
        recycleDB = FirebaseDatabase.getInstance().getReference().child("barcode");
        recycleDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    // The child exist
                    for (final DataSnapshot childsnapshot : dataSnapshot.getChildren()) {

                        final DataSnapshot childsnapshot_local = childsnapshot;

                        if(childsnapshot.child("barcode").getValue().equals(barcode)) {

                            barcodeText.setText("Barcode: " + barcode);

                            if(childsnapshot.child("Verified").getValue().equals("False")) {

                                crowd_sourcing.setVisibility(View.VISIBLE);

                                imageWaste.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        crowd_sourcing.setVisibility(View.GONE);
                                        resultsLayout.setVisibility(View.VISIBLE);
                                        int waste_persent;
                                        int recy_persent;
                                        int waste_vote = Integer.parseInt(childsnapshot_local.child("NonRecycleVotes").getValue().toString());
                                        int recy_vote = Integer.parseInt(childsnapshot_local.child("RecycleVotes").getValue().toString());
                                        imageWaste.setVisibility(View.GONE);
                                        imageRecy.setVisibility(View.GONE);
                                        textViewWaste.setVisibility(View.VISIBLE);
                                        textViewRecy.setVisibility(View.VISIBLE);
                                        progressBarWaste.setVisibility(View.VISIBLE);
                                        progressBarRecy.setVisibility(View.VISIBLE);

                                        waste_persent = ((100*waste_vote)/(waste_vote+recy_vote));
                                        recy_persent =(((100*recy_vote)/(waste_vote+recy_vote)));
                                        progressBarWaste.setProgress(waste_persent);
                                        progressBarRecy.setProgress(recy_persent);
                                        final Map newMessageMap = new HashMap<>();
                                        newMessageMap.put("NonRecycleVotes", waste_vote + 1);
                                        recycleDB.child(childsnapshot_local.getKey()).updateChildren(newMessageMap);
                                    }
                                });
                                imageRecy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        crowd_sourcing.setVisibility(View.GONE);
                                        resultsLayout.setVisibility(View.VISIBLE);
                                        int waste_persent;
                                        int recy_persent;
                                        int waste_vote = Integer.parseInt(childsnapshot_local.child("NonRecycleVotes").getValue().toString());
                                        int recy_vote = Integer.parseInt(childsnapshot_local.child("RecycleVotes").getValue().toString());
                                        imageWaste.setVisibility(View.GONE);
                                        imageRecy.setVisibility(View.GONE);
                                        textViewWaste.setVisibility(View.VISIBLE);
                                        textViewRecy.setVisibility(View.VISIBLE);
                                        progressBarWaste.setVisibility(View.VISIBLE);
                                        progressBarRecy.setVisibility(View.VISIBLE);

                                        waste_persent = ((100*waste_vote)/(waste_vote+recy_vote));
                                        recy_persent =(((100*recy_vote)/(waste_vote+recy_vote)));
                                        progressBarWaste.setProgress(waste_persent);
                                        progressBarRecy.setProgress(recy_persent);
                                        final Map newMessageMap = new HashMap<>();
                                        newMessageMap.put("RecycleVotes", recy_vote + 1);
                                        recycleDB.child(childsnapshot_local.getKey()).updateChildren(newMessageMap);
                                    }
                                });

                                verifiedText.setText("not verified by moderators");
                                verifiedImage.setImageResource(R.drawable.not_verified_image);

                                if(Integer.parseInt(childsnapshot.child("NonRecycleVotes").getValue().toString())
                                        == Integer.parseInt(childsnapshot.child("RecycleVotes").getValue().toString())) {
                                    typeImage.setImageResource(R.drawable.not_sure_image);
                                    typeText.setText("Not Sure???");
                                }

                                else if(Integer.parseInt(childsnapshot.child("NonRecycleVotes").getValue().toString())
                                        > Integer.parseInt(childsnapshot.child("RecycleVotes").getValue().toString())) {
                                    typeImage.setImageResource(R.drawable.trash_image);
                                    typeText.setText("Non-Recyclable");
                                }

                                else {
                                    typeImage.setImageResource(R.drawable.recycle_image);
                                    typeText.setText("Recyclable");
                                }
                            }

                            else {
                                verifiedLayout.setVisibility(View.VISIBLE);
                                verifiedText.setText("verified by moderators");
                                verifiedImage.setImageResource(R.drawable.verified_image);
                                if(childsnapshot.child("Type").getValue().equals("N")) {
                                    typeText.setText("Non-Recyclable");
                                    typeImage.setImageResource(R.drawable.trash_image);
                                }
                                else {
                                    typeImage.setImageResource(R.drawable.recycle_image);
                                    typeText.setText("Recyclable");
                                }
                            }

                            linearLayout.setVisibility(View.VISIBLE);

                            return;
                        }
                    }
                }
                System.out.println("I AM IN ELSE");
                barcodeText.setText("Barcode: " + barcode);
                crowd_sourcing.setVisibility(View.VISIBLE);
                verifiedText.setText("not verified by moderators");
                verifiedImage.setImageResource(R.drawable.not_verified_image);
                typeImage.setImageResource(R.drawable.not_sure_image);
                typeText.setText("Not Sure???");
                linearLayout.setVisibility(View.VISIBLE);
                imageWaste.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crowd_sourcing.setVisibility(View.GONE);
                        resultsLayout.setVisibility(View.VISIBLE);
                        int waste_persent=0;
                        int recy_persent=0;
//                        int waste_vote = 0;
//                        int recy_vote = 0;
                        imageWaste.setVisibility(View.GONE);
                        imageRecy.setVisibility(View.GONE);
                        textViewWaste.setVisibility(View.VISIBLE);
                        textViewRecy.setVisibility(View.VISIBLE);
                        progressBarWaste.setVisibility(View.VISIBLE);
                        progressBarRecy.setVisibility(View.VISIBLE);

//                        waste_persent = ((100*waste_vote)/(waste_vote+recy_vote));
//                        recy_persent =(((100*recy_vote)/(waste_vote+recy_vote)));
                        progressBarWaste.setProgress(waste_persent);
                        progressBarRecy.setProgress(recy_persent);
                        final Map myMap = new HashMap<>();
                        myMap.put("barcode", barcode);
                        myMap.put("NonRecycleVotes", 1);
                        myMap.put("RecycleVotes", 0);
                        myMap.put("Type", "NotSure");
                        myMap.put("Verified", "False");
                        recycleDB.push().setValue(myMap);

                    }
                });
                imageRecy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crowd_sourcing.setVisibility(View.GONE);
                        resultsLayout.setVisibility(View.VISIBLE);
                        int waste_persent=0;
                        int recy_persent=0;
//                        int waste_vote = 0;
//                        int recy_vote = 0;
                        imageWaste.setVisibility(View.GONE);
                        imageRecy.setVisibility(View.GONE);
                        textViewWaste.setVisibility(View.VISIBLE);
                        textViewRecy.setVisibility(View.VISIBLE);
                        progressBarWaste.setVisibility(View.VISIBLE);
                        progressBarRecy.setVisibility(View.VISIBLE);

//                        waste_persent = ((100*waste_vote)/(waste_vote+recy_vote));
//                        recy_persent =(((100*recy_vote)/(waste_vote+recy_vote)));
                        progressBarWaste.setProgress(waste_persent);
                        progressBarRecy.setProgress(recy_persent);
                        final Map myMap = new HashMap<>();
                        myMap.put("barcode", barcode);
                        myMap.put("NonRecycleVotes", 0);
                        myMap.put("RecycleVotes", 1);
                        myMap.put("Type", "NotSure");
                        myMap.put("Verified", "False");
                        recycleDB.push().setValue(myMap);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
