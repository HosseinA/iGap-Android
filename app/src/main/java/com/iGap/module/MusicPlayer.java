package com.iGap.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityMediaPlayer;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatHistoryFields;
import com.iGap.realm.RealmRoomMessage;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by android3 on 10/2/2016.
 */
public class MusicPlayer {

    public static final int notificationId = 19;
    public static String repeatMode = RepeatMode.noRepeat.toString();
    public static boolean isShuffelOn = false;
    public static TextView txt_music_time;
    public static TextView txt_music_time_counter;
    public static String musicTime = "";
    public static String roomName;
    public static String musicPath;
    public static String musicName;
    public static String musicInfo = "";
    public static String musicInfoTitle = "";
    public static long roomId = 0;
    public static Bitmap mediaThumpnail = null;
    public static MediaPlayer mp;
    public static OnComplete onComplete = null;
    public static boolean isShowMediaPlayer = false;
    public static int musicProgress = 0;
    private static LinearLayout layoutTripMusic;
    private static Button btnPlayMusic;
    private static Button btnCloseMusic;
    private static TextView txt_music_name;
    private static RemoteViews remoteViews;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static boolean isPause = false;
    private static RealmList<RealmRoomMessage> mediaList;
    private static int selectedMedia = 0;
    private static Timer mTimer, mTimeSecend;
    private static long time = 0;
    private static double amoungToupdate;
    private static String strTimer = "";
    private static Handler handler;

    public MusicPlayer(LinearLayout layoutTripMusic) {

        remoteViews =
            new RemoteViews(G.context.getPackageName(), R.layout.music_layout_notification);
        notificationManager =
            (NotificationManager) G.context.getSystemService(Context.NOTIFICATION_SERVICE);
        handler = new Handler(G.context.getMainLooper());

        if (this.layoutTripMusic != null) this.layoutTripMusic.setVisibility(View.GONE);

        initLayoutTripMusic(layoutTripMusic);

        getAtribuits();
    }

