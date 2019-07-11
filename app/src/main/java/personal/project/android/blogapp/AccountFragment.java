package personal.project.android.blogapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private Button button;
    private TextView textView;
    private ImageView imageView;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private AccountRecyclerAdapter accountRecyclerAdapter;
    private RecyclerView recyclerView;
    private List<BlogPost> list;
    String curid;
    private  String blogId;
    Boolean LoadedforfirstTime=false;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_account, container, false);
        button=v.findViewById(R.id.button3);
        textView=v.findViewById(R.id.textView3);
        imageView=v.findViewById(R.id.imageView2);
        recyclerView=v.findViewById(R.id.rev);
        auth=FirebaseAuth.getInstance();
        list=new ArrayList<>();

        firebaseFirestore=FirebaseFirestore.getInstance();
        accountRecyclerAdapter=new AccountRecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(accountRecyclerAdapter);




        if(auth.getCurrentUser()!=null){

             curid=auth.getCurrentUser().getUid();
            firebaseFirestore.collection("users").document(curid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String uname = task.getResult().getString("name");
                            String url = task.getResult().getString("Url");
                            textView.setText(uname);
                            Glide.with(AccountFragment.this).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
                        }
                    } else {
                        Log.i("Tag", "Error");
                    }
                }
            });

            Query query = firebaseFirestore.collectionGroup("Posts").whereEqualTo("user_id", curid).orderBy("timeStamp",Query.Direction.ASCENDING);
            if(auth.getCurrentUser()!=null) {
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {

                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                                    blogId = documentChange.getDocument().getId();
                                    BlogPost blogPost = documentChange.getDocument().toObject(BlogPost.class).withId(blogId);//From here the data is sent to the constructor for gettong the details
                                    if (LoadedforfirstTime) {
                                        list.add(blogPost);
                                    } else {
                                        list.add(0, blogPost);  //Adds the new post to the top
                                    }
                                    accountRecyclerAdapter.notifyDataSetChanged();
                                }

                                if(documentChange.getType()==DocumentChange.Type.REMOVED){
                                    recyclerView.getRecycledViewPool().clear();
                                    list.clear();
                                   accountRecyclerAdapter.notifyDataSetChanged();
                                }

                               //TODO When data removed the changes should take place in RealTime
                            }

                            LoadedforfirstTime = false;
                        }
                    }

                });
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getActivity(),AccountSettings.class);
                startActivity(in);

            }
        });


        return v;
    }

}
