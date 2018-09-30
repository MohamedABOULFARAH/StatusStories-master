package com.rahuljanagouda.statusstories.sample;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    boolean isCacheEnabled = false;
    boolean isImmersiveEnabled = false;
    boolean isTextEnabled = false;
    long storyDuration = 3000L;
    ArrayList<Photo> photo;
    private String userID;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        photo = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


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
                    findViewById(R.id.storyTime).setOnClickListener(new View.OnClickListener() {
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
                    findViewById(R.id.uploadStory).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent a = new Intent(view.getContext(), Main2Activity.class);
                            startActivity(a);
                        }
                    });


                }

//                System.out.println("Size :"+ photo.get(0).getUrl() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        ((SwitchCompat) findViewById(R.id.isCacheEnabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCacheEnabled = b;
            }
        });

        ((SwitchCompat) findViewById(R.id.isImmersiveEnabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isImmersiveEnabled = b;
            }
        });

        ((SwitchCompat) findViewById(R.id.isTextEnabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isTextEnabled = b;
            }
        });

        ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                storyDuration = i < 4 ? 3 * 1000L : i * 1000L;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
    public void signOutPhone(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent a = new Intent(view.getContext(), AuthPhone.class);
        startActivity(a);
    }

}
