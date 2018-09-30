package com.rahuljanagouda.statusstories;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rahuljanagouda.statusstories.glideProgressBar.DelayBitmapTransformation;
import com.rahuljanagouda.statusstories.glideProgressBar.LoggingListener;
import com.rahuljanagouda.statusstories.glideProgressBar.ProgressTarget;


import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatusStoriesActivity extends AppCompatActivity implements StoryStatusView.UserInteractionListener {

    static DatabaseReference databaseReferenceSeen;
    static DatabaseReference databaseReferencePhoto;
    static DatabaseReference databaseReferenceComment;
    private FirebaseFirestore mFirestore;
    private String mUserId;
    private String mUserName;
    private String mCurrentId;
    static  DatabaseReference databaseReference;
    static Date currentTime;
    static ArrayList<Seen> seenList;
    static ArrayList<Commentaire> commentList;
    private FirebaseAuth mAuth;
    private String userID;
private boolean firstVue=true;
    public static final String STATUS_RESOURCES_KEY = "statusStoriesResources";
    public static final String STATUS_DURATION_KEY = "statusStoriesDuration";
    public static final String STATUS_DURATIONS_ARRAY_KEY = "statusStoriesDurations";
    public static final String IS_IMMERSIVE_KEY = "isImmersive";
    public static final String IS_CACHING_ENABLED_KEY = "isCaching";
    public static final String IS_TEXT_PROGRESS_ENABLED_KEY = "isText";
    public static final String STATUS_RESOURCES_ID="id of photos";
    public static final String STATUS_RESOURCES_ID2="id of user";
    public static final String id_user = "isText";
    public static final String name_user="id of photos";
    private static StoryStatusView storyStatusView;
    private ImageView image;
    private int counter = 0;
private EditText editTextComment;
private Button buttonComment;
    private String[] statusResources;
    private String[] statusResourcesid;
    private String statusResourcesid2;
    //    private long[] statusResourcesDuration;
    private long statusDuration;
    private boolean isImmersive = true;
    private boolean isCaching = true;
    private static boolean isTextEnabled = true;
    private ProgressTarget<String, Bitmap> target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_stories);

        mUserId=getIntent().getStringExtra(id_user);
        mUserName=getIntent().getStringExtra(name_user);
        statusResourcesid=getIntent().getStringArrayExtra(STATUS_RESOURCES_ID);
        statusResourcesid2=getIntent().getStringExtra(STATUS_RESOURCES_ID2);
        statusResources = getIntent().getStringArrayExtra(STATUS_RESOURCES_KEY);
        statusDuration = getIntent().getLongExtra(STATUS_DURATION_KEY, 3000L);
//        statusResourcesDuration = getIntent().getLongArrayExtra(STATUS_DURATIONS_ARRAY_KEY);
        isImmersive = getIntent().getBooleanExtra(IS_IMMERSIVE_KEY, true);
        isCaching = getIntent().getBooleanExtra(IS_CACHING_ENABLED_KEY, true);
        isTextEnabled = getIntent().getBooleanExtra(IS_TEXT_PROGRESS_ENABLED_KEY, true);

        ProgressBar imageProgressBar = findViewById(R.id.imageProgressBar);
        TextView textView = findViewById(R.id.textView);
        image = findViewById(R.id.image);


        editTextComment=findViewById(R.id.editText);


        KeyboardVisibilityEvent.setEventListener(
                StatusStoriesActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen){
                            image.setImageAlpha(20);
                            storyStatusView.pause();}
                        else{
                            image.setImageAlpha(200);
                            storyStatusView.resume();}
                    }
                });
      editTextComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean hasFocus) {
                  // editTextComment.requestFocus();
                  // editTextComment.setHint("My text");
                   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


           }
       });

        buttonComment=findViewById(R.id.buttoncomment);

        storyStatusView = findViewById(R.id.storiesStatus);
        storyStatusView.setStoriesCount(statusResources.length);
        storyStatusView.setStoryDuration(statusDuration);
        // or
        // statusView.setStoriesCountWithDurations(statusResourcesDuration);
        storyStatusView.setUserInteractionListener(this);
        storyStatusView.playStories();
        target = new MyProgressTarget<>(new BitmapImageViewTarget(image), imageProgressBar, textView);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyStatusView.skip();
            }
        });

        storyStatusView.pause();
        target.setModel(statusResources[counter]);
        Glide.with(image.getContext())
                .load(target.getModel())
                .asBitmap()
                .crossFade()
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .transform(new CenterCrop(image.getContext()), new DelayBitmapTransformation(1000))
                .listener(new LoggingListener<String, Bitmap>())
                .into(target);

