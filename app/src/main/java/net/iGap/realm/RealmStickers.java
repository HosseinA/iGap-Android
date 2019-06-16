package net.iGap.realm;


import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class RealmStickers extends RealmObject {

    private String st_id;
    private long createdAt;
    private long refId;
    private String name;
    private String avatarToken;
    private String uri = "";
    private long avatarSize;
    private String avatarName;
    private long price;
    private boolean isVip;
    private int sort;
    private boolean approved;
    private long createdBy;
    private boolean isFavorite;
    private RealmList<RealmStickersDetails> realmStickersDetails;


    public static RealmStickers put(long createdAt, String st_id, long refId, String name, String avatarToken, long avatarSize, String avatarName, long price, boolean isVip, int sort, boolean approved, long createdBy, List<StructItemSticker> stickers, boolean isFavorite) {

        Realm realm = Realm.getDefaultInstance();
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, st_id).findFirst();

        if (realmStickers == null) {
            realmStickers = realm.createObject(RealmStickers.class);
            realmStickers.setCreatedAt(createdAt);
            realmStickers.setSt_id(st_id);
            realmStickers.setRefId(refId);
            realmStickers.setName(name);
            realmStickers.setAvatarToken(avatarToken);
            realmStickers.setUri(HelperDownloadSticker.createPathFile(avatarToken, avatarName));
            realmStickers.setAvatarSize(avatarSize);
            realmStickers.setAvatarName(avatarName);
            realmStickers.setPrice(price);
            realmStickers.setVip(isVip);
            realmStickers.setSort(sort);
            realmStickers.setApproved(approved);
            realmStickers.setCreatedBy(createdBy);
            realmStickers.setFavorite(isFavorite);

            HelperDownloadSticker.stickerDownload(avatarToken, avatarName, avatarSize, ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {
                @Override
                public void OnProgress(String path, String token, int progress) {
                }

                @Override
                public void OnError(String token) {

                }
            });

            RealmList<RealmStickersDetails> realmStickersDetails = new RealmList<>();
            for (StructItemSticker itemSticker : stickers) {
                realmStickersDetails.add(RealmStickersDetails.put(itemSticker.getId(), itemSticker.getRefId(), itemSticker.getName(), itemSticker.getToken(), "", itemSticker.getAvatarSize(), itemSticker.getAvatarName(), itemSticker.getSort(), itemSticker.getGroupId()));
            }
            realmStickers.setRealmStickersDetails(realmStickersDetails);
        }

        realm.close();

        return realmStickers;
    }

    public static List<com.vanniktech.emoji.sticker.struct.StructGroupSticker> getAllStickers(boolean isFavorite) {
        List<com.vanniktech.emoji.sticker.struct.StructGroupSticker> stickers = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmStickers> realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.IS_FAVORITE, isFavorite).findAll();

        for (RealmStickers item : realmStickers) {
            com.vanniktech.emoji.sticker.struct.StructGroupSticker itemSticker = new com.vanniktech.emoji.sticker.struct.StructGroupSticker();

            itemSticker.setCreatedAt(item.getCreatedAt());
            itemSticker.setId(item.st_id);
            itemSticker.setRefId(item.refId);
            itemSticker.setName(item.name);
            itemSticker.setAvatarToken(item.avatarToken);
            itemSticker.setUri(item.getUri());
            itemSticker.setAvatarSize((int) item.getAvatarSize());
            itemSticker.setAvatarName(item.getAvatarName());
            itemSticker.setPrice(item.getPrice());
            itemSticker.setVip(item.isVip);
            itemSticker.setSort(item.sort);
            itemSticker.setCreatedBy(item.createdBy);

            List<com.vanniktech.emoji.sticker.struct.StructItemSticker> stickerDetails = new ArrayList<>();

            for (RealmStickersDetails it : item.getRealmStickersDetails()) {

                com.vanniktech.emoji.sticker.struct.StructItemSticker itemSticker1 = new com.vanniktech.emoji.sticker.struct.StructItemSticker();
                itemSticker1.setId(it.getSt_id());
                itemSticker1.setRefId(it.getRefId());
                itemSticker1.setName(it.getName());
                itemSticker1.setToken(it.getToken());
                itemSticker1.setUri(it.getUri());
                itemSticker1.setAvatarName(it.getFileName());
                itemSticker1.setAvatarSize((int) it.getFileSize());
                itemSticker1.setSort(it.getSort());
                itemSticker1.setGroupId(it.getGroupId());
                stickerDetails.add(itemSticker1);

            }
            itemSticker.setStickers(stickerDetails);
            stickers.add(itemSticker);
        }

        realm.close();
        return stickers;
    }

    public static com.vanniktech.emoji.sticker.struct.StructGroupSticker getEachSticker(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, groupId).findFirst();

        if (realmStickers == null) return null;
        com.vanniktech.emoji.sticker.struct.StructGroupSticker itemSticker = new com.vanniktech.emoji.sticker.struct.StructGroupSticker();

        itemSticker.setCreatedAt(realmStickers.getCreatedAt());
        itemSticker.setId(realmStickers.st_id);
        itemSticker.setRefId(realmStickers.refId);
        itemSticker.setName(realmStickers.name);
        itemSticker.setAvatarToken(realmStickers.avatarToken);
        itemSticker.setUri(realmStickers.getUri());
        itemSticker.setAvatarSize((int) realmStickers.getAvatarSize());
        itemSticker.setAvatarName(realmStickers.getAvatarName());
        itemSticker.setPrice(realmStickers.getPrice());
        itemSticker.setVip(realmStickers.isVip);
        itemSticker.setSort(realmStickers.sort);
        itemSticker.setCreatedBy(realmStickers.createdBy);

        List<com.vanniktech.emoji.sticker.struct.StructItemSticker> stickerDetails = new ArrayList<>();

        for (RealmStickersDetails it : realmStickers.getRealmStickersDetails()) {

            com.vanniktech.emoji.sticker.struct.StructItemSticker itemSticker1 = new com.vanniktech.emoji.sticker.struct.StructItemSticker();
            itemSticker1.setId(it.getSt_id());
            itemSticker1.setRefId(it.getRefId());
            itemSticker1.setName(it.getName());
            itemSticker1.setToken(it.getToken());
            itemSticker1.setUri(it.getUri());
            itemSticker1.setAvatarName(it.getFileName());
            itemSticker1.setAvatarSize((int) it.getFileSize());
            itemSticker1.setSort(it.getSort());
            itemSticker1.setGroupId(it.getGroupId());
            stickerDetails.add(itemSticker1);

        }
        itemSticker.setStickers(stickerDetails);


        realm.close();
        return itemSticker;
    }

    public static RealmStickers checkStickerExist(String groupId, Realm realm) {
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, groupId).findFirst();

        if (realmStickers == null) return realmStickers;

        return realmStickers;
    }

    public static RealmStickers updateUri(String groupId, String uri) {
        Realm realm = Realm.getDefaultInstance();

        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, groupId).findFirst();
        if (realmStickers != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmStickers.setUri(uri);
                }
            });
        }

        realm.close();

        return realmStickers;
    }

    public static RealmStickers updateFavorite(String groupId, boolean isFavorite) {
        Realm realm = Realm.getDefaultInstance();
        RealmStickers realmStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.ST_ID, groupId).findFirst();
        if (realmStickers != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmStickers.setFavorite(isFavorite);
                }
            });
        }
        realm.close();

        return realmStickers;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getSt_id() {
        return st_id;
    }

    public void setSt_id(String st_id) {
        this.st_id = st_id;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarToken() {
        return avatarToken;
    }

    public void setAvatarToken(String avatarToken) {
        this.avatarToken = avatarToken;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public RealmList<RealmStickersDetails> getRealmStickersDetails() {
        return realmStickersDetails;
    }

    public void setRealmStickersDetails(RealmList<RealmStickersDetails> realmStickersDetails) {
        this.realmStickersDetails = realmStickersDetails;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void removeFromRealm() {
        if (realmStickersDetails != null) {
            for (Iterator<RealmStickersDetails> iterator = realmStickersDetails.iterator(); iterator.hasNext();) {
                RealmStickersDetails stickersDetails = iterator.next();
                if (stickersDetails != null) {
                    iterator.remove();
                    stickersDetails.deleteFromRealm();
                }
            }
        }

        deleteFromRealm();
    }

    public static void updateStickers(List<StructGroupSticker> mData) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                HashSet<String> hashedData = new HashSet<>();
                ArrayList<RealmStickers> itemToDelete = new ArrayList<>();
                HashSet<String> itemNotNeedToAdd = new HashSet<>();
                for (StructGroupSticker structGroupSticker: mData) {
                    hashedData.add(structGroupSticker.getId());
                }

                RealmResults<RealmStickers> allStickers = realm.where(RealmStickers.class).equalTo(RealmStickersFields.IS_FAVORITE, true).findAll();
                for (RealmStickers realmStickers: allStickers) {
                    if (!hashedData.contains(realmStickers.st_id)) {
                        itemToDelete.add(realmStickers);
                    } else {
                        itemNotNeedToAdd.add(realmStickers.st_id);
                    }
                }

                for (RealmStickers realmStickers: itemToDelete) {
                    realmStickers.removeFromRealm();
                }

                for (StructGroupSticker item: mData) {
                    if (!itemNotNeedToAdd.contains(item.getId())) {
                        RealmStickers.put(item.getCreatedAt(), item.getId(), item.getRefId(), item.getName(), item.getAvatarToken(), item.getAvatarSize(), item.getAvatarName(), item.getPrice(), item.getIsVip(), item.getSort(), item.getIsVip(), item.getCreatedBy(), item.getStickers(), true);
                    }
                }
            }
        });
        realm.close();
    }
}
