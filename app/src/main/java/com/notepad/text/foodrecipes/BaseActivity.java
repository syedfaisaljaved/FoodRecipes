package com.notepad.text.foodrecipes;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressBar mProgressbar;
    @Override
    public void setContentView(int layoutResID) {

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base,null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        mProgressbar = constraintLayout.findViewById(R.id.progress_bar);

        getLayoutInflater().inflate(layoutResID,frameLayout,true);
        super.setContentView(layoutResID);
    }

    public void showProgressBar(boolean visibility){

        mProgressbar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);

    }
}
