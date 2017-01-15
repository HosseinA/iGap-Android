package com.iGap.response;

import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserAvatarGetList;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import io.realm.Realm;

public class UserAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        Realm realm = Realm.getDefaultInstance();
        final long userId = Long.parseLong(identity);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                // delete all avatar in roomId
                realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findAll().deleteAllFromRealm();

                ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder userAvatarGetListResponse = (ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder) message;

                // add all list to realm avatar
                for (ProtoGlobal.Avatar avatar : userAvatarGetListResponse.getAvatarList()) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                    realmAvatar.setOwnerId(userId);
                    realmAvatar.setUid(SUID.id().get());
                    realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR, null));
                }
            }
        });

        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();

    }

    @Override
    public void error() {
        super.error();
    }
}


