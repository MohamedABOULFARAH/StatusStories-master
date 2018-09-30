package com.rahuljanagouda.statusstories.sample;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity {

    private TextView mProfileLabel;
    private TextView mUsersLabel;
    private TextView mPhotoLabel;

    private ViewPager mMainPager;

    private PagerViewAdapter mPagerViewAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();

        mProfileLabel = (TextView) findViewById(R.id.profileLabel);
        mUsersLabel = (TextView) findViewById(R.id.usersLabel);
        mPhotoLabel = (TextView) findViewById(R.id.photosLabel);

        mMainPager = (ViewPager) findViewById(R.id.mainPager);
        mMainPager.setOffscreenPageLimit(2);

        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);

        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(0);

            }
        });
        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(1);

            }
        });

        mPhotoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(2);

            }
        });

        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTabs(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changeTabs(int position) {

        if(position == 0){

            mProfileLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabBright));
            mProfileLabel.setTextSize(20);

            mUsersLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mPhotoLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mPhotoLabel.setTextSize(16);

        }

        if(position == 1){

            mProfileLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabBright));
            mUsersLabel.setTextSize(20);

            mPhotoLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mPhotoLabel.setTextSize(16);

        }

        if(position == 2){

            mProfileLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mPhotoLabel.setTextColor(ContextCompat.getColor(Main2Activity.this,R.color.textTabBright));
            mPhotoLabel.setTextSize(22);


        }

    }
}
