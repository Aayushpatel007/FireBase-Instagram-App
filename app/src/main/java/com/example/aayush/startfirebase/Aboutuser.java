package com.example.aayush.startfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Aboutuser extends AppCompatActivity {

    ImageView imageView;
    TextView username;
    DatabaseReference databaseReferenceusers;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutuser);
        imageView = (ImageView) findViewById(R.id.imgidid);
        username = (TextView) findViewById(R.id.textidid);
        databaseReferenceusers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceusers.keepSynced(true);

        firebaseAuth=FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReferenceusers.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String proname= (String) dataSnapshot.child("name").getValue();
                    String proimg= (String) dataSnapshot.child("image").getValue();
                    System.out.println(proname);
                    username.setText(proname);
                    Picasso.with(Aboutuser.this).load(proimg).into(imageView);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}
