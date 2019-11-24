/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import android.text.format.DateUtils;

import net.iGap.G;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnClientGetRoomMessage;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.proto.ProtoClientGetPromote;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientGetRoomMessage;
import net.iGap.request.RequestGroupUpdateDraft;

import java.util.HashSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static net.iGap.G.userId;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

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
    private RealmRoomMessage firstUnreadMessage;
    private RealmRoomDraft draft;
    private RealmDraftFile draftFile;
    private RealmAvatar avatar;
    private long updatedTime;
    private String sharedMediaCount = "";
    //if it was needed in the future we can combine this two under fields in RealmAction (actionStateUserId and actionState).
    private long actionStateUserId;
    private String actionState;
    private boolean isDeleted = false;
    private boolean isPinned;
    private long pinId;
    private long pinMessageId;
    private long pinMessageIdDeleted;
    private int priority;
    private boolean isFromPromote;
    private long promoteId;

    public long getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(long promoteId) {
        this.promoteId = promoteId;
    }

    public boolean isFromPromote() {
        return isFromPromote;
    }

    public void setFromPromote(boolean fromPromote) {
        isFromPromote = fromPromote;
    }

    /**
     * client need keepRoom info for show in forward message that forward
     * from a room that user don't have that room
     */
    private boolean keepRoom = false;
    private long lastScrollPositionMessageId;
    private int lastScrollPositionOffset;

    public RealmRoom() {

    }

    public RealmRoom(long id) {
        this.id = id;
    }

    public static RealmRoom getRealmRoom(Realm realm, long roomId) {
        return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    }

    public static void putOrUpdate(final ProtoGlobal.Room room) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    putOrUpdate(room, realm);
                }
            });
        }
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     * hint : call this method in execute transaction
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom putOrUpdate(ProtoGlobal.Room room, Realm realm) {

        RealmClientCondition.putOrUpdateIncomplete(realm, room.getId());

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom == null) {
            realmRoom = realm.createObject(RealmRoom.class, room.getId());
        }


        realmRoom.isDeleted = false;
        realmRoom.keepRoom = false;

        realmRoom.setColor(room.getColor());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        realmRoom.setMute(room.getRoomMute());
        realmRoom.setPriority(room.getPriority());
        realmRoom.setPinId(room.getPinId());

        if (room.getPinId() > 0) {
            realmRoom.setPinned(true);
        } else {
            realmRoom.setPinned(false);
        }


        if (room.getPinnedMessage() != null) {
            realmRoom.setPinMessageId(room.getPinnedMessage().getMessageId());
        }

        realmRoom.setActionState(null, 0);
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getChannelRoomExtra().getAvatar()));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());
                realmRoom.getChannelRoom().setSeenId(room.getChannelRoomExtra().getSeenId());
                realmRoom.getChannelRoom().setPrivate(room.getChannelRoomExtra().hasPrivateExtra());
                realmRoom.getChannelRoom().setVerified(room.getChannelRoomExtra().getVerified());
                realmRoom.getChannelRoom().setReactionStatus(room.getChannelRoomExtra().getReactionStatus());
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(realm, room.getChatRoomExtra()));
                /**
                 * update user info for detect current status(online,offline,...)
                 * and also update another info
                 */

                RealmRegisteredInfo.putOrUpdate(realm, room.getChatRoomExtra().getPeer());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, room.getChatRoomExtra().getPeer().getId(), room.getChatRoomExtra().getPeer().getAvatar()));
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.putOrUpdate(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getGroupRoomExtra().getAvatar()));
                realmRoom.getGroupRoom().setInvite_token(room.getGroupRoomExtra().getPrivateExtra().getInviteToken());
                if (!room.getGroupRoomExtra().getPrivateExtra().getInviteLink().isEmpty()) {
                    realmRoom.getGroupRoom().setInvite_link(room.getGroupRoomExtra().getPrivateExtra().getInviteLink());
                }
                realmRoom.getGroupRoom().setUsername(room.getGroupRoomExtra().getPublicExtra().getUsername());
                realmRoom.getGroupRoom().setPrivate(room.getGroupRoomExtra().hasPrivateExtra());
                break;
        }

        /**
         * set setFirstUnreadMessage
         */
        if (room.hasFirstUnreadMessage()) {
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm, room.getId(), room.getFirstUnreadMessage(), new StructMessageOption());
            realmRoomMessage.setFutureMessageId(room.getFirstUnreadMessage().getMessageId());
            realmRoomMessage.setPreviousMessageId(room.getFirstUnreadMessage().getMessageId());
            realmRoom.setFirstUnreadMessage(realmRoomMessage);
        }

        if (room.hasLastMessage()) {
            /**
             * if this message not exist set gap otherwise don't change in gap state
             */
            boolean setGap = false;
            if (!RealmRoomMessage.existMessageInRoom(room.getLastMessage().getMessageId(), room.getId())) {
                setGap = true;
            }
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm, room.getId(), room.getLastMessage(), new StructMessageOption());
            if (setGap) {
                realmRoomMessage.setPreviousMessageId(room.getLastMessage().getMessageId());
                realmRoomMessage.setFutureMessageId(room.getLastMessage().getMessageId());
            }
            realmRoom.setLastMessage(realmRoomMessage);
            if (room.getLastMessage().getUpdateTime() == 0) {
                realmRoom.setUpdatedTime(room.getLastMessage().getCreateTime() * (DateUtils.SECOND_IN_MILLIS));
            } else {
                realmRoom.setUpdatedTime(room.getLastMessage().getUpdateTime() * (DateUtils.SECOND_IN_MILLIS));
            }
        }

        realmRoom.setDraft(RealmRoomDraft.putOrUpdate(realm, realmRoom.getDraft(), room.getDraft().getMessage(), room.getDraft().getReplyTo(), room.getDraft().getDraftTime()));

        return realmRoom;
    }

    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    public static void putChatToDatabase(final List<ProtoGlobal.Room> rooms) {

        /**
         * (( hint : i don't used from mRealm instance ,because i have an error
         * that realm is closed, and for avoid from that error i used from
         * new instance for this action ))
         */
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HashSet<Long> allUnPined = new HashSet<>();
                    HashSet<Long> allPinned = new HashSet<>();
                    long timeMin = Long.MAX_VALUE;
                    long timeMax = Long.MIN_VALUE;
                    long pinIdMin = Long.MAX_VALUE;
                    long pinIdMax = Long.MIN_VALUE;

                    for (int i = 0; i < rooms.size(); i++) {
                        RealmRoom.putOrUpdate(rooms.get(i), realm);
                        if (rooms.get(i).getPinId() == 0L) {
                            allUnPined.add(rooms.get(i).getId());
                            if (rooms.get(i).getLastMessage().getCreateTime() != 0 && timeMin > rooms.get(i).getLastMessage().getCreateTime() * 1000L) {
                                timeMin = rooms.get(i).getLastMessage().getCreateTime() * 1000L;
                            }

                            if (rooms.get(i).getLastMessage().getUpdateTime() != 0 && timeMin > rooms.get(i).getLastMessage().getUpdateTime() * 1000L) {
                                timeMin = rooms.get(i).getLastMessage().getUpdateTime() * 1000L;
                            }

                            if (rooms.get(i).getLastMessage().getCreateTime() != 0 && timeMax < rooms.get(i).getLastMessage().getCreateTime() * 1000L) {
                                timeMax = rooms.get(i).getLastMessage().getCreateTime() * 1000L;
                            }

                            if (rooms.get(i).getLastMessage().getUpdateTime() != 0 && timeMax < rooms.get(i).getLastMessage().getUpdateTime() * 1000L) {
                                timeMax = rooms.get(i).getLastMessage().getUpdateTime() * 1000L;
                            }
                        } else {
                            allPinned.add(rooms.get(i).getId());
                            if (pinIdMin > rooms.get(i).getPinId()) {
                                pinIdMin = rooms.get(i).getPinId();
                            }

                            if (pinIdMax < rooms.get(i).getPinId()) {
                                pinIdMax = rooms.get(i).getPinId();
                            }
                        }
                    }

                    RealmResults<RealmRoom> deletedRoomsListUnPined = realm.where(RealmRoom.class)
                            .greaterThanOrEqualTo(RealmRoomFields.LAST_MESSAGE.UPDATE_TIME, timeMin)
                            .lessThanOrEqualTo(RealmRoomFields.LAST_MESSAGE.UPDATE_TIME, timeMax)
                            .equalTo(RealmRoomFields.IS_PINNED, false)
                            .equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();

                    RealmResults<RealmRoom> deletedRoomsListPined = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.IS_PINNED, true)
                            .greaterThanOrEqualTo(RealmRoomFields.PIN_ID, pinIdMin)
                            .lessThanOrEqualTo(RealmRoomFields.PIN_ID, pinIdMax)
                            .equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();

                    for (RealmRoom item : deletedRoomsListUnPined) {
                        if (allUnPined.contains(item.getId())) {
                            continue;
                        }
                        /**
                         * delete all message in deleted room
                         *
                         * hint: {@link RealmRoom#deleteRoom(long)} also do following actions but it is in
                         * transaction and client can't use a transaction inside another
                         */
                        RealmRoomMessage.deleteAllMessage(realm, item.getId());
                        RealmClientCondition.deleteCondition(realm, item.getId());
                        item.deleteFromRealm();
                    }

                    for (RealmRoom item : deletedRoomsListPined) {
                        if (allPinned.contains(item.getId())) {
                            continue;
                        }
                        /**
                         * delete all message in deleted room
                         *
                         * hint: {@link RealmRoom#deleteRoom(long)} also do following actions but it is in
                         * transaction and client can't use a transaction inside another
                         */
                        RealmRoomMessage.deleteAllMessage(realm, item.getId());
                        RealmClientCondition.deleteCondition(realm, item.getId());
                        item.deleteFromRealm();
                    }
                }
            });
        }
    }

    public static void convertAndSetDraft(final long roomId, final String message, final long replyToMessageId, int draftTime) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId, draftTime));
                        if (!message.isEmpty() && draftTime != 0) {
                            realmRoom.setUpdatedTime(draftTime * (DateUtils.SECOND_IN_MILLIS));
                        }
                        if (realmRoom.getDraft() == null) {
                            realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId, draftTime));
                        } else {
                            realmRoom.setDraft(RealmRoomDraft.putOrUpdate(realm, realmRoom.getDraft(), message, replyToMessageId, draftTime));
                        }
                    }
                }
            });
        }
    }

    /**
     * create RealmRoom without info ,just have roomId and type
     * use this for detect that a room is a private channel
     * set deleted true and keep true for not showing in room list
     * and keep info for use in another subjects
     */
    public static void createEmptyRoom(final long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
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
        }
    }

    public static void needGetRoom(long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom == null) {
                new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
            }
        }
    }

    /**
     * check with this roomId that room is showing in room list or no
     */
    public static boolean isMainRoom(long roomId) {
        boolean isMainRoom = false;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();
            if (realmRoom != null) {
                isMainRoom = true;
            }
        }
        return isMainRoom;
    }

    /**
     * check updater author for detect that updater is another device for
     * this account and finally update unread count if another account
     * was saw message for this room
     *
     * @param roomId     roomId for room that get update status from that
     * @param authorHash updater author hash
     */
    public static void clearUnreadCount(long roomId, String authorHash, ProtoGlobal.RoomMessageStatus messageStatus, long messageId) {
        if (G.authorHash.equals(authorHash) && messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null && (realmRoom.getLastMessage() != null && realmRoom.getLastMessage().getMessageId() <= messageId)) {
                    realmRoom.setUnreadCount(0);
                }
            }
        }
    }

    public static void updateMineRole(long roomId, long memberId, final String role) {

        try (Realm realm = Realm.getDefaultInstance()) {
            if (memberId == userId) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom == null) {
                    return;
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                            GroupChatRole mRole;
                            if (role.contains(GroupChatRole.ADMIN.toString())) {
                                mRole = GroupChatRole.ADMIN;
                            } else if (role.contains(GroupChatRole.MODERATOR.toString())) {
                                mRole = GroupChatRole.MODERATOR;
                            } else {
                                mRole = GroupChatRole.MEMBER;
                            }
                            if (realmRoom.getGroupRoom() != null) {
                                realmRoom.getGroupRoom().setRole(mRole);
                            }
                        } else {
                            ChannelChatRole mRole;
                            if (role.contains(ChannelChatRole.ADMIN.toString())) {
                                mRole = ChannelChatRole.ADMIN;
                            } else if (role.contains(ChannelChatRole.MODERATOR.toString())) {
                                mRole = ChannelChatRole.MODERATOR;
                            } else {
                                mRole = ChannelChatRole.MEMBER;
                            }
                            if (realmRoom.getChannelRoom() != null) {
                                realmRoom.getChannelRoom().setRole(mRole);
                            }

                            updateReadOnlyChannel(mRole, realmRoom);

                        }
                    }
                });
            }
        }
    }

    private static void updateReadOnlyChannel(ChannelChatRole role, RealmRoom realmRoom) {
        switch (role) {
            case MODERATOR:
            case ADMIN:
            case OWNER:
                realmRoom.setReadOnly(false);
                break;
            default:
                realmRoom.setReadOnly(true);
                break;
        }
    }

    public static void updateMemberRole(final long roomId, final long userId, final String role) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmList<RealmMember> realmMemberRealmList = null;
                        if (realmRoom.getType() == GROUP) {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmMemberRealmList = realmGroupRoom.getMembers();
                            }
                        } else if (realmRoom.getType() == CHANNEL) {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmMemberRealmList = realmChannelRoom.getMembers();
                            }
                        }

                        if (realmMemberRealmList != null) {
                            for (RealmMember member : realmMemberRealmList) {
                                if (member.getPeerId() == userId) {
                                    member.setRole(role);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public static String getMemberCount(Realm realm, long roomId) {

        String memberCount = "";
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
            } else if (realmRoom.getType() == CHANNEL) {
                memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
            }
        }

        return memberCount;
    }

    /**
     * delete room with transaction from realm and also delete all messages
     * from this room and finally delete RealmClientCondition
     */
    public static void deleteRoom(final long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.deleteFromRealm();
                    }

                    RealmClientCondition.deleteCondition(realm, roomId);
                    RealmRoomMessage.deleteAllMessage(realm, roomId);
                }
            });
        }
    }

    public static void addOwnerToDatabase(long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getType() == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        final RealmList<RealmMember> members = realmChannelRoom.getMembers();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                members.add(RealmMember.put(realm, userId, ProtoGlobal.ChannelRoom.Role.OWNER.toString()));
                            }
                        });
                    }
                } else if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                members.add(RealmMember.put(realm, userId, ProtoGlobal.GroupRoom.Role.OWNER.toString()));
                            }
                        });
                    }
                }
            }
        }
    }

    public static boolean showSignature(long roomId) {
        boolean signature = false;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
                signature = true;
            }
        }
        return signature;
    }

    /**
     * if room isn't exist get info from server
     */
    public static boolean needUpdateRoomInfo(long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                return false;
            }
            new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
        }
        return true;
    }

    public static void updateChatTitle(final long userId, final String title) {// TODO [Saeed Mozaffari] [2017-10-24 3:36 PM] - Can Write Better Code?
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.TYPE, ProtoGlobal.Room.Type.CHAT.toString()).findAll()) {
                        if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == userId) {
                            realmRoom.setTitle(title.trim());
                        }
                    }
                }
            });
        }
    }

    public static void updateMemberCount(long roomId, final ProtoGlobal.Room.Type roomType, final long memberCount) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (roomType == CHANNEL) {
                        if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                            realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                        }
                    } else {
                        if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                            realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                        }
                    }
                }
            });
        }
    }

    public static void updateMemberCount(final long roomId, final boolean plus) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    updateMemberCount(realm, roomId, plus);
                }
            });
        }
    }

    public static int updateMemberCount(Realm realm, final long roomId, final boolean plus) {
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            String participantsCountLabel;
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
            } else {
                if (realmRoom.getChannelRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
            }

            if (HelperString.isNumeric(participantsCountLabel)) {
                int memberCount = Integer.parseInt(participantsCountLabel);
                if (plus) {
                    memberCount++;
                } else {
                    memberCount--;
                }

                if (realmRoom.getType() == GROUP) {
                    realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                } else {
                    realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                }
                return memberCount;
            }
        }
        return 0;
    }

    public static void updatePin(final long roomId, final boolean pin, final long pinId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
                    if (room != null) {
                        room.setPinned(pin);
                        room.setPinId(pinId);
                    }
                }
            });
        }
    }

    public static void updateSignature(final long roomId, final boolean signature) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.setSignature(signature);
                        }
                    }
                }
            });
        }
    }

    public static void updateUsername(final long roomId, final String username) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        if (realmRoom.getType() == GROUP) {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.setUsername(username);
                                realmGroupRoom.setPrivate(false);
                            }
                        } else {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.setUsername(username);
                                realmChannelRoom.setPrivate(false);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * check exist chat room with userId(peerId) and set a value for notify room item
     */
    public static void updateChatRoom(final long userId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.TYPE, CHAT.toString()).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
                    if (room != null) {
                        room.setReadOnly(room.getReadOnly());// set data for update room item
                    }
                }
            });
        }
    }

    public static void updateTime(Realm realm, long roomId, long time) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realmRoom.setUpdatedTime(time);
        }
    }

    public static void setPrivate(final long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        if (realmRoom.getType() == GROUP) {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.setPrivate(true);
                            }
                        } else {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.setPrivate(true);
                            }
                        }
                    }
                }
            });
        }
    }

    public static void setCountShearedMedia(final long roomId, final String count) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (room != null) {
                        room.setSharedMediaCount(count);
                    }
                }
            });
        }
    }

    public static void setCount(final long roomId, final int count) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    setCount(realm, roomId, count);
                }
            });
        }
    }

    public static RealmRoom setCount(Realm realm, final long roomId, final int count) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null) {
            room.setUnreadCount(count);
        }
        return room;
    }

    public static RealmRoom removeFirstUnreadMessage(Realm realm, final long roomId) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null) {
            room.setFirstUnreadMessage(null);
        }
        return room;
    }

    public static RealmRoom setCountWithCallBack(Realm realm, final long roomId, final int count) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null) {
            room.setUnreadCount(count);
        }

        return room;
    }

    public static void setAction(final long roomId, final long userId, final String action) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setActionState(action, userId);
                    }
                }
            });
        }
    }

    public static void setLastScrollPosition(final long roomId, final long messageId, final int offset) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setLastScrollPositionMessageId(messageId);
                        realmRoom.setLastScrollPositionOffset(offset);
                    }
                }
            });
        }
    }

    public static void clearAllScrollPositions() {
        try (Realm realm = Realm.getDefaultInstance()) {
            for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                setLastScrollPosition(realm, realmRoom.id);
            }
        }
    }

    private static void setLastScrollPosition(Realm realm, long roomId) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realmRoom.setLastScrollPositionMessageId(0);
            realmRoom.setLastScrollPositionOffset(0);
        }
    }


    public static void setDraft(final long roomId, final String message, final long replyToMessageId, ProtoGlobal.Room.Type chatType) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

            if (realmRoom != null) {
                if (realmRoom.getDraft() == null || realmRoom.getDraft().getMessage() == null || !realmRoom.getDraft().getMessage().equals(message)) {
                    if (chatType == CHAT) {
                        new RequestChatUpdateDraft().chatUpdateDraft(roomId, message, replyToMessageId);
                    } else if (chatType == GROUP) {
                        new RequestGroupUpdateDraft().groupUpdateDraft(roomId, message, replyToMessageId);
                    } else if (chatType == CHANNEL) {
                        new RequestChannelUpdateDraft().channelUpdateDraft(roomId, message, replyToMessageId);
                    }
                }
            }
        }
    }

    public static void editRoom(final long roomId, final String title, final String description) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setTitle(title);
                        if (realmRoom.getType() == GROUP) {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.setDescription(description);
                            }
                        } else {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.setDescription(description);
                            }
                        }
                    }
                }
            });
        }
    }

    public static void clearDraft(final long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null && realmRoom.getLastMessage() != null) {
                        if (realmRoom.getLastMessage().getUpdateTime() == 0) {
                            realmRoom.setUpdatedTime(realmRoom.getLastMessage().getCreateTime());
                        } else {
                            realmRoom.setUpdatedTime(realmRoom.getLastMessage().getUpdateTime());
                        }
                    }
                }
            });
        }
    }

    /**
     * clear all actions from RealmRoom for all rooms
     */
    public static void clearAllActions() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                        realmRoom.setActionState(null, 0);
                    }
                }
            });
        }
    }

    public static void joinRoom(final long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (realmRoom != null && realmRoom.isValid()) {
                        realmRoom.setDeleted(false);
                        if (realmRoom.getType() == GROUP) {
                            realmRoom.setReadOnly(false);
                        }
                    } else {
                        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
                    }
                }
            });

        }
    }

    public static void joinByInviteLink(long roomId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                            realmRoom.setReadOnly(false);
                        }
                        realmRoom.setDeleted(false);
                    }
                });
            }
        }
    }

    public static boolean isNotificationServices(long roomId) {
        boolean isNotificationService = false;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (room != null && room.getType() == CHAT && room.getChatRoom() != null) {
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
                if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.SERVICE_NOTIFICATIONS.toString())) {
                    isNotificationService = true;
                }
            }
        }

        return isNotificationService;
    }

    public static ProtoGlobal.Room.Type detectType(long roomId) {
        ProtoGlobal.Room.Type roomType = ProtoGlobal.Room.Type.UNRECOGNIZED;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                roomType = realmRoom.getType();
            }
        }

        return roomType;
    }

    public static String detectTitle(long roomId) {
        String title = "";
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                title = realmRoom.getTitle();
            }
        }
        return title;
    }

    public static void setLastMessageWithRoomMessage(Realm realm, long roomId, RealmRoomMessage roomMessage) {
        if (roomMessage != null) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setLastMessage(roomMessage);
            }
        }
    }

    public static void setLastMessageWithRoomMessage(final long roomId, final RealmRoomMessage roomMessage) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    setLastMessageWithRoomMessage(realm, roomId, roomMessage);
                }
            });
        }
    }

    public static void setLastMessageAfterLocalDelete(final long roomId, final long messageId) { // FragmentChat, is need this method?
        //TODO [Saeed Mozaffari] [2017-10-23 9:38 AM] - Write Better Code
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        RealmRoomMessage realmRoomMessage = null;
                        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.EDITED, false).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).findAll();
                        if (realmRoomMessages.size() > 0) {
                            realmRoomMessage = realmRoomMessages.last();
                        }

                        if (realmRoom != null && realmRoomMessage != null) {
                            realmRoom.setLastMessage(realmRoomMessage);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void convertChatToGroup(final long roomId, final String title, final String description, final ProtoGlobal.GroupRoom.Role role) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setType(RoomType.GROUP);
                        realmRoom.setTitle(title);
                        realmRoom.setGroupRoom(RealmGroupRoom.putIncomplete(realm, role, description, "2"));
                        realmRoom.setChatRoom(null);
                    }
                }
            });
        }
    }

    public static long getRoomIdByPeerId(long peerId){
        long roomId = 0 ;
        try(Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();
            if (realmRoom != null){
                roomId =  realmRoom.getId();
            }
        }
        return roomId ;
    }

    public static void clearMessage(final long roomId, final long clearId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null && ((realmRoom.getLastMessage() == null) || (realmRoom.getLastMessage().getMessageId() <= clearId))) {
                        realmRoom.setUnreadCount(0);
                        realmRoom.setLastMessage(null);
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
        return (isValid() && type != null) ? ProtoGlobal.Room.Type.valueOf(type) : null;
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

    public void setMute(ProtoGlobal.RoomMute muteState) {
        this.mute = muteState == ProtoGlobal.RoomMute.MUTE;
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

        /*String[] countList = sharedMediaCount.split("\n");
        try {

            int countOFImage = Integer.parseInt(countList[0]);
            int countOFVIDEO = Integer.parseInt(countList[1]);
            int countOFAUDIO = Integer.parseInt(countList[2]);
            int countOFVOICE = Integer.parseInt(countList[3]);
            int countOFGIF = Integer.parseInt(countList[4]);
            int countOFFILE = Integer.parseInt(countList[5]);
            int countOFLink = Integer.parseInt(countList[6]);

            String result = "";

            if (countOFImage > 0)
                result += "\n" + countOFImage + " " + context.getString(R.string.shared_image);
            if (countOFVIDEO > 0)
                result += "\n" + countOFVIDEO + " " + context.getString(R.string.shared_video);
            if (countOFAUDIO > 0)
                result += "\n" + countOFAUDIO + " " + context.getString(R.string.shared_audio);
            if (countOFVOICE > 0)
                result += "\n" + countOFVOICE + " " + context.getString(R.string.shared_voice);
            if (countOFGIF > 0)
                result += "\n" + countOFGIF + " " + context.getString(R.string.shared_gif);
            if (countOFFILE > 0)
                result += "\n" + countOFFILE + " " + context.getString(R.string.shared_file);
            if (countOFLink > 0)
                result += "\n" + countOFLink + " " + context.getString(R.string.shared_links);

            result = result.trim();

            if (result.length() < 1) {
                result = context.getString(R.string.there_is_no_sheared_media);
            }

            return result;
        } catch (Exception e) {

            return sharedMediaCount;
        }*/
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

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public long getPinId() {
        return pinId;
    }

    public void setPinId(long pinId) {
        this.pinId = pinId;
    }


    public long getPinMessageId() {
        return pinMessageId;
    }

    public void setPinMessageId(long pinMessageId) {
        this.pinMessageId = pinMessageId;
    }

    public long getPinMessageIdDeleted() {
        return pinMessageIdDeleted;
    }

    public void setPinMessageIdDeleted(long pinMessageIdDeleted) {
        this.pinMessageIdDeleted = pinMessageIdDeleted;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static boolean isPinedMessage(long roomId, long messageId) {
        boolean result = false;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                if (room.getPinMessageId() == messageId) {
                    result = true;
                }
            }
        }
        return result;
    }

    public static void updatePinedMessage(long roomId, final long messageId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        room.setPinMessageId(messageId);
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (G.onPinedMessage != null) {
                            G.onPinedMessage.onPinMessage();
                        }
                    }
                }, 200);

            }
        }
    }

    public static void updatePinedMessageDeleted(long roomId, final boolean reset) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        room.setPinMessageIdDeleted(reset ? 0 : room.getPinMessageId());
                    }
                });
            }
        }
    }

    public static long hasPinedMessage(long roomId) {
        long result = 0;
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                if (room.getPinMessageId() > 0) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                            equalTo(RealmRoomMessageFields.MESSAGE_ID, room.getPinMessageId()).findFirst();
                    if (roomMessage == null) {
                        new RequestClientGetRoomMessage().clientGetRoomMessage(roomId, room.getPinMessageId(), new OnClientGetRoomMessage() {
                            @Override
                            public void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage message) {
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (G.onPinedMessage != null) {
                                            G.onPinedMessage.onPinMessage();
                                        }
                                    }
                                }, 200);
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {

                            }
                        });
                    } else {
                        RealmRoomMessage roomMessage1 = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                                equalTo(RealmRoomMessageFields.MESSAGE_ID, room.getPinMessageId()).notEqualTo(RealmRoomMessageFields.MESSAGE_ID, room.getPinMessageIdDeleted()).
                                equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findFirst();
                        if (roomMessage1 != null) {
                            result = roomMessage1.getMessageId();
                        }
                    }
                }
            }
        }

        return result;
    }

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

    public long getLastScrollPositionMessageId() {
        if (lastScrollPositionMessageId == 0 || !RealmRoomMessage.existMessageInRoom(lastScrollPositionMessageId, id))
            return 0;

        return lastScrollPositionMessageId;
    }

    public void setLastScrollPositionMessageId(long lastScrollPositionMessageId) {
        this.lastScrollPositionMessageId = lastScrollPositionMessageId;
    }

    public int getLastScrollPositionOffset() {
        return lastScrollPositionOffset;
    }

    public void setLastScrollPositionOffset(int lastScrollPositionOffset) {
        this.lastScrollPositionOffset = lastScrollPositionOffset;
    }

    public RealmRoomMessage getFirstUnreadMessage() {
        return firstUnreadMessage;
    }

    public void setFirstUnreadMessage(RealmRoomMessage firstUnreadMessage) {
        this.firstUnreadMessage = firstUnreadMessage;
    }

    public RealmRoomMessage getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(final long roomId) {
        //TODO [Saeed Mozaffari] [2017-10-22 5:26 PM] - Write Better Code
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
            if (realmRoomMessages.size() > 0 && realmRoomMessages.first() != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (realmRoom != null) {
                            realmRoom.setLastMessage(realmRoomMessages.first());
                        }
                    }
                });
            }
        }
    }

    public void setLastMessage(RealmRoomMessage lastMessage) {
        if (lastMessage != null) {
            setUpdatedTime(lastMessage.getUpdateOrCreateTime());
        }
        this.lastMessage = lastMessage;
    }

    public long getOwnerId() {
        if (ProtoGlobal.Room.Type.valueOf(type) == CHAT) {
            return getChatRoom().getPeerId();
        }
        return id;
    }

    public static boolean isBot(long userId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (realmRegisteredInfo != null) {
                return realmRegisteredInfo.isBot();
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] getUnreadCountPages() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RealmRoom> results = realm.where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.MUTE, false).equalTo(RealmRoomFields.IS_DELETED, false).findAll();
            int all = 0, chat = 0, group = 0, channel = 0;
            for (RealmRoom rm : results) {
                switch (rm.getType()) {
                    case CHANNEL:
                        channel += rm.getUnreadCount();
                        break;
                    case CHAT:
                        chat += rm.getUnreadCount();
                        break;
                    case GROUP:
                        group += rm.getUnreadCount();
                        break;
                }
                all += rm.getUnreadCount();
            }
            String[] ar;
            if (HelperCalander.isPersianUnicode) {
                ar = new String[]{"0", "0", all + ""};
            } else {
                ar = new String[]{all + "", "0", "0"};
            }
            return ar;
        }
    }


    public static void setPromote(Long id, ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type type) {

        if (type == ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type.USER) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();

                        if (realmRoom != null) {
                            realmRoom.setFromPromote(true);
                        }
                    }

                });
            }
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
                        if (realmRoom != null) {
                            realmRoom.setFromPromote(true);
                        } else {
                            realmRoom.setFromPromote(false);
                        }

                    }
                });
            }
        }

    }

    public static boolean isPromote(Long id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
            if (realmRoom != null) {
                return realmRoom.isFromPromote();
            }
        }

        return false;
    }
}
