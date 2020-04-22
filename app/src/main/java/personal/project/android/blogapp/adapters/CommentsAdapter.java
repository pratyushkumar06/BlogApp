package personal.project.android.blogapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import personal.project.android.blogapp.models.CommentsContent;
import personal.project.android.blogapp.R;
import personal.project.android.blogapp.models.User;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    public List<CommentsContent> comments;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private Boolean button=true;
    public List<User> userList;
    CommentsContent commentsContent;

    //  public ImageView likebtn;
    FirebaseAuth auth;
    //  public TextView likect;


    public CommentsAdapter(List<CommentsContent> comments){
        this.comments=comments;//Used for getting the values of the passed list
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        context=parent.getContext();

        firebaseFirestore=FirebaseFirestore.getInstance();

        auth=FirebaseAuth.getInstance();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);  //Smooth Loading without Recycling




        final String desc_data=comments.get(position).getMessage();
        holder.setDescri(desc_data);

        final String curruid=auth.getCurrentUser().getUid();
       // final String BlogPostid=blogPosts.get(position).BlogPostid;

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {


        }
        final String uid=comments.get(position).getUid();
        System.out.println("UIIIIIDDDD"+uid);

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



  //      long milisecs=comments.get(position).getDate().getTime();
    //    String datestr= DateFormat.format("dd/MM/yyyy",new Date(milisecs)).toString();
//        holder.setTime(datestr);


    }

    @Override
    public int getItemCount() {
        if(comments!=null){
            return comments.size();
        }
        else{
            return 0;
        }
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


        public void setDescri(String text){
            textView=view.findViewById(R.id.com);
            textView.setText(text);
        }

        public void setTime(String t){
            datee=view.findViewById(R.id.dt);
            datee.setText(t);
        }
        public void setname(String n){
            name=view.findViewById(R.id.uname);
            name.setText(n);
        }

        public void setuserimage(String urii){
            user=view.findViewById(R.id.uimage);
            Glide.with(context).load(urii).apply(RequestOptions.circleCropTransform()).into(user);

        }


    }
}

