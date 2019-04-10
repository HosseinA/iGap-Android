package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

public class Type6ViewHolder extends BaseViewHolder {
    private ImageView img0, img1, img2;
    private CardView card0, card1, card2;

    public Type6ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type6_img0);
        img1 = itemView.findViewById(R.id.type6_img1);
        img2 = itemView.findViewById(R.id.type6_img2);
        card0 = itemView.findViewById(R.id.type6_card0);
        card1 = itemView.findViewById(R.id.type6_card1);
        card2 = itemView.findViewById(R.id.type6_card2);
    }

    @Override
    public void bindView(ProtoGlobal.Discovery item) {
        List<ProtoGlobal.DiscoveryField> discoveryFields = item.getDiscoveryfieldsList();
        G.imageLoader.displayImage(discoveryFields.get(0).getImageurl(), img0);
        G.imageLoader.displayImage(discoveryFields.get(1).getImageurl(), img1);
        G.imageLoader.displayImage(discoveryFields.get(2).getImageurl(), img2);
    }
}
