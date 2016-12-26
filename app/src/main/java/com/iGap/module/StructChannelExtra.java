package com.iGap.module;

import com.iGap.realm.RealmChannelExtra;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;

import org.parceler.Parcel;

import io.realm.Realm;

@Parcel
public class StructChannelExtra {

    public long messageId = 0;
    public String signature = "";
    public String viewsLabel = "1";
    public String thumbsUp = "0";
    public String thumbsDown = "0";

    public static StructChannelExtra
    convert(RealmChannelExtra realmChannelExtra) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();

        /*if (realmChannelExtra.getSignature().isEmpty()) {
            if (showSignature(roomId)) {
                structChannelExtra.signature = getName();
            }
        } else {
            structChannelExtra.signature = realmChannelExtra.getSignature();
        }*/

        structChannelExtra.signature = realmChannelExtra.getSignature();
        structChannelExtra.thumbsUp = realmChannelExtra.getThumbsUp();
        structChannelExtra.thumbsDown = realmChannelExtra.getThumbsDown();
        structChannelExtra.viewsLabel = realmChannelExtra.getViewsLabel();
        return structChannelExtra;
    }

    private static boolean showSignature(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        boolean signature = false;
        if (realmRoom != null && realmRoom.getChannelRoom() != null) {
            signature = realmRoom.getChannelRoom().isSignature();
        }
        realm.close();
        return signature;
    }

    private static String getName() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        String name = realmUserInfo.getUserInfo().getDisplayName();
        realm.close();
        return name;
    }
}
