package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupKickAdmin;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupKickAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder = (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();


        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", builder.getRoomId()).findFirst();

        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
            for (final RealmMember member : realmMembers) {
                if (member.getPeerId() == builder.getMemberId()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                        }
                    });
                    G.onGroupKickAdmin.onGroupKickAdmin(builder.getRoomId(), builder.getMemberId());
                    break;
                }
            }
        }

        realm.close();







        Log.e("ddd", "hhhhhhhhhh      " + builder.getRoomId() + "   " + builder.getMemberId());

    }

    @Override
    public void error() {
        Log.e("ddd", "hhhhhhhhhh      erore      " + message);
    }

    @Override
    public void timeOut() {

        Log.e("ddd", "hhhhhhhhhh      timout      " + message);
        super.timeOut();
    }
}
