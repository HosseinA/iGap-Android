package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageLocation extends RealmObject {
    private double locationLat;
    private double locationLong;
    @PrimaryKey
    private long id;

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(double locationLong) {
        this.locationLong = locationLong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static RealmRoomMessageLocation build(final ProtoGlobal.RoomMessageLocation input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageLocation messageLocation = realm.createObject(RealmRoomMessageLocation.class);
        messageLocation.setId(System.nanoTime());
        messageLocation.setLocationLat(input.getLat());
        messageLocation.setLocationLong(input.getLon());
        realm.close();

        return messageLocation;
    }
}
