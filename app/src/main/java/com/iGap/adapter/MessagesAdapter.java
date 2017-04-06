package com.iGap.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import com.iGap.R;
import com.iGap.adapter.items.chat.AbstractMessage;
import com.iGap.adapter.items.chat.TimeItem;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnChatMessageRemove;
import com.iGap.interfaces.OnChatMessageSelectionChanged;
import com.iGap.module.StructMessageAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter<Item extends AbstractMessage> extends FastItemAdapter<Item> implements FastAdapter.OnLongClickListener<Item> {
    // contain sender id
    public static List<String> avatarsRequested = new ArrayList<>();
    public static List<String> usersInfoRequested = new ArrayList<>();

    private OnChatMessageSelectionChanged<Item> onChatMessageSelectionChanged;
    private IMessageItem iMessageItem;
    private OnChatMessageRemove onChatMessageRemove;
    private OnLongClickListener longClickListener = new OnLongClickListener<Item>() {
        @Override
        public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {

            if (item instanceof TimeItem) {
                if (item.isSelected()) v.performLongClick();
            } else {
                if (iMessageItem != null && !item.mMessage.senderID.equalsIgnoreCase("-1")) {

                    if (item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString()) || item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {

                        if (item.isSelected()) v.performLongClick();
                        return true;
                    }

                    if (onChatMessageSelectionChanged != null) {
                        onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
                    }
                }
            }

            return true;
        }
    };

    public int findPositionByMessageId(long messageId) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage != null && Long.parseLong(item.mMessage.messageID) == messageId) {
                return getAdapterPosition(item);
            }
        }
        return -1;
    }


    public MessagesAdapter(OnChatMessageSelectionChanged<Item> OnChatMessageSelectionChangedListener, final IMessageItem iMessageItemListener, final OnChatMessageRemove chatMessageRemoveListener) {
        onChatMessageSelectionChanged = OnChatMessageSelectionChangedListener;
        iMessageItem = iMessageItemListener;
        onChatMessageRemove = chatMessageRemoveListener;

        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);

        withSelectable(true);
        withMultiSelect(true);
        withSelectOnLongClick(true);
        withOnPreLongClickListener(this);
        withOnLongClickListener(longClickListener);
        withOnClickListener(new OnClickListener<Item>() {
            @Override
            public boolean onClick(View v, IAdapter<Item> adapter, Item item, int position) {
                if (getSelectedItems().size() == 0) {
                    if (iMessageItem != null && item.mMessage != null && item.mMessage.senderID != null && !item.mMessage.senderID.equalsIgnoreCase("-1")) {
                        if (item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            return true;
                        }
                        if (item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            iMessageItem.onFailedMessageClick(v, item.mMessage, position);
                        } else {
                            iMessageItem.onContainerClick(v, item.mMessage, position);
                        }
                    }
                } else {
                    if (!(item instanceof TimeItem)) {
                        if (!item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            v.performLongClick();
                        }
                    }
                }
                return false;
            }
        });
    }

    public List<StructMessageInfo> getFailedMessages() {
        List<StructMessageInfo> failedMessages = new ArrayList<>();
        for (Item item : getAdapterItems()) {
            if (item.mMessage != null && !item.mMessage.senderID.equalsIgnoreCase("-1") && item.mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                failedMessages.add(item.mMessage);
            }
        }
        return failedMessages;
    }


    public void updateChengedItem(ArrayList<String> list) {

        int count = list.size();

        for (int i = 0; i < count; i++) {
            String id = list.get(i);

            for (int j = getAdapterItemCount() - 1; j >= 0; j--) {
                try {
                    if (getItem(j).mMessage.messageID.equals(id)) {
                        notifyItemChanged(j);
                        break;
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }


    public void updateChatAvatar(long userId, RealmRegisteredInfo registeredInfo) {
        for (Item item : getAdapterItems()) {
            if (item.mMessage != null && !item.mMessage.isSenderMe() && item.mMessage.senderID.equalsIgnoreCase(Long.toString(userId))) {
                int pos = getAdapterItems().indexOf(item);
                item.mMessage.senderAvatar = StructMessageAttachment.convert(registeredInfo.getLastAvatar());
                item.mMessage.initials = registeredInfo.getInitials();
                item.mMessage.senderColor = registeredInfo.getColor();
                notifyItemChanged(pos);
            }
        }
    }

    /**
     * update message text
     *
     * @param messageId message id
     * @param updatedText new message text
     */
    public void updateMessageText(long messageId, String updatedText) {

        for (int i = getAdapterItemCount() - 1; i >= 0; i--) {
            Item item = getAdapterItem(i);

            if (item.mMessage != null) {
                if (item.mMessage.messageID != null) {
                    if (item.mMessage.messageID.equals(Long.toString(messageId))) {
                        item.mMessage.messageText = updatedText;
                        item.mMessage.isEdited = true;
                        set(i, item);
                        break;
                    }
                }
            }
        }
    }

    /**
     * update message vote
     *
     * @param forwardedMessageId when forward message from channel to another chats , make new messageId.
     * mainMessageId is new messageId that created and messageId is for message
     * that forwarded to another chats
     */
    public void updateVote(long roomId, long messageId, String vote, ProtoGlobal.RoomMessageReaction reaction, long forwardedMessageId) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage != null) {
                /**
                 * if not forwarded message update structure otherwise just notify position
                 * mainMessageId == 0 means that this message not forwarded
                 */
                if (forwardedMessageId == 0) {
                    if (Long.toString(messageInfo.mMessage.roomId).equals(Long.toString(roomId)) && messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                        int pos = items.indexOf(messageInfo);
                        if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                            messageInfo.mMessage.channelExtra.thumbsUp = vote;
                        } else if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
                            messageInfo.mMessage.channelExtra.thumbsDown = vote;
                        }
                        set(pos, messageInfo);
                        break;
                    }
                } else {
                    if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                        int pos = items.indexOf(messageInfo);
                        set(pos, messageInfo);
                        break;
                    }
                }
            }
        }
    }

    /**
     * update message state
     */
    public void updateMessageState(long messageId, String voteUp, String voteDown, String viewsLabel) {
        List<Item> items = getAdapterItems();
        for (Item messageInfo : items) {
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.forwardedFrom != null && messageInfo.mMessage.forwardedFrom.getMessageId() == messageId) {
                    int pos = items.indexOf(messageInfo);
                    set(pos, messageInfo);
                } else if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                    int pos = items.indexOf(messageInfo);
                    messageInfo.mMessage.channelExtra.thumbsUp = voteUp;
                    messageInfo.mMessage.channelExtra.thumbsDown = voteDown;
                    messageInfo.mMessage.channelExtra.viewsLabel = viewsLabel;
                    set(pos, messageInfo);
                    break;
                }
            }
        }
    }

    public void updateToken(long messageId, String token) {
        Item item = getItemByFileIdentity(messageId);
        if (item != null) {
            int pos = getAdapterItems().indexOf(item);
            item.mMessage.attachment.token = token;

            set(pos, item);
        }
    }

    /**
     * get item by its file hash
     * useful for finding item which tries to upload something
     *
     * @param messageId String
     * @return Item
     */
    public Item getItemByFileIdentity(long messageId) {
        for (Item item : getAdapterItems()) {
            if (item != null) {
                if (item.mMessage != null) {
                    if (item.mMessage.messageID.equalsIgnoreCase(Long.toString(messageId))) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public void removeMessage(long messageId) {

        for (int i = getAdapterItemCount() - 1; i >= 0; i--) {
            Item item = getAdapterItem(i);
            if (item.mMessage != null) {
                if (item.mMessage.messageID != null) {
                    if (item.mMessage.messageID.equals(Long.toString(messageId))) {
                        if (onChatMessageRemove != null) {
                            onChatMessageRemove.onPreChatMessageRemove(item.mMessage, i);
                        }
                        remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void removeMessage(int pos) {
        if (onChatMessageRemove != null) {
            AbstractMessage message = getAdapterItem(pos);
            onChatMessageRemove.onPreChatMessageRemove(message.mMessage, pos);
        }
        remove(pos);
    }

    /**
     * update message status
     *
     * @param messageId message id
     * @param status ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageStatus(long messageId, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();

        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.messageID != null) {
                    if (messageInfo.mMessage.messageID.equals(Long.toString(messageId))) {
                        messageInfo.mMessage.status = status.toString();
                        set(i, messageInfo);
                        break;
                    }
                }
            }
        }
    }

    /**
     * update message id and status
     *
     * @param messageId new message id
     * @param identity old manually defined as identity id
     * @param status ProtoGlobal.RoomMessageStatus
     */
    public void updateMessageIdAndStatus(long messageId, String identity, ProtoGlobal.RoomMessageStatus status) {
        List<Item> items = getAdapterItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.messageID != null) {
                    if (messageInfo.mMessage.messageID.equals(identity)) {
                        messageInfo.mMessage.status = status.toString();
                        messageInfo.mMessage.messageID = Long.toString(messageId);
                        set(i, messageInfo);
                        break;
                    }
                }
            }
        }
    }

    /**
     * update video message time and name after that compressed file
     *
     * @param messageId for find message in adapter
     * @param fileDuration new duration for set in item
     * @param fileSize new size for set in item
     */
    public void updateVideoInfo(long messageId, long fileDuration, long fileSize) {
        List<Item> items = getAdapterItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            Item messageInfo = items.get(i);
            if (messageInfo.mMessage != null) {
                if (messageInfo.mMessage.messageID != null) {
                    if (Long.parseLong(messageInfo.mMessage.messageID) == messageId) {
                        messageInfo.mMessage.attachment.duration = fileDuration;
                        messageInfo.mMessage.attachment.size = fileSize;
                        messageInfo.mMessage.attachment.compressing = "";
                        set(i, messageInfo);
                        break;
                    }
                }
            }
        }
    }


    @Override
    public void notifyAdapterItemRemoved(int position) {
        super.notifyAdapterItemRemoved(position);

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    @Override
    public void deselect() {
        super.deselect();

        if (onChatMessageSelectionChanged != null) {
            onChatMessageSelectionChanged.onChatMessageSelectionChanged(getSelectedItems().size(), getSelectedItems());
        }
    }

    private void makeSelected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(v.getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
    }

    private void makeDeselected(View v) {
        //noinspection RedundantCast
        ((FrameLayout) v).setForeground(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position) {
        if (!item.isSelected()) {
            makeSelected(v);
        } else {
            makeDeselected(v);
        }
        return false;
    }
}
