package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructContactInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */

/**
 * chat item for main displaying chats
 */
public class ChatItem extends AbstractItem<ChatItem, ChatItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mInfo;
    public OnComplete mComplete;

    public ChatItem setComplete(OnComplete complete) {
        this.mComplete = complete;
        return this;
    }

    public ChatItem setInfo(StructContactInfo info) {
        this.mInfo = info;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * get string chat icon
     *
     * @param chatType chat type
     * @return String
     */
    private String getStringChatIcon(MyType.ChatType chatType) {
        switch (chatType) {
            case singleChat:
                return G.context.getString(R.string.fa_user);
            case channel:
                return G.context.getString(R.string.fa_bullhorn);
            case groupChat:
                return G.context.getString(R.string.fa_group);
            default:
                return null;
        }
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        // TODO fill

        if (mInfo.imageSource.length() > 0) {
            holder.image.setImageResource(Integer.parseInt(mInfo.imageSource));
        } else {
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp60), mInfo.contactName, ""));
        }

        holder.chatIcon.setText(getStringChatIcon(mInfo.contactType));
        if (mInfo.contactType == MyType.ChatType.singleChat) {
            holder.chatIcon.setVisibility(View.GONE);
        } else {
            holder.chatIcon.setVisibility(View.VISIBLE);
        }

        holder.name.setText(mInfo.contactName);
        holder.lastMessage.setText(mInfo.lastmessage);
        holder.lastSeen.setText(mInfo.lastSeen);

        int unread = mInfo.unreadMessag;
        if (unread < 1) {
            holder.unreadMessage.setVisibility(View.INVISIBLE);
        } else {
            holder.unreadMessage.setVisibility(View.VISIBLE);
//            if(unread>99)
//               holder.unreadMessage.setText("+99");
//            else
            holder.unreadMessage.setText(unread + "");

            if (mInfo.muteNotification) {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_gray);
            } else {
                holder.unreadMessage.setBackgroundResource(R.drawable.oval_green);
            }
        }

        if (mInfo.muteNotification) {
            holder.mute.setVisibility(View.VISIBLE);
        } else {
            holder.mute.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected View distanceColor;
        protected TextView chatIcon;
        protected TextView name;
        protected TextView mute;
        protected TextView lastMessage;
        protected TextView lastSeen;
        protected TextView unreadMessage;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
            distanceColor = view.findViewById(R.id.cs_view_distance_color);
            chatIcon = (TextView) view.findViewById(R.id.cs_txt_contact_icon);
            name = (TextView) view.findViewById(R.id.cs_txt_contact_name);
            lastMessage = (TextView) view.findViewById(R.id.cs_txt_last_message);
            lastSeen = (TextView) view.findViewById(R.id.cs_txt_contact_time);
            unreadMessage = (TextView) view.findViewById(R.id.cs_txt_unread_message);
            mute = (TextView) view.findViewById(R.id.cs_txt_mute);

            chatIcon.setTypeface(G.fontawesome);
            name.setTypeface(G.arialBold);
            lastMessage.setTypeface(G.arial);
            mute.setTypeface(G.fontawesome);
            lastSeen.setTypeface(G.arial);
            unreadMessage.setTypeface(G.arial);
        }
    }
}
