/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.adapter.items;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.SearchFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.EmojiTextViewE;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import static net.iGap.fragments.SearchFragment.SearchType.contact;

public class SearchItem extends AbstractItem<SearchItem, SearchItem.ViewHolder> {
    public SearchFragment.StructSearch item;
    private Typeface typeFaceIcon;
    private AvatarHandler avatarHandler;

    public SearchItem(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
    }

    public SearchItem setContact(SearchFragment.StructSearch item) {
        this.item = item;
        return this;
    }

    @Override
    public int getType() {
        return R.id.sfsl_imv_contact_avatar;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setAvatar(holder);

        holder.name.setText(item.name);
        if (item.comment.isEmpty()){
            holder.lastSeen.setText(item.userName);
        }else {
            holder.lastSeen.setText(item.comment);
        }

        //holder.txtTime.setText(TimeUtils.toLocal(item.time, G.CHAT_MESSAGE_TIME));
        holder.txtTime.setText(HelperCalander.getTimeForMainRoom(item.time));

        if (HelperCalander.isPersianUnicode) {
            //holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
//            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }

        holder.txtIcon.setVisibility(View.GONE);

        if (item.roomType == ProtoGlobal.Room.Type.CHAT) {
            //holder.txtIcon.setVisibility(View.VISIBLE);
            //holder.txtIcon.setText(G.context.getString(R.string.md_user_shape));
        } else if (item.roomType == ProtoGlobal.Room.Type.GROUP) {
            typeFaceIcon = G.typeface_Fontico;
            holder.txtIcon.setTypeface(typeFaceIcon);
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_users_social_symbol));
        } else if (item.roomType == ProtoGlobal.Room.Type.CHANNEL) {
            typeFaceIcon = G.typeface_Fontico;
            holder.txtIcon.setTypeface(typeFaceIcon);
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_channel_icon));
        }
    }

    private void setAvatar(final ViewHolder holder) {
        AvatarHandler.AvatarType avatarType;
        if (item.type == contact) {
            avatarType = AvatarHandler.AvatarType.USER;
        } else {
            if (item.roomType == ProtoGlobal.Room.Type.CHAT) {
                avatarType = AvatarHandler.AvatarType.USER;
            } else {
                avatarType = AvatarHandler.AvatarType.ROOM;
            }
        }
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.avatar, item.idDetectAvatar).avatarType(avatarType));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected EmojiTextViewE lastSeen;
        protected TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            avatar = (CircleImageView) view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = (CustomTextViewMedium) view.findViewById(R.id.sfsl_txt_contact_name);
            lastSeen = (EmojiTextViewE) view.findViewById(R.id.sfsl_txt_contact_lastseen);
            txtIcon = (TextView) view.findViewById(R.id.sfsl_txt_icon);
            txtTime = (TextView) view.findViewById(R.id.sfsl_txt_time);
        }
    }
}


