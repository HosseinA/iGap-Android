package com.iGap.helper;

import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * helper methods while working with Realm
 * note: when any field of classes was changed, update this helper.
 */
public final class HelperRealm {
    private HelperRealm() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static RealmRoomMessage getLastMessage(long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmRoomMessage> realmRoomMessages =
            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
            .findAll();
        long lastMessageId = 0;
        long lastMessageTime = 0;
        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (realmRoomMessage != null) {
                if (realmRoomMessage.getUpdateTime() >= lastMessageTime) {
                    lastMessageId = realmRoomMessage.getMessageId();
                }
            }
        }

        RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, lastMessageId)
            .findFirst();
        realm.close();

        return lastMessage;
    }

    /**
     * when call this method all objects in realm will be deleted
     */

    public static void realmTruncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        realm.close();
    }
}
