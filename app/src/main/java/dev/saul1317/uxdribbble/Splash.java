package dev.saul1317.uxdribbble;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    private Handler handler;
    private Runnable myRunnable;
    private String TAG = "Splash";
    private ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img_logo = (ImageView) findViewById(R.id.img_logo);
    }

    //Abrir la siguiente ventana
    private void openNewActivity() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition( 0, R.anim.fade_out_animation );
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerSplash();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(myRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void timerSplash(){
        //hilo para abrir la siguiente ventana despues despues de tres segundos
        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                openNewActivity();
            }
        };
        handler.postDelayed(myRunnable, 2000);
    }

}
