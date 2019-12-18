package net.iGap.repository.sticker;

import android.util.Log;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructEachSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.realm.RealmStickers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StickerRepository {

    private APIEmojiService apiService;
    private StructIGStickerGroup stickerGroup;
    private String TAG = "abbasiSticker Repository";

    public StickerRepository(StructIGStickerGroup stickerGroup) {
        this();
        this.stickerGroup = stickerGroup;
    }

    public StickerRepository() {
        apiService = ApiEmojiUtils.getAPIService();
    }

    public void getStickerListForStickerDialog(ResponseCallback<StructIGStickerGroup> callback) {
        if (stickerGroup != null && stickerGroup.hasData()) {
            callback.onSuccess(stickerGroup);
            Log.i(TAG, "load sticker from DB with group id --> " + stickerGroup.getGroupId());
        } else if (stickerGroup != null) {
            getStickerFromServer(callback);
            Log.i(TAG, "get sticker from API SERVICE with group id --> " + stickerGroup.getGroupId());
        }
    }

    private void getStickerFromServer(ResponseCallback<StructIGStickerGroup> callback) {
        if (apiService != null && stickerGroup != null)
            apiService.getSticker(stickerGroup.getGroupId()).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(@NotNull Call<StructEachSticker> call, @NotNull Response<StructEachSticker> response) {
                    if (response.body() != null) {
                        if (response.body().getOk() && response.body().getData() != null) {

                            StructGroupSticker structGroupSticker = response.body().getData();

                            DbManager.getInstance().doRealmTransaction(realm -> {
                                RealmStickers realmStickers = RealmStickers.put(realm, structGroupSticker.getCreatedAt(), structGroupSticker.getId(), structGroupSticker.getRefId(), structGroupSticker.getName(), structGroupSticker.getAvatarToken(), structGroupSticker.getAvatarSize(), structGroupSticker.getAvatarName(), structGroupSticker.getPrice(), structGroupSticker.getIsVip(), structGroupSticker.getSort(), structGroupSticker.getIsVip(), structGroupSticker.getCreatedBy(), structGroupSticker.getStickers(), false);
                                stickerGroup.setValueWithRealmStickers(realmStickers);
                            });

                            G.handler.postDelayed(() -> callback.onSuccess(stickerGroup), 300);

                            Log.i(TAG, "get sticker from API SERVICE with group id" + stickerGroup.getGroupId() + " * and size " + stickerGroup.getGroupId() + " * successfully * ");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructEachSticker> call, @NotNull Throwable t) {
                    Log.i(TAG, "get sticker from API SERVICE  with group id" + stickerGroup.getGroupId() + " with error " + t.getMessage());
                    callback.onError(t.getMessage());
                }
            });
    }

    private void getStickerFromServerAndInsetToDb(String groupId) {
        if (apiService != null)
            apiService.getSticker(groupId).enqueue(new Callback<StructEachSticker>() {
                @Override
                public void onResponse(@NotNull Call<StructEachSticker> call, @NotNull Response<StructEachSticker> response) {
                    if (response.body() != null) {
                        if (response.body().getOk() && response.body().getData() != null) {

                            StructGroupSticker structGroupSticker = response.body().getData();

                            DbManager.getInstance().doRealmTransaction(realm -> {
                                RealmStickers.put(realm, structGroupSticker.getCreatedAt(), structGroupSticker.getId(), structGroupSticker.getRefId(), structGroupSticker.getName(), structGroupSticker.getAvatarToken(), structGroupSticker.getAvatarSize(), structGroupSticker.getAvatarName(), structGroupSticker.getPrice(), structGroupSticker.getIsVip(), structGroupSticker.getSort(), structGroupSticker.getIsVip(), structGroupSticker.getCreatedBy(), structGroupSticker.getStickers(), false);
                            });

                            Log.i(TAG, "get sticker from API SERVICE with group id" + groupId + " * successfully * ");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructEachSticker> call, @NotNull Throwable t) {
                    Log.i(TAG, "get sticker from API SERVICE  with group id" + stickerGroup.getGroupId() + " with error " + t.getMessage());
                }
            });
    }

    public void addStickerGroupToFavorite(String groupId, ResponseCallback<Boolean> callback) {
        if (apiService != null)
            apiService.addSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                @Override
                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                    if (response.body() != null && response.body().isSuccess()) {

                        DbManager.getInstance().doRealmTask(realm -> {
                            realm.executeTransactionAsync(realm1 -> {
                                RealmStickers realmStickers = RealmStickers.checkStickerExist(groupId, realm1);
                                if (realmStickers == null) {
                                    getStickerFromServerAndInsetToDb(groupId);
                                } else {
                                    RealmStickers.updateFavorite(realm1, groupId, true);
                                }
                            }, () -> {
                                if (FragmentChat.onUpdateSticker != null) {
                                    FragmentChat.onUpdateSticker.update();
                                }
                            });
                        });

                        callback.onSuccess(true);

                        Log.i(TAG, "add sticker to category successfully with group id --> " + groupId);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                    callback.onError(t.getMessage());
                    Log.i(TAG, "add sticker to category API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
    }

    public void removeStickerGroupFromFavorite(String groupId, ResponseCallback<Boolean> callback) {
        if (apiService != null) {
            apiService.removeSticker(groupId).enqueue(new Callback<StructStickerResult>() {
                @Override
                public void onResponse(@NotNull Call<StructStickerResult> call, @NotNull Response<StructStickerResult> response) {
                    if (response.body() != null && response.body().isSuccess()) {
                        DbManager.getInstance().doRealmTask(realm -> {
                            realm.executeTransactionAsync(realm1 -> RealmStickers.updateFavorite(realm1, groupId, false), () -> FragmentChat.onUpdateSticker.update());
                        });

                        callback.onSuccess(false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<StructStickerResult> call, @NotNull Throwable t) {
                    callback.onError(t.getMessage());
                    Log.i(TAG, "remove sticker to category API SERVICE  with group id" + groupId + " with error " + t.getMessage());
                }
            });
        }
    }

    public List<StructIGStickerGroup> getFavoriteStickers() {
        return RealmStickers.getFavoriteStickers();
    }
}
