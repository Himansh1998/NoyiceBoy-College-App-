package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button BTstudentlogin ,BTfacultylogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BTstudentlogin=(Button) findViewById(R.id.BTStudentlogin);
        BTfacultylogin=(Button) findViewById(R.id.BTfacultylogin);

        BTstudentlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               studentLogin();

            }
        });

        BTfacultylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facultyLogin();
            }
        });



    }


    private void facultyLogin() {

      //  Boolean isFirst = getSharedPreferences("PREFRENCE",MODE_PRIVATE).getBoolean("isFirstRUN",true);
/*
        if(isFirst) {
            }

        else
        {
            Intent in = new Intent(MainActivity.this, Faculty2.class);
            in.putExtra("name","Faculty");
            startActivity(in);
        }*/
FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
                if(firebaseAuth.getCurrentUser() != null){
                    Intent i1= new Intent(MainActivity.this,Faculty2.class);
                    i1.putExtra("name","Faculty");
                    startActivity(i1);
                }
                else {
                    Intent in = new Intent(MainActivity.this, FacultyLogin.class);
                    in.putExtra("name","Faculty");
                    startActivity(in);
                }





    }

    private void studentLogin()
    {
        Intent in =new Intent( MainActivity.this,Faculty2.class);
        in.putExtra("name","Student");
        startActivity(in);
    }
}
