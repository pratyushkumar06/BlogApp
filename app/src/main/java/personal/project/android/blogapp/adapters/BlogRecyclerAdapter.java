package personal.project.android.blogapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import personal.project.android.blogapp.models.BlogPost;
import personal.project.android.blogapp.activities.Comments;
import personal.project.android.blogapp.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blogPosts;
   // public List<User> userList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    FirebaseAuth auth;
    private String sendId;
    //  public TextView likect;


    public BlogRecyclerAdapter(List<BlogPost> blogPosts){
        this.blogPosts=blogPosts;   //Used for getting the values of the passed list
      //  this.userList=userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();


        return new ViewHolder(v);
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //holder.setIsRecyclable(false);  //Smooth Loading without Recycling

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
               firebaseFirestore.setFirestoreSettings(settings);



        final String uid=blogPosts.get(position).getUser_id();
        if(uid!=null) {
            firebaseFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String uname = task.getResult().get("name").toString();
                            String url = task.getResult().get("Url").toString();
                            holder.setname(uname);
                            holder.setuserimage(url);
                        }
                    } else {
                        Log.i("Tag", "Error");
                    }
                }
            });


        }



        final String desc_data=blogPosts.get(position).getDescription();
        holder.setDescri(desc_data);

        final String curruid=auth.getCurrentUser().getUid();
        final String BlogPostid=blogPosts.get(position).BlogPostid;

        holder.init();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            //Likes Feature

            holder.likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   DocumentReference mref=firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid);
                   mref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @Override
                       public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                           if(task.isSuccessful()){
                               DocumentSnapshot document = task.getResult();
                               if (document.exists()) {
                                   firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid).delete();
                                   firebaseFirestore.collection("users").document(curruid).collection("Notifications").document(curruid).delete();
                               } else {

                                   firebaseFirestore.collection("Posts").document(BlogPostid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                           if(task2.isSuccessful()){
                                               if(task2.getResult()!=null){
                                                   sendId=task2.getResult().get("user_id").toString();

                                                   String message=auth.getCurrentUser().getDisplayName()+" "+"Liked your Post";
                                                   Map<String ,Object> notifs=new HashMap<>();
                                                   notifs.put("message",message);
                                                   notifs.put("id",curruid);
                                                   notifs.put("postId",BlogPostid);
                                                   notifs.put("timestamp", FieldValue.serverTimestamp());
                                                   DocumentReference reference = firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid);

                                                   firebaseFirestore.collection("users").document(sendId).collection("Notifications").add(notifs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                       @Override
                                                       public void onSuccess(DocumentReference documentReference) {

                                                       }
                                                   })
                                                           .addOnFailureListener(new OnFailureListener() {
                                                               @Override
                                                               public void onFailure(@NonNull Exception e) {
                                                                   Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                                                               }
                                                           });
                                               }
                                           }
                                       }
                                   });
                                   Map<String, Object> likesMap = new HashMap<>();
                                   likesMap.put("uid", curruid);
                                   firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid).set(likesMap);


                               }
                           }
                       }
                   });
                }
            });

            //Like Count
            firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int count = queryDocumentSnapshots.size();
                            holder.updatelikescount(count);
                        } else {
                            holder.updatelikescount(0);
                        }

                    }
                }
            });

            //Change Drawable
            firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (documentSnapshot.exists()) {
                            holder.likebtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_48dp));
                        } else if (!documentSnapshot.exists()) {
                            holder.likebtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_48dp));
                        }
                    }
                }
            });


        }
        firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int count = queryDocumentSnapshots.size();
                        holder.updatecmntcount(count);
                    } else {
                        holder.updatecmntcount(0);
                    }

                }
            }
        });

        holder.cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context, Comments.class);
                in.putExtra("blogPostId",BlogPostid);  //We pass the BlogPost Id too
                context.startActivity(in);
            }
        });

        String ti=blogPosts.get(position).getTitle();
        holder.setTitle(ti);

        String url=blogPosts.get(position).getUrl();
        holder.setPostimage(url);


        long milisecs=blogPosts.get(position).getTimeStamp().getTime();
        String datestr= DateFormat.format("dd/MM/yyyy",new Date(milisecs)).toString();
        holder.setTime(datestr);

       //Likes Feature

    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView likebtn,cbtn;
        TextView likect,cmntct;
        private View view;


        private TextView textView,getTextView,datee,name;
        private ImageView post,user;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void init(){
            likebtn=view.findViewById(R.id.likeimage);
            likect=view.findViewById(R.id.likecount);
            cbtn=view.findViewById(R.id.comment);
            cmntct=view.findViewById(R.id.commentcnt);

        }


        public void setDescri(String text){
            textView=view.findViewById(R.id.descr);
            textView.setText(text);
        }

        public void setTitle(String t){
            getTextView=view.findViewById(R.id.title);
            getTextView.setText(t);
        }

        public void setPostimage(String uri){
            post=view.findViewById(R.id.post);
            Glide.with(context).load(uri).into(post);

        }

        public void setTime(String t){
            datee=view.findViewById(R.id.date);
            datee.setText(t);
        }
        public void setname(String n){
            name=view.findViewById(R.id.name);
            name.setText(n);
        }

        public void setuserimage(String urii){
            user=view.findViewById(R.id.userimage);
            Glide.with(context).load(urii).apply(RequestOptions.circleCropTransform()).into(user);

        }

        @SuppressLint("SetTextI18n")
        public void updatelikescount(int c){
            likect=view.findViewById(R.id.likecount);
            if(c==1){
                likect.setText(c+" Like");
            }
            else{
                likect.setText(c+" Likes");
            }
        }

        @SuppressLint("SetTextI18n")
        public void updatecmntcount(int c){
            if(c==1){
                cmntct.setText(c+" Comment");
            }
            else{
                cmntct.setText(c+" Comments");
            }

        }



    }
}
