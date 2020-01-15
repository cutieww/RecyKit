package com.example.recykit.recykit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AfterML extends Activity {

    RelativeLayout mlRelLayout;
    TextView typeText, materialText;
    ImageView typeImage;
    String material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_ml);

        mlRelLayout = (RelativeLayout)findViewById(R.id.mlRelLayout);
        typeImage = (ImageView)findViewById(R.id.typeImage);
        typeText = (TextView)findViewById(R.id.typeText);
        materialText = (TextView)findViewById(R.id.materialText);

        material = getIntent().getExtras().getString("material");

        materialText.setText(material);

        if (material.equals("metal") || material.equals("plastic")) {
            typeText.setText("RecyKit!");
            typeImage.setImageResource(R.drawable.recycle_image);
        }
        else {
            typeText.setText("!RecyKit");
            typeImage.setImageResource(R.drawable.trash_image);
        }

        mlRelLayout.setVisibility(View.VISIBLE);

    }


}
