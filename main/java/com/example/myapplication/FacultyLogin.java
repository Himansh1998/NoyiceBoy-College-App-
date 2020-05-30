package com.example.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FacultyLogin extends AppCompatActivity {

    private EditText l1;
    private EditText p1;
    private Button b1;
    private FirebaseAuth mAuth;
    boolean active=false;
 //   private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);
/*
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
                    Intent i=getIntent();
                    String name=i.getStringExtra("name");
                    Intent i1= new Intent(FacultyLogin.this,Faculty2.class);
                    i1.putExtra("name",name);
                    startActivity(i1);
                    finish();
                }
            }
        };*/

        mAuth=FirebaseAuth.getInstance();
        l1=(EditText)findViewById(R.id.login);
        p1=(EditText)findViewById(R.id.password);
        b1=(Button)findViewById(R.id.login_button);
        //EditText edittext = (EditText)findViewById(R.id.password);
        p1.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                startSignIn();
            }

        });

    }

   /* @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
*/
    private void startSignIn(){
        String email=l1.getText().toString();
        final String password=p1.getText().toString();
        if(TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){
            Toast.makeText(FacultyLogin.this, "Fields are Empty", Toast.LENGTH_LONG).show();

        }
        else {
            final ProgressDialog progressdialog;
            progressdialog = new ProgressDialog(this);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressdialog.setTitle( "Loading.....!");
            progressdialog.show();


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        progressdialog.cancel();
                        Toast.makeText(FacultyLogin.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                        p1.setText("");
                       }
                    else {
                        progressdialog.cancel();
                        Intent i1= new Intent(FacultyLogin.this,Faculty2.class);
                        i1.putExtra("name","Faculty");
                        startActivity(i1);
                        finish();
                    }
                    //else getSharedPreferences("PREFRENCE",MODE_PRIVATE).edit().putBoolean("isFirstRUN",false).commit();
                }
            });
        }
    }

        public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            private class PasswordCharSequence implements CharSequence {
                private CharSequence mSource;
                public PasswordCharSequence(CharSequence source) {
                    mSource = source; // Store char sequence
                }
                public char charAt(int index) {
                    return '*'; // This is the important part
                }
                public int length() {
                    return mSource.length(); // Return default
                }
                public CharSequence subSequence(int start, int end) {
                    return mSource.subSequence(start, end); // Return default
                }
            }



    }

}
