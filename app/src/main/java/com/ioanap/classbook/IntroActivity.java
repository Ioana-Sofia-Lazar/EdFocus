package com.ioanap.classbook;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setDepthAnimation();

        String[] titles = getResources().getStringArray(R.array.teacher_intro_titles);
        String[] descriptions = getResources().getStringArray(R.array.teacher_intro_descriptions);
        int[] colors = getResources().getIntArray(R.array.slide_colors);
        TypedArray images = getResources().obtainTypedArray(R.array.teacher_intro_images);

        for (int i = 0; i < titles.length; i++) {
            addSlide(AppIntroFragment.newInstance(titles[0], descriptions[0], images.getResourceId(i, -1), colors[i]));
        }

        images.recycle();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