editTextComment.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

storyStatusView.pause();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
});

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotif();
                addComment();

            }
        });

        // bind reverse view
        findViewById(R.id.reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyStatusView.reverse();
            }
        });

        // bind skip view
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyStatusView.skip();
            }
        });

        findViewById(R.id.actions).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    storyStatusView.pause();
                } else {
                    storyStatusView.resume();
                }
                return true;
            }
        });
        addVue();
    }

    @Override
    public void onNext() {

        storyStatusView.pause();
        ++counter;
        target.setModel(statusResources[counter]);
        Glide.with(image.getContext())
                .load(target.getModel())
                .asBitmap()
                .crossFade()
                .centerCrop()
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .transform(new CenterCrop(image.getContext()), new DelayBitmapTransformation(1000))
                .listener(new LoggingListener<String, Bitmap>())
                .into(target);
        addVue();
        editTextComment.setText("");
    }

    @Override
    public void onPrev() {

        if (counter - 1 < 0) return;
        storyStatusView.pause();
        --counter;
        target.setModel(statusResources[counter]);
        Glide.with(image.getContext())
                .load(target.getModel())
                .asBitmap()
                .centerCrop()
                .crossFade()
                .skipMemoryCache(!isCaching)
                .diskCacheStrategy(isCaching ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                .transform(new CenterCrop(image.getContext()), new DelayBitmapTransformation(1000))
                .listener(new LoggingListener<String, Bitmap>())
                .into(target);
        editTextComment.setText("");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isImmersive && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storyStatusView.destroy();
        super.onDestroy();
    }

    /**
     * Demonstrates 3 different ways of showing the progress:
     * <ul>
     * <li>Update a full fledged progress bar</li>
     * <li>Update a text view to display size/percentage</li>
     * <li>Update the placeholder via Drawable.level</li>
     * </ul>
     * This last one is tricky: the placeholder that Glide sets can be used as a progress drawable
     * without any extra Views in the view hierarchy if it supports levels via <code>usesLevel="true"</code>
     * or <code>level-list</code>.
     *
     * @param <Z> automatically match any real Glide target so it can be used flexibly without reimplementing.
     */
    @SuppressLint("SetTextI18n") // text set only for debugging
    private static class MyProgressTarget<Z> extends ProgressTarget<String, Z> {
        private final TextView text;
        private final ProgressBar progress;

        public MyProgressTarget(Target<Z> target, ProgressBar progress, TextView text) {
            super(target);
            this.progress = progress;
            this.text = text;
        }

        @Override
        public float getGranualityPercentage() {
            return 0.1f; // this matches the format string for #text below
        }

        @Override
        protected void onConnecting() {
            storyStatusView.pause();
            progress.setIndeterminate(true);
            progress.setVisibility(View.VISIBLE);

            if (isTextEnabled) {

                text.setVisibility(View.VISIBLE);
                text.setText("connecting");
            } else {
                text.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected void onDownloading(long bytesRead, long expectedLength) {
            progress.setIndeterminate(true);
            progress.setProgress((int) (100 * bytesRead / expectedLength));

            if (isTextEnabled) {
                text.setVisibility(View.VISIBLE);
                text.setText(String.format(Locale.ROOT, "downloading %.2f/%.2f MB %.1f%%",
                        bytesRead / 1e6, expectedLength / 1e6, 100f * bytesRead / expectedLength));
            } else {
                text.setVisibility(View.INVISIBLE);
            }


            storyStatusView.pause();

        }

        @Override
        protected void onDownloaded() {
            progress.setIndeterminate(true);
            if (isTextEnabled) {
                text.setVisibility(View.VISIBLE);
                text.setText("decoding and transforming");
            } else {
                text.setVisibility(View.INVISIBLE);
            }

            storyStatusView.pause();
        }

        @Override
        protected void onDelivered() {
            progress.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
            storyStatusView.resume();

        }

    }

    public void addVue(){

        databaseReferenceSeen = FirebaseDatabase.getInstance().getReference("photo")
                .child(statusResourcesid[counter]).child("Seen");
        databaseReferencePhoto = FirebaseDatabase.getInstance().getReference("photo");
        currentTime = Calendar.getInstance().getTime();
        seenList=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        final boolean[] tt = {false};
        final boolean[] go = {true};

        databaseReferenceSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seenList.clear();
                for(DataSnapshot seenSnapshot: dataSnapshot.getChildren() ){

                    Seen ss = seenSnapshot.getValue(Seen.class);
                    seenList.add(ss);

                }
                System.out.println("seenList size : "+seenList.size());
                int tailleDeLaBoucle = seenList.size();
                if (tailleDeLaBoucle==0){
                    if (!tt[0]){
                        Seen s=new Seen(userID,currentTime);
                        //      seenList=new ArrayList<>();
                        seenList.add(s);
                        databaseReferencePhoto.child(statusResourcesid[counter]).child("Seen").setValue(seenList);

                        tt[0] =true;
                        }
                }else{
                    for (int i = 0; i < tailleDeLaBoucle; i++) {
                        System.out.println("UserIdList : "+seenList.get(i).getUserId()+" // UserAuthId : "+userID);
                        if (seenList.get(i).getUserId().equals(userID)){

                            go[0] =false;

                        }}
                            if (!tt[0] && go[0]) {

                                Seen s = new Seen(userID, currentTime);
                                //      seenList=new ArrayList<>();
                                seenList.add(s);
                                databaseReferencePhoto.child(statusResourcesid[counter]).child("Seen").setValue(seenList);

                                tt[0] = true;
                            }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void addComment(){

        databaseReferenceComment = FirebaseDatabase.getInstance().getReference("photo")
                .child(statusResourcesid[counter]).child("Commentaire");
        databaseReferencePhoto = FirebaseDatabase.getInstance().getReference("photo");
        currentTime = Calendar.getInstance().getTime();
        commentList=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        final boolean[] tt = {false};

        databaseReferenceComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot commentaireSnapshot: dataSnapshot.getChildren() ){

                    Commentaire cc = commentaireSnapshot.getValue(Commentaire.class);
                    commentList.add(cc);

                }

                int tailleDeLaBoucle = commentList.size();
                if (tailleDeLaBoucle==0){
                    if (!tt[0]){
                        Commentaire c=new Commentaire(userID,editTextComment.getText().toString(),currentTime);
                        //      seenList=new ArrayList<>();
                        commentList.add(c);
                        databaseReferencePhoto.child(statusResourcesid[counter]).child("Commentaire").setValue(commentList);

                        tt[0] =true;
                    }
                }else{
                    for (int i = 0; i < tailleDeLaBoucle; i++) {
                            if (!tt[0]) {
                                Commentaire c=new Commentaire(userID,editTextComment.getText().toString(),currentTime);
                                //      seenList=new ArrayList<>();
                                commentList.add(c);
                                databaseReferencePhoto.child(statusResourcesid[counter]).child("Commentaire").setValue(commentList);
                                editTextComment.setText("");
                                tt[0] = true;
                            }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        Toast.makeText(StatusStoriesActivity.this, "Commenté!",
                Toast.LENGTH_LONG).show();

    }

public void sendNotif(){

    mFirestore = FirebaseFirestore.getInstance();
    mCurrentId = FirebaseAuth.getInstance().getUid();
    //hna lmochki
    //
    //
    //UserID


    String message = editTextComment.getText().toString();

    if(!TextUtils.isEmpty(message)){



        Map<String, Object> notificationMessage = new HashMap<>();
        notificationMessage.put("message", message);
        notificationMessage.put("from", mCurrentId);


        mFirestore.collection("Users/" + statusResourcesid2 + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(StatusStoriesActivity.this, "Notification Sent."+statusResourcesid2, Toast.LENGTH_LONG).show();
                editTextComment.setText("");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(StatusStoriesActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }
}

}
