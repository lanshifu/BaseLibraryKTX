package com.lanshifu.baselibraryktx.animations;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import com.lanshifu.baselibraryktx.R;


public class SharedElementActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedelement);

        Sample sample = new Sample(getResources().getColor(R.color.sample_blue), "Shared Elements");
        setupWindowAnimations();
        setupLayout(sample);
    }


    private void setupWindowAnimations() {
        // We are not interested in defining a new Enter Transition. Instead we change default transition duration
        getWindow().getEnterTransition().setDuration(getResources().getInteger(R.integer.anim_duration_long));
    }

    private void setupLayout(Sample sample) {
        // Transition for fragment1
        Slide slideTransition = new Slide(Gravity.LEFT);
        slideTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        // Create fragment and define some of it transitions
        SharedElementFragment1 sharedElementFragment1 = SharedElementFragment1.newInstance(sample);
        sharedElementFragment1.setReenterTransition(slideTransition);
        sharedElementFragment1.setExitTransition(slideTransition);
        sharedElementFragment1.setSharedElementEnterTransition(new ChangeBounds());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample2_content, sharedElementFragment1)
                .commit();
    }
}
