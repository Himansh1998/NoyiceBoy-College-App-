package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class ScrollingActivity_student extends AppCompatActivity {

    FirebaseStorage storage;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    CollapsingToolbarLayout TBar;
    Intent in;
    Uri fileuri;
    String Title;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.S_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.S_recyclerView);
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.S_toolbar);
        setSupportActionBar(toolbar);


        TBar = (CollapsingToolbarLayout) findViewById(R.id.S_toolbar_layout);
        in = getIntent();
        Title = in.getStringExtra("Name");
        TBar.setTitle(Title);


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child(Title);
        dataref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //call indv items

                String itemfileName = dataSnapshot.getKey();
                String url = dataSnapshot.getValue(String.class);
                ((MyAdapter) recyclerView.getAdapter()).update(itemfileName, url);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(ScrollingActivity_student.this));
        myAdapter = new MyAdapter(recyclerView, ScrollingActivity_student.this, new ArrayList<String>(), new ArrayList<String>(),Title,"ForStudent");
        recyclerView.setAdapter(myAdapter);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.searching,menu);
        MenuItem searchitems = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchitems.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

}
