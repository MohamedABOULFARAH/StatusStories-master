package com.rahuljanagouda.statusstories.sample;


import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends android.support.v4.app.Fragment {

    ArrayList<Photo> photo;
    ArrayList<User> userData;
    private String userID;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;


    private RecyclerView mUsersListView;

    private List<Users> usersList;
    private UsersRecyclerAdapter usersRecyclerAdapter;

    private FirebaseFirestore mFirestore;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        mUsersListView = (RecyclerView) view.findViewById(R.id.users_list_view);

        usersList = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(container.getContext(), usersList);

        mUsersListView.setHasFixedSize(true);
        mUsersListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mUsersListView.setAdapter(usersRecyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EtatStatue();
        usersList.clear();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirestore.collection("Users").whereEqualTo("statue", true).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String user_id = doc.getDocument().getId();

                        Users users = doc.getDocument().toObject(Users.class).withId(user_id);
                        if (!user_id.equals(userID))
                        usersList.add(users);

                        usersRecyclerAdapter.notifyDataSetChanged();

                    }

                }

            }
        });

    }
    public void EtatStatue(){
        photo = new ArrayList<>();
        userData=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        final String[] u = new String[1];

        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("user");



        databaseReference = FirebaseDatabase.getInstance().getReference("photo");

        zonesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot UserSnapshot: dataSnapshot.getChildren() ){
                    User u = UserSnapshot.getValue(User.class);
                    userData.add(u);

                }

                 for (int i= 0;i<userData.size();i++) {


    DatabaseReference dataUser = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference datauser1 = dataUser.child(userData.get(i).getId());
    DatabaseReference dataPhoto = datauser1.child("photo");
    final int finalI = i;
    datauser1.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            DocumentReference statueRef = mFirestore.collection("Users").document(userData.get(finalI).getId());
            if (dataSnapshot.hasChild("photo")) {
                statueRef
                        .update("statue", true);
            }else{
                statueRef
                        .update("statue", false);
            }



        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    });

} }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

}
