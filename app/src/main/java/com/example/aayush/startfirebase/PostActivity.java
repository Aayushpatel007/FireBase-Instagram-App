package com.example.aayush.startfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.R.attr.bitmap;

public class PostActivity extends AppCompatActivity {

    ImageButton imageButton;
    EditText title, des,price;
    Button submit;
    Uri filepath = null;
    private static final int gal = 2;
    Bitmap bitmap;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceuser;
    StorageReference mstoragerefrence;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        mstoragerefrence = FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        imageButton = (ImageButton) findViewById(R.id.imagebuttonid);
        title = (EditText) findViewById(R.id.nameid);
        des = (EditText) findViewById(R.id.descrid);
        databaseReferenceuser=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        submit = (Button) findViewById(R.id.buttonid);
        price=(EditText)findViewById(R.id.priceid);
        progressDialog = new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
                //     Intent intent=new Intent(Intent.ACTION_PICK);
                //   intent.setType("image/+");
                // startActivityForResult(intent,gal);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startposting();

            }


        });


    }

    private void startposting() {

        progressDialog.setMessage("Posting....");
        progressDialog.show();
        final String titleval = title.getText().toString().trim();
        final String desval = des.getText().toString().trim();
        final  String amount=price.getText().toString().trim();
        if (!TextUtils.isEmpty(titleval) && !TextUtils.isEmpty(desval) && filepath != null) {

            StorageReference f = mstoragerefrence.child("Images").child(filepath.getLastPathSegment());
            f.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri dow = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost=databaseReference.push();
                    databaseReferenceuser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newpost.child("title").setValue(titleval);
                            newpost.child("desc").setValue(desval);
                            newpost.child("image").setValue(dow.toString());
                            newpost.child("uid").setValue(firebaseUser.getUid());
                            newpost.child("price").setValue(amount);

                            newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });
                    progressDialog.dismiss();
                   // startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });

        }


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), gal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gal && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageButton.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " =? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;

    }


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gal && requestCode == RESULT_OK) {
            Uri imageurl = data.getData();
            imageButton.setImageURI(imageurl);
            String path = getPathFromURI(imageurl);
          //  Log.i(TAG, "Image Path : " + path);
            // Set the image in ImageView
            imageButton.setImageURI(imageurl);


        }


    }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
*/
}
