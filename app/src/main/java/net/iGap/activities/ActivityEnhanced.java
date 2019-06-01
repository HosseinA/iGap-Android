/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.WindowManager;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.UserStatusController;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.module.StatusBarUtil;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.request.RequestUserUpdateStatus;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static net.iGap.G.updateResources;


public class ActivityEnhanced extends AppCompatActivity {

    public AvatarHandler avatarHandler;
    protected boolean canSetUserStatus = true;
    public boolean isOnGetPermission = false;
    BroadcastReceiver mybroadcast = new BroadcastReceiver() {
        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.i("[BroadcastReceiver]", "MyReceiver");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//                if (G.isPassCode && !ActivityMain.isActivityEnterPassCode ) {
//                    G.isFirstPassCode = true;
//                    Intent i = new Intent(ActivityEnhanced.this, ActivityEnterPassCode.class);
//                    startActivity(i);
//                } else {
                if (G.isPassCode) ActivityMain.isLock = true;
//                }

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            }

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(updateResources(newBase)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeDirectoriesIfNotExist();

        G.currentActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {

        avatarHandler = new AvatarHandler();
        setThemeSetting();


        checkFont();

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mybroadcast, screenStateFilter);

        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        boolean allowScreen = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);

        if (G.isPassCode && !allowScreen) {
            try {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            } catch (Exception e) {
                HelperLog.setErrorLog(e);
            }
        } else {
            try {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            } catch (Exception e) {
                HelperLog.setErrorLog(e);
            }
        }

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(this, Color.parseColor(G.appBarColor), 50);
        }

        makeDirectoriesIfNotExist();

        boolean checkedEnableDataShams = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
        if (!checkedEnableDataShams) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void setThemeSetting() {
        switch (G.themeColor) {
            case Theme.CUSTOM:
                this.setTheme(R.style.Material_lightCustom);
                break;
            case Theme.DEFAULT:
                this.setTheme(R.style.Material_lightCustom);
                break;
            case Theme.DARK:
                this.setTheme(R.style.Material_blackCustom);
                break;
            case Theme.RED:
                this.setTheme(R.style.Material_red);
                break;
            case Theme.PINK:
                this.setTheme(R.style.Material_pink);
                break;
            case Theme.PURPLE:
                this.setTheme(R.style.Material_purple);
                break;
            case Theme.DEEPPURPLE:
                this.setTheme(R.style.Material_deepPurple);
                break;
            case Theme.INDIGO:
                this.setTheme(R.style.Material_indigo);
                break;
            case Theme.BLUE:
                this.setTheme(R.style.Material_blue);
                break;

            case Theme.LIGHT_BLUE:
                this.setTheme(R.style.Material_lightBlue);
                break;

            case Theme.CYAN:
                this.setTheme(R.style.Material_cyan);
                break;

            case Theme.TEAL:
                this.setTheme(R.style.Material_teal);
                break;

            case Theme.GREEN:
                this.setTheme(R.style.Material_green);
                break;

            case Theme.LIGHT_GREEN:
                this.setTheme(R.style.Material_lightGreen);
                break;

            case Theme.LIME:
                this.setTheme(R.style.Material_lime);
                break;

            case Theme.YELLLOW:
                this.setTheme(R.style.Material_yellow);
                break;
            case Theme.AMBER:
                this.setTheme(R.style.Material_amber);
                break;

            case Theme.ORANGE:
                this.setTheme(R.style.Material_orange);
                break;

            case Theme.DEEP_ORANGE:
                this.setTheme(R.style.Material_deepOrange);
                break;
            case Theme.BROWN:
                this.setTheme(R.style.Material_brown);
                break;
            case Theme.GREY:
                this.setTheme(R.style.Material_grey);
                break;
            case Theme.BLUE_GREY:
                this.setTheme(R.style.Material_blueGrey);
                break;
            case Theme.BLUE_GREY_COMPLETE:
                this.setTheme(R.style.Material_blueGreyComplete);
                break;
            case Theme.INDIGO_COMPLETE:
                this.setTheme(R.style.Material_indigoComplete);
                break;
            case Theme.BROWN_COMPLETE:
                this.setTheme(R.style.Material_BrownComplete);
                break;
            case Theme.TEAL_COMPLETE:
                this.setTheme(R.style.Material_TealComplete);
                break;
            case Theme.GREY_COMPLETE:
                this.setTheme(R.style.Material_GreyComplete);
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            HelperPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {

        if (!G.isAppInFg) {
            G.isAppInFg = true;
            G.isChangeScrFg = false;

            /**
             * if user isn't login and page come in foreground try for reconnect
             */
            if (!G.userLogin) {
                WebSocketClient.reconnect(true);
            }
        } else {
            G.isChangeScrFg = true;
        }
        G.isScrInFg = true;

        AttachFile.isInAttach = false;
        if (canSetUserStatus)
            UserStatusController.getInstance().setOnline();

        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();

        if (!G.isScrInFg || !G.isChangeScrFg) {
            G.isAppInFg = false;
        }
        G.isScrInFg = false;
        try{

            HelperDataUsage.insertDataUsage(null, true, true);
            HelperDataUsage.insertDataUsage(null, true, false);

            HelperDataUsage.insertDataUsage(null, false, true);
            HelperDataUsage.insertDataUsage(null, false, false);

        }catch (Exception e){};

        if (!AttachFile.isInAttach && canSetUserStatus) {
            UserStatusController.getInstance().setOffline();
        }
    }

    /**
     * check the selected language user and set the language if change it
     */

    private void checkFont() {

        if (G.typeface_IRANSansMobile == null) {
            G.typeface_IRANSansMobile = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }

        if (G.typeface_IRANSansMobile_Bold == null) {
            G.typeface_IRANSansMobile_Bold = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile_Bold.ttf");
        }

        if (G.typeface_Fontico == null) {
            G.typeface_Fontico = Typeface.createFromAsset(getAssets(), "fonts/iGap-Fontico.ttf");
        }

        if (G.typeface_neuropolitical == null) {
            G.typeface_neuropolitical = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        }
    }

    private void makeDirectoriesIfNotExist() {
        StartupActions.makeFolder();

//        if (isOnGetPermission) {
//            return;
//        }
//
//        if (this instanceof ActivityRegisteration) {
//            return;
//        }
//
//        isOnGetPermission = true;
//
//        try {
//            HelperPermision.getStoragePermision(this, new OnGetPermission() {
//                @Override
//                public void Allow() throws IOException {
//                    checkIsDirectoryExist();
//                }
//
//                @Override
//                public void deny() {
//                    //don't need to finish app because we can continue use from app with private data folder
//                    //finish();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void checkIsDirectoryExist() {

        isOnGetPermission = false;

        if (new File(G.DIR_APP).exists() && new File(G.DIR_IMAGES).exists() && new File(G.DIR_VIDEOS).exists() && new File(G.DIR_AUDIOS).exists() && new File(G.DIR_DOCUMENT).exists() && new File(G.DIR_CHAT_BACKGROUND).exists() && new File(G.DIR_IMAGE_USER).exists() && new File(G.DIR_TEMP).exists()) {
            return;
        } else {
            StartupActions.makeFolder();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mybroadcast);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources(getApplicationContext());
    }

}
