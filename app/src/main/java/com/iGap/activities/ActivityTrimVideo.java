package com.iGap.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.helper.HelperCalander;
import java.io.File;
import java.text.DecimalFormat;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class ActivityTrimVideo extends ActivityEnhanced implements OnTrimVideoListener, OnK4LVideoListener {

    private String path;
    private long originalSize;
    private long duration;
    private TextView txtDetail;
    private TextView txtTime;
    private TextView txtSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_trime);

        txtDetail = (TextView) findViewById(R.id.stfaq_txt_detail);
        txtTime = (TextView) findViewById(R.id.stfaq_txt_time);
        txtSize = (TextView) findViewById(R.id.stfaq_txt_size);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            path = bundle.getString("PATH");
        }
        path = getRealPathFromURI(Uri.parse(path));
        durationVideo(path);

        K4LVideoTrimmer videoTrimmer = (K4LVideoTrimmer) findViewById(R.id.timeLine);
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(Uri.parse(path));
            videoTrimmer.setMaxDuration((int) duration);
            videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setDestinationPath(path);
            videoTrimmer.setVideoInformationVisibility(true);
        }
    }

    private void durationVideo(String path) {

        File file = new File(path);
        originalSize = file.length();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.toString()); // Enter Full File Path Here
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(time);
        int seconds = (int) ((duration) / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        txtTime.setText("," + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

        txtSize.setText("," + formatFileSize((long) originalSize));
    }

    @Override
    public void onTrimStarted() {
        Log.i("VVVVVVV", "onTrimStarted: ");
    }

    @Override
    public void getResult(final Uri uri) {
        Log.i("VVVVVVV", "getResult: ");
        Intent data = new Intent();
        data.setData(uri);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void cancelAction() {
        Log.i("VVVVVVV", "cancelAction: ");
        Uri uriCancel = Uri.parse(path);
        Intent data = new Intent();
        data.setData(uriCancel);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onError(String message) {
        Log.i("VVVVVVV", "onError: " + message);
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.0");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return HelperCalander.isLanguagePersian ? HelperCalander.convertToUnicodeFarsiNumber(hrSize) : hrSize;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onVideoPrepared() {
        Log.i("VVVVVVV", "onVideoPrepared: ");
    }
}
