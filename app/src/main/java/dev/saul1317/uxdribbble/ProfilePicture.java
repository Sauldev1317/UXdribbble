package dev.saul1317.uxdribbble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class ProfilePicture extends AppCompatActivity {

    private ImageView img_profile_picture_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        img_profile_picture_edit = (ImageView) findViewById(R.id.img_profile_picture_edit);
    }
}
