package com.iGap.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmClientCondition extends RealmObject {
    @PrimaryKey
    private long roomId;
    private long messageVersion;
    private long statusVersion;
    private long deleteVersion;
    private RealmList<RealmOfflineDelete> offlineDeleted;
    private RealmList<RealmOfflineEdited> offlineEdited;
    private RealmList<RealmOfflineSeen> offlineSeen;
    private long clearId;
    private long cacheStartId;
    private long cacheEndId;
    private String offlineMute;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(long messageVersion) {
        this.messageVersion = messageVersion;
    }

    public long getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(long statusVersion) {
        this.statusVersion = statusVersion;
    }

    public long getDeleteVersion() {
        return deleteVersion;
    }

    public void setDeleteVersion(long deleteVersion) {
        this.deleteVersion = deleteVersion;
    }

    public RealmList<RealmOfflineDelete> getOfflineDeleted() {
        return offlineDeleted;
    }

    public void setOfflineDeleted(RealmList<RealmOfflineDelete> offlineDeleted) {
        this.offlineDeleted = offlineDeleted;
    }

    public RealmList<RealmOfflineEdited> getOfflineEdited() {
        return offlineEdited;
    }

    public void setOfflineEdited(RealmList<RealmOfflineEdited> offlineEdited) {
        this.offlineEdited = offlineEdited;
    }

    public RealmList<RealmOfflineSeen> getOfflineSeen() {
        return offlineSeen;
    }

    public void setOfflineSeen(RealmList<RealmOfflineSeen> offlineSeen) {
        this.offlineSeen = offlineSeen;
    }

    public long getClearId() {
        return clearId;
    }

    public void setClearId(long clearId) {
        this.clearId = clearId;
    }

    public long getCacheStartId() {
        return cacheStartId;
    }

    public void setCacheStartId(long cacheStartId) {
        this.cacheStartId = cacheStartId;
    }

    public long getCacheEndId() {
        return cacheEndId;
    }

    public void setCacheEndId(long cacheEndId) {
        this.cacheEndId = cacheEndId;
    }

    public String getOfflineMute() {
        return offlineMute;
    }

    public void setOfflineMute(String offlineMute) {
        this.offlineMute = offlineMute;
    }
}
