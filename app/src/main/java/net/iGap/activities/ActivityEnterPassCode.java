package net.iGap.activities;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
*/

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import com.andrognito.patternlockview.PatternLockView;

import net.iGap.R;
import net.iGap.databinding.ActivityEnterPassCodeBinding;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.ActivityEnterPassCodeViewModel;

public class ActivityEnterPassCode extends ActivityEnhanced {

    private ActivityEnterPassCodeViewModel activityManageSpaceViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityEnterPassCodeBinding activityEnterPassCodeBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pass_code);
        activityManageSpaceViewModel = new ActivityEnterPassCodeViewModel(this, activityEnterPassCodeBinding);

        activityEnterPassCodeBinding.setActivityEnterPassCodeViewModel(activityManageSpaceViewModel);
        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean isLinePattern = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);

        activityEnterPassCodeBinding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);       // Set the current viee more
        activityEnterPassCodeBinding.patternLockView.setInStealthMode(!isLinePattern);                                     // Set the pattern in stealth mode (pattern drawing is hidden)
        activityEnterPassCodeBinding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
        activityEnterPassCodeBinding.patternLockView.setInputEnabled(true);                                     // Disables any input from the pattern lock view completely

        activityEnterPassCodeBinding.patternLockView.setDotCount(4);
        activityEnterPassCodeBinding.patternLockView.setDotNormalSize((int) getResources().getDimension(R.dimen.dp22));
        activityEnterPassCodeBinding.patternLockView.setDotSelectedSize((int) getResources().getDimension(R.dimen.dp32));
        activityEnterPassCodeBinding.patternLockView.setPathWidth((int) getResources().getDimension(R.dimen.pattern_lock_path_width));
        activityEnterPassCodeBinding.patternLockView.setAspectRatioEnabled(true);
        activityEnterPassCodeBinding.patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        activityEnterPassCodeBinding.patternLockView.setNormalStateColor(getResources().getColor(R.color.white));
        activityEnterPassCodeBinding.patternLockView.setCorrectStateColor(getResources().getColor(R.color.white));
        activityEnterPassCodeBinding.patternLockView.setWrongStateColor(getResources().getColor(R.color.red));
        activityEnterPassCodeBinding.patternLockView.setDotAnimationDuration(150);
        activityEnterPassCodeBinding.patternLockView.setPathEndAnimationDuration(100);

    }

    @Override
    public void onResume() {
        super.onResume();

        activityManageSpaceViewModel.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityManageSpaceViewModel.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ActivityMain.finishActivity != null) {
            ActivityMain.finishActivity.finishActivity();
        }
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityManageSpaceViewModel.onStart();
    }

}
