package net.iGap.fragments.giftStickers;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.view.StickerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyStickerListAdapter extends RecyclerView.Adapter<MyStickerListAdapter.ViewHolder> {

    private List<StructIGGiftSticker> items = new ArrayList<>();
    private Delegate delegate;
    private AvatarHandler avatarHandler;

    public MyStickerListAdapter() {
        avatarHandler = new AvatarHandler();
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setItems(List<StructIGGiftSticker> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_gift_sticker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private StickerView stickerView;
        private AppCompatTextView giftStickerTitle;
        private AppCompatTextView giftStickerPrice;
        private ImageView userAvatarIv;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerView = itemView.findViewById(R.id.stickerView);
            giftStickerTitle = itemView.findViewById(R.id.giftStickerTitle);
            giftStickerPrice = itemView.findViewById(R.id.giftStickerPrice);
            giftStickerTitle.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
            giftStickerPrice.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
            userAvatarIv = itemView.findViewById(R.id.userAvatar);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bindView(StructIGGiftSticker giftSticker) {
            stickerView.loadSticker(giftSticker.getStructIGSticker());
            String rrn = itemView.getContext().getResources().getString(R.string.rrn) + ": " + (HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(giftSticker.getRrn()) : giftSticker.getRrn());
            giftStickerTitle.setText(rrn);

            DecimalFormat df = new DecimalFormat("#,###");
            String price = (HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(giftSticker.getStructIGSticker().getGiftAmount()))) : df.format(Double.valueOf(giftSticker.getStructIGSticker().getGiftAmount()))) + " " + itemView.getContext().getResources().getString(R.string.rial);
            giftStickerPrice.setText(price);

            progressBar.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> delegate.onClick(giftSticker, visibility -> progressBar.setVisibility(visibility)));


            Long userId = null;

            if (giftSticker.getFromUserId() != null) {
                userId = Long.valueOf(giftSticker.getFromUserId());
            } else if (giftSticker.getToUserId() != null) {
                userId = Long.valueOf(giftSticker.getToUserId());
            }

            if (userId != null) {
                userAvatarIv.setVisibility(View.VISIBLE);
                avatarHandler.getAvatar(new ParamWithAvatarType(userAvatarIv, userId).avatarType(AvatarHandler.AvatarType.USER).showMain(), true);
            } else {
                userAvatarIv.setVisibility(View.GONE);
            }
        }
    }

    public interface Delegate {
        void onClick(StructIGGiftSticker giftSticker, ProgressDelegate progressDelegate);
    }

    public interface ProgressDelegate {
        void onView(int visibility);
    }
}
