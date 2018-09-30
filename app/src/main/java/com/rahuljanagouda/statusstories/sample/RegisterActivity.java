package com.rahuljanagouda.statusstories.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private CircleImageView mImageBtn;
    private EditText mEmailField;
    private EditText mNameField;
    private EditText mPasswordField;
    private Button mRegBtn;

    ArrayList<Photo> photoList;
    private Uri imageUri;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    DatabaseReference databaseReference;
    private ProgressBar mRegisterProgressBar;
    RelativeLayout RelativeLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        RelativeLayout1=(RelativeLayout) findViewById(R.id.RelativeLayout1);
        RelativeLayout1.setVisibility(View.VISIBLE);

        String userID = mAuth.getCurrentUser().getUid();

        DocumentReference mDocRef = mFirestore.collection("Users").document(userID);

        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            //Toast.makeText(RegisterActivity.this, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_LONG).show();

                        }

                        public void onFinish() {
                            RelativeLayout1.setVisibility(View.GONE);
                            //Toast.makeText(RegisterActivity.this, "done" , Toast.LENGTH_LONG).show();
                            sendToMain();
                        }
                    }.start();

                    //

                }else {
                    RelativeLayout1.setVisibility(View.GONE);
                }
            }
        });

        imageUri = null;

        mStorage = FirebaseStorage.getInstance().getReference().child("images");

        mImageBtn = (CircleImageView) findViewById(R.id.register_image_btn);
        mNameField = (EditText) findViewById(R.id.register_name);
        mRegBtn = (Button) findViewById(R.id.register_btn);
        mRegisterProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);


        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
test();
            }
                        });





        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });


    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, Main2Activity.class);
        startActivity(mainIntent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

            imageUri = data.getData();
            mImageBtn.setImageURI(imageUri);

        }

    }
private void test(){
    if(imageUri != null){

        mRegisterProgressBar.setVisibility(View.VISIBLE);

        final String name = mNameField.getText().toString();

        final String user_id = mAuth.getCurrentUser().getUid();

        StorageReference user_profile = mStorage.child(user_id + ".jpg");

        user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {

                if(uploadTask.isSuccessful()){

                    final String download_url = uploadTask.getResult().getDownloadUrl().toString();


                    String token_id = FirebaseInstanceId.getInstance().getToken();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("image", download_url);
                    userMap.put("token_id", token_id);

                    mFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            databaseReference = FirebaseDatabase.getInstance().getReference("user");
                            String id = databaseReference.push().getKey();
                            photoList=new ArrayList<>();
                            User u = new User(user_id, name, photoList);
                            databaseReference.child(user_id).setValue(u);
                            mRegisterProgressBar.setVisibility(View.INVISIBLE);

                            sendToMain();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(RegisterActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mRegisterProgressBar.setVisibility(View.INVISIBLE);

                        }
                    });


                } else {

                    Toast.makeText(RegisterActivity.this, "Error : " + uploadTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    mRegisterProgressBar.setVisibility(View.INVISIBLE);

                }

            }
        });


    } else {

        Toast.makeText(RegisterActivity.this, "Error : verifier l'image de profile svp", Toast.LENGTH_SHORT).show();
        mRegisterProgressBar.setVisibility(View.INVISIBLE);

    }

}

}