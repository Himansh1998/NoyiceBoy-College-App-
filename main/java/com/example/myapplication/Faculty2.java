package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Faculty2 extends AppCompatActivity {

    TextView notice,dbms,sdl,cn,sepm,isee,toc;
    String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty2);

        Intent intent = getIntent();
        Name = intent.getStringExtra("name");


       /* if(Name.equals("Faculty")) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currUser = auth.getCurrentUser();
            if (currUser == null) {
                finish();
            }
        }*/

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        notice = (TextView) findViewById(R.id.notice);
        dbms = (TextView) findViewById(R.id.DBMS);
        sdl = (TextView) findViewById(R.id.SDL);
        cn = (TextView) findViewById(R.id.CN);
        sepm = (TextView) findViewById(R.id.SEPM);
        isee = (TextView) findViewById(R.id.ISEE);
        toc = (TextView) findViewById(R.id.TOC);





        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("NOTICE", Name);
            }
        });
        dbms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("DBMS", Name);
            }
        });
        sdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("SDL", Name);
            }
        });
        cn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("CN", Name);
            }
        });
        sepm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("SEPM", Name);
            }
        });
        isee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("ISEE", Name);
            }
        });
        toc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotice("TOC", Name);
            }
        });


    }

    public void openNotice(String title,String Name)
    {

        Intent in=null;

        switch(Name){
            case "Student":
                in = new Intent(this, ScrollingActivity_student.class);
                in.putExtra("Name", title);
                in.putExtra("class",Name);
                startActivity(in);
                break;
            case "Faculty":
                 in = new Intent(this, ScrollingActivity.class);
                in.putExtra("Name", title);
                in.putExtra("class",Name);
                startActivity(in);
                 break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Name.equals("Faculty")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_scrolling_activity_faculty, menu);
            MenuItem items = menu.findItem(R.id.action_logout);

            items.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    FirebaseAuth.getInstance().signOut();
                   // getSharedPreferences("PREFRENCE",MODE_PRIVATE).edit().putBoolean("isFirstRUN",true).commit();
                    finish();
                    return true;
                }
            });
            return  true;
        }
        else
        return false;
    }

}
