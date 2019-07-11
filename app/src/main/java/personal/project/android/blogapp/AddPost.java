package personal.project.android.blogapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AddPost extends AppCompatActivity {

    private Button button;
    private ImageView imageView;
    private Uri resulturi,url;
    String t,d;
    private String user_id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private EditText title,description;
    ProgressDialog getProgressDialog,progressDialog;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storageReference=FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        button=findViewById(R.id.post);
        imageView=findViewById(R.id.imageView3);
        title=findViewById(R.id.title);
        description=findViewById(R.id.desc);

        getProgressDialog=new ProgressDialog(AddPost.this);
        progressDialog=new ProgressDialog(AddPost.this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()      //Sends us to the crop activity page
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(5,3)
                        .setMinCropResultSize(512,512)
                        .start(AddPost.this);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=title.getText().toString();
                d=description.getText().toString();

                if(t.isEmpty() || d.isEmpty()){
                    title.setError("Cannot be Empty");
                    description.setError("Cannot be Empty");
                }
                else{
                    progressDialog.setTitle("Uploading Blog...");
                    progressDialog.show();
                    adddata();

                }
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resulturi = result.getUri();
                try {
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),resulturi);
                    uploadFile(bitmap);
                    getProgressDialog.setTitle("Uploading Image");
                    getProgressDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageURI(resulturi);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void uploadFile(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();


        final StorageReference ImagesRef = storageRef.child("post_images/").child(resulturi.getLastPathSegment());


        //TODO compressed thumbnail post image for loading fast

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ImagesRef.putBytes(data);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Error:",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }

                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            getProgressDialog.dismiss();
                            Toast.makeText(AddPost.this,"Upload Successfull",Toast.LENGTH_SHORT).show();
                            url=downloadUri;


                            //StorageReference ref = FirebaseStorage.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

                            assert downloadUri != null;
                            Log.i("seeThisUri", downloadUri.toString());// This is the one you should store

                            //ref.child("imageURL").setValue(downloadUri.toString());


                        } else {
                             getProgressDialog.dismiss();
                            Log.i("wentWrong","downloadUri failure");
                        }
                    }
                });
            }
        });

    }

    public void adddata(){

        Map<String ,Object> post=new HashMap<>();
        post.put("user_id",user_id);
        post.put("url",url.toString());
        post.put("title",t);
        post.put("description",d);
        post.put("timeStamp",FieldValue.serverTimestamp());
        //TODO add Timestamp

        firebaseFirestore.collection("Posts").add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(AddPost.this,"Post Uploaded",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent in=new Intent(AddPost.this,PostActivity.class);
                startActivity(in);
                finish();
            }
        });
    }
}
