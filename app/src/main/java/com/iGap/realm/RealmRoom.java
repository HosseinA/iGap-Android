package com.iGap.realm;

import com.iGap.G;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.RoomType;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoom extends RealmObject {
    @PrimaryKey
    private long id;
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
    private String actionState;

    public long getUpdatedTime() {
        if (getLastMessage() != null && getLastMessage().isValid()) {
            if (getLastMessage().getUpdateOrCreateTime() > updatedTime) {
                return getLastMessage().getUpdateOrCreateTime();
            }
        }
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public RealmRoomMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(RealmRoomMessage lastMessage) {
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
        putChatToClientCondition(room);

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom == null) {
            realmRoom = realm.createObject(RealmRoom.class, room.getId());
            realmRoom.setUpdatedTime(TimeUtils.currentLocalTime());
        }

        realmRoom.setColor(room.getColor());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        realmRoom.setMute(false); //TODO [Saeed Mozaffari] [2016-09-07 9:59 AM] - agar mute ro az server gereftim be jaye false sabt mikonim
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.put(realmRoom.getId(), room.getChannelRoomExtra().getAvatar(), true));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());

                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(room.getChatRoomExtra()));
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, room.getChatRoomExtra().getPeer().getId()).findFirst();
                realmRoom.setAvatar(realmRegisteredInfo != null ? realmRegisteredInfo.getLastAvatar() : null);
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.convert(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.put(realmRoom.getId(), room.getGroupRoomExtra().getAvatar(), true));
                realmRoom.getGroupRoom().setInvite_token(room.getGroupRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getGroupRoom().setInvite_link(room.getGroupRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getGroupRoom().setUsername(room.getGroupRoomExtra().getPublicExtra().getUsername());

                break;
        }
        //realmRoom.setLastMessage(RealmRoomMessage.putOrUpdate(room.getLastMessage(), room.getId()));
        realmRoom.setLastMessage(RealmRoomMessage.putOrUpdate(room.getLastMessage(), room.getId()));

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


    private static void putChatToClientCondition(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, room.getId()).findFirst() == null) {
            realm.createObject(RealmClientCondition.class, room.getId());
        }
        realm.close();
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
        this.title = title;
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

    public void setActionState(String actionState) {
        this.actionState = actionState;
    }
}
