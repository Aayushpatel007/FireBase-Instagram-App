package com.example.aayush.startfirebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private static  FirebaseAuth firebaseAuth;
    private DatabaseReference likedata;
    private DatabaseReference databaseReferenceusers;
    private boolean mproceeslike = false;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        };

        databaseReferenceusers = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceusers.keepSynced(true);
        likedata = FirebaseDatabase.getInstance().getReference().child("Likes");
        likedata.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            checkuserexist();
        }
        firebaseAuth.addAuthStateListener(authStateListener);
        //checkuserexist();
        final FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class, R.layout.blog_row, BlogViewHolder.class, databaseReference
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String postkey = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.SetDesc(model.getDesc());
                viewHolder.SetRupees(model.getAmount());
                viewHolder.setusername(model.getUsername());
                viewHolder.setlike(postkey);
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent=new Intent(MainActivity.this,BlogSingleActivity.class);
                        intent.putExtra("Blogid",postkey);
                        startActivity(intent);
                    }
                });




                viewHolder.likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mproceeslike = true;

                        likedata.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mproceeslike) {
                                    if (dataSnapshot.child(postkey).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                        likedata.child(postkey).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                        mproceeslike = false;
                                    } else {
                                        likedata.child(postkey).child(firebaseAuth.getCurrentUser().getUid()).setValue("Random value");
                                        mproceeslike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void checkuserexist() {

        if (firebaseAuth.getCurrentUser() != null) {
            final String userid = firebaseAuth.getCurrentUser().getUid();
            databaseReferenceusers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(userid)) {
                        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {


        View view;
        ImageButton likebutton;
        DatabaseReference likedataaa;
        FirebaseAuth mauthhh;

        public BlogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            likebutton = (ImageButton) view.findViewById(R.id.likeid);
            likedataaa = FirebaseDatabase.getInstance().getReference().child("Likes");
            mauthhh = FirebaseAuth.getInstance();
            likedataaa.keepSynced(true);

        }

        public void setlike(final String postkey) {
            likedataaa.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (firebaseAuth.getCurrentUser() == null) {


                    } else {

                        if (dataSnapshot.child(postkey).hasChild(mauthhh.getCurrentUser().getUid())) {
                            likebutton.setImageResource(R.drawable.likered);
                        } else {

                            likebutton.setImageResource(R.drawable.likegrey);


                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


        }


        public void setTitle(String title) {

            TextView posttitle = (TextView) view.findViewById(R.id.titlepostid);
            posttitle.setText(title);

        }

        public void SetDesc(String desc) {
            TextView postdesc = (TextView) view.findViewById(R.id.despostid);
            postdesc.setText(desc);
        }

        public void SetRupees(String amount) {
            TextView r = (TextView) view.findViewById(R.id.rupeespostid);
            r.setText(amount);
        }

        public void setImage(Context ctx, String image) {
            ImageView post = (ImageView) view.findViewById(R.id.imagepostid);
            Picasso.with(ctx).load(image).into(post);
        }

        public void setusername(String username) {
            TextView un = (TextView) view.findViewById(R.id.usernamepostid);
            un.setText(username);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.aboutuser){
            Intent intent=new Intent(MainActivity.this,Aboutuser.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        firebaseAuth.signOut();
    }
}
