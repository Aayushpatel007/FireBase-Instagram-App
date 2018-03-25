package com.example.aayush.startfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    Button registerbutton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    EditText nameed, emailed, passed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerbutton = (Button) findViewById(R.id.registerbuttonid);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(this);
        nameed = (EditText) findViewById(R.id.registernameid);
        emailed = (EditText) findViewById(R.id.registeremailid);
        passed = (EditText) findViewById(R.id.registerpasswordid);
        firebaseAuth = FirebaseAuth.getInstance();

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String n = nameed.getText().toString();
        final String e = emailed.getText().toString();
        String p = passed.getText().toString();

        if (!TextUtils.isEmpty(n) && !TextUtils.isEmpty(p) && !TextUtils.isEmpty(e)) {
            progressDialog.setMessage("REGISTERING USER");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String userid = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference currentuserdb = databaseReference.child(userid);
                        currentuserdb.child("name").setValue(n);
                        currentuserdb.child("image").setValue("default");
                        progressDialog.dismiss();
                        Intent intent=new Intent(Register.this,SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else{

                        Toast.makeText(Register.this,"Error",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }

    }
}
