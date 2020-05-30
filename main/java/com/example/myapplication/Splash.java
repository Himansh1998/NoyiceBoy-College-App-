package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity  {

    TextView t1;
    ImageView i1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        t1= findViewById(R.id.textView);
        i1= findViewById(R.id.imageView2);

        Animation anim= AnimationUtils.loadAnimation(this,R.anim.transaction);
        t1.setAnimation(anim);
        i1.setAnimation(anim);
        final Intent intent=new Intent(this,MainActivity.class);
        Thread timer=new Thread(){

        public void run ()
        {
            try{

                sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally {
                startActivity(intent);
                finish();
            }
        }
        };
        timer.start();
    }
}
