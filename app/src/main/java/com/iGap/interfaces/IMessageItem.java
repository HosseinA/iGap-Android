package com.iGap.interfaces;

import android.view.View;

import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;

public interface IMessageItem {
    /**
     * open means open for files and play for videos
     */
    void onOpenClick(View view, StructMessageInfo message, int pos);

    void onContainerClick(View view, StructMessageInfo message, int pos);

    void onSenderAvatarClick(View view, StructMessageInfo message, int pos);

    void onUploadCancel(View view, StructMessageInfo message, int pos);

    void onDownloadCancel(View view, StructMessageInfo message, int pos);

    void onDownloadStart(View view, StructMessageInfo message, int pos);

    void onFailedMessageClick(View view, StructMessageInfo message, int pos);

    void onReplyClick(RealmRoomMessage replyMessage);

    void onVoteClick(StructMessageInfo message, int vote, ProtoGlobal.RoomMessageReaction reaction);
}
