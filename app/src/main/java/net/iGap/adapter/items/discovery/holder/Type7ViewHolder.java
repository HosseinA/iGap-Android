package net.iGap.adapter.items.discovery.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class Type7ViewHolder extends BaseViewHolder {
    private ImageView img0, img1, img2;
    private CardView card0, card1, card2;

    public Type7ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        img0 = itemView.findViewById(R.id.type7_img0);
        img1 = itemView.findViewById(R.id.type7_img1);
        img2 = itemView.findViewById(R.id.type7_img2);
        card0 = itemView.findViewById(R.id.type7_card0);
        card1 = itemView.findViewById(R.id.type7_card1);
        card2 = itemView.findViewById(R.id.type7_card2);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
        card1.setCardBackgroundColor(G.getThemeBackgroundColor());
        card2.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(DiscoveryItem item) {
        img0.setImageDrawable(null);
        img1.setImageDrawable(null);
        img2.setImageDrawable(null);
        if (item.discoveryFields == null || item.discoveryFields.size() < 3)
            return;
        loadImage(img0, item.discoveryFields.get(0).imageUrl);
        loadImage(img1, item.discoveryFields.get(1).imageUrl);
        loadImage(img2, item.discoveryFields.get(2).imageUrl);

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
