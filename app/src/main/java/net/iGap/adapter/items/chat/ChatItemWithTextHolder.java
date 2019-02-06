package net.iGap.adapter.items.chat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.fragments.FragmentChat;
import net.iGap.module.EmojiTextViewE;

public class ChatItemWithTextHolder extends ChatItemHolder {
    private LinearLayout layoutMessageContainer;
    EmojiTextViewE messageView;

    public ChatItemWithTextHolder(View view) {
        super(view);
    }

    private void initViews() {
        messageView = ViewMaker.makeTextViewMessage();
        layoutMessageContainer = ViewMaker.getTextView();
        layoutMessageContainer.addView(messageView);
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("bagi" , "OnClickMessage" + FragmentChat.isInSelectionMode);

                if (FragmentChat.isInSelectionMode) {
                    itemView.performLongClick();
                } else {
                    mainContainer.performClick();
                }

            }
        });

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemView.performLongClick();
                return true;
            }
        });

    }

    protected void setLayoutMessageContainer(LinearLayout.LayoutParams layout_param) {
        initViews();
        layoutMessageContainer.setLayoutParams(layout_param);
        m_container.addView(layoutMessageContainer);
    }

    protected void setLayoutMessageContainer() {
        initViews();
        m_container.addView(layoutMessageContainer);
    }

    public void addButtonLayout(LinearLayout view) {
        layoutMessageContainer.addView(view);
    }

    public void removeButtonLayout() {
        for (int i = 1; i < layoutMessageContainer.getChildCount(); i++) {
            View child = layoutMessageContainer.getChildAt(i);
            if (child instanceof ViewGroup) {
                removeAllChildViews((ViewGroup) child);
            }
            layoutMessageContainer.removeView(child);

        }
    }

    private void removeAllChildViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                removeAllChildViews(((ViewGroup) child));
            } else {
                viewGroup.removeView(child);
            }
        }
    }

}

