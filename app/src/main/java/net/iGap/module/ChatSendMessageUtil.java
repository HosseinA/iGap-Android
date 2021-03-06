/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnChatSendMessageResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelSendMessage;
import net.iGap.request.RequestChatSendMessage;
import net.iGap.request.RequestGroupSendMessage;

import static net.iGap.proto.ProtoGlobal.RoomMessageType.STICKER;

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class ChatSendMessageUtil implements OnChatSendMessageResponse {
    private RequestChatSendMessage requestChatSendMessage;
    private RequestGroupSendMessage requestGroupSendMessage;
    private RequestChannelSendMessage requestChannelSendMessage;

    private ProtoGlobal.Room.Type roomType;
    private OnChatSendMessageResponse onChatSendMessageResponseChat;
    private OnChatSendMessageResponse onChatSendMessageResponseRoom;

    public ChatSendMessageUtil newBuilder(ProtoGlobal.Room.Type roomType, ProtoGlobal.RoomMessageType messageType, long roomId) {
        this.roomType = roomType;

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage = new RequestChatSendMessage().newBuilder(messageType, roomId);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage = new RequestGroupSendMessage().newBuilder(messageType, roomId);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage = new RequestChannelSendMessage().newBuilder(messageType, roomId);
        }
        return this;
    }

    public ChatSendMessageUtil message(String value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.message(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.message(value);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.message(value);
        }
        return this;
    }

    public ChatSendMessageUtil additional(RealmAdditional realmAdditional) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.additionalData(realmAdditional);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.additionalData(realmAdditional);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.additionalData(realmAdditional);
        }
        return this;
    }

    public ChatSendMessageUtil attachment(String value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.attachment(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.attachment(value);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.attachment(value);
        }
        return this;
    }

    public ChatSendMessageUtil build(ProtoGlobal.Room.Type roomType, long roomId, RealmRoomMessage message) {
        ChatSendMessageUtil builder = newBuilder(roomType, message.getMessageType(), roomId);
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            builder.message(message.getMessage());
        }
        if (message.getAttachment() != null && message.getAttachment().getToken() != null && !message.getAttachment().getToken().isEmpty()) {
            builder.attachment(message.getAttachment().getToken());
        }
        if (message.getRoomMessageContact() != null) {
            builder.contact(message.getRoomMessageContact().getFirstName(), message.getRoomMessageContact().getLastName(), message.getRoomMessageContact().getPhones().get(0).getString());
        }
        if (message.getLocation() != null) {
            builder.location(message.getLocation().getLocationLat(), message.getLocation().getLocationLong());
        }

        if (message.getForwardMessage() != null) {
            builder.forwardMessage(message.getForwardMessage().getRoomId(), message.getForwardMessage().getMessageId());
        }
        if (message.getReplyTo() != null) {
            builder.replyMessage(message.getReplyTo().getMessageId());
        }
        if (message.getRealmAdditional() != null) {
            builder.additional(message.getRealmAdditional());
        }

        builder.sendMessage(Long.toString(message.getMessageId()));
        return this;
    }

    public ChatSendMessageUtil buildForward(ProtoGlobal.Room.Type roomType, long roomId, RealmRoomMessage message, long forwardRoomId, long forwardMessageId) {
        ChatSendMessageUtil builder = newBuilder(roomType, message.getMessageType(), roomId);
        if (message.getMessage() != null && !message.getMessage().isEmpty()) {
            builder.message(message.getMessage());
        }
        if (message.getAttachment() != null && message.getAttachment().getToken() != null && !message.getAttachment().getToken().isEmpty()) {
            builder.attachment(message.getAttachment().getToken());
        }
        if (message.getRoomMessageContact() != null) {
            builder.contact(message.getRoomMessageContact().getFirstName(), message.getRoomMessageContact().getLastName(), message.getRoomMessageContact().getPhones().get(0).getString());
        }
        if (message.getLocation() != null) {
            builder.location(message.getLocation().getLocationLat(), message.getLocation().getLocationLong());
        }

        if (message.getForwardMessage() != null) {
            builder.forwardMessage(forwardRoomId, forwardMessageId);
        }
        if (message.getReplyTo() != null) {
            builder.replyMessage(message.getReplyTo().getMessageId());
        }

        builder.sendMessage(Long.toString(message.getMessageId()));
        return this;
    }

    public ChatSendMessageUtil contact(ProtoGlobal.RoomMessageContact value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.contact(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.contact(value);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.contact(value);
        }
        return this;
    }

    public ChatSendMessageUtil contact(String firstName, String lastName, String phoneNumber) {
        ProtoGlobal.RoomMessageContact.Builder value = ProtoGlobal.RoomMessageContact.newBuilder();
        value.setFirstName(firstName);
        value.setLastName(lastName);
        //value.addEmail();
        //value.setNickname();
        value.addPhone(phoneNumber);

        ProtoGlobal.RoomMessageContact built = value.build();

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.contact(built);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.contact(built);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.contact(built);
        }
        return this;
    }

    public ChatSendMessageUtil location(ProtoGlobal.RoomMessageLocation value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.location(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.location(value);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.location(value);
        }
        return this;
    }

    public ChatSendMessageUtil replyMessage(long messageId) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.replyMessage(messageId);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.replyMessage(messageId);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.replyMessage(messageId);
        }
        return this;
    }

    public ChatSendMessageUtil location(double lat, double lon) {
        ProtoGlobal.RoomMessageLocation.Builder location = ProtoGlobal.RoomMessageLocation.newBuilder();
        location.setLat(lat);
        location.setLon(lon);

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.location(location.build());
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.location(location.build());
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.location(location.build());
        }
        return this;
    }

    public ChatSendMessageUtil forwardMessage(long roomId, long messageId) {

        ProtoGlobal.RoomMessageForwardFrom.Builder forward = ProtoGlobal.RoomMessageForwardFrom.newBuilder();
        forward.setRoomId(roomId);
        forward.setMessageId(messageId);

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.forwardMessage(forward.build());
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.forwardMessage(forward.build());
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.forwardMessage(forward.build());
        }

        return this;
    }

    public void setOnChatSendMessageResponseChatPage(OnChatSendMessageResponse response) {
        this.onChatSendMessageResponseChat = response;
    }

    public void setOnChatSendMessageResponseRoomList(OnChatSendMessageResponse response) {
        this.onChatSendMessageResponseRoom = response;
    }

    public void sendMessage(String fakeMessageIdAsIdentity) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.sendMessage(fakeMessageIdAsIdentity);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.sendMessage(fakeMessageIdAsIdentity);
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            requestChannelSendMessage.sendMessage(fakeMessageIdAsIdentity);
        }

        if (!G.userLogin) {
            makeFailed(Long.parseLong(fakeMessageIdAsIdentity));
        }
    }

    /**
     * change message status from sending to failed
     *
     * @param fakeMessageId messageId that create when created this message
     */
    private void makeFailed(final long fakeMessageId) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    RealmRoomMessage.setStatusFailedInChat(realm1, fakeMessageId);
                });
            });
        }).start();
    }

    @Override
    public void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        if (onChatSendMessageResponseChat != null) {
            onChatSendMessageResponseChat.onMessageUpdate(roomId, messageId, status, identity, roomMessage);
        }

        if (onChatSendMessageResponseRoom != null) {
            onChatSendMessageResponseRoom.onMessageUpdate(roomId, messageId, status, identity, roomMessage);
        }
    }

    @Override
    public void onMessageReceive(long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (onChatSendMessageResponseChat != null) {
            onChatSendMessageResponseChat.onMessageReceive(roomId, message, messageType, roomMessage, roomType);
        }

        if (onChatSendMessageResponseRoom != null) {
            onChatSendMessageResponseRoom.onMessageReceive(roomId, message, messageType, roomMessage, roomType);
        }

        if (roomMessage.getMessageType() == STICKER && roomMessage.getAdditionalData() != null && roomMessage.getAdditionalType() == AdditionalType.GIFT_STICKER) {
            StructIGSticker sticker = new Gson().fromJson(roomMessage.getAdditionalData(), StructIGSticker.class);
            EventManager.getInstance().postEvent(EventManager.STICKER_CHANGED, sticker.getGroupId());
        } else if (roomMessage.getForwardFrom() != null && roomMessage.getForwardFrom().getMessageType() == STICKER && roomMessage.getForwardFrom().getAdditionalData() != null && roomMessage.getForwardFrom().getAdditionalType() == AdditionalType.GIFT_STICKER) {
            StructIGSticker sticker = new Gson().fromJson(roomMessage.getForwardFrom().getAdditionalData(), StructIGSticker.class);
            EventManager.getInstance().postEvent(EventManager.STICKER_CHANGED, sticker.getGroupId());
        }

    }

    @Override
    public void onMessageFailed(long roomId, long roomMessageId) {
        if (onChatSendMessageResponseChat != null) {
            onChatSendMessageResponseChat.onMessageFailed(roomId, roomMessageId);
        }

        if (onChatSendMessageResponseRoom != null) {
            onChatSendMessageResponseRoom.onMessageFailed(roomId, roomMessageId);
        }
    }
}
