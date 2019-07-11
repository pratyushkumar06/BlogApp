package personal.project.android.blogapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountSettings extends AppCompatActivity {
    private Button button;
    private FirebaseAuth mAuth;
    private EditText editText;
    private ImageView imageView;
    private static final int INT_CONST=023;
    private static final int REQ=22;
    private Uri uri,url;
    private String name;
    private Bitmap bitmap;
    String user_id;
    ProgressDialog progressDialog,getProgressDialog;
    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();
        editText=findViewById(R.id.editText3);
        button=findViewById(R.id.button);
        imageView=findViewById(R.id.imageView);

        firebaseFirestore=FirebaseFirestore.getInstance();  //Link
        loadUserInfo();
        progressDialog=new ProgressDialog(AccountSettings.this,R.style.AppCompatAlertDialogStyle);
        getProgressDialog=new ProgressDialog(AccountSettings.this,R.style.AppCompatAlertDialogStyle);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(AccountSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(AccountSettings.this,"Denied",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(AccountSettings.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},33);
            }
          }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=editText.getText().toString();

                if(name.isEmpty()){
                    editText.setError("Name Required");
                    editText.requestFocus();
                }
                else{
                saveUserInfo();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(AccountSettings.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Choose Image"), INT_CONST);
                }

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @SuppressLint("SetTextI18n")
    private void loadUserInfo() {   //We use Glide to Load the Image
        final FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(AccountSettings.this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
            if (user.getDisplayName() != null) {
                String displayname = user.getDisplayName();
                editText.setText(displayname);
            }
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==android.R.id.home){
           finish();
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==INT_CONST && resultCode==RESULT_OK && data.getData()!=null){
            uri=data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            uploadFile(bitmap);
            getProgressDialog.setTitle("Uploading Image");
            getProgressDialog.show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()==null){
            finish();
            Intent intent=new Intent(AccountSettings.this,MainActivity.class);
            startActivity(intent);

        }
    }

    @SuppressLint("NewApi")
    private void saveUserInfo() {

            progressDialog.setTitle("Updating Details..");
            progressDialog.show();

        final FirebaseUser user=mAuth.getCurrentUser();   //We get the current user

        user_id=FirebaseAuth.getInstance().getUid();


        if(user!=null && url!=null){

            UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(url).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Map<String ,String> u=new HashMap<>();
                        u.put("name",name);
                        u.put("Url",url.toString());
                        firebaseFirestore.collection("users").document(user_id)
                                .set(u)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(AccountSettings.this,"Success",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(AccountSettings.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Toast.makeText(AccountSettings.this,"Profile Updated",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
       else if(user!=null && url==null){
           UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Map<String ,String> u=new HashMap<>();
                        u.put("name",name);
                        u.put("Url",user.getPhotoUrl().toString());
                        firebaseFirestore.collection("users").document(user_id)
                                .set(u)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(AccountSettings.this,"Success",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(AccountSettings.this,"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Toast.makeText(AccountSettings.this,"Profile Updated",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
    private void uploadFile(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        final StorageReference ImagesRef = storageRef.child("images/"+mAuth.getCurrentUser().getUid()+".jpg");


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
                            Toast.makeText(AccountSettings.this,"Upload Successfull",Toast.LENGTH_SHORT).show();
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

}
