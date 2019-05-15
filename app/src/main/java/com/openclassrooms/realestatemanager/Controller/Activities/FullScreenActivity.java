package com.openclassrooms.realestatemanager.Controller.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;

/*
 * This activity is used only on smart phone to display an image in fullscreen
 * When the user click on the image we display the description of it
 */
public class FullScreenActivity extends AppCompatActivity {
    private TextView descriptionView;
    private boolean isDescriptionVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        ImageView img = findViewById(R.id.activity_fullscreen_img);
        descriptionView = findViewById(R.id.activity_fullscreen_description);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).load(Uri.parse(getIntent().getStringExtra("uri"))).into(img);
        descriptionView.setText(getIntent().getStringExtra("description"));

        img.setOnClickListener(v -> {
            if(!isDescriptionVisible) {
                descriptionView.setVisibility(View.VISIBLE);
                isDescriptionVisible = true;
            }
            else {
                descriptionView.setVisibility(View.GONE);
                isDescriptionVisible = false;
            }
        });
    }


}
