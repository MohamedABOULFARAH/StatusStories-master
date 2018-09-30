package com.rahuljanagouda.statusstories.sample;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends android.support.v4.app.Fragment {


    private String userID;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private List<Photo> photosList;

    private RecyclerView mPhotosListView;


    private PhotosRecyclerAdapter photosRecyclerAdapter;

    private FirebaseFirestore mFirestore;

    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        mPhotosListView = (RecyclerView) view.findViewById(R.id.photos_list_view);

        photosList = new ArrayList<>();
        photosRecyclerAdapter = new PhotosRecyclerAdapter(container.getContext(), photosList);

        mPhotosListView.setHasFixedSize(true);
        mPhotosListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mPhotosListView.setAdapter(photosRecyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("user");
        DatabaseReference zone1Ref = zonesRef.child(userID);
        DatabaseReference zone1NameRef = zone1Ref.child("photo");



        zone1NameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photosList.clear();
                for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                    Photo p = photoSnapshot.getValue(Photo.class);
                    photosList.add(p);



                }
                if (photosList.size()>0){
                    DatabaseReference zonesRef12 = FirebaseDatabase.getInstance().getReference("user").child(userID).child("photo");
                    zonesRef12.setValue(photosList);
                }



                photosRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });




    }

}
