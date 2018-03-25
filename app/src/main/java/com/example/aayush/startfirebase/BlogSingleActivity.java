package com.example.aayush.startfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private String mpostkey = null;
    private DatabaseReference databaseReference;
    ImageView imageView;
    Button remove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        remove=(Button)findViewById(R.id.removepostid);
        imageView = (ImageView) findViewById(R.id.singleimageid);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        mpostkey = getIntent().getExtras().getString("Blogid");
        //  Toast.makeText(BlogSingleActivity.this,mpostkey,Toast.LENGTH_SHORT).show();
        databaseReference.child(mpostkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String posttitl = (String) dataSnapshot.child("title").getValue();
                String desss = (String) dataSnapshot.child("desc").getValue();
                String am = (String) dataSnapshot.child("price").getValue();
                String img = (String) dataSnapshot.child("image").getValue();
                Picasso.with(BlogSingleActivity.this).load(img).into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child(mpostkey).removeValue();
                Intent intent = new Intent(BlogSingleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
