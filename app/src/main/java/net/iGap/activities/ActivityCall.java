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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.UserStatusController;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnHoldBackgroundChanegeListener;
import net.iGap.interfaces.OnVideoCallFrame;
import net.iGap.interfaces.VideoCallListener;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.audioManagement.BluethoothIntentReceiver;
import net.iGap.module.audioManagement.MusicIntentReceiver;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.webrtc.WebRTC;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.VideoFrame;
import org.webrtc.voiceengine.WebRtcAudioEffects;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.io.IOException;



import static android.bluetooth.BluetoothProfile.HEADSET;

public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView, OnVideoCallFrame, BluetoothProfile.ServiceListener {

    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";
    public static final String CALL_TYPE = "CALL_TYPE";
    private static final int SENSOR_SENSITIVITY = 4;

    //public static TextView txtTimeChat, txtTimerMain;
    public static boolean isGoingfromApp = false;
    public static View stripLayoutChat;
    public static View stripLayoutMain;
    public static boolean isNearDistance = false;
    public static OnFinishActivity onFinishActivity;
    boolean isIncomingCall = false;
    long userId;
    boolean canClick = false;
    boolean canTouch = false;
    boolean down = false;
    VerticalSwipe verticalSwipe;
    LinearLayout layoutCaller;
    FrameLayout layoutAnswer;
    MaterialDesignTextView btnCircleChat;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnAnswer;
    MediaPlayer player;
    MediaPlayer ringtonePlayer;
    SensorEventListener sensorEventListener;
    HeadsetPluginReciver headsetPluginReciver;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private ActivityCallViewModel activityCallViewModel;
    private ActivityCallBinding activityCallBinding;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;

    private int frameWidth;
    private int frameHeight;
    private int rotateFrame;
    private int phoneWidth;
    private int phoneHeight;
    private float screenScale;
    private boolean isRotated = false;
    private boolean isFrameChange = true;
    private boolean isVerticalOrient = true;
    private boolean isFirst = true;
    private boolean isHiddenButtons = false;


    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   <code>true</code> to enable, <code>false</code> to disable
     *                  the views.
     */
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = viewGroup.getChildAt(i);
                view.setEnabled(enabled);
                if (view instanceof ViewGroup) {
                    enableDisableViewGroup((ViewGroup) view, enabled);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (G.speakerControlListener != null) {
            G.speakerControlListener = null;
        }

        if (activityCallViewModel != null) {
            activityCallViewModel.onDestroy();

        }
        if (G.onHoldBackgroundChanegeListener != null) {
            G.onHoldBackgroundChanegeListener = null;
        }


       /* if (G.onRejectCallStatus != null) {
            G.onRejectCallStatus = null;
        }*/

    }

    @Override
    public void onBackPressed() throws IllegalStateException {
        //super.onBackPressed();
        //
        //if (!isSendLeave) {
        //    new WebRTC().leaveCall();
        //}

        startActivity(new Intent(ActivityCall.this, ActivityMain.class));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        canSetUserStatus = false;
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        /** register receiver for headset*/
        registerReceiver(new MusicIntentReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(new BluethoothIntentReceiver(), filter);


        //  registerReceiver(new BluethoothIntentReceiver(), new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


        /** First Check Is Headset Connected or Not */
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            G.isHandsFreeConnected = true;
        }


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth

        } else {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mBluetoothAdapter.getProfileConnectionState(HEADSET) == BluetoothAdapter.STATE_CONNECTED) {
                G.isBluetoothConnected = true;
                am.setSpeakerphoneOn(false);
            } else {
                G.isBluetoothConnected = false;
                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING)
                    am.setSpeakerphoneOn(true);
            }
        }

        super.onCreate(savedInstanceState);

        /** to get in pixel
         DisplayMetrics displayMetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int height = displayMetrics.heightPixels;
         int width = displayMetrics.widthPixels;*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            phoneHeight = displayMetrics.heightPixels;
            phoneWidth = displayMetrics.widthPixels;
        } else {
            phoneHeight = displayMetrics.widthPixels;
            phoneWidth = displayMetrics.heightPixels;
        }

        if (isGoingfromApp) {
            isGoingfromApp = false;
        } else {

            G.isInCall = false;

            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finish();
            return;
        }

        G.isInCall = true;

        userId = getIntent().getExtras().getLong(USER_ID_STR);
        isIncomingCall = getIntent().getExtras().getBoolean(INCOMING_CALL_STR);
        callTYpe = (ProtoSignalingOffer.SignalingOffer.Type) getIntent().getExtras().getSerializable(CALL_TYPE);


        try {
            HelperPermission.getMicroPhonePermission(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {

                    if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {

                        HelperPermission.getCameraPermission(ActivityCall.this, new OnGetPermission() {
                            @Override
                            public void Allow() throws IOException {
                                init();
                         /*       G.onRejectCallStatus = new OnRejectCallStatus() {
                                    @Override
                                    public void setReject(boolean state) {
                                        if (state)
                                            doReject();
                                    }
                                };*/
                            }

                            @Override
                            public void deny() {
                                G.isInCall = false;
                                finish();
                                if (isIncomingCall) {
                                    WebRTC.getInstance().leaveCall();
                                }
                            }
                        });

                    } else {
                        init();


                    }
                }

                @Override
                public void deny() {
                    G.isInCall = false;
                    finish();
                    if (isIncomingCall) {
                        WebRTC.getInstance().leaveCall();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerSensor();

        headsetPluginReciver = new HeadsetPluginReciver();

        onFinishActivity = new OnFinishActivity() {
            @Override
            public void finishActivity() {

                try {
                    if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                        activityCallBinding.fcrSurfacePeer.release();
                        activityCallBinding.fcrSurfaceRemote.release();
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

                finish();
            }
        };
    }

    private void init() {
        WebRTC.getInstance().setCallType(callTYpe);
        //setContentView(R.layout.activity_call);
        activityCallBinding = DataBindingUtil.setContentView(ActivityCall.this, R.layout.activity_call);
        activityCallViewModel = new ActivityCallViewModel(ActivityCall.this, userId, isIncomingCall, activityCallBinding, callTYpe);
        activityCallBinding.setActivityCallViewModel(activityCallViewModel);
        initComponent();
        //initCallBack();
        G.onCallLeaveView = ActivityCall.this;
        if (!isIncomingCall) {
            WebRTC.getInstance().createOffer(userId);
        }
    }

    private void doReject() {
        G.isInCall = false;
        finish();
        if (isIncomingCall) {
            WebRTC.getInstance().leaveCall();
        }
    }

    //***************************************************************************************

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (verticalSwipe != null) {
            verticalSwipe.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLeaveView(String type) {
        if (activityCallViewModel != null) {
            activityCallViewModel.onLeaveView(type);
        }
    }

    private void initComponent() {

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {

            EglBase rootEglBase = EglBase.create();
            activityCallBinding.fcrSurfacePeer.init(rootEglBase.getEglBaseContext(), null);
            activityCallBinding.fcrSurfacePeer.setEnableHardwareScaler(true);
            activityCallBinding.fcrSurfacePeer.setMirror(true);
            activityCallBinding.fcrSurfacePeer.setZOrderMediaOverlay(true);
            activityCallBinding.fcrSurfacePeer.setZOrderOnTop(true);
            activityCallBinding.fcrSurfacePeer.setVisibility(View.VISIBLE);

            activityCallBinding.fcrSurfaceRemote.init(rootEglBase.getEglBaseContext(), null);
            activityCallBinding.fcrSurfaceRemote.setEnableHardwareScaler(true);
            activityCallBinding.fcrSurfaceRemote.setMirror(false);
            activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);


            activityCallBinding.fcrImvBackground.setVisibility(View.VISIBLE);
            activityCallBinding.fcrTxtCallType.setText(getResources().getString(R.string.video_calls));
            activityCallBinding.fcrTxtCallType.setShadowLayer(10, 0, 3, Color.BLACK);
            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.VISIBLE);
            activityCallBinding.poweredBy.setVisibility(View.VISIBLE);
            activityCallBinding.poweredBy.setShadowLayer(10, 0, 3, Color.BLACK);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (activityCallBinding.poweredBy != null)
                    activityCallBinding.poweredBy.setVisibility(View.GONE);
            }


            G.videoCallListener = new VideoCallListener() {
                @Override
                public void notifyBackgroundChange() {

                    // activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
                                    activityCallBinding.fcrImvBackground.setVisibility(View.GONE);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
            };

            try {

                activityCallBinding.fcrSurfaceRemote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (G.isWebRtcConnected) {

                            if (!isHiddenButtons) {
                                activityCallBinding.fcrBtnChat.setVisibility(View.INVISIBLE);
                                activityCallBinding.fcrBtnSpeaker.setVisibility(View.INVISIBLE);
                                activityCallBinding.fcrBtnEnd.setVisibility(View.INVISIBLE);
                                activityCallBinding.fcrBtnChat.setVisibility(View.INVISIBLE);
                                activityCallBinding.fcrBtnMic.setVisibility(View.INVISIBLE);
                                activityCallBinding.fcrBtnSwichCamera.setVisibility(View.INVISIBLE);

                                isHiddenButtons = true;
                            } else {
                                activityCallBinding.fcrBtnChat.setVisibility(View.VISIBLE);
                                activityCallBinding.fcrBtnSpeaker.setVisibility(View.VISIBLE);
                                activityCallBinding.fcrBtnEnd.setVisibility(View.VISIBLE);
                                activityCallBinding.fcrBtnChat.setVisibility(View.VISIBLE);
                                activityCallBinding.fcrBtnMic.setVisibility(View.VISIBLE);
                                activityCallBinding.fcrBtnSwichCamera.setVisibility(View.VISIBLE);
                                isHiddenButtons = false;
                            }

                        }
                    }
                });

            } catch (Exception e) {
            }

        } else {
            activityCallBinding.fcrBtnSwichCamera.setVisibility(View.GONE);
        }

        verticalSwipe = new VerticalSwipe();
        layoutCaller = activityCallBinding.fcrLayoutCaller;

        /**
         * *************** layoutCallEnd ***************
         */

        final FrameLayout layoutCallEnd = activityCallBinding.fcrLayoutChatCallEnd;
        btnEndCall = activityCallBinding.fcrBtnEnd;

        if (isIncomingCall) {
            layoutCallEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (canClick) {
                        layoutCallEnd.setVisibility(View.INVISIBLE);
                        activityCallViewModel.endCall();
                    }
                }
            });

            btnEndCall.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setUpSwap(layoutCallEnd);
                    return false;
                }
            });
        } else {

            btnEndCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityCallViewModel.endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }

        /**
         * *************** layoutChat ***************
         */

        final FrameLayout layoutChat = activityCallBinding.fcrLayoutChatCall;
        btnCircleChat = activityCallBinding.fcrBtnCircleChat;

        if (isIncomingCall) {
            layoutChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canClick) {

                        activityCallViewModel.onClickBtnChat(v);
                        //btnChat.performClick();
                        layoutChat.setVisibility(View.INVISIBLE);
                    }
                }
            });

            btnCircleChat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setUpSwap(layoutChat);
                    return false;
                }
            });
        }

        /**
         * *************** layoutAnswer ***************
         */

        layoutAnswer = activityCallBinding.fcrLayoutAnswerCall;
        btnAnswer = activityCallBinding.fcrBtnCall;

        if (isIncomingCall) {
            layoutAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer(layoutAnswer, layoutChat);
                }
            });

            btnAnswer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    G.isWebRtcConnected = true;
                    // activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                    activityCallBinding.fcrImvBackground.setVisibility(View.GONE);
                    G.isVideoCallRinging = false;
                    setUpSwap(layoutAnswer);

                    return false;
                }
            });
        }

        /**
         * *********************************************
         */

        G.onHoldBackgroundChanegeListener = new OnHoldBackgroundChanegeListener() {
            @Override
            public void notifyBakcgroundChanege(boolean isHold) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isHold) {
                            activityCallBinding.fcrSurfaceRemote.setVisibility(View.INVISIBLE);
                            activityCallBinding.fcrImvBackground.setVisibility(View.VISIBLE);
                        } else {
                            activityCallBinding.fcrImvBackground.setVisibility(View.GONE);
                            activityCallBinding.fcrSurfaceRemote.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };

        setAnimation();


    }

    /**
     * *************** common methods ***************
     */

    private void answer(FrameLayout layoutAnswer, FrameLayout layoutChat) {
        UserStatusController.getInstance().setOnline();
        if (canClick) {
            layoutAnswer.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);

            WebRTC.getInstance().createAnswer();
            cancelRingtone();
        /*    try {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            } catch (Exception e) {
            }*/

            btnEndCall.setOnTouchListener(null);

            btnEndCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityCallViewModel.endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_enter_down_circke_button);
        layoutCaller.startAnimation(animation);
    }

    private void cancelRingtone() {

        try {
            if (ringtonePlayer != null) {
                ringtonePlayer.stop();
                ringtonePlayer.release();
                ringtonePlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            if (activityCallViewModel.vibrator != null) {
                activityCallViewModel.vibrator.cancel();
                activityCallViewModel.vibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }

                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopRingAnimation();
    }

    //*****************************  distance sensor  **********************************************************

    private void stopRingAnimation() {

        try {

            if (btnAnswer != null) {
                btnAnswer.clearAnimation();
            }

            // btnEndCall.clearAnimation();
            // btnCircleChat.clearAnimation();

        } catch (Exception e) {

            Log.e("debug", "activityCall     stopRingAnimation      " + e.toString());
        }


    }

    private void registerSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (activityCallBinding != null) {
                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                        boolean newIsNear = Math.abs(event.values[0]) < Math.min(event.sensor.getMaximumRange(), 3);
                        if (newIsNear != isNearDistance) {
                            isNearDistance = newIsNear;
                            if (isNearDistance) {
                                // near
                                screenOff();
                            } else {
                                //far
                                screenOn();
                            }
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    private void screenOn() {

        WindowManager.LayoutParams params = this.getWindow().getAttributes();

        params.screenBrightness = 1;
        this.getWindow().setAttributes(params);

        enableDisableViewGroup((ViewGroup) activityCallBinding.acLayoutCallRoot, true);
    }

    private void screenOff() {

        if (ActivityCallViewModel.isConnected) {

            WindowManager.LayoutParams params = this.getWindow().getAttributes();

            params.screenBrightness = 0;
            this.getWindow().setAttributes(params);

            enableDisableViewGroup((ViewGroup) activityCallBinding.acLayoutCallRoot, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            G.onVideoCallFrame = ActivityCall.this;
            if (!G.isCalling) {
                WebRTC.getInstance().startVideoCapture();
                WebRTC.getInstance().unMuteSound();
            }

        }

        mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPluginReciver, filter);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {

            if (activityCallBinding.poweredBy != null) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                    activityCallBinding.poweredBy.setVisibility(View.VISIBLE);
                } else {
                    activityCallBinding.poweredBy.setVisibility(View.GONE);

                }
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
        }

        rotateScreen(frameWidth, frameHeight);
        rotatePeer();

    }

    private void rotatePeer() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            android.widget.FrameLayout.LayoutParams
                    params = new android.widget.FrameLayout.LayoutParams(ViewMaker.dpToPixel(100), ViewMaker.dpToPixel(140));
            activityCallBinding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;

        } else {
            android.widget.FrameLayout.LayoutParams
                    params = new android.widget.FrameLayout.LayoutParams(ViewMaker.dpToPixel(140), ViewMaker.dpToPixel(100));
            activityCallBinding.fcrSurfacePeer.setLayoutParams(params);
            params.gravity = Gravity.TOP | Gravity.RIGHT;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            WebRTC.getInstance().pauseVideoCapture();
        }
        G.onVideoCallFrame = null;
        mSensorManager.unregisterListener(sensorEventListener);
        unregisterReceiver(headsetPluginReciver);
    }

    //***************************************************************************************

    private void setUpSwap(View view) {
        if (!down) {
            verticalSwipe.setView(view);
            canTouch = true;
            down = true;

            stopRingAnimation();
        }
    }

    @Override
    public void onRemoteFrame(VideoFrame videoFrame) {
        activityCallBinding.fcrSurfaceRemote.onFrame(videoFrame);
        if (isFrameChange) {
            frameWidth = videoFrame.getRotatedWidth();
            frameHeight = videoFrame.getRotatedHeight();
            rotateFrame = videoFrame.getRotation();
            isFrameChange = false;
        }

        if (isFirst) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rotateScreen(videoFrame.getRotatedWidth(), videoFrame.getRotatedHeight());
                }
            });

            isFirst = false;
        }

        if (rotateFrame != videoFrame.getRotation()) {
            int height = 0;
            int width = 0;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isVerticalOrient = true;
                    rotateScreen(videoFrame.getRotatedWidth(), videoFrame.getRotatedHeight());
                }
            });


            isFrameChange = true;


        }
    }

    public void rotateScreen(int frameWidth, int frameHeight) {

        float dpWidth = (Integer) phoneWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpHeight = phoneHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpFrameHeight = frameHeight / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        float dpFrameWidth = frameWidth / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            android.widget.FrameLayout.LayoutParams
                    params = new android.widget.FrameLayout.LayoutParams(phoneWidth, (int) (frameHeight * (dpWidth / dpFrameWidth)));
            activityCallBinding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;

        } else {
            android.widget.FrameLayout.LayoutParams
                    params = new android.widget.FrameLayout.LayoutParams((int) (frameWidth * (dpWidth / dpFrameHeight)), phoneWidth);
            activityCallBinding.fcrSurfaceRemote.setLayoutParams(params);
            params.gravity = Gravity.CENTER;


        }
    }


    @Override
    public void onPeerFrame(VideoFrame videoFrame) {

        activityCallBinding.fcrSurfacePeer.onFrame(videoFrame);

    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.i("#peymanProxy", "Activity call");
    }

    @Override
    public void onServiceDisconnected(int profile) {

    }

    //***************************************************************************************

    public interface OnFinishActivity {
        void finishActivity();
    }

    class HeadsetPluginReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                // if you need ti dermine plugin state

               /* int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d("dddddd", "Headset is unplugged");
                        break;
                    case 1:
                        Log.d("dddddd", "Headset is plugged");
                        break;
                    default:
                        Log.d("dddddd", "I have no idea what the headset state is");
                }

              */

                if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
                    cancelRingtone();
                    activityCallViewModel.playRingtone();
                }
            }
        }
    }

    class VerticalSwipe {

        boolean accept = false;
        private int AllMoving = 0;
        private int lastY;
        private int DistanceToAccept = (int) G.context.getResources().getDimension(R.dimen.dp120);
        private View view;

        public void setView(View view) {
            this.view = view;
        }

        void dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startMoving((int) event.getY());

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canTouch) {
                        moving((int) event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (canTouch) {
                        reset();
                    }

                    down = false;
                    break;
            }
        }

        private void startMoving(int y) {
            lastY = y;
            accept = false;
        }

        private void moving(int y) {
            int i = lastY - y;

            if (i > 0 || AllMoving > 0) {
                AllMoving += i;

                view.setPadding(0, 0, 0, view.getPaddingBottom() + i);

                lastY = y;
                if (AllMoving >= DistanceToAccept) {
                    accept = true;
                    reset();
                }
            }
        }

        private void reset() {
            view.setPadding(0, 0, 0, 0);
            canTouch = false;
            AllMoving = 0;

            if (accept) {
                canClick = true;
                view.performClick();
                canClick = false;

                accept = false;
            }

            view = null;
        }
    }

}
