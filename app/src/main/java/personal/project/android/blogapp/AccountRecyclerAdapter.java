package personal.project.android.blogapp;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AccountRecyclerAdapter extends RecyclerView.Adapter<AccountRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blogPosts;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    private AccountRecyclerAdapter adapter;
    //  public ImageView likebtn;
    BlogRecyclerAdapter blogRecyclerAdapter;
    FirebaseAuth auth;
    //  public TextView likect;


    public AccountRecyclerAdapter(List<BlogPost> blogPosts){
        this.blogPosts=blogPosts;   //Used for getting the values of the passed list
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item2,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);  //Smooth Loading without Recycling
        final String uid=blogPosts.get(position).getUser_id();
        if(uid!=null) {
            firebaseFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String uname = task.getResult().getString("name");
                            String url = task.getResult().getString("Url");
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

                    firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.getResult().isEmpty()) {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("uid", curruid);
                                //likesMap.put("timestamp", FieldValue.serverTimestamp());
                                System.out.println(BlogPostid);
                                firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid).set(likesMap);


                            }else if (task.getResult() != null) {
                                firebaseFirestore.collection("Posts/").document(BlogPostid.toString()).collection("/Likes").document(curruid).delete();

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

        }

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseFirestore.collection("Posts").document(BlogPostid)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_LONG).show();
                                    Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                    blogPosts.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error deleting document", e);
                                }
                            });

                }
            });

        holder.cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,Comments.class);
                in.putExtra("blogPostId",BlogPostid);  //We pass the BlogPost Id too
                context.startActivity(in);
            }
        });

        String ti=blogPosts.get(position).getTitle();
        holder.setTitle(ti);

        String url=blogPosts.get(position).getUrl();
        holder.setPostimage(url);


        System.out.println(uid);

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
        private ImageView delete;


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
            delete=view.findViewById(R.id.del);

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
            likect.setText(c+" Likes");
        }
        @SuppressLint("SetTextI18n")
        public void updatecmntcount(int c){
            cmntct.setText(c+" Comments");
        }


    }
}
