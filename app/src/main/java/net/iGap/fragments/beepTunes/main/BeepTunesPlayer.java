package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.module.CircleImageView;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;

public class BeepTunesPlayer extends BaseFragment {

    private static final String TAG = "aabolfazlPlayer";
    private View rootView;
    private TextView playTv;
    private TextView nextTv;
    private TextView previousTv;
    private TextView artistNameTv;
    private TextView songNameTv;
    private TextView totalTimeTv;
    private TextView currentTimeTv;
    private SeekBar seekBar;
    private CircleImageView songArtIv;
    private ImageView backgroundIv;

    private MutableLiveData<PlayingSong> songMutableLiveData;
    private MutableLiveData<PlayingSong> songFromPlayerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> seekBarLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> progressDurationLiveData;


    public static BeepTunesPlayer getInstance(MutableLiveData<PlayingSong> songMutableLiveData, MutableLiveData<ProgressDuration> progressDurationLiveData) {
        BeepTunesPlayer beepTunesPlayer = new BeepTunesPlayer();
        beepTunesPlayer.songMutableLiveData = songMutableLiveData;
        beepTunesPlayer.progressDurationLiveData = progressDurationLiveData;
        return beepTunesPlayer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpViews();

        songMutableLiveData.observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songArtIv.setImageBitmap(playingSong.getBitmap());
                backgroundIv.setImageBitmap(playingSong.getBitmap());
                if (playingSong.isPlay()) {
                    playTv.setText(getContext().getResources().getString(R.string.pause_icon));
                } else {
                    playTv.setText(getContext().getResources().getString(R.string.play_icon));
                }
            }
        });


//        realmDownloadSong = getRealm().where(RealmDownloadSong.class).equalTo("artistId", songMutableLiveData.getValue().getArtistId()).findFirst();
//
//        realmDownloadSong.addChangeListener((RealmObjectChangeListener<RealmDownloadSong>) (realmDownloadSong, changeSet) -> {
//            downloadedTracks = getRealm().copyFromRealm(getRealm().where(RealmDownloadSong.class)
//                    .equalTo("artistId", realmDownloadSong.getArtistId()).findAll());
//            adapter.setDownloadSongs(downloadedTracks);
//        });


        progressDurationLiveData.observe(getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null && songMutableLiveData.getValue() != null) {
                if (songMutableLiveData.getValue().getSongId() == progressDuration.getId()) {
                    seekBar.setProgress(progressDuration.getCurrent());
                    seekBar.setMax(progressDuration.getTotal());
                    currentTimeTv.setText(getTimeString(progressDuration.getCurrent() * 1000));
                    totalTimeTv.setText(getTimeString(progressDuration.getTotal() * 1000));

                }
            }
        });

        playTv.setOnClickListener(v -> {
            if (songMutableLiveData.getValue() != null && songMutableLiveData.getValue().isPlay()) {
                songMutableLiveData.getValue().setStatus(PlayingSong.PAUSE);
                songMutableLiveData.getValue().setFromPlayer(true);
                songFromPlayerLiveData.postValue(songMutableLiveData.getValue());
            } else {
                songMutableLiveData.getValue().setStatus(PlayingSong.PLAY);
                songMutableLiveData.getValue().setFromPlayer(true);
                songFromPlayerLiveData.postValue(songMutableLiveData.getValue());
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBarLiveData.postValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setUpViews() {
        playTv = rootView.findViewById(R.id.tv_btPlayer_play);
        nextTv = rootView.findViewById(R.id.tv_btPlayer_nextSong);
        previousTv = rootView.findViewById(R.id.tv_ptPlayer_previous);
        artistNameTv = rootView.findViewById(R.id.tv_btPlayer_artistName);
        songNameTv = rootView.findViewById(R.id.tv_btPlayer_songName);
        totalTimeTv = rootView.findViewById(R.id.tv_btPlayer_totalDuration);
        currentTimeTv = rootView.findViewById(R.id.tv_btPlayer_currentDuration);
        seekBar = rootView.findViewById(R.id.sb_ptPlayer);
        songArtIv = rootView.findViewById(R.id.iv_btPlayer_songArt);
        backgroundIv = rootView.findViewById(R.id.iv_btPlayer_cover);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#00D20E"), PorterDuff.Mode.SRC_IN);
    }

    private String getTimeString(long millis) {

        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
        String time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

        return time;
    }

    public MutableLiveData<PlayingSong> getSongFromPlayerLiveData() {
        return songFromPlayerLiveData;
    }

    public MutableLiveData<Integer> getPlayingSongSeekBarLiveData() {
        return seekBarLiveData;
    }
}

