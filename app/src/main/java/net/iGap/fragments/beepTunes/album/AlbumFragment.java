package net.iGap.fragments.beepTunes.album;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.beepTunes.AlbumTrackAdapter;
import net.iGap.adapter.beepTunes.ItemAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.ImageLoadingService;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.Track;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_COMPLETE;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_DOWNLOADING;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_ERROR;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_PAUSE;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_START;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_STOP;

public class AlbumFragment extends BaseFragment implements ToolbarListener {
    private static final String TAG = "aabolfazlAlbumView";
    private static String PATH;
    private View rootView;
    private ImageView albumAvatarIv;
    private ViewGroup actionButton;
    private ProgressBar progressBar;
    private TextView albumNameTv;
    private TextView artistNameTv;
    private TextView albumPriceTv;
    private TextView otherAlbumTv;
    private TextView statusTv;
    private RecyclerView otherAlbumRecyclerView;
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;
    private AlbumViewModel viewModel;
    private AlbumTrackAdapter trackAdapter;
    private ItemAdapter albumAdapter;
    private Album album;
    private List<Track> tracks;
    private SharedPreferences sharedPreferences;

    public AlbumFragment getInstance(Album album) {
        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.album = album;
        return albumFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_album, container, false);
        viewModel = new AlbumViewModel();
        trackAdapter = new AlbumTrackAdapter();
        albumAdapter = new ItemAdapter();
        tracks = new ArrayList<>();
        PATH = getContext().getFilesDir().getPath() + "beepTunes";
        sharedPreferences = getContext().getSharedPreferences(SHP_SETTING.KEY_BEEP_TUNES, Context.MODE_PRIVATE);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpViews();
        setUpAlbumInfo(album);

        viewModel.getAlbumSong(album.getId());
        viewModel.getArtistOtherAlbum(album.getArtists().get(0).getId());

        viewModel.getTrackMutableLiveData().observe(this, tracks -> {
            trackAdapter.setTracks(tracks);
            otherAlbumTv.setVisibility(View.VISIBLE);
            otherAlbumRecyclerView.setVisibility(View.VISIBLE);
            this.tracks = tracks;
        });

        viewModel.getAlbumMutableLiveData().observe(this, albums -> {
            if (albums != null) {
                albumAdapter.setAlbums(albums.getData());
            }
        });

        albumAdapter.setOnItemClick(album -> {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
            appBarLayout.setExpanded(true, true);
            if (!this.album.getId().equals(album.getId())) {
                this.album = album;
                setUpAlbumInfo(album);
                viewModel.getAlbumSong(album.getId());
            }
        });

        viewModel.getProgressMutableLiveData().observe(this, visibility -> {
            if (visibility != null && visibility) {
                progressBar.setVisibility(View.VISIBLE);
                statusTv.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                statusTv.setVisibility(View.VISIBLE);
            }
        });

        actionButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD, false);
            editor.apply();
        });

        trackAdapter.setOnclick(track -> {
            viewModel.onDownloadClick(track, PATH, getFragmentManager(), sharedPreferences);
        });

        viewModel.getDownloadStatusMutableLiveData().observe(this, downloadSong -> {
            switch (downloadSong.getDownloadStatus()) {
                case STATUS_START:
                    Log.i(TAG, "start: " + downloadSong.getId());
                    break;
                case STATUS_STOP:
                    Log.i(TAG, "stop: " + downloadSong.getId());
                    break;
                case STATUS_PAUSE:
                    Log.i(TAG, "pause: " + downloadSong.getId());
                    break;
                case STATUS_COMPLETE:
                    Log.i(TAG, "complete: " + downloadSong.getId());
                    break;
                case STATUS_ERROR:
                    Log.i(TAG, "error: " + downloadSong.getId());
                    break;
                case STATUS_DOWNLOADING:
                    Log.i(TAG, "downloading: " + downloadSong.getId());
                    break;
            }
        });
    }

    private void setUpViews() {
        albumAvatarIv = rootView.findViewById(R.id.iv_albumFragment_header);
        artistNameTv = rootView.findViewById(R.id.tv_album_artistName);
        albumPriceTv = rootView.findViewById(R.id.tv_album_albumeCost);
        RecyclerView songRecyclerView = rootView.findViewById(R.id.rv_album_songs);
        albumNameTv = rootView.findViewById(R.id.tv_album_name);
        otherAlbumRecyclerView = rootView.findViewById(R.id.rv_album_artistAlbums);
        actionButton = rootView.findViewById(R.id.fl_album_actionButton);
        statusTv = rootView.findViewById(R.id.tv_album_status);
        progressBar = rootView.findViewById(R.id.pb_album_progress);
        otherAlbumTv = rootView.findViewById(R.id.tv_album_artistOtherAlbum);
        appBarLayout = rootView.findViewById(R.id.ab_album);
        scrollView = rootView.findViewById(R.id.ns_album);

        songRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        songRecyclerView.setAdapter(trackAdapter);
        otherAlbumRecyclerView.setAdapter(albumAdapter);
    }

    private void setUpAlbumInfo(Album album) {
        artistNameTv.setText(album.getArtists().get(0).getName());
        albumNameTv.setText(album.getName());
        ImageLoadingService.load(album.getImage(), albumAvatarIv);
        albumPriceTv.setText(album.getFinalPrice().toString());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    public interface OnProgress {
        void progress(boolean visibility);
    }
}