    public static void repeatClick() {

        String str = "";
        if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
            str = RepeatMode.repeatAll.toString();
        } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {
            str = RepeatMode.oneRpeat.toString();
        } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
            str = RepeatMode.noRepeat.toString();
        }

        repeatMode = str;

        SharedPreferences sharedPreferences = sharedPreferences =
            G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RepeatMode", str);
        editor.apply();

        if (onComplete != null) {
            onComplete.complete(true, "RepeatMode", "");
        }
    }

    public static void shuffelClick() {

        isShuffelOn = !isShuffelOn;
        SharedPreferences sharedPreferences = sharedPreferences =
            G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Shuffel", isShuffelOn);
        editor.apply();
        if (onComplete != null) {
            onComplete.complete(true, "Shuffel", "");
        }
    }

    public static void initLayoutTripMusic(LinearLayout layout) {

        MusicPlayer.layoutTripMusic = layout;

        layout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(G.context, ActivityMediaPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
            }
        });

        txt_music_time = (TextView) layout.findViewById(R.id.mls_txt_music_time);
        txt_music_time_counter = (TextView) layout.findViewById(R.id.mls_txt_music_time_counter);
        txt_music_name = (TextView) layout.findViewById(R.id.mls_txt_music_name);

        btnPlayMusic = (Button) layout.findViewById(R.id.mls_btn_play_music);
        btnPlayMusic.setTypeface(G.flaticon);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                playAndPause();
            }
        });

        btnCloseMusic = (Button) layout.findViewById(R.id.mls_btn_close);
        btnCloseMusic.setTypeface(G.flaticon);
        btnCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                closeLayoutMediaPlayer();
            }
        });

        if (MusicPlayer.mp != null) {
            layout.setVisibility(View.VISIBLE);
            txt_music_name.setText(MusicPlayer.musicName);
            txt_music_time.setText(
                MusicPlayer.milliSecondsToTimer((long) MusicPlayer.mp.getDuration()));

            if (MusicPlayer.mp.isPlaying()) {
                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
            } else {
                btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));
            }
        }
    }

    public static void playAndPause() {

        if (mp != null) {
            if (mp.isPlaying()) {
                pauseSound();
            } else {
                playSound();
            }
        } else {
            closeLayoutMediaPlayer();
        }
    }

    private static void pauseSound() {
        try {

            stopTimer();
            btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            if (!isShowMediaPlayer) {
                notificationManager.notify(notificationId, notification);
            } else {
                onComplete.complete(true, "play", "");
            }
        } catch (Exception e) {
        }

        isPause = true;
        mp.pause();
    }

    //**************************************************************************

    private static void playSound() {
        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
            if (!isShowMediaPlayer) {
                notificationManager.notify(notificationId, notification);
            } else {
                onComplete.complete(true, "pause", "");
            }
        } catch (Exception e) {
        }

        if (isPause) {
            if (mp != null) {
                mp.start();
                isPause = false;
                updateProgress();
            }
        } else {
            startPlayer(musicPath, roomName, roomId, false);
        }
    }

    public static void stopSound() {

        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            if (!isShowMediaPlayer) {
                notificationManager.notify(notificationId, notification);
            } else {
                onComplete.complete(true, "play", "");
                musicProgress = 100;
                onComplete.complete(true, "updateTime", strTimer);
            }
            stopTimer();
        } catch (Exception e) {
        }

        if (mp != null) {
            mp.stop();
        }
    }

    public static void nextMusic() {
        if (selectedMedia < mediaList.size()) {
            startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName,
                roomId, false);
            selectedMedia++;
            if (onComplete != null) onComplete.complete(true, "update", "");
        } else {
            startPlayer(mediaList.get(0).getAttachment().getLocalFilePath(), roomName, roomId,
                false);
            selectedMedia = 1;
            if (onComplete != null) onComplete.complete(true, "update", "");
        }
    }

    private static void nextRandomMusic() {
        Random r = new Random();
        selectedMedia = r.nextInt(mediaList.size());
        startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName,
            roomId, false);

        if (onComplete != null) onComplete.complete(true, "update", "");
    }

    public static void previousMusic() {

        if (selectedMedia > 1) {
            selectedMedia--;
            startPlayer(mediaList.get(selectedMedia - 1).getAttachment().getLocalFilePath(),
                roomName, roomId, false);

            if (onComplete != null) onComplete.complete(true, "update", "");
        } else {
            int item = mediaList.size();
            if (item > 0) {
                startPlayer(mediaList.get(item - 1).getAttachment().getLocalFilePath(), roomName,
                    roomId, false);
                selectedMedia = item;
                if (onComplete != null) onComplete.complete(true, "update", "");
            }
        }
    }

    private static void closeLayoutMediaPlayer() {
        if (layoutTripMusic != null) layoutTripMusic.setVisibility(View.GONE);
        stopSound();
        if (mp != null) {
            mp.release();
            mp = null;
        }
        try {
            notificationManager.cancel(notificationId);
        } catch (RuntimeException e) {
        }
    }

    public static void startPlayer(String musicPath, String roomName, long roomId,
        boolean updateList) {

        MusicPlayer.musicPath = musicPath;
        MusicPlayer.roomName = roomName;
        mediaThumpnail = null;
        MusicPlayer.roomId = roomId;

        if (layoutTripMusic.getVisibility() == View.GONE) {
            layoutTripMusic.setVisibility(View.VISIBLE);
        }

        if (mp != null) {
            mp.stop();
            mp.reset();

            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();

                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));

                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
                if (!isShowMediaPlayer) {

                    handler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                notificationManager.notify(notificationId, notification);
                            } catch (RuntimeException e) {
                            }
                        }
                    });
                } else {
                    onComplete.complete(true, "pause", "");
                }

                musicTime = milliSecondsToTimer((long) mp.getDuration());
                txt_music_time.setText(musicTime);

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);

                updateNotification();
            } catch (Exception e) {
            }
        } else {

            mp = new MediaPlayer();
            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();

                musicTime = milliSecondsToTimer((long) mp.getDuration());
                txt_music_time.setText(musicTime);

                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
                if (!isShowMediaPlayer) {

                    handler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                notificationManager.notify(notificationId, notification);
                            } catch (RuntimeException e) {
                            }
                        }
                    });
                } else {
                    onComplete.complete(true, "pause", "");
                }

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override public void onCompletion(MediaPlayer mp) {

                        if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
                            stopSound();
                        } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {

                            if (isShuffelOn) {
                                nextRandomMusic();
                            } else {

                                if (selectedMedia >= mediaList.size()) {
                                    selectedMedia = 0;
                                }
                                nextMusic();
                            }
                        } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
                            stopSound();
                            playAndPause();
                        }
                    }
                });

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);
                updateNotification();
            } catch (Exception e) {
            }
        }

        updateProgress();

        if (updateList) fillMediaList();
    }

    public static String milliSecondsToTimer(long milliseconds) {

        if (milliseconds == -1) return " ";

        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static void updateNotification() {

        getMusicInfo();

        PendingIntent pi = PendingIntent.getActivity(G.context, 10,
            new Intent(G.context, ActivityMediaPlayer.class), PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setTextViewText(R.id.mln_txt_music_name, MusicPlayer.musicName);
        remoteViews.setTextViewText(R.id.mln_txt_music_outher, MusicPlayer.musicInfoTitle);

        if (mp != null) {
            if (mp.isPlaying()) {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
            } else {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            }
        }

        Intent intentPrevious = new Intent(G.context, customButtonListener.class);
        intentPrevious.putExtra("mode", "previous");
        PendingIntent pendingIntentPrevious =
            PendingIntent.getBroadcast(G.context, 1, intentPrevious, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_Previous_music, pendingIntentPrevious);

        Intent intentPlayPause = new Intent(G.context, customButtonListener.class);
        intentPlayPause.putExtra("mode", "play");
        PendingIntent pendingIntentPlayPause =
            PendingIntent.getBroadcast(G.context, 2, intentPlayPause, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_play_music, pendingIntentPlayPause);

        Intent intentforward = new Intent(G.context, customButtonListener.class);
        intentforward.putExtra("mode", "forward");
        PendingIntent pendingIntentforward =
            PendingIntent.getBroadcast(G.context, 3, intentforward, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_forward_music, pendingIntentforward);

        Intent intentClose = new Intent(G.context, customButtonListener.class);
        intentClose.putExtra("mode", "close");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(G.context, 4, intentClose, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);

        notification =
            new NotificationCompat.Builder(G.context.getApplicationContext()).setTicker("music")
                .setSmallIcon(R.mipmap.j_audio)
                .setContentTitle(musicName)
                //  .setContentText(place)
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        handler.post(new Runnable() {
            @Override public void run() {
                try {
                    if (!isShowMediaPlayer) {
                        notificationManager.notify(notificationId, notification);
                    }
                } catch (RuntimeException e) {
                }
            }
        });
    }

    public static void fillMediaList() {

        mediaList = new RealmList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class)
            .equalTo(RealmChatHistoryFields.ROOM_ID, roomId)
            .findAll();

        if (chatHistories != null) {
            for (RealmChatHistory chatHistory : chatHistories) {
                if (chatHistory.getRoomMessage().getMessageType().equals("VOICE")
                    || chatHistory.getRoomMessage().getMessageType().equals("AUDIO")
                    || chatHistory.getRoomMessage().getMessageType().equals("AUDIO_TEXT")) {
                    mediaList.add(chatHistory.getRoomMessage());

                    if (chatHistory.getRoomMessage().getAttachment() != null) {
                        String tmpPath =
                            chatHistory.getRoomMessage().getAttachment().getLocalFilePath();
                        if (tmpPath != null) {
                            if (tmpPath.equals(musicPath)) selectedMedia = mediaList.size();
                        }
                    }
                }
            }
        }

        realm.close();
    }

    private static void updateProgress() {

        stopTimer();

        double duration = MusicPlayer.mp.getDuration();
        amoungToupdate = duration / 100;
        time = MusicPlayer.mp.getCurrentPosition();
        musicProgress = ((int) (time / amoungToupdate));

        mTimeSecend = new Timer();

        mTimeSecend.schedule(new TimerTask() {
            @Override public void run() {

                updatePlayerTime();
                time += 1000;
            }
        }, 0, 1000);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override public void run() {

                if (musicProgress < 100) {
                    musicProgress++;
                } else {
                    stopTimer();
                }
            }

            ;
        }, 0, (int) amoungToupdate);
    }

    private static void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeSecend != null) {
            mTimeSecend.cancel();
            mTimeSecend = null;
        }
    }

    private static void updatePlayerTime() {

        strTimer = MusicPlayer.milliSecondsToTimer(time);

        if (txt_music_time_counter != null) {

            txt_music_time_counter.post(new Runnable() {
                @Override public void run() {
                    txt_music_time_counter.setText(strTimer + "/");
                }
            });
        }

        if (isShowMediaPlayer) {
            onComplete.complete(true, "updateTime", strTimer);
        }
    }

    public static void setMusicProgress(int percent) {
        try {
            musicProgress = percent;
            if (MusicPlayer.mp != null) {
                MusicPlayer.mp.seekTo((int) (musicProgress * amoungToupdate));
                time = MusicPlayer.mp.getCurrentPosition();
                updatePlayerTime();
            }
        } catch (IllegalStateException e) {
        }
    }

    private static void getMusicInfo() {

        musicInfo = "";
        musicInfoTitle = "";

        MediaMetadataRetriever mediaMetadataRetriever =
            (MediaMetadataRetriever) new MediaMetadataRetriever();
        Uri uri = (Uri) Uri.fromFile(new File(MusicPlayer.musicPath));
        mediaMetadataRetriever.setDataSource(G.context, uri);

        String title = (String) mediaMetadataRetriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_TITLE);

        if (title != null) {
            musicInfo += title + "       ";
            musicInfoTitle = title;
        }

        String albumName =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if (albumName != null) {
            musicInfo += albumName + "       ";
            musicInfoTitle = albumName;
        }

        String artist =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist != null) {
            musicInfo += artist + "       ";
            musicInfoTitle = artist;
        }

        if (musicInfoTitle.trim().length() == 0) {
            musicInfoTitle = G.context.getString(R.string.unknown_artist);
        }

        try {
            mediaMetadataRetriever.setDataSource(G.context, uri);
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if (data != null) {
                mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                int size = (int) G.context.getResources().getDimension(R.dimen.dp48);
                remoteViews.setImageViewBitmap(R.id.mln_img_picture_music,
                    Bitmap.createScaledBitmap(mediaThumpnail, size, size, false));
            } else {
                remoteViews.setImageViewResource(R.id.mln_img_picture_music,
                    R.mipmap.music_icon_green);
            }
        } catch (Exception e) {
        }
    }

    private void getAtribuits() {
        SharedPreferences sharedPreferences = sharedPreferences =
            G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        repeatMode = sharedPreferences.getString("RepeatMode", RepeatMode.noRepeat.toString());
        isShuffelOn = sharedPreferences.getBoolean("Shuffel", false);
    }

    public enum RepeatMode {
        noRepeat,
        oneRpeat,
        repeatAll;
    }

    public static class customButtonListener extends BroadcastReceiver {

        @Override public void onReceive(Context context, Intent intent) {

            String str = intent.getExtras().getString("mode");

            if (str.equals("previous")) {
                previousMusic();
            } else if (str.equals("play")) {
                playAndPause();
            } else if (str.equals("forward")) {
                nextMusic();
            } else if (str.equals("close")) {
                closeLayoutMediaPlayer();
            }
        }
    }
}
