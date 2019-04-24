package com.openclassrooms.realestatemanager.Controller.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;

public class FullScreenActivity extends AppCompatActivity {
    private ImageView img;
    private TextView descriptionView;
    private boolean isDescritpionVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        img = findViewById(R.id.activity_fullscreen_img);
        descriptionView = findViewById(R.id.activity_fullscreen_description);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).load(Uri.parse(getIntent().getStringExtra("uri"))).into(img);
        descriptionView.setText(getIntent().getStringExtra("description"));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDescritpionVisible) {
                    descriptionView.setVisibility(View.VISIBLE);
                    isDescritpionVisible = true;
                }
                else {
                    descriptionView.setVisibility(View.GONE);
                    isDescritpionVisible = false;
                }
            }
        });
    }


}
