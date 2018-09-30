package com.rahuljanagouda.statusstories.sample;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;



import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends android.support.v4.app.Fragment {

    DatabaseReference databaseReference;
    private FirebaseFirestore mFirestore;
    boolean isCacheEnabled = false;
    boolean isImmersiveEnabled = false;
    boolean isTextEnabled = false;
    long storyDuration = 3000L;
    ArrayList<Photo> photo;
    private String userID,userPhone;
    private FirebaseAuth mAuth;
private Button storyTime,uploadStory,RegisterBtn,SignOut;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileName2;
    private ImageView imv1,imv2,imv3;
    private ProgressBar mRegisterProgressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        mRegisterProgressBar = (ProgressBar) view.findViewById(R.id.registerProgressBar);
        mRegisterProgressBar.setVisibility(View.VISIBLE);
        photo = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userPhone=user.getPhoneNumber();
        userID = user.getUid();
     //   storyTime = (Button) view.findViewById(R.id.storyTime);
       // uploadStory = (Button) view.findViewById(R.id.uploadStory);
        RegisterBtn = (Button) view.findViewById(R.id.registerBtns);
        SignOut =(Button) view.findViewById(R.id.SignOut);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        mProfileName = (TextView) view.findViewById(R.id.profile_name);
        mProfileName2 = (TextView) view.findViewById(R.id.profile_name2);
        imv1=(ImageView) view.findViewById(R.id.imageView);

        mFirestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String user_name = documentSnapshot.getString("name");
                String user_image = documentSnapshot.getString("image");

                mProfileName.setText(user_name);
                mProfileName2.setText(userPhone);



                //Glide.with(ProfileFragment.this).setDefaultRequestOptions(placeholderOption).load(user_image).into(mProfileImage);
if (!user_image.equals(""))
                Picasso.get().load(user_image).placeholder(R.mipmap.default_image).resize(250,250).into(mProfileImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mRegisterProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception ex) {
                        mRegisterProgressBar.setVisibility(View.INVISIBLE);
                    }
                });


            }
        });
        imv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(view.getContext(), EditImageActivity.class);
                startActivity(a);
            }
        });
      /*
        uploadStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(view.getContext(), EditImageActivity.class);
                startActivity(a);
            }
        });

        */
RegisterBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent a = new Intent(view.getContext(), RegisterActivity.class);
        startActivity(a);

    }
});

        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutPhone(view);
            }
        });

        DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("user");
        DatabaseReference zone1Ref = zonesRef.child(userID);
        DatabaseReference zone1NameRef = zone1Ref.child("photo");


        databaseReference = FirebaseDatabase.getInstance().getReference("photo");
      /* String id = databaseReference.push().getKey();
        Photo p = new Photo(id,"IMAGE PATH");
        databaseReference.child(id).setValue(p);*/
        zone1NameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot photoSnapshot: dataSnapshot.getChildren() ){
                    Photo p = photoSnapshot.getValue(Photo.class);
                    photo.add(p);

                    int tailleDeLaBoucle = photo.size();
                    final String[] resources = new String[tailleDeLaBoucle];
                    final String[] idResources = new String[tailleDeLaBoucle];
                    for (int i = 0; i < tailleDeLaBoucle; i++) {
                        resources[i] = String.valueOf(photo.get(i).getUrl());
                        idResources[i] = String.valueOf(photo.get(i).getId());
                    }
                    mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent a = new Intent(view.getContext(), StatusStoriesActivity.class);
                            a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, resources);
                            a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, storyDuration);
                            a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, isImmersiveEnabled);
                            a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, isCacheEnabled);
                            a.putExtra(StatusStoriesActivity.IS_TEXT_PROGRESS_ENABLED_KEY, isTextEnabled);
                            a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_ID, idResources);
                            startActivity(a);



                        }
                    });




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });




        return view;

    }
    public void signOutPhone(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent a = new Intent(view.getContext(), AuthPhone.class);
        startActivity(a);
    }

}
