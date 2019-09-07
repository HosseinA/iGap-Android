package net.iGap.adapter.items.popularChannel;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.popularChannel.Channel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeatureCategoryAdapter extends RecyclerView.Adapter<FeatureCategoryAdapter.ChannelViewHolder> {
    private List<Channel> channelList;
    private OnClickedChannelEventCallBack onClickedChannelEventCallBack;

    FeatureCategoryAdapter(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.item_favorite_channel_rv_row, viewGroup, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder channelViewHolder, int i) {
        channelViewHolder.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    void setOnClickedChannelEventCallBack(OnClickedChannelEventCallBack onClickedChannelEventCallBack) {
        this.onClickedChannelEventCallBack = onClickedChannelEventCallBack;
    }

    public interface OnClickedChannelEventCallBack {
        void onClickedChannel(Channel channel);
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImage;
        private TextView channelTitle;
        private CardView root;

        ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImage = itemView.findViewById(R.id.circle_item_popular_rv_linear);
            channelTitle = itemView.findViewById(R.id.tv_item_popular_rv_linear);
            root = itemView.findViewById(R.id.card_item_pop_row);
            Utils.setCardsBackground(root, R.color.white, R.color.navigation_dark_mode_bg);

        }


        void bindChannel(Channel channel) {
            ImageLoadingService.load(channel.getIcon(), channelImage);
            if (G.isAppRtl)
                channelTitle.setText(channel.getTitle());
            else
                channelTitle.setText(channel.getTitleEn());

            itemView.setOnClickListener(v -> {
                if (onClickedChannelEventCallBack != null)
                    onClickedChannelEventCallBack.onClickedChannel(channel);
            });

        }
    }
}
