package com.rahuljanagouda.statusstories.sample;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends android.support.v4.app.Fragment {

    public static final String IDPHOTO="id of photos";
    private String[] statusResourcesid;

    private String userID;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private List<Commentaire> commentsList;

    private RecyclerView mCommentsListView;


    private CommentsRecyclerAdapter commentsRecyclerAdapter;

    private FirebaseFirestore mFirestore;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);


        mFirestore = FirebaseFirestore.getInstance();

        mCommentsListView = (RecyclerView) view.findViewById(R.id.comments_list_view);

        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(container.getContext(), commentsList);

        mCommentsListView.setHasFixedSize(true);
        mCommentsListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mCommentsListView.setAdapter(commentsRecyclerAdapter);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        //statusResourcesid=getActivity().getIntent().getStringArrayExtra(IDPHOTO);
        String getArgument = getArguments().getString("data");
    DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("photo");
    DatabaseReference zone1Ref = zonesRef.child(getArgument);
    DatabaseReference zone1NameRef = zone1Ref.child("Commentaire");


    commentsList.clear();
    zone1NameRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            for(DataSnapshot commentSnapshot: dataSnapshot.getChildren() ){
                Commentaire c = commentSnapshot.getValue(Commentaire.class);
                commentsList.add(c);

                commentsRecyclerAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    });








    }


}
