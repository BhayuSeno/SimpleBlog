package com.granat.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton setupImageButton;
    private EditText setupNameField;
    private Button setupSubmitButton;
    private Uri setupImageUri = null;
    private DatabaseReference databaseReferenceUsers;
    private FirebaseAuth firebaseAuthUsers;
    private StorageReference storageReferenceImage;
    private ProgressDialog progressDialog;
    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuthUsers = FirebaseAuth.getInstance();
        storageReferenceImage = FirebaseStorage.getInstance().getReference().child("profileImages");

        setupImageButton = (ImageButton) findViewById(R.id.setupImageButton);
        setupNameField = (EditText) findViewById(R.id.setupNameField);
        setupSubmitButton = (Button) findViewById(R.id.setupSubmitButton);
        progressDialog = new ProgressDialog(this);

        setupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        setupSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {
        final String nameValue = setupNameField.getText().toString().trim();
        final String userId = firebaseAuthUsers.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(nameValue) && setupImageUri != null){
            progressDialog.setMessage("Setting up user, please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference filePath = storageReferenceImage.child(setupImageUri.getLastPathSegment());
            filePath.putFile(setupImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    databaseReferenceUsers.child(userId).child("name").setValue(nameValue);
                    databaseReferenceUsers.child(userId).child("image").setValue(downloadUri);
                    progressDialog.dismiss();
                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });


            /**/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                setupImageUri = result.getUri();
                setupImageButton.setImageURI(setupImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}
