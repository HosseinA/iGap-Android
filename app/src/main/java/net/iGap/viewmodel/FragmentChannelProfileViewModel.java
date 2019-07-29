package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChannelProfile;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.model.GoToSharedMediaModel;
import net.iGap.model.GoToShowMemberModel;
import net.iGap.module.AttachFile;
import net.iGap.module.MEditText;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddModerator;
import net.iGap.request.RequestClientMuteRoom;

import org.jetbrains.annotations.NotNull;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;

public class FragmentChannelProfileViewModel extends ViewModel
        /*implements OnChannelAddMember, OnChannelAddModerator, OnChannelUpdateReactionStatus, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete,
        OnChannelLeft, OnChannelEdit, OnChannelRevokeLink*/ {

    public static final String FRAGMENT_TAG = "FragmentChannelProfile";

    public ObservableInt haveDescription = new ObservableInt(View.VISIBLE);
    public ObservableInt isVerifiedChannel = new ObservableInt(View.GONE);
    public ObservableField<String> channelLink = new ObservableField<>("Link");
    public ObservableInt isShowLink = new ObservableInt(View.GONE);
    public ObservableInt channelLinkTitle = new ObservableInt(R.string.invite_link_title);
    public ObservableBoolean isMuteNotification = new ObservableBoolean(false);
    public MutableLiveData<Boolean> muteNotifListener = new MutableLiveData<>();
    public ObservableField<String> subscribersCount = new ObservableField<>("0");
    public ObservableField<String> administratorsCount = new ObservableField<>("0");
    public ObservableField<String> moderatorsCount = new ObservableField<>("0");
    public ObservableInt showMemberList = new ObservableInt(View.GONE);
    public ObservableInt noMediaVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedPhotoVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedPhotoCount = new ObservableInt(0);
    public ObservableInt sharedVideoVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedVideoCount = new ObservableInt(0);
    public ObservableInt sharedAudioVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedAudioCount = new ObservableInt(0);
    public ObservableInt sharedVoiceVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedVoiceCount = new ObservableInt(0);
    public ObservableInt sharedGifVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedGifCount = new ObservableInt(0);
    public ObservableInt sharedFileVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedFileCount = new ObservableInt(0);
    public ObservableInt sharedLinkVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedLinkCount = new ObservableInt(0);
    public ObservableInt showLoading = new ObservableInt(View.GONE);
    //Ui event
    public MutableLiveData<String> channelName = new MutableLiveData<>();
    public MutableLiveData<String> channelSecondsTitle = new MutableLiveData<>();
    public MutableLiveData<String> channelDescription = new MutableLiveData<>();
    public MutableLiveData<Integer> menuPopupVisibility = new MutableLiveData<>();
    public MutableLiveData<Integer> editButtonVisibility = new MutableLiveData<>();
    public MutableLiveData<Long> goToShowAvatarPage = new MutableLiveData<>();
    public MutableLiveData<GoToShowMemberModel> goToShowMemberList = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToRoomListPage = new MutableLiveData<>();
    public MutableLiveData<String> showDialogCopyLink = new MutableLiveData<>();
    public MutableLiveData<GoToSharedMediaModel> goToSharedMediaPage = new MutableLiveData<>();

    private ChannelChatRole role;
    public long roomId;
    private boolean isPrivate;
    private boolean isNotJoin;
    private Realm realmChannelProfile;
    private RealmRoom mRoom;
    private FragmentChannelProfile fragment;

    public static OnMenuClick onMenuClick;
    private boolean isNeedGetMemberList = true;
    private RealmChangeListener<RealmModel> changeListener;

    public FragmentChannelProfileViewModel( FragmentChannelProfile fragmentChannelProfile , long roomId, boolean isNotJoin) {

        this.fragment = fragmentChannelProfile;

        this.roomId = roomId;
        this.isNotJoin = isNotJoin;

        realmChannelProfile = Realm.getDefaultInstance();

        mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom == null || mRoom.getChannelRoom() == null) {
            goBack.setValue(true);
            return;
        }
        /*initials = mRoom.getInitials();*/
        role = mRoom.getChannelRoom().getRole();
        isPrivate = mRoom.getChannelRoom().isPrivate();
        channelName.setValue(mRoom.getTitle());

        channelDescription.setValue(mRoom.getChannelRoom().getDescription());
        if (mRoom.getChannelRoom().getDescription() != null && !mRoom.getChannelRoom().getDescription().isEmpty()) {
            haveDescription.set(View.VISIBLE);
        } else {
            haveDescription.set(View.GONE);
        }

        isVerifiedChannel.set(mRoom.getChannelRoom().isVerified() ? View.VISIBLE : View.GONE);

        // show link for logic
        if (isPrivate) {
            channelLink.set(mRoom.getChannelRoom().getInviteLink());
            channelLinkTitle.set(R.string.channel_link);
            if (role == ChannelChatRole.MEMBER) {
                isShowLink.set(View.GONE);
            } else {
                isShowLink.set(View.VISIBLE);
            }
        } else {
            channelLink.set(mRoom.getChannelRoom().getUsername());
            channelLinkTitle.set(R.string.st_username);
            isShowLink.set(View.VISIBLE);
        }

        isMuteNotification.set(mRoom.getMute());

        subscribersCount.set(mRoom.getChannelRoom().getParticipantsCountLabel());
        administratorsCount.set(String.valueOf(RealmMember.filterRole(realmChannelProfile, roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size()));
        moderatorsCount.set(String.valueOf(RealmMember.filterRole(realmChannelProfile, roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()).size()));

        if (role == ChannelChatRole.ADMIN || role == ChannelChatRole.OWNER) {
            //Todo : fixed it
            channelSecondsTitle.setValue(mRoom.getChannelRoom().isPrivate() ? G.currentActivity.getString(R.string.private_channel) : G.currentActivity.getString(R.string.public_channel));
            showMemberList.set(View.VISIBLE);
            editButtonVisibility.setValue(View.VISIBLE);
        } else {
            channelSecondsTitle.setValue(String.format("%s %s", mRoom.getChannelRoom().getParticipantsCountLabel(), G.currentActivity.getString(R.string.subscribers_title)));
            showMemberList.set(View.GONE);
            editButtonVisibility.setValue(View.GONE);
        }
        initRecycleView();

        FragmentShearedMedia.getCountOfSharedMedia(roomId);
    }

    public void onNotificationCheckChange() {
        isMuteNotification.set(!isMuteNotification.get());
        muteNotifListener.setValue(isMuteNotification.get());
    }

    public void onClickCircleImage() {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
            goToShowAvatarPage.setValue(roomId);
        }
    }

    public void onClickChannelLink() {
        if (isPrivate) {
            showDialogCopyLink.setValue(mRoom.getChannelRoom().getInviteLink());
        } else {
            showDialogCopyLink.setValue(mRoom.getChannelRoom().getUsername());
        }
    }

    public void onSubscribersClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), G.userId, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onAdministratorsClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), G.userId, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onModeratorClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), G.userId, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onClickGroupShearedMedia(int type) {
        goToSharedMediaPage.setValue(new GoToSharedMediaModel(roomId, type));
    }

    public void onResume() {
        mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom != null) {
            if (changeListener == null) {
                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(@NotNull RealmModel element) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (((RealmRoom) element).isValid()) {
                                    String countText = ((RealmRoom) element).getSharedMediaCount();
                                    Log.wtf("group profile view model", "value: " + countText);
                                    if (HelperCalander.isPersianUnicode) {
                                        countText = HelperCalander.convertToUnicodeFarsiNumber(countText);
                                    }
                                    if (countText == null || countText.length() == 0) {
                                        noMediaVisibility.set(View.GONE);
                                    } else {
                                        String[] countList = countText.split("\n");
                                        int countOFImage = Integer.parseInt(countList[0]);
                                        int countOFVIDEO = Integer.parseInt(countList[1]);
                                        int countOFAUDIO = Integer.parseInt(countList[2]);
                                        int countOFVOICE = Integer.parseInt(countList[3]);
                                        int countOFGIF = Integer.parseInt(countList[4]);
                                        int countOFFILE = Integer.parseInt(countList[5]);
                                        int countOFLink = Integer.parseInt(countList[6]);

                                        if (countOFImage > 0 || countOFVIDEO > 0 || countOFAUDIO > 0 || countOFVOICE > 0 || countOFGIF > 0 || countOFFILE > 0 || countOFLink > 0) {
                                            noMediaVisibility.set(View.VISIBLE);
                                            if (countOFImage > 0) {
                                                sharedPhotoVisibility.set(View.VISIBLE);
                                                sharedPhotoCount.set(countOFImage);
                                            } else {
                                                sharedPhotoVisibility.set(View.GONE);
                                            }
                                            if (countOFVIDEO > 0) {
                                                sharedVideoVisibility.set(View.VISIBLE);
                                                sharedVideoCount.set(countOFVIDEO);
                                            } else {
                                                sharedVideoVisibility.set(View.GONE);
                                            }
                                            if (countOFAUDIO > 0) {
                                                sharedAudioVisibility.set(View.VISIBLE);
                                                sharedAudioCount.set(countOFAUDIO);
                                            } else {
                                                sharedAudioVisibility.set(View.GONE);
                                            }
                                            if (countOFVOICE > 0) {
                                                sharedVoiceVisibility.set(View.VISIBLE);
                                                sharedVoiceCount.set(countOFVOICE);
                                            } else {
                                                sharedVoiceVisibility.set(View.GONE);
                                            }
                                            if (countOFGIF > 0) {
                                                sharedGifVisibility.set(View.VISIBLE);
                                                sharedGifCount.set(countOFGIF);
                                            } else {
                                                sharedGifVisibility.set(View.GONE);
                                            }
                                            if (countOFFILE > 0) {
                                                sharedFileVisibility.set(View.VISIBLE);
                                                sharedFileCount.set(countOFFILE);
                                            } else {
                                                sharedFileVisibility.set(View.GONE);
                                            }
                                            if (countOFLink > 0) {
                                                sharedLinkVisibility.set(View.VISIBLE);
                                                sharedLinkCount.set(countOFLink);
                                            } else {
                                                sharedLinkVisibility.set(View.GONE);
                                            }
                                        } else {
                                            noMediaVisibility.set(View.GONE);
                                        }
                                    }
                                }
                            }
                        });
                    }
                };
            }
            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            noMediaVisibility.set(View.GONE);
            /*callbackGroupShearedMedia.set(context.getString(R.string.there_is_no_sheared_media));*/
        }
    }

    public void onStop() {
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
        showLoading.set(View.GONE);
    }

    public void onDestroy() {
        if (realmChannelProfile != null && !realmChannelProfile.isClosed()) {
            realmChannelProfile.close();
        }
    }

    private Realm getRealm() {
        if (realmChannelProfile == null || realmChannelProfile.isClosed()) {
            realmChannelProfile = Realm.getDefaultInstance();
        }
        return realmChannelProfile;
    }

    //****** show admin or moderator list

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }


    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    //********** set roles

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(G.fragmentActivity, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == ChannelChatRole.OWNER) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == ChannelChatRole.ADMIN) {

                /**
                 *  ----------- Admin ---------------
                 *  1- admin dose'nt access set another admin
                 *  2- admin can set moderator
                 *  3- can remove moderator
                 *  4- can kick moderator and Member
                 */

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == ChannelChatRole.MODERATOR) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                }
            } else {

                return;
            }

            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_setAdmin:
                            setToAdmin(info.peerId);
                            return true;
                        case R.id.menu_set_moderator:
                            setToModerator(info.peerId);
                            return true;
                        case R.id.menu_remove_admin:
                            ((FragmentChannelProfile) fragment).kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            ((FragmentChannelProfile) fragment).kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            ((FragmentChannelProfile) fragment).kickMember(info.peerId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();
        }
    }

}