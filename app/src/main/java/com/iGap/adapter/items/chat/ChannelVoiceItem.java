package com.iGap.adapter.items.chat;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityComment;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ChannelVoiceItem extends AbstractMessage<ChannelVoiceItem, ChannelVoiceItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ChannelVoiceItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        super(false, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_channel_voice;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.cslr_txt_message.setText(mMessage.messageText);

        holder.cslch_ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "omment click");

                Intent intent = new Intent(view.getContext(), ActivityComment.class);
                intent.putExtra("MessageID", mMessage.messageID);

                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout cslch_ll_parent;
        protected LinearLayout cslr_ll_content_main;
        protected TextView cslr_txt_message;
        protected Button cslch_btn_item_menu;
        protected LinearLayout cslch_ll_info;
        protected TextView cslch_txt_time_date;
        protected TextView cslch_txt_time_clock;
        protected LinearLayout cslch_ll_like;
        protected TextView cslch_txt_image_like;
        protected TextView cslch_txt_like;
        protected LinearLayout cslch_ll_unlike;
        protected TextView cslch_txt_image_unlike;
        protected TextView cslch_txt_unlike;
        protected LinearLayout cslch_ll_comment;
        protected TextView cslch_txt_image_comment;
        protected TextView cslch_txt_comment;
        protected TextView cslch_txt_image_seen;
        protected TextView cslch_txt_seen;
        protected LinearLayout cslr_ll_forward;
        protected TextView cslr_txt_forward_from;
        protected ImageView csla_imv_state_audio;
        protected TextView csla_txt_audio_name;
        protected TextView csla_txt_audio_mime_type;
        protected TextView csla_txt_audio_size;
        protected ImageView chslr_imv_icon_replay;
        protected View chslr_v_vertical_line;
        protected ImageView chslr_imv_replay_pic;
        protected TextView chslr_txt_replay_from;
        protected TextView chslr_txt_replay_message;


        public ViewHolder(View view) {
            super(view);

            cslch_ll_parent = (LinearLayout) view.findViewById(R.id.cslch_ll_parent);
            cslr_ll_content_main = (LinearLayout) view.findViewById(R.id.cslr_ll_content_main);
            cslr_txt_message = (TextView) view.findViewById(R.id.messageText);
            cslr_txt_message.setTextSize(G.userTextSize);
            cslch_btn_item_menu = (Button) view.findViewById(R.id.cslch_btn_item_menu);
            cslch_ll_info = (LinearLayout) view.findViewById(R.id.cslch_ll_info);
            cslch_txt_time_date = (TextView) view.findViewById(R.id.cslch_txt_time_date);
            cslch_txt_time_clock = (TextView) view.findViewById(R.id.cslch_txt_time_clock);
            cslch_ll_like = (LinearLayout) view.findViewById(R.id.cslch_ll_like);
            cslch_txt_image_like = (TextView) view.findViewById(R.id.cslch_txt_image_like);
            cslch_txt_like = (TextView) view.findViewById(R.id.cslch_txt_like);
            cslch_ll_unlike = (LinearLayout) view.findViewById(R.id.cslch_ll_unlike);
            cslch_txt_image_unlike = (TextView) view.findViewById(R.id.cslch_txt_image_unlike);
            cslch_txt_unlike = (TextView) view.findViewById(R.id.cslch_txt_unlike);
            cslch_ll_comment = (LinearLayout) view.findViewById(R.id.cslch_ll_comment);
            cslch_txt_image_comment = (TextView) view.findViewById(R.id.cslch_txt_image_comment);
            cslch_txt_comment = (TextView) view.findViewById(R.id.cslch_txt_comment);
            cslch_txt_image_seen = (TextView) view.findViewById(R.id.cslch_txt_image_seen);
            cslch_txt_seen = (TextView) view.findViewById(R.id.cslch_txt_seen);
            cslr_ll_forward = (LinearLayout) view.findViewById(R.id.cslr_ll_forward);
            cslr_txt_forward_from = (TextView) view.findViewById(R.id.cslr_txt_forward_from);
            csla_imv_state_audio = (ImageView) view.findViewById(R.id.csla_imv_state_audio);
            csla_txt_audio_name = (TextView) view.findViewById(R.id.csla_txt_audio_name);
            csla_txt_audio_mime_type = (TextView) view.findViewById(R.id.csla_txt_audio_mime_type);
            csla_txt_audio_size = (TextView) view.findViewById(R.id.csla_txt_audio_size);

            chslr_imv_replay_pic = (ImageView) view.findViewById(R.id.chslr_imv_replay_pic);
            chslr_txt_replay_from = (TextView) view.findViewById(R.id.chslr_txt_replay_from);
            chslr_txt_replay_message = (TextView) view.findViewById(R.id.chslr_txt_replay_message);

            cslch_txt_image_like.setTypeface(G.fontawesome);
            cslch_txt_image_unlike.setTypeface(G.fontawesome);
            cslch_txt_image_comment.setTypeface(G.fontawesome);
            cslch_txt_image_seen.setTypeface(G.fontawesome);
            cslch_btn_item_menu.setTypeface(G.fontawesome);

            cslch_btn_item_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "item menu click");
                }
            });
            cslch_ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "like click");
                }
            });
            cslch_ll_unlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "Unclick");
                }
            });
        }
    }
}
