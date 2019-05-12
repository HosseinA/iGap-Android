package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class Type2ViewHolder extends BaseViewHolder {
    private ImageView img0, img1;
    private CardView card0, card1;

    public Type2ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type2_img0);
        img1 = itemView.findViewById(R.id.type2_img1);
        card0 = itemView.findViewById(R.id.type2_card0);
        card1 = itemView.findViewById(R.id.type2_card1);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
        card1.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(DiscoveryItem item) {
        img0.setImageDrawable(null);
        img1.setImageDrawable(null);

        if (item.discoveryFields == null || item.discoveryFields.size() < 2)
            return;

        loadImage(img0, item.discoveryFields.get(0).imageUrl);
        loadImage(img1, item.discoveryFields.get(1).imageUrl);

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

    }
}
