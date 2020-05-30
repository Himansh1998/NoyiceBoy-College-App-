package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {
    CollapsingToolbarLayout TBar;
    Intent in;
    Uri fileuri;
    String Title;
    TextView tw;
    FirebaseStorage storage;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ProgressDialog progressdialog;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        Toolbar toolbar = (Toolbar) findViewById(R.id.S_toolbar);
        setSupportActionBar(toolbar);


        TBar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        in = getIntent();
        Title = in.getStringExtra("Name");
        TBar.setTitle(Title);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();

            }
        });

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

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(ScrollingActivity.this));
        myAdapter = new MyAdapter(recyclerView, ScrollingActivity.this, new ArrayList<String>(), new ArrayList<String>(),Title,"ForFaculty");
        recyclerView.setAdapter(myAdapter);


    }
//////////////////////////////////////////////////////////////////////////

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


    ///////////////////////////////////////////////////////////////



    void openFile() {

        if (ContextCompat.checkSelfPermission(ScrollingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else {
            ActivityCompat.requestPermissions(ScrollingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else
            Toast.makeText(ScrollingActivity.this, "NOT SELECT ANY FILE", Toast.LENGTH_SHORT).show();
    }

    void selectFile() {
        Intent in = new Intent();
        in.setType("application/pdf");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(in, 8);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 8 && resultCode == RESULT_OK && data != null) {
            fileuri = data.getData();
           uploadDialogBox(fileuri);
        } else
            Toast.makeText(ScrollingActivity.this, "NOT SELECT ANY FILE", Toast.LENGTH_SHORT).show();

    }

void uploadDialogBox(final Uri file)
{


    AlertDialog.Builder dia =new AlertDialog.Builder(this);

    LayoutInflater inflater =this.getLayoutInflater();
    final View view1 =inflater.inflate(R.layout.dialogupload,null);
    dia.setView(view1);

    dia.setTitle("Upload");
    TextView select  = (TextView) view1.findViewById(R.id.selectedfile);
    final String filename = getFileName(file);
    select.setText("SELECTED FILE - "+filename);

    dia.setPositiveButton("ok", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
try {
    EditText newname = (EditText) view1.findViewById(R.id.newname);

    // Extract File name from URI
    String ext = extenstion(file);
    String newfilename = newname.getText().toString();

    if (newfilename.equals("")) {
        uploadFile(file, filename, ext);
    } else uploadFile(file, newfilename, ext);
}catch (Exception e)
{
    Toast.makeText(ScrollingActivity.this,"ENTER PROPER FILENAME",Toast.LENGTH_SHORT);
}
        }
    }).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    });

    dia.show();

}
    void uploadFile(final Uri file,final String filename, final String ext) {

        try {
            final StorageReference stRef;
            storage = FirebaseStorage.getInstance();
            database = FirebaseDatabase.getInstance();
            stRef = storage.getReference();


            progressdialog = new ProgressDialog(this);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressdialog.setTitle("Uploading " + filename + ".....!");
            progressdialog.setProgress(0);
            progressdialog.show();


            final String downloadUrl;

            stRef.child(Title).child(filename + "." + ext).putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            stRef.child(Title).child(filename + "." + ext).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    DatabaseReference dataRef = database.getReference();
                                    dataRef.child(Title).child(filename + "_" + ext).setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(ScrollingActivity.this, filename + " UPLOADED", Toast.LENGTH_SHORT).show();
                                                progressdialog.cancel();

                                            } else
                                                Toast.makeText(ScrollingActivity.this, "UNUPLOADED", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(ScrollingActivity.this, "UNUPLOADED", Toast.LENGTH_SHORT).show();

                            // ...
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int currentprog = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressdialog.setProgress(currentprog);
                }
            });




        }
        catch(Exception e)
        {
            Toast.makeText(ScrollingActivity.this, "UNUPLOADED -- "+e.toString(), Toast.LENGTH_SHORT).show();

        }
    }


    public String extenstion(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        int ind = result.lastIndexOf('.');

        if(ind!=-1)
        {
            result = result.substring(ind+1);
        }
        else
            result="";

        return result;
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        try {
            int ind = result.lastIndexOf('.');
            result = result.substring(0, ind);
        } catch (Exception e) {

        }

        return result;
    }

}
