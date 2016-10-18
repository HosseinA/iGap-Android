package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageLog extends RealmObject {
    private String type;
    @PrimaryKey
    private long id;

    public ProtoGlobal.RoomMessageLog.Type getType() {
        return ProtoGlobal.RoomMessageLog.Type.valueOf(type);
    }

    public void setType(ProtoGlobal.RoomMessageLog.Type type) {
        this.type = type.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static RealmRoomMessageLog build(final ProtoGlobal.RoomMessageLog input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageLog messageLocation = realm.createObject(RealmRoomMessageLog.class);
        messageLocation.setId(System.nanoTime());
        messageLocation.setType(input.getType());
        realm.close();

        return messageLocation;
    }
}
