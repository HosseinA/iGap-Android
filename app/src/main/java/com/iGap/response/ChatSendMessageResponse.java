package com.iGap.response;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import com.iGap.G;
import com.iGap.module.SHP_SETTING;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageContact;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmRoomMessageLocation;
import com.iGap.realm.RealmRoomMessageLog;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private ArrayList<Long> messageId = new ArrayList<>();

    public ChatSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.identity = identity;
        this.message = protoClass;
    }

    @Override public void handler() {
        Realm realm = Realm.getDefaultInstance();
        final ProtoChatSendMessage.ChatSendMessageResponse.Builder chatSendMessageResponse =
            (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;

        final ProtoGlobal.RoomMessage roomMessage = chatSendMessageResponse.getRoomMessage();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                // set info for clientCondition
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
                    .equalTo(RealmClientConditionFields.ROOM_ID,
                        chatSendMessageResponse.getRoomId())
                    .findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                    realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                }

                Log.i("CLI_XX", "Chat getRoomId : " + chatSendMessageResponse.getRoomId());
                Log.i("CLI_XX", "Chat getMessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI_XX", "Chat getStatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI_XX", "Chat getMessageId : " + roomMessage.getMessageId());
                Log.i("CLI_XX", "Chat getMessage : " + roomMessage.getMessage());
                Log.i("CLI_XX", "***");
                Log.i("CLI_XX", "**********************************************");
                Log.i("CLI_XX", "***");

                Log.i("CLI", "send message MessageVersion : " + roomMessage.getMessageVersion());
                Log.i("CLI", "send message StatusVersion : " + roomMessage.getStatusVersion());
                Log.i("CLI", "send message MessageId : " + roomMessage.getMessageId());

                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class)
                    .equalTo(RealmRoomFields.ID, chatSendMessageResponse.getRoomId())
                    .findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(chatSendMessageResponse.getRoomId());
                } else {
                    // update last message sent/received in room table
                    if (room.getLastMessageTime()
                        < roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS) {
                        room.setLastMessageId(roomMessage.getMessageId());
                        room.setLastMessageTime(roomMessage.getUpdateTime());

                        realm.copyToRealmOrUpdate(room);
                    }
                }

                // because user may have more than one device, his another device should not be
                // recipient
                // but sender. so I check current userId with room message user id, and if not
                // equals
                // and response is null, so we sure recipient is another user

                if (chatSendMessageResponse.getResponse()
                    .getId()
                    .isEmpty()) {//TODO [Saeed Mozaffari] [2016-10-06 12:35 PM] - check this
                    // comment Alireza added and removed with saeed ==> //userId != roomMessage
                    // .getUserId() &&
                    // i'm the recipient

                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId())
                        .findFirst();

                    /*
                     *  if this is new message and not exist this messageId createObject from
                     *  RealmChatHistory
                     *  else this message is repetitious find fetch RealmChatHistory with
                     *  messageId and update it
                     *  client check this because maybe receive repetitious message from server
                     */
                    if (realmRoomMessage == null) {
                        realmRoomMessage = realm.createObject(RealmRoomMessage.class);
                        realmRoomMessage.setMessageId(roomMessage.getMessageId());
                        realmRoomMessage.setRoomId(chatSendMessageResponse.getRoomId());
                    } else {

                        /*
                        * message exist in chat room so don't calling onMessageReceive callback ,
                        * just
                        * update message . this process for this is that maybe receive
                        * repetitious message
                        * from server so client should handle this subject
                        */
                        messageId.add(roomMessage.getMessageId());
                    }

                    fillRoomMessage(realmRoomMessage, roomMessage);
                    if (roomMessage.getForwardFrom() != null) { // forward message

                        RealmRoomMessage forward = realm.createObject(RealmRoomMessage.class);
                        forward.setMessageId(System.nanoTime());

                        realmRoomMessage.setForwardMessage(fillRoomMessage(forward, roomMessage));
                    } else if (roomMessage.getReplyTo() != null) { // reply message

                        RealmRoomMessage reply = realm.createObject(RealmRoomMessage.class);
                        reply.setMessageId(System.currentTimeMillis());

                        realmRoomMessage.setReplyTo(fillRoomMessage(reply, roomMessage));
                    }

                    if (roomMessage.getUserId()
                        != userId) { // show notification if this message isn't for another account

                        SharedPreferences sharedPreferences =
                            G.context.getSharedPreferences(SHP_SETTING.FILE_NAME,
                                Context.MODE_PRIVATE);
                        int checkAlert =
                            sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                        if (checkAlert == 1) {
                            G.helperNotificationAndBadge.updateNotificationAndBadge(true, 0);
                        }
                    }
                } else {
                    // i'm the sender
                    // update message fields into database
                    RealmResults<RealmRoomMessage> realmRoomMessages =
                        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID,
                                chatSendMessageResponse.getRoomId())
                            .findAll();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        // find the message using identity and update it
                        if (realmRoomMessage != null
                            && realmRoomMessage.getMessageId() == Long.parseLong(identity)) {
                            realmRoomMessage.setMessageId(roomMessage.getMessageId());

                            realmRoomMessage = fillRoomMessage(realmRoomMessage, roomMessage);

                            if (roomMessage.getForwardFrom() != null) { // forward message

                                RealmRoomMessage forwardMessage =
                                    realmRoomMessage.getForwardMessage();
                                // forwardMessage shouldn't be null but client check it for insuring
                                if (forwardMessage == null) {
                                    forwardMessage = realm.createObject(RealmRoomMessage.class);
                                    forwardMessage.setMessageId(System.nanoTime());
                                }
                                realmRoomMessage.setForwardMessage(
                                    fillRoomMessage(forwardMessage, roomMessage));
                            } else if (roomMessage.getReplyTo() != null) { // reply message

                                RealmRoomMessage replyMessage =
                                    realmRoomMessage.getForwardMessage();
                                // replyMessage shouldn't be null but client check it for insuring
                                if (replyMessage == null) {
                                    replyMessage = realm.createObject(RealmRoomMessage.class);
                                    replyMessage.setMessageId(System.nanoTime());
                                }
                                realmRoomMessage.setReplyTo(
                                    fillRoomMessage(replyMessage, roomMessage));
                            }

                            realm.copyToRealmOrUpdate(realmRoomMessage);
                            break;
                        }
                    }
                }
            }
        });

        if (chatSendMessageResponse.getResponse()
            .getId()
            .isEmpty()) {//TODO [Saeed Mozaffari] [2016-10-06 12:35 PM] - check this comment
            // Alireza added and removed with saeed ==> //userId != roomMessage.getUserId() &&
            // invoke following callback when i'm not the sender, because I already done
            // everything after sending message
            if (!messageId.contains(roomMessage.getMessageId())) {
                if (realm.where(RealmRoom.class)
                    .equalTo(RealmRoomFields.ID, chatSendMessageResponse.getRoomId())
                    .findFirst() != null) {
                    G.chatSendMessageUtil.onMessageReceive(chatSendMessageResponse.getRoomId(),
                        roomMessage.getMessage(), roomMessage.getMessageType().toString(),
                        roomMessage, ProtoGlobal.Room.Type.CHAT);
                }
            } else {
                messageId.remove(messageId.indexOf(roomMessage.getMessageId()));
            }
        } else {
            // invoke following callback when I'm the sender and the message has updated
            G.chatSendMessageUtil.onMessageUpdate(chatSendMessageResponse.getRoomId(),
                roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        realm.close();
    }

    private RealmRoomMessage fillRoomMessage(RealmRoomMessage realmRoomMessage,
        ProtoGlobal.RoomMessage roomMessage) {
        realmRoomMessage.setMessageVersion(roomMessage.getMessageVersion());
        realmRoomMessage.setStatus(roomMessage.getStatus().toString());
        realmRoomMessage.setMessageType(roomMessage.getMessageType().toString());
        realmRoomMessage.setMessage(roomMessage.getMessage());

        if (roomMessage.getMessageType() != ProtoGlobal.RoomMessageType.TEXT
            && roomMessage.getMessageType() != ProtoGlobal.RoomMessageType.LOCATION
            && roomMessage.getMessageType() != ProtoGlobal.RoomMessageType.LOG
            && roomMessage.getMessageType() != ProtoGlobal.RoomMessageType.CONTACT) {
            realmRoomMessage.setAttachment(roomMessage.getMessageId(), roomMessage.getAttachment());
        }
        realmRoomMessage.setUserId(roomMessage.getUserId());
        if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.LOCATION) {
            realmRoomMessage.setLocation(RealmRoomMessageLocation.build(roomMessage.getLocation()));
        } else if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.LOG) {
            realmRoomMessage.setLog(RealmRoomMessageLog.build(roomMessage.getLog()));
        } else if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.CONTACT) {
            realmRoomMessage.setRoomMessageContact(
                RealmRoomMessageContact.build(roomMessage.getContact()));
        }
        realmRoomMessage.setEdited(roomMessage.getEdited());
        realmRoomMessage.setUpdateTime(roomMessage.getUpdateTime());

        return realmRoomMessage;
    }

    @Override public void error() {
        super.error();
    }

    @Override public void timeOut() {
        super.timeOut();
        // message failed
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage message = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity))
                    .findFirst();
                if (message != null) {
                    message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                    G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message,
                        ProtoGlobal.Room.Type.CHAT);
                }
            }
        });
    }
}
