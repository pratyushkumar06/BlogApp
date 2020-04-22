package personal.project.android.blogapp.activities;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import javax.annotation.Nullable;

import personal.project.android.blogapp.models.BlogPost;
import personal.project.android.blogapp.R;
import personal.project.android.blogapp.models.User;
import personal.project.android.blogapp.adapters.BlogRecyclerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<BlogPost> list;
    private SwipeRefreshLayout refreshLayout;
  //  private List<User> userList;
    User user;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private FirebaseAuth mauth;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private Boolean isdataloadedfirstTime=true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.rview);
        firebaseFirestore=FirebaseFirestore.getInstance();
        refreshLayout=view.findViewById(R.id.refresh);
        mauth=FirebaseAuth.getInstance();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                Query firstquery = firebaseFirestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING).limit(3);
                firstquery.addSnapshotListener(new EventListener<QuerySnapshot>() {  //To access RealtimeData
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //Pagination decides the number of posts that are to be loaded per page
                        if(mauth.getCurrentUser()!=null){
                            if (!queryDocumentSnapshots.isEmpty()) {
                                if (isdataloadedfirstTime) {
                                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                    // list.clear();
                                }
                                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                    if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                                        String blogId = documentChange.getDocument().getId();
                                        final BlogPost blogPost = documentChange.getDocument().toObject(BlogPost.class).withId(blogId);//From here the data is sent to the constructor for gettong the details
                                        final String bloguid = documentChange.getDocument().getString("user_id");
                                            list.add(blogPost);
                                        blogRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
            }
        });
        list=new ArrayList<>();
      //  userList=new ArrayList<>();
        blogRecyclerAdapter=new BlogRecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(blogRecyclerAdapter);
        if(mauth.getCurrentUser()!=null) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            Boolean reachedBottom = !recyclerView.canScrollVertically(1);  //When it reaches the end of the limit more posts will be loaded
            if (reachedBottom) {
                loadNext();
            }
        }
    });
            Query firstquery = firebaseFirestore.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING).limit(3);
            firstquery.addSnapshotListener(new EventListener<QuerySnapshot>() {  //To access RealtimeData
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            //Pagination decides the number of posts that are to be loaded per page
            if(mauth.getCurrentUser()!=null){
                if (!queryDocumentSnapshots.isEmpty()) {
                    if (isdataloadedfirstTime) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                       // list.clear();
                    }
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {//Everytime the data is added the document goes to the BlogPost class and is added to list
                            String blogId = documentChange.getDocument().getId();
                            final BlogPost blogPost = documentChange.getDocument().toObject(BlogPost.class).withId(blogId);//From here the data is sent to the constructor for gettong the details
                            final String bloguid = documentChange.getDocument().getString("user_id");
                            if (isdataloadedfirstTime) {
                                list.add(blogPost);
                            } else {
                                list.add(0, blogPost);  //Adds the new post to the top
                            }
                           /* firebaseFirestore.collection("users").document(bloguid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        user = task.getResult().toObject(User.class);

                                       // if (isdataloadedfirstTime) {
                                            userList.add(user);
                                       // } //else {
                                           // userList.add(0, user);//Adds the new post to the top
                                        //}
                                        blogRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                           */
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isdataloadedfirstTime = false;
                }
            }
        }
    });

    // Inflate the layout for this fragment
         }
        return view;
    }
/*
    @Override public void onResume() {
        super.onResume();
        lastVisible =null;
        isdataloadedfirstTime = true;
    }
*/

    public void loadNext(){
        Query nextquery=firebaseFirestore.collection("Posts").orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
        nextquery.addSnapshotListener(new EventListener<QuerySnapshot>() {  //To access RealtimeData
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(mauth.getCurrentUser()!=null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {                   //Everytime the data is added the document goes to the BlogPost class and is added to list
                                final String blogId=documentChange.getDocument().getId();
                                final String bloguid = documentChange.getDocument().getString("user_id");
                                final BlogPost blogPost = documentChange.getDocument().toObject(BlogPost.class).withId(blogId);
                                list.add(blogPost);
                             /*   firebaseFirestore.collection("users").document(bloguid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            user = task.getResult().toObject(User.class);
                                                userList.add(user);
                                            blogRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });*/
                                blogRecyclerAdapter.notifyDataSetChanged();
                            }
                            if(documentChange.getType()==DocumentChange.Type.REMOVED){
                                recyclerView.getRecycledViewPool().clear();
                                blogRecyclerAdapter.notifyDataSetChanged();

                                list.clear();
                                blogRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        isdataloadedfirstTime=false;
                    }
                }
            }
        });

    }

}
