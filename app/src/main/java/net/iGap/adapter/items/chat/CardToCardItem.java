/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.FontIconTextView;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class CardToCardItem extends AbstractMessage<CardToCardItem, CardToCardItem.ViewHolder> {

    public CardToCardItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cardToCard;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        super.bindView(holder, payloads);
        holder.getChatBloke().setBackgroundResource(0);


        setTextIfNeeded(holder.messageTv);

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder {
        private TextView cardToCardAmountTv;
        private TextView messageTv;
        private Button payButton;
        private FontIconTextView cardIcon;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private LinearLayout innerLayout;

        public ViewHolder(View view) {
            super(view);
            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();
            innerLayout = new LinearLayout(getContext());
            messageTv = new TextView(getContext());
            payButton = new Button(getContext());
            cardToCardAmountTv = new TextView(getContext());
            cardIcon = new FontIconTextView(getContext());

            messageTv.setTextColor(getColor(R.color.black));
            setTextSize(messageTv, R.dimen.smallTextSize);
            setTypeFace(messageTv);


            payButton.setId(R.id.cardToCard_button);
            payButton.setText(getResources().getString(R.string.pay));
            setTextSize(payButton, R.dimen.standardTextSize);
            payButton.setTextColor(getColor(R.color.white));
            payButton.setBackground(getDrawable(R.drawable.background_button_card_to_card));
            setTypeFace(payButton);


            cardToCardAmountTv.setText("مبلغ:1200");
            cardToCardAmountTv.setTextColor(getColor(R.color.black));
            setTypeFace(cardToCardAmountTv);


            cardIcon.setId(R.id.cardToCard_icon);
            cardIcon.setText("4");
            setTextSize(cardIcon, R.dimen.dp24);
            cardIcon.setTextColor(getColor(R.color.green));
            cardIcon.setBackground(getDrawable(R.drawable.background_card_to_card_icon));


            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setPadding(LayoutCreator.dp(4), LayoutCreator.dp(4), LayoutCreator.dp(4), LayoutCreator.dp(4));
            innerLayout.setId(R.id.cardToCard_innerLayout);
            innerLayout.setBackground(getDrawable(R.drawable.background_item_card_to_card));

            innerLayout.addView(messageTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT,
                    Gravity.CENTER, 8, 24, 8, 4));
            innerLayout.addView(cardToCardAmountTv, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT,
                    Gravity.CENTER, 8, 4, 8, 8));


            set.connect(payButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(payButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(payButton.getId(), ConstraintSet.TOP, innerLayout.getId(), ConstraintSet.BOTTOM, LayoutCreator.dp(8));

            set.constrainHeight(payButton.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(payButton.getId(), ConstraintSet.MATCH_CONSTRAINT);

            set.constrainHeight(innerLayout.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(innerLayout.getId(), ConstraintSet.MATCH_CONSTRAINT);

            set.constrainHeight(cardIcon.getId(), LayoutCreator.dp(56));
            set.constrainWidth(cardIcon.getId(), LayoutCreator.dp(56));

            set.connect(cardIcon.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(cardIcon.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(cardIcon.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

            set.connect(innerLayout.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, LayoutCreator.dp(30));

            rootView.addView(innerLayout);
            rootView.addView(payButton);
            rootView.addView(cardIcon);
            set.applyTo(rootView);


            rootView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
            getContentBloke().addView(rootView, 0);
        }

        public TextView getCardToCardAmountTv() {
            return cardToCardAmountTv;
        }
    }
}
