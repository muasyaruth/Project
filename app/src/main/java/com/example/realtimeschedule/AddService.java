package com.example.realtimeschedule;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.PrivateKey;

public class AddService extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST=1;//constant

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView sImageView;
    private ProgressBar mProgressBar;
    
    private Uri sImageUri;
    
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask sUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_service);

        mButtonChooseImage= findViewById(R.id.chooseImage);//Button to go to images
        mButtonUpload= findViewById(R.id.upload);//button to upload image
        mTextViewShowUploads=findViewById(R.id.viewUploads);//see the services with the coresponding names and services
        mEditTextFileName=findViewById(R.id.serviceName);//input service name
        sImageView= findViewById(R.id.imageView);//
        mProgressBar= findViewById(R.id.progressbar);
        
        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sUpload!= null && sUpload.isInProgress()){
                    Toast.makeText(AddService.this, "upload in progress", Toast.LENGTH_SHORT).show();
                }
                uploadFile();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void openFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");//only see files in the file chooser
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== PICK_IMAGE_REQUEST && resultCode== RESULT_OK && data!=null && data.getData() !=null){
            sImageUri= data.getData();

            Picasso.get().load(sImageUri).into(sImageView);
        }
    }
    private String getFileExtension(Uri uri){  //to get file extension from image
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    
    private void uploadFile() {
        if (sImageUri !=null){
            //give files unique names
            StorageReference fileReference= storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(sImageUri));
            //check if there is a current upload running
            sUpload= fileReference.putFile(sImageUri).addOnSuccessListener
                    (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Delay reset of progress bar for 5 seconds
                    Handler handler= new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    }, 5000);

                    Toast.makeText(AddService.this, "image uploaded", Toast.LENGTH_SHORT).show();
                    Upload upload= new Upload(mEditTextFileName.getText().toString().trim(), 
                            taskSnapshot.getStorage().getDownloadUrl().toString());

                    String uploadId= databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);// create new entry in the database with unique Id

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddService.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    
                }
            });
//            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    double progress= (100.0* snapshot.getBytesTransferred() /snapshot.getTotalByteCount());
//                    mProgressBar.setProgress((int) progress);
//
//                }
//            });
        }
    }
}