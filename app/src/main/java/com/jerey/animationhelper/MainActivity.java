package com.jerey.animationhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jerey.animationlib.AnimationHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView mImageView;
    Button mStartBtn;
    Button mHideBtn;
    Button mStartAvtivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.img);
        mStartBtn = (Button) findViewById(R.id.show);
        mHideBtn = (Button) findViewById(R.id.hide);
        mStartAvtivityBtn = (Button) findViewById(R.id.startactivity);
        mStartBtn.setOnClickListener(this);
        mHideBtn.setOnClickListener(this);
        mStartAvtivityBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show:
                AnimationHelper.show(mImageView);
                break;
            case R.id.hide:
                AnimationHelper.hide(mImageView);
                break;
            case R.id.startactivity:
                AnimationHelper.startActivity(MainActivity.this,
                        new Intent(MainActivity.this, LoginActivity.class),
                        mStartAvtivityBtn,
                        R.color.colorPrimary
                );
                break;
        }
    }
}
