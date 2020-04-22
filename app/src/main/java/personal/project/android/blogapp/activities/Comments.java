package personal.project.android.blogapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import personal.project.android.blogapp.models.CommentsContent;
import personal.project.android.blogapp.R;
import personal.project.android.blogapp.adapters.CommentsAdapter;

public class Comments extends AppCompatActivity {

    private String blogPostId;
    private EditText editText;
    private ImageView combutton;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    CommentsAdapter commentsAdapter;
    private String curr_uid;
    private List<CommentsContent> list;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText=findViewById(R.id.comtext);
        recyclerView=findViewById(R.id.comview);
        combutton=findViewById(R.id.combtn);
        list=new ArrayList<>();;
        firebaseFirestore=FirebaseFirestore.getInstance();
        commentsAdapter=new CommentsAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(Comments.this));
        recyclerView.setAdapter(commentsAdapter);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null)
           curr_uid=auth.getCurrentUser().getUid();

        blogPostId= getIntent().getStringExtra("blogPostId");

        combutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String msg=editText.getText().toString();
              System.out.println("YOOOO"+msg);
              if(!msg.isEmpty()){
                  System.out.println("YOOOO"+msg);
                  Map<String ,Object> comt=new HashMap<>();
                  comt.put("message",msg);
                  comt.put("uid",curr_uid);
                  comt.put("timestamp",FieldValue.serverTimestamp());
                  editText.setText("");
                  firebaseFirestore.collection("Posts/").document(blogPostId).collection("/Comments").add(comt).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentReference> task) {
                     if(task.isSuccessful()){
                         Toast.makeText(Comments.this,"Comment Posted",Toast.LENGTH_SHORT).show();
                     }
                     else {
                         Toast.makeText(Comments.this,task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                     }
                      }
                  });

              }
            }
        });



        //Retrieve
        if(auth.getCurrentUser()!=null) {

            firebaseFirestore.collection("Posts/").document(blogPostId).collection("/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (auth.getCurrentUser() != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                                    final String bloguid = documentChange.getDocument().getString("uid");
                                    final CommentsContent commentsContent = documentChange.getDocument().toObject(CommentsContent.class).withId(blogPostId);
                                    list.add(commentsContent);

                                    commentsAdapter.notifyDataSetChanged();
                                }


                            }
                        }
                    }
                }

            });



        }

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
