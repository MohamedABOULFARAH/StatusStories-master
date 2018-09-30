package com.rahuljanagouda.statusstories.sample;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.email.RecoverPasswordActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 04/01/18.
 */

public class PhotosRecyclerAdapter extends RecyclerView.Adapter<PhotosRecyclerAdapter.ViewHolder> {
    private List<String> data;
    ArrayList<Photo> photoListo;
    private String resources;
    boolean porte =true;
    private List<Photo> photosList;
    private List<Seen> seenList;
    private List<String> userList;
    private Context context;
    DatabaseReference databaseReference,databaseReferencePhoto2;
    boolean isCacheEnabled = false;
    boolean isImmersiveEnabled = false;
    boolean isTextEnabled = false;
    long storyDuration = 3000L;
    ArrayList<Photo> photo;
    ListView listView=null;
    ImageView deleteBtn;
    ImageButton commentBtn,ViewBtn;
    private String userID;
    private FirebaseAuth mAuth;

    public PhotosRecyclerAdapter(Context context, List<Photo> photosList){

        this.photosList = photosList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //final String photo_name = photosList.get(position).get;
       // holder.photo_name_view.setText(photo_name);

        CircleImageView photo_image_view = holder.photo_image_view;
        Picasso.get().load(photosList.get(position).getUrl()).resize(250,250).into(photo_image_view);


        final String photo_id = photosList.get(position).getId();
        final Photo photo_pos=photosList.get(position);
        final String pos= String.valueOf(position);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                userID = user.getUid();




                FirebaseDatabase.getInstance().getReference("user").child(userID).child("photo").child(pos).removeValue();
                //uploadImage();

            }
        });




        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(view.getContext(), CommentsFragment.class);
                resources=photo_id;
                a.putExtra(CommentsFragment.IDPHOTO, resources);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                CommentsFragment myFragment = new CommentsFragment();
                Bundle data = new Bundle();//Use bundle to pass data
                data.putString("data", resources);//put string, int, etc in bundle with a key value
                myFragment.setArguments(data);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

            }
        });

        ViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                seenList = new ArrayList<>();
                userList = new ArrayList<>();


                // Add data to the ListView
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                userID = user.getUid();


                DatabaseReference zonesRef = FirebaseDatabase.getInstance().getReference("photo").child(photo_id).child("Seen");

                seenList.clear();
                zonesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot seenSnapshot: dataSnapshot.getChildren() ){
                            Seen s = seenSnapshot.getValue(Seen.class);
                            seenList.add(s);
                        }
                        for (int i=0;i<seenList.size();i++){
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(seenList.get(i).getUserId());
                            userList.clear();
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {



                                    for(DataSnapshot userSnapshot: dataSnapshot.getChildren() ){
                                        if (userSnapshot.getKey().equals("nom")) {
                                            String us = userSnapshot.getValue(String.class);
                                            userList.add(us);
                                        }

                                    }
                                    for (int t=0;t<userList.size();t++){
                                        System.out.println("***--*** users are :"+userList.get(t));
                                        data = new ArrayList<>();
                                        data.add(userList.get(t));
                                    }
                                    if (porte){
                                        rr(userList);
                                        porte=false;
                                    }



                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });
                            }
                        porte=true;



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });






            }
        });




    }
public void rr(List data){
    listView = new ListView(context);
    ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, R.layout.list_seen, R.id.txtitem,data);
    listView.setAdapter(adapter);

    // Perform action when an item is clicked
    AlertDialog.Builder builder=new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
    builder.setCancelable(true);
    builder.setPositiveButton("OK",null);
    builder.setView(listView);
    AlertDialog dialog=builder.create();
    dialog.show();
}

    @Override
    public int getItemCount() {
        return photosList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private CircleImageView photo_image_view;
      //  private TextView photo_name_view;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            photo_image_view = (CircleImageView) mView.findViewById(R.id.photo_list_image);
           // photo_name_view = (TextView) mView.findViewById(R.id.photo_list_name);
            commentBtn=(ImageButton) mView.findViewById(R.id.comments);
            ViewBtn=(ImageButton) mView.findViewById(R.id.views);
            deleteBtn=(ImageView) mView.findViewById(R.id.deleteBtn);
        }
    }

}