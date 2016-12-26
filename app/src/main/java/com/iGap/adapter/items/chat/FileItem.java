package com.iGap.adapter.items.chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

import io.github.meness.emoji.EmojiTextView;
import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class FileItem extends AbstractMessage<FileItem, FileItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public FileItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutFile;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_file;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        //ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.thumbnail);
        //holder.thumbnail.setImageResource(R.drawable.file_icon);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.cslf_txt_file_name.setText(mMessage.forwardedFrom.getAttachment().getName());
                holder.cslf_txt_file_size.setText(
                        AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
            }

            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                holder.cslf_txt_file_name.setText(mMessage.attachment.name);
                holder.cslf_txt_file_size.setText(
                        AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
            }

            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.valueOf(mMessage.messageID)).findFirst();
        if (roomMessage != null) {
            //AppUtils.rightFileThumbnailIcon(holder.thumbnail, mMessage.messageType, roomMessage.getAttachment());
            //holder.thumbnail.setImageResource(R.drawable.file_icon);
            holder.thumbnail.setVisibility(View.VISIBLE);
            if (roomMessage.getForwardMessage() != null) {
                if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                    holder.thumbnail.setImageResource(R.drawable.pdf_icon);
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".txt")) {
                    holder.thumbnail.setImageResource(R.drawable.txt_icon);
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".exe")) {
                    holder.thumbnail.setImageResource(R.drawable.exe_icon);
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".docs")) {
                    holder.thumbnail.setImageResource(R.drawable.docx_icon);
                } else {
                    holder.thumbnail.setImageResource(R.drawable.file_icon);
                }
            } else {
                if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                    holder.thumbnail.setImageResource(R.drawable.pdf_icon);
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                    holder.thumbnail.setImageResource(R.drawable.txt_icon);
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".exe")) {
                    holder.thumbnail.setImageResource(R.drawable.exe_icon);
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".docs")) {
                    holder.thumbnail.setImageResource(R.drawable.docx_icon);
                } else {
                    holder.thumbnail.setImageResource(R.drawable.file_icon);
                }
            }
        }
        realm.close();
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        holder.cslf_txt_file_name.setTextColor(Color.WHITE);
        holder.cslf_txt_file_size.setTextColor(Color.WHITE);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.cslf_txt_file_name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        holder.cslf_txt_file_size.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_size;
        protected EmojiTextView messageText;
        protected ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            cslf_txt_file_name = (TextView) view.findViewById(R.id.songArtist);
            cslf_txt_file_size = (TextView) view.findViewById(R.id.fileSize);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
