package personal.project.android.blogapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import personal.project.android.blogapp.R;
import personal.project.android.blogapp.models.Notifications;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    Context ctx;
    String uname,url;
    private List<Notifications> List;
    public NotificationAdapter(java.util.List<Notifications> List, Context ctx){
        this.List =List;
        this.ctx=ctx;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_2,parent,false);  //inflating the view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, final int position) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        // holder.setIsRecyclable(false);
        final String uid=List.get(position).getId();
        if (uid!=null){
            firebaseFirestore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot!=null){
                        uname = documentSnapshot.getString("name");
                        url = documentSnapshot.getString("Url");
                        holder.setDetails(ctx,uname,url,List.get(position).getMessage());
                        firebaseFirestore.collection("Posts/").document(List.get(position).getPostId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot2) {
                               if(documentSnapshot2!=null){
                                   String imageUrl= Objects.requireNonNull(documentSnapshot2.get("url")).toString();
                                   holder.setImage(imageUrl);
                               }

                            }
                        });
                    }
                }
            });

        }
    }
    @Override
    public int getItemCount() {
        return List.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView;
        }

        public void setDetails(Context context,String username, String userimage,String message){
            TextView uname=v.findViewById(R.id.naaame);
            TextView msg=v.findViewById(R.id.textView5);
            ImageView imageView=v.findViewById(R.id.imageView);
            uname.setText(username);
            msg.setText(message);
            Glide.with(context).load(userimage).apply(RequestOptions.circleCropTransform()).into(imageView);
        }

        public void setImage(String url){
            ImageView imageView2=v.findViewById(R.id.imageView2);
            Glide.with(ctx).load(url).into(imageView2);
        }
    }
}
