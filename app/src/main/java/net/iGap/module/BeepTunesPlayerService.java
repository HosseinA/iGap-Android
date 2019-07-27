package net.iGap.module;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static net.iGap.G.context;

public class BeepTunesPlayerService extends Service {
    public static final String SONG_PATH = "songUri";
    public static final String SONG_ID = "songId";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PLAY_NEW = "play";
    public static final String ACTION_PAUSE = "pause";
    private static final String TAG = "aabolfazlService";
    private static boolean serviceRunning = false;
    private BeepTunesBinder binder = new BeepTunesBinder();
    private MediaPlayer mediaPlayer;
    private PlayingSong playingSong;

    private MutableLiveData<PlayingSong> playingSongMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> progressDurationLiveData = new MutableLiveData<>();

    public static boolean isServiceRunning() {
        return serviceRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        serviceRunning = true;
        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            playingSong = new PlayingSong();
            playingSong.setSongPath(intent.getStringExtra(SONG_PATH));
            playingSong.setSongId(intent.getLongExtra(SONG_ID, 0));
        }
        if (playingSong != null) {
            play(playingSong);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        mediaPlayer.release();
        super.onDestroy();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private void play(PlayingSong playingSong) {
        try {
            mediaPlayer.setDataSource(playingSong.getSongPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            getSongInfo(playingSong);
            progress(playingSong);
            playingSong.setStatus(PlayingSong.PLAY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playingSongMutableLiveData.postValue(playingSong);
    }

    private void getSongInfo(PlayingSong playingSong) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(new File(playingSong.getSongPath()));
        if (uri != null) {
            mediaMetadataRetriever.setDataSource(context, uri);
            playingSong.setTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            playingSong.setAlbumName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            playingSong.setArtistName(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if (data != null)
                playingSong.setImageData(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    }

    public MutableLiveData<PlayingSong> getPlayingSongMutableLiveData() {
        return playingSongMutableLiveData;
    }

    private void progress(PlayingSong playingSong) {
        Timer timer = new Timer();
        ProgressDuration progressDuration = new ProgressDuration();

        progressDuration.setId(playingSong.getSongId());
        progressDuration.setTotal(mediaPlayer.getDuration() / 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progressDuration.setCurrent(mediaPlayer.getCurrentPosition() / 1000);
                progressDurationLiveData.postValue(progressDuration);
            }
        }, 0, 1000);
    }

    public interface ServiceUpdate {
        void playingSong(PlayingSong playingSong);
    }

    public class BeepTunesBinder extends Binder {
        public BeepTunesPlayerService getService() {
            return BeepTunesPlayerService.this;
        }
    }

    public MutableLiveData<ProgressDuration> getProgressDurationLiveData() {
        return progressDurationLiveData;
    }
}
