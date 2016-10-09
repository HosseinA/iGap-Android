package com.iGap.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;

import java.io.File;

/**
 * Created by android3 on 10/2/2016.
 */
public class ActicityMediaPlayer extends ActivityEnhanced {

    private TextView txt_MusicName;
    private TextView txt_MusicPlace;
    private TextView txt_MusicTime;
    private TextView txt_Timer;
    private TextView txt_musicInfo;
    private SeekBar musikSeekbar;
    private ImageView img_MusicImage;
    private ImageView img_MusicImage_default_icon;
    private Button btnPlay;


    private String str_info = "";
    OnComplete onComplete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        MusicPlayer.isShowMediaPlayer = true;

        if (MusicPlayer.mp == null) {
            finish();
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancel(MusicPlayer.notificationId);
            return;
        }



        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    btnPlay.setText(R.string.md_play_rounded_button);

                } else if (messageOne.equals("pause")) {
                    btnPlay.setText(R.string.md_round_pause_button);

                } else if (messageOne.equals("update")) {
                    updateUi();
                } else if (messageOne.equals("updateTime")) {
                    txt_Timer.post(new Runnable() {
                        @Override
                        public void run() {
                            txt_Timer.setText(MessageTow);
                            musikSeekbar.setProgress(MusicPlayer.musicProgress);
                        }
                    });

                }

            }
        };

        MusicPlayer.onComplete = onComplete;

        initComponent();

        getMusicInfo();

    }


    @Override
    protected void onPause() {
        super.onPause();
        MusicPlayer.isShowMediaPlayer = false;
        MusicPlayer.onComplete = null;
        MusicPlayer.updateNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.isShowMediaPlayer = true;
        MusicPlayer.onComplete = onComplete;
        updateUi();
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(MusicPlayer.notificationId);

    }

    //*****************************************************************************************

    private void getMusicInfo() {

        str_info = "";

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        Uri uri = (Uri) Uri.fromFile(new File(MusicPlayer.musicPath));
        mediaMetadataRetriever.setDataSource(ActicityMediaPlayer.this, uri);

        String title = (String) mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if (title != null) {
            str_info += title + "       ";
        }

        String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if (albumName != null) {
            str_info += albumName + "       ";
        }

        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist != null) {
            str_info += artist + "       ";
        }

        if (str_info.trim().length() > 0) {
            txt_musicInfo.setVisibility(View.VISIBLE);
            txt_musicInfo.setText(str_info);

            txt_musicInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            txt_musicInfo.setSelected(true);
            txt_musicInfo.setSingleLine(true);
        } else {
            txt_musicInfo.setVisibility(View.GONE);
        }

//        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
//        if (data != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            img_MusicImage.setImageBitmap(bitmap);
//        }

    }

    private void initComponent() {

        txt_MusicName = (TextView) findViewById(R.id.ml_txt_music_name);
        txt_MusicPlace = (TextView) findViewById(R.id.ml_txt_music_place);
        txt_MusicTime = (TextView) findViewById(R.id.ml_txt_music_time);
        txt_Timer = (TextView) findViewById(R.id.ml_txt_timer);



        txt_musicInfo = (TextView) findViewById(R.id.ml_txt_music_info);
        img_MusicImage = (ImageView) findViewById(R.id.ml_img_music_picture);
        img_MusicImage_default_icon = (ImageView) findViewById(R.id.ml_img_music_icon_default);

        if (MusicPlayer.mediaThumpnail != null) {
            img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            img_MusicImage.setVisibility(View.VISIBLE);
            img_MusicImage_default_icon.setVisibility(View.GONE);
        } else {
            img_MusicImage.setVisibility(View.GONE);
            img_MusicImage_default_icon.setVisibility(View.VISIBLE);
        }


        musikSeekbar = (SeekBar) findViewById(R.id.ml_seekBar1);
        musikSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP)
                    MusicPlayer.setMusicProgress(musikSeekbar.getProgress());
                return false;
            }
        });

        Button btnBack = (Button) findViewById(R.id.ml_btn_back);
        btnBack.setTypeface(G.flaticon);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button btnMusicMenu = (Button) findViewById(R.id.ml_btn_music_menu);
        btnMusicMenu.setTypeface(G.flaticon);
        btnMusicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("ddd", "menu clicked");
            }
        });


        Button btnPrevious = (Button) findViewById(R.id.ml_btn_Previous_music);
        btnPrevious.setTypeface(G.flaticon);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.previousMusic();
            }
        });

        Button btnShuffel = (Button) findViewById(R.id.ml_btn_shuffel_music);
        btnShuffel.setTypeface(G.flaticon);
        btnShuffel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ddd", "shuffel click");
            }
        });

        Button btnReplay = (Button) findViewById(R.id.ml_btn_replay_music);
        btnReplay.setTypeface(G.flaticon);
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ddd", "btnReplay click");
            }
        });


        btnPlay = (Button) findViewById(R.id.ml_btn_play_music);
        btnPlay.setTypeface(G.flaticon);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.playAndPause();
            }
        });

        Button btnNextMusic = (Button) findViewById(R.id.ml_btn_forward_music);
        btnNextMusic.setTypeface(G.flaticon);
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.nextMusic();
            }
        });


    }

    private void updateUi() {
        txt_MusicTime.setText(MusicPlayer.musicTime);
        txt_MusicPlace.setText(MusicPlayer.roomName);
        txt_MusicName.setText(MusicPlayer.musicName);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                btnPlay.setText(getString(R.string.md_round_pause_button));
            } else {
                btnPlay.setText(getString(R.string.md_play_rounded_button));
            }

            if (MusicPlayer.mediaThumpnail != null) {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
                img_MusicImage.setVisibility(View.VISIBLE);
                img_MusicImage_default_icon.setVisibility(View.GONE);
            } else {
                img_MusicImage.setVisibility(View.GONE);
                img_MusicImage_default_icon.setVisibility(View.VISIBLE);
            }

            getMusicInfo();

        }


    }






}
