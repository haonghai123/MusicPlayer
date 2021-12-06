package com.example.soc_macmini_15.musicplayer.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.soc_macmini_15.musicplayer.R;
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent =new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();
            }
        },1500);
    }
}