/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.realm;

import android.os.Handler;
import android.os.Looper;
import com.iGap.G;
import com.iGap.helper.HelperString;
import com.iGap.module.TimeUtils;
import com.iGap.module.enums.ChannelChatRole;
import com.iGap.module.enums.GroupChatRole;
import com.iGap.module.enums.RoomType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import java.util.List;

public class RealmRoom extends RealmObject {
    @PrimaryKey private long id;
    private String type;
    private String title;
    private String initials;
    private String color;
    private int unreadCount;
    private boolean readOnly;
    private RealmChatRoom chatRoom;
    private boolean mute;
    private RealmGroupRoom groupRoom;
    private RealmChannelRoom channelRoom;
    private RealmRoomMessage lastMessage;
    private RealmRoomDraft draft;
    private long updatedTime;
    private String sharedMediaCount = "";
    //TODO [Saeed Mozaffari] [2017-02-13 12:06 PM] - combine this two under fields in RealmAction
    private long actionStateUserId;
    private String actionState;
    private boolean isDeleted = false;
    /**
     * client need keepRoom info for show in forward message that forward
     * from a room that user don't have that room
     */
    private boolean keepRoom = false;
    private long lastScrollPositionMessageId;

    public RealmRoom() {

    }

    public RealmRoom(long id) {
        this.id = id;
    }

    public long getUpdatedTime() {
        if (getLastMessage() != null && getLastMessage().isValid()) {
            if (getLastMessage().getUpdateOrCreateTime() > updatedTime) {
                return getLastMessage().getUpdateOrCreateTime();
            }
        }
        return updatedTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isKeepRoom() {
        return keepRoom;
    }

    public void setKeepRoom(boolean keepRoom) {
        this.keepRoom = keepRoom;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getLastScrollPositionMessageId() {
        return lastScrollPositionMessageId;
    }

    public void setLastScrollPositionMessageId(long lastScrollPositionMessageId) {
        this.lastScrollPositionMessageId = lastScrollPositionMessageId;
    }


    public RealmRoomMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(RealmRoomMessage lastMessage) {
        if (lastMessage != null) {
            setUpdatedTime(lastMessage.getUpdateOrCreateTime());
        }
        this.lastMessage = lastMessage;
    }

    private RealmDraftFile draftFile;
    private RealmAvatar avatar;

    public long getOwnerId() {
        switch (ProtoGlobal.Room.Type.valueOf(type)) {
            case CHAT:
                return getChatRoom().getPeerId();
            default:
                return id;
        }
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     * hint : call this method in execute transaction
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom putOrUpdate(ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        putChatToClientCondition(realm, room);

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom == null) {
            realmRoom = realm.createObject(RealmRoom.class, room.getId());
            realmRoom.setUpdatedTime(TimeUtils.currentLocalTime());
        }

        realmRoom.isDeleted = false;

        realmRoom.setColor(room.getColor());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        //realmRoom.setMute(false); //TODO [Saeed Mozaffari] [2016-09-07 9:59 AM] - after get mute state from server unComment this code and set server value
        realmRoom.setActionState(null, 0);
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.put(realmRoom.getId(), room.getChannelRoomExtra().getAvatar(), true));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());
                realmRoom.getChannelRoom().setSeenId(room.getChannelRoomExtra().getSeenId());
                realmRoom.getChannelRoom().setPrivate(room.getChannelRoomExtra().hasPrivateExtra());
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(room.getChatRoomExtra()));
                /**
                 * update user info for detect current status(online,offline,...)
                 * and also update another info
                 */
                realmRoom.setAvatar(RealmRegisteredInfo.putOrUpdate(room.getChatRoomExtra().getPeer()).getLastAvatar());
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.convert(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.put(realmRoom.getId(), room.getGroupRoomExtra().getAvatar(), true));
                realmRoom.getGroupRoom().setInvite_token(room.getGroupRoomExtra().getPrivateExtra().getInviteToken());
                if (!room.getGroupRoomExtra().getPrivateExtra().getInviteLink().isEmpty()) {
                    realmRoom.getGroupRoom().setInvite_link(room.getGroupRoomExtra().getPrivateExtra().getInviteLink());
                }
                realmRoom.getGroupRoom().setUsername(room.getGroupRoomExtra().getPublicExtra().getUsername());
                realmRoom.getGroupRoom().setPrivate(room.getGroupRoomExtra().hasPrivateExtra());
                break;
        }
        realmRoom.setLastMessage(RealmRoomMessage.putOrUpdate(room.getLastMessage(), room.getId()));
        if (room.getLastMessage().getUpdateTime() == 0) {
            realmRoom.setUpdatedTime(room.getLastMessage().getCreateTime());
        } else {
            realmRoom.setUpdatedTime(room.getLastMessage().getUpdateTime());
        }

        RealmRoomDraft realmRoomDraft = realmRoom.getDraft();
        if (realmRoomDraft == null) {
            realmRoomDraft = realm.createObject(RealmRoomDraft.class);
        }
        realmRoomDraft.setMessage(room.getDraft().getMessage());
        realmRoomDraft.setReplyToMessageId(room.getDraft().getReplyTo());

        realmRoom.setDraft(realmRoomDraft);

        realm.close();

        return realmRoom;
    }


    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    public static void putChatToDatabase(final List<ProtoGlobal.Room> rooms, final boolean deleteBefore, final boolean cleanDeletedRoommessage) {

        /**
         * (( hint : i don't used from mRealm instance ,because i have an error
         * that realm is closed, and for avoid from that error i used from
         * new instance for this action ))
         */

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        if (deleteBefore) {
                            RealmResults<RealmRoom> list = realm.where(RealmRoom.class).findAll();
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setDeleted(true);
                            }
                        }

                        for (ProtoGlobal.Room room : rooms) {
                            RealmRoom.putOrUpdate(room);
                        }

                        if (cleanDeletedRoommessage) {
                            // delete messages and rooms that was deleted
                            RealmResults<RealmRoom> deletedRoomsList = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();
                            for (RealmRoom item : deletedRoomsList) {
                                /**
                                 * delete all message in deleted room
                                 */
                                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, item.getId()).findAll().deleteAllFromRealm();
                                item.deleteFromRealm();
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }


    private static void putChatToClientCondition(Realm realm, final ProtoGlobal.Room room) {
        if (realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, room.getId()).findFirst() == null) {
            realm.createObject(RealmClientCondition.class, room.getId());
        }
    }

    public static void convertAndSetDraft(final long roomId, final String message, final long replyToMessageId) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    RealmRoomDraft realmRoomDraft = realm.createObject(RealmRoomDraft.class);
                    realmRoomDraft.setMessage(message);
                    realmRoomDraft.setReplyToMessageId(replyToMessageId);
                    realmRoom.setDraft(realmRoomDraft);

                    if (G.onDraftMessage != null) {
                        G.onDraftMessage.onDraftMessage(roomId, message);
                    }
                }
            }
        });

        realm.close();
    }

    /**
     * create RealmRoom without info ,just have roomId and type
     * use this for detect that a room is a private channel
     * set deleted true and keep true for not showing in room list
     * and keep info for use in another subjects
     */
    public static void createEmptyRoom(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom == null) {
                    realmRoom = realm.createObject(RealmRoom.class, roomId);
                }
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setTitle("private channel");
                realmRoom.setDeleted(true);
                realmRoom.setKeepRoom(true);
            }
        });
        realm.close();
    }


    public static void needGetRoom(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null) {
            new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
        }
        realm.close();
    }

    public static boolean isCloudRoom(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == realmUserInfo.getUserId()) {
                return true;
            }
        }
        realm.close();
        return false;
    }

    /**
     * check updater author for detect that updater is another device for
     * this account and finally update unread count if another account
     * was saw message for this room
     *
     * @param roomId roomId for room that get update status from that
     * @param authorHash updater author hash
     */
    public static void clearUnreadCount(long roomId, String authorHash, ProtoGlobal.RoomMessageStatus messageStatus) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(RealmUserInfo.class).findFirst().isAuthorMe(authorHash) && messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                //  realmRoom.setUnreadCount(0);
            }
        }
        realm.close();
    }

    public static void updateRole(final ProtoGlobal.Room.Type type, long roomId, long memberId, final String role) {

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (memberId == realmUserInfo.getUserId()) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if (type == ProtoGlobal.Room.Type.GROUP) {
                        GroupChatRole mRole;
                        if (role.contains(GroupChatRole.ADMIN.toString())) {
                            mRole = GroupChatRole.ADMIN;
                        } else if (role.contains(GroupChatRole.MODERATOR.toString())) {
                            mRole = GroupChatRole.MODERATOR;
                        } else {
                            mRole = GroupChatRole.MEMBER;
                        }
                        realmRoom.getGroupRoom().setRole(mRole);
                    } else {
                        ChannelChatRole mRole;
                        if (role.contains(ChannelChatRole.ADMIN.toString())) {
                            mRole = ChannelChatRole.ADMIN;
                        } else if (role.contains(ChannelChatRole.MODERATOR.toString())) {
                            mRole = ChannelChatRole.MODERATOR;
                        } else {
                            mRole = ChannelChatRole.MEMBER;
                        }
                        realmRoom.getChannelRoom().setRole(mRole);
                    }
                }
            });
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProtoGlobal.Room.Type getType() {
        return (type != null) ? ProtoGlobal.Room.Type.valueOf(type) : null;
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        try {
            this.title = title;
        } catch (Exception e) {
            this.title = HelperString.getUtf8String(title);
        }


    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public RealmChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(RealmChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public RealmGroupRoom getGroupRoom() {
        return groupRoom;
    }

    public void setGroupRoom(RealmGroupRoom groupRoom) {
        this.groupRoom = groupRoom;
    }

    public RealmChannelRoom getChannelRoom() {
        return channelRoom;
    }

    public void setChannelRoom(RealmChannelRoom channelRoom) {
        this.channelRoom = channelRoom;
    }

    public RealmRoomDraft getDraft() {
        return draft;
    }

    public void setDraft(RealmRoomDraft draft) {
        this.draft = draft;
    }

    public RealmDraftFile getDraftFile() {
        return draftFile;
    }

    public void setDraftFile(RealmDraftFile draftFile) {
        this.draftFile = draftFile;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }

    public String getSharedMediaCount() {
        return sharedMediaCount;
    }

    public void setSharedMediaCount(String sharedMediaCount) {
        this.sharedMediaCount = sharedMediaCount;
    }

    public String getActionState() {
        return actionState;
    }

    public void setActionState(String actionState, long userId) {
        this.actionState = actionState;
        this.actionStateUserId = userId;
    }

    public long getActionStateUserId() {
        return actionStateUserId;
    }
}
