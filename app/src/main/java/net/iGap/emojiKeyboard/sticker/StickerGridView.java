package net.iGap.emojiKeyboard.sticker;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;

public class StickerGridView extends FrameLayout implements StickerAdapter.AddStickerDialogListener {
    private AppCompatTextView stickerNameTv;
    private StickerAdapter adapter;

    private StickerAdapter.AddStickerDialogListener listener;

    public void setListener(StickerAdapter.AddStickerDialogListener listener) {
        this.listener = listener;
    }

    public StickerGridView(@NonNull Context context) {
        super(context);
        setBackgroundColor(Color.parseColor("#F5F5F5"));
        boolean isRtl = G.isAppRtl;

        stickerNameTv = new AppCompatTextView(getContext());
        stickerNameTv.setTextColor(new Theme().getTitleTextColor(getContext()));
        stickerNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        stickerNameTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        stickerNameTv.setLines(1);
        stickerNameTv.setMaxLines(1);
        stickerNameTv.setSingleLine(true);
        stickerNameTv.setEllipsize(TextUtils.TruncateAt.END);
        stickerNameTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);

        addView(stickerNameTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 12, 4, isRtl ? 12 : 0, 0));

        adapter = new StickerAdapter();
        adapter.setListener(this);

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        recyclerView.setAdapter(adapter);
        recyclerView.setClipToPadding(false);

        addView(recyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 24, 0, 10));

    }

    public void setStickerGroup(StructIGStickerGroup stickerGroup) {
        stickerNameTv.setText(stickerGroup.getName());
        adapter.setIgStickers(stickerGroup.getStickers());
    }

    @Override
    public void onStickerClick(StructIGSticker structIGSticker) {
        if (listener != null)
            listener.onStickerClick(structIGSticker);
    }
}
