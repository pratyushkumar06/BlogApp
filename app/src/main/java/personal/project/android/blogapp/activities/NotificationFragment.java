package personal.project.android.blogapp.activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import personal.project.android.blogapp.adapters.NotificationAdapter;
import personal.project.android.blogapp.R;
import personal.project.android.blogapp.models.Notifications;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notifications> list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vv=inflater.inflate(R.layout.fragment_notification, container, false);
        // Inflate the layout for this fragment

        recyclerView=vv.findViewById(R.id.rev);
        auth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        list=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(list, Objects.requireNonNull(getActivity()).getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        // recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.list_item_2, 0);
        if(auth.getCurrentUser()!=null){
            String curid=auth.getCurrentUser().getUid();
            Query q=firebaseFirestore.collection("users").document(curid).collection("Notifications").orderBy("timestamp",Query.Direction.ASCENDING);
            list.clear();
            q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(queryDocumentSnapshots!=null){
                        for(DocumentChange change:queryDocumentSnapshots.getDocumentChanges()){
                            if(change.getType()==DocumentChange.Type.ADDED){
                                Notifications notifications=change.getDocument().toObject(Notifications.class);
                                list.add(0,notifications);
                                notificationAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }

        return vv;
    }

    @Override
    public void onStart() {
        super.onStart();
        notificationAdapter.notifyDataSetChanged();
    }
}
