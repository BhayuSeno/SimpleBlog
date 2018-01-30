package com.granat.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 32;
    private ImageButton imageSelect;
    private EditText postTitle, postDesc;
    private Button submitButton;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageSelect = (ImageButton) findViewById(R.id.imageSelect);
        postTitle = (EditText) findViewById(R.id.titleField);
        postDesc = (EditText) findViewById(R.id.descField);
        submitButton = (Button) findViewById(R.id.submitButton);
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("com.granat.simpleblog.Blog");

        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, GALLERY_REQUEST);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        try {
            progressDialog.setMessage("Posting");
            progressDialog.show();
            final String titleValue = postTitle.getText().toString().trim();
            final String descValue = postDesc.getText().toString().trim();

            if (!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(descValue) && imageUri != null){
                StorageReference filePath = storageReference.child("Blog_Images").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            DatabaseReference createNewPost = databaseReference.push();
                            createNewPost.child("post_title").setValue(titleValue);
                            createNewPost.child("post_desc").setValue(descValue);
                            createNewPost.child("post_image_url").setValue(downloadUri.toString());
                            progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }
    /*public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            imageSelect.setImageURI(imageUri);
        }
    }
}
