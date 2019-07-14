package net.iGap.adapter.items.poll.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;

public class TypeUnknownViewHolder extends BaseViewHolder {
    private TextView txt0;
    private CardView card0;

    public TypeUnknownViewHolder(PollAdapter pollAdapter,  @NonNull View itemView) {
        super(pollAdapter, itemView);
        txt0 = itemView.findViewById(R.id.unknown_text);
        card0 = itemView.findViewById(R.id.unknown_card);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(PollItem item) {
    }
}
