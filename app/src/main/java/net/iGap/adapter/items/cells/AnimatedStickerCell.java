package net.iGap.adapter.items.cells;

import android.content.Context;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import net.iGap.module.structs.StructMessageInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AnimatedStickerCell extends LottieAnimationView {
    private String TAG = "abbasiAnimation";

    private InputStream inputStream;
    public boolean animatedLoaded;
    private String path;
    private boolean detached = false;
    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public AnimatedStickerCell(Context context) {
        super(context);
        setRepeatCount(LottieDrawable.INFINITE);
        setRepeatMode(LottieDrawable.REVERSE);
    }

    public void setMessage(StructMessageInfo message) {
        if (message == null)
            return;

        if (message.getAttachment() != null && message.getAttachment().isFileExistsOnLocal()) {
            path = message.getAttachment().getLocalFilePath();
            loadAnimation(path);
        }

    }

    public void playAnimation(String path) {
        if (path != null && this.path == null) {
            this.path = path;
            loadAnimation(path);
        }
    }

    private void loadAnimation(String path) {
        if (path == null || path.isEmpty())
            return;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(path));
            setAnimation(inputStream, null);
            animatedLoaded = true;

            playAnimation();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            animatedLoaded = false;
        }
    }

    @Override
    public void playAnimation() {
        super.playAnimation();
        Log.i(TAG, "playAnimation: " + path);
        playing = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detached = true;
        try {
            if (inputStream != null)
                inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            animatedLoaded = false;
            playing = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (detached)
            if (animatedLoaded)
                playAnimation();
            else if (path != null) {
                loadAnimation(path);
            }
        detached = false;
    }
}
