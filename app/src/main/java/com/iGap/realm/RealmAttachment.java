package com.iGap.realm;

import android.support.annotation.Nullable;

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/26/2016.
 */
public class RealmAttachment extends RealmObject {
    // should be message id for message attachment and user id for avatar
    @PrimaryKey
    private long id;
    private String token;
    private String name;
    private long size;
    private int width;
    private int height;
    private double duration;
    private String cacheId;

    public static RealmAttachment build(ProtoGlobal.File file) {
        Realm realm = Realm.getDefaultInstance();

        RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, file.getToken()).findFirst();
        if (realmAttachment == null) {
            realmAttachment = realm.createObject(RealmAttachment.class);
            long id = System.nanoTime();

            realmAttachment.setId(id);

            realmAttachment.setCacheId(file.getCacheId());
            realmAttachment.setDuration(file.getDuration());
            realmAttachment.setHeight(file.getHeight());

            long largeId = System.nanoTime();
            RealmThumbnail.create(largeId, id, file.getLargeThumbnail());
            long smallId = System.nanoTime();
            RealmThumbnail.create(smallId, id, file.getSmallThumbnail());

            RealmThumbnail largeThumbnail = realm.where(RealmThumbnail.class).equalTo("id", largeId).findFirst();
            realmAttachment.setLargeThumbnail(largeThumbnail);
            RealmThumbnail smallThumbnail = realm.where(RealmThumbnail.class).equalTo("id", smallId).findFirst();
            realmAttachment.setSmallThumbnail(smallThumbnail);

            realmAttachment.setLocalFilePath(G.DIR_IMAGE_USER + "/" + file.getToken() + "_" + file.getName());
            realmAttachment.setLocalThumbnailPath(G.DIR_TEMP + "/" + file.getToken() + "_" + file.getName());
            realmAttachment.setName(file.getName());
            realmAttachment.setSize(file.getSize());
            realmAttachment.setToken(file.getToken());
            realmAttachment.setWidth(file.getWidth());
        }

        return realmAttachment;
    }

    public RealmThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(RealmThumbnail largeThumbnail) {
        this.largeThumbnail = largeThumbnail;
    }

    public RealmThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(RealmThumbnail smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    private RealmThumbnail largeThumbnail;
    private RealmThumbnail smallThumbnail;

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public boolean thumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public boolean fileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    public void setLocalThumbnailPath(@Nullable String localThumbnailPath) {
        this.localThumbnailPath = localThumbnailPath;
    }

    @Nullable
    private String localThumbnailPath;
    @Nullable
    private String localFilePath;

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(@Nullable String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }
}
