package com.rahuljanagouda.statusstories.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 04/01/18.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {

    private List<Users> usersList;
    private Context context;
    DatabaseReference databaseReference;
    boolean isCacheEnabled = false;
    boolean isImmersiveEnabled = false;
    boolean isTextEnabled = false;
    long storyDuration = 3000L;
    ArrayList<Photo> photo;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers;


    public UsersRecyclerAdapter(Context context, List<Users> usersList){

        this.usersList = usersList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String user_name = usersList.get(position).getName();

        holder.user_name_view.setText(user_name);

        CircleImageView user_image_view = holder.user_image_view;
        Picasso.get().load(usersList.get(position).getImage()).resize(250,250).into(user_image_view);


        final String user_id = usersList.get(position).userId;


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {










                photo = new ArrayList<>();
                mAuth = FirebaseAuth.getInstance();




                DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("user");
                DatabaseReference zone1Ref = zonesRef.child(user_id);
                DatabaseReference zone1NameRef = zone1Ref.child("photo");


                databaseReference = FirebaseDatabase.getInstance().getReference("photo");
      /* String id = databaseReference.push().getKey();
        Photo p = new Photo(id,"IMAGE PATH");
        databaseReference.child(id).setValue(p);*/
                zone1NameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String[] resources = new String[0];
                        String[] idResources = new String[0];
                        String idResources2 = null;
                        for(DataSnapshot photoSnapshot: dataSnapshot.getChildren() ){
                            Photo p = photoSnapshot.getValue(Photo.class);
                            photo.add(p);

                            int tailleDeLaBoucle = photo.size();
                            resources = new String[tailleDeLaBoucle];
                            idResources = new String[tailleDeLaBoucle];
                            idResources2=user_id;
                            for (int i = 0; i < tailleDeLaBoucle; i++) {
                                resources[i] = String.valueOf(photo.get(i).getUrl());
                                idResources[i] = String.valueOf(photo.get(i).getId());
                            }




                        }

                        Intent a = new Intent(context, StatusStoriesActivity.class);
                        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_KEY, resources);
                        a.putExtra(StatusStoriesActivity.STATUS_DURATION_KEY, storyDuration);
                        a.putExtra(StatusStoriesActivity.IS_IMMERSIVE_KEY, isImmersiveEnabled);
                        a.putExtra(StatusStoriesActivity.IS_CACHING_ENABLED_KEY, isCacheEnabled);
                        a.putExtra(StatusStoriesActivity.IS_TEXT_PROGRESS_ENABLED_KEY, isTextEnabled);
                        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_ID, idResources);
                        a.putExtra(StatusStoriesActivity.STATUS_RESOURCES_ID2, idResources2);
                        context.startActivity(a);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private CircleImageView user_image_view;
        private TextView user_name_view;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            user_image_view = (CircleImageView) mView.findViewById(R.id.user_list_image);
            user_name_view = (TextView) mView.findViewById(R.id.user_list_name);

        }
    }

}