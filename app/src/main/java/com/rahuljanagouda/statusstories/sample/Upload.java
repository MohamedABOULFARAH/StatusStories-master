package com.rahuljanagouda.statusstories.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahuljanagouda.statusstories.StatusStoriesActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Upload extends AppCompatActivity {

//variables
    private Button btnChoose,btnUplaod,btnCamera;
    private ImageView imageView;
    private Uri filePath;
    private String userID;
    private final int PICK_IMAGE_REQUEST=71;
    private static String refChild, url;
    private FirebaseAuth mAuth;
    Date currentTime;
    ArrayList<Photo> photoList;
    ArrayList<Seen> seenList;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferencePhoto,databaseReferencePhoto2;
    DatabaseReference databaseReferenceSeen;
    FirebaseStorage storage;
    StorageReference storageReference;
    static boolean tte=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        //init view
        btnChoose=(Button)findViewById(R.id.btnChoose);
        btnUplaod=(Button)findViewById(R.id.btnUpload);
        btnCamera=(Button)findViewById(R.id.btnCamera);
        imageView=(ImageView)findViewById(R.id.imgView);
btnChoose.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        chooseImage();
    }
});
btnUplaod.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(filePath!=null){
            uploadImage();
        }else{
            Toast.makeText(Upload.this,"choisi une image svp",Toast.LENGTH_SHORT).show();
        }

    }
});
btnCamera.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(intent,330);
    }
});

    }

    private void uploadImage() {
final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
         refChild="images/"+ UUID.randomUUID().toString();
        StorageReference ref =storageReference.child(refChild);
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Upload.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        StorageReference storageRef = storage.getReference();
                        storageRef.child(refChild).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                seenList = new ArrayList<>();

                                databaseReferencePhoto = FirebaseDatabase.getInstance().getReference("photo");
                                String id = databaseReferencePhoto.push().getKey();
                                url= uri.toString();

                                final Photo p = new Photo(id,url,seenList);
                                databaseReferencePhoto.child(id).setValue(p);
                                mAuth = FirebaseAuth.getInstance();
                                FirebaseUser user = mAuth.getCurrentUser();
                                databaseReference = FirebaseDatabase.getInstance().getReference("user");

                                userID = user.getUid();
                                // User u = new User(userID, "Simo", photoList);

                                photoList = new ArrayList<>();
                                photoList.clear();
                                databaseReferencePhoto2 = FirebaseDatabase.getInstance().getReference("user").child(userID);
          databaseReferencePhoto2.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  if (dataSnapshot.hasChild("photo")) {
                      for (DataSnapshot photoSnapshot : dataSnapshot.child("photo").getChildren()) {
                          Photo p2 = photoSnapshot.getValue(Photo.class);
                          photoList.add(p2);

                      }
                      photoList.add(p);
                      databaseReference.child(userID).child("photo").setValue(photoList);
                      photoList.clear();
                      Intent intent = new Intent(Upload.this, Main2Activity.class);
                      startActivity(intent);
                  }else{
                      photoList.add(p);
                      databaseReference.child(userID).child("photo").setValue(photoList);
                      photoList.clear();
                      Intent intent = new Intent(Upload.this, Main2Activity.class);
                      startActivity(intent);
                  }


              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }

          });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                        }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Upload.this,"Failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");

                    }
                });

    }


    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==330){
            filePath=data.getData();
            Bitmap bitmap1=(Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap1);
        }

        if (requestCode ==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            filePath=data.getData();
            try {

                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }






    }



