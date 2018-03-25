package com.example.aayush.startfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.R.attr.bitmap;

public class SetupActivity extends AppCompatActivity {
    ImageButton imageButton;
    Bitmap bitmap;
    Uri filepath = null;
    EditText name;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Uri mimageuri = null;
    private static int gal = 2;
    Button setupbutton;
    private DatabaseReference databaseReferenceusers;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        imageButton = (ImageButton) findViewById(R.id.setupimageButton);
        name = (EditText) findViewById(R.id.setupnameid);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_images");
        setupbutton = (Button) findViewById(R.id.setupbuttonid);
        databaseReferenceusers = FirebaseDatabase.getInstance().getReference().child("Users");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/+");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), gal);
                showFileChooser();
            }
        });
        setupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startsetupaccount();
            }
        });
    }

    private void startsetupaccount() {


        final String n = name.getText().toString();
        final String id = firebaseAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(n) && filepath != null) {

            progressDialog.setMessage("Finishing Setup");
            progressDialog.show();
            StorageReference f = storageReference.child(filepath.getLastPathSegment());
            f.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String down = taskSnapshot.getDownloadUrl().toString();
                    databaseReferenceusers.child(id).child("name").setValue(n);
                    databaseReferenceusers.child(id).child("image").setValue(down);
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


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

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ga && resultCode == RESULT_OK && data != null && data.getData() != null) {

             imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                imageButton.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

               // imageButton.setImageBitmap(bitmap);
              //  Uri resultUri = result.getUri();
            //    imageButton.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
*/
}
