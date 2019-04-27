package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class Type5ViewHolder extends BaseViewHolder {
    private ImageView img0, img1, img2;
    private CardView card0, card1, card2;

    public Type5ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type5_img0);
        img1 = itemView.findViewById(R.id.type5_img1);
        img2 = itemView.findViewById(R.id.type5_img2);
        card0 = itemView.findViewById(R.id.type5_card0);
        card1 = itemView.findViewById(R.id.type5_card1);
        card2 = itemView.findViewById(R.id.type5_card2);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
        card1.setCardBackgroundColor(G.getThemeBackgroundColor());
        card2.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(DiscoveryItem item) {
        if (item.discoveryFields.get(0).imageUrl.endsWith(".gif")) {
            Glide.with(G.context)
                    .asGif()
                    .load(item.discoveryFields.get(0).imageUrl)
                    .into(img0);
        } else {
            G.imageLoader.displayImage(item.discoveryFields.get(0).imageUrl, img0, option);
        }

        if (item.discoveryFields.get(1).imageUrl.endsWith(".gif")) {
            Glide.with(G.context)
                    .asGif()
                    .load(item.discoveryFields.get(1).imageUrl)
                    .into(img1);
        } else {
            G.imageLoader.displayImage(item.discoveryFields.get(1).imageUrl, img1, option);
        }

        if (item.discoveryFields.get(2).imageUrl.endsWith(".gif")) {
            Glide.with(G.context)
                    .asGif()
                    .load(item.discoveryFields.get(2).imageUrl)
                    .into(img2);
        } else {
            G.imageLoader.displayImage(item.discoveryFields.get(2).imageUrl, img2, option);
        }

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(0));
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(1));
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(2));
            }
        });
    }
}