package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.SpannableStringBuilder;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.EditChannelFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnChannelEdit;
import net.iGap.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.interfaces.OnChannelUpdateSignature;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AttachFile;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelEdit;
import net.iGap.request.RequestChannelUpdateReactionStatus;
import net.iGap.request.RequestChannelUpdateSignature;

import java.util.HashMap;

import io.realm.Realm;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;

public class EditChannelViewModel extends ViewModel implements OnChannelAvatarAdd, OnChannelAvatarDelete, OnChannelUpdateReactionStatus {

    public MutableLiveData<String> avatarImage = new MutableLiveData<>();
    public MutableLiveData<String> groupName = new MutableLiveData<>();
    public MutableLiveData<SpannableStringBuilder> channelDescription = new MutableLiveData<>();
    public MutableLiveData<String> channelType = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSignedMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isReactionMessage = new MutableLiveData<>();
    public MutableLiveData<String> administratorsCount = new MutableLiveData<>();
    public MutableLiveData<String> moderatorsCount = new MutableLiveData<>();
    public MutableLiveData<String> subscribersCount = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLayoutReactStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> isShowLoading = new MutableLiveData<>();
    public MutableLiveData<String> leaveChannelText = new MutableLiveData<>();
    //ui
    public MutableLiveData<Boolean> goToMembersPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToAdministratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToModeratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> initEmoji = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();
    public MutableLiveData<Boolean> showSelectImageDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConvertChannelDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDeleteChannelDialog = new MutableLiveData<>();

    public long roomId;
    private boolean isPrivate;
    public ChannelChatRole role;
    private Realm realmChannelProfile;
    private AttachFile attachFile;
    private String pathSaveImage;
    public String inviteLink;
    public String linkUsername;

    public EditChannelViewModel(long roomId) {
        this.roomId = roomId;
        initEmoji.setValue(false);

        G.onChannelAvatarAdd = this;
        G.onChannelAvatarDelete = this;
        /*G.onChannelAddMember = this;*/
        /*G.onChannelAddAdmin = this;*/
        /*G.onChannelKickAdmin = this;*/
        /*G.onChannelAddModerator = this;*/
        /*G.onChannelKickModerator = this;*/
        /*G.onChannelDelete = this;*/
        /*G.onChannelLeft = this;*/
        /*G.onChannelEdit = this;*/
        /*G.onChannelRevokeLink = this;*/

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                final long finalMAvatarId = mAvatarId;
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //todo: fixed it
                        /*avatarHandler.avatarDelete(new ParamWithAvatarType(imgCircleImageView, fragmentChannelProfileViewModel.roomId)
                                .avatarType(AvatarHandler.AvatarType.ROOM).turnOffCache().onAvatarChange(new OnAvatarChange() {
                                    @Override
                                    public void onChange(boolean fromCache) {
                                        imgCircleImageView.setPadding(0, 0, 0, 0);
                                    }
                                }), finalMAvatarId);*/
                    }
                });
            }
        };
        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {
                pathSaveImage = null;
                pathSaveImage = path;
                long avatarId = SUID.id().get();
                long lastUploadedAvatarId = avatarId + 1L;

                isShowLoading.setValue(true);
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            /*prgWait.setProgress(progress);*/
                        } else {
                            new RequestChannelAvatarAdd().channelAvatarAdd(roomId, struct.token);
                        }
                    }

                    @Override
                    public void OnError() {
                        isShowLoading.setValue(false);
                    }
                });
            }
        };
        G.onChannelEdit = new OnChannelEdit() {
            @Override
            public void onChannelEdit(final long roomId, final String name, final String description) {
                G.handler.post(() -> {
                    isShowLoading.setValue(false);
                    groupName.setValue(name);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> isShowLoading.setValue(false));
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> isShowLoading.setValue(false));
            }
        };

        realmChannelProfile = Realm.getDefaultInstance();

        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            if (EditChannelFragment.onBackFragment != null)
                EditChannelFragment.onBackFragment.onBack();
            return;
        }
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        /*initials = realmRoom.getInitials();*/
        role = realmChannelRoom.getRole();
        isPrivate = realmChannelRoom.isPrivate();
        channelType.setValue(isPrivate ? G.context.getString(R.string.private_channel) : G.context.getString(R.string.public_channel));
        isSignedMessage.setValue(realmChannelRoom.isSignature());
        isReactionMessage.setValue(realmChannelRoom.isReactionStatus());
        String description = realmChannelRoom.getDescription();
        groupName.setValue(realmRoom.getTitle());
        linkUsername = realmChannelRoom.getUsername();
        inviteLink = realmChannelRoom.getInviteLink();
        /*isVerifiedChannel.setValue(realmChannelRoom.isVerified());*/
        /*if (isPrivate) {
            channelLink.setValue(realmChannelRoom.getInviteLink());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.channel_link));
        } else {
            channelLink.setValue(realmChannelRoom.getUsername());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.st_username));
        }*/
        /*isShowLink.setValue(!(isPrivate && ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR))));*/

        if (role == ChannelChatRole.OWNER) {
            showLayoutReactStatus.setValue(true);
            G.onChannelUpdateReactionStatus = this;
        } else {
            showLayoutReactStatus.setValue(false);
            G.onChannelUpdateReactionStatus = null;
        }

        /*try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }*/
        subscribersCount.setValue(String.valueOf(realmChannelRoom.getParticipantsCountLabel()));
        administratorsCount.setValue(String.valueOf(RealmMember.filterRole(roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size()));
        moderatorsCount.setValue(String.valueOf(RealmMember.filterRole(roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()).size()));

        if (role == ChannelChatRole.OWNER) {
            leaveChannelText.setValue(G.fragmentActivity.getString(R.string.channel_delete));
        } else {
            leaveChannelText.setValue(G.fragmentActivity.getString(R.string.channel_left));
        }

        /*if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            addMemberVisibility.set(View.GONE);
        }*/
        /*if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            menuPopupVisibility.setValue(View.VISIBLE);
        } else {
            menuPopupVisibility.setValue(View.GONE);
        }*/

        channelDescription.setValue(new SpannableStringBuilder(""));

        if (description != null && !description.isEmpty()) {
            SpannableStringBuilder spannableStringBuilder = HelperUrl.setUrlLink(description, true, false, null, true);
            if (spannableStringBuilder != null) {
                channelDescription.setValue(spannableStringBuilder);
            }
        }

        G.onChannelUpdateSignature = new OnChannelUpdateSignature() {
            @Override
            public void onChannelUpdateSignatureResponse(final long roomId, final boolean signature) {
                // handle realm to response class
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isSignedMessage.getValue() != null) {
                            if (isSignedMessage.getValue()) {
                                isSignedMessage.setValue(false);
                            } else {
                                isSignedMessage.setValue(true);
                            }
                        }
                    }
                });
            }
        };
        attachFile = new AttachFile(G.fragmentActivity);
    }


    public void chooseImage() {
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            showSelectImageDialog.setValue(true);
        } else {
            showSelectImageDialog.setValue(false);
        }
    }

    public void onEmojiClickListener() {
        if (initEmoji.getValue() != null) {
            initEmoji.setValue(!initEmoji.getValue());
        }
    }

    public void onChannelTypeClick() {
        showConvertChannelDialog.setValue(isPrivate);
    }

    public void onSingedMessageCheckedChange(boolean state) {
        if (state != isSignedMessage.getValue()) {
            if (state) {
                new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
            } else {
                new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);
            }
            isShowLoading.setValue(true);
        }
    }

    public void onReactionMessageCheckedChange(boolean state) {
        if (state != isReactionMessage.getValue()) {
            if (state) {
                new RequestChannelUpdateReactionStatus().channelUpdateReactionStatus(roomId, false);
            } else {
                new RequestChannelUpdateReactionStatus().channelUpdateReactionStatus(roomId, true);
            }
            isShowLoading.setValue(true);
        }
    }

    public void onAdministratorClick() {
        goToAdministratorPage.setValue(true);
    }

    public void onModeratorClick() {
        goToModeratorPage.setValue(true);
    }

    public void onMemberClick() {
        goToMembersPage.setValue(true);
    }

    public void onRecentActionClick() {

    }

    public void onDeleteChannelClick() {
        if (role.equals(ChannelChatRole.OWNER)) {
            showDeleteChannelDialog.setValue(true);
        } else {
            showDeleteChannelDialog.setValue(true);
        }
    }

    public void setData(String channelName, String channelDescription) {
        new RequestChannelEdit().channelEdit(roomId, channelName, channelDescription);
        isShowLoading.setValue(true);
    }

    @Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         * to download avatars . for do this action call HelperAvatar.getAvatar
         */

        isShowLoading.setValue(false);
        /*if (pathSaveImage == null) {
            setAvatar();
        } else {
            avatarHandler.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }*/
    }

    @Override
    public void onAvatarAddError() {
        isShowLoading.setValue(false);
    }

    @Override
    public void onChannelAvatarDelete(long roomId, long avatarId) {

    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }

    private Realm getRealm() {
        if (realmChannelProfile == null || realmChannelProfile.isClosed()) {
            realmChannelProfile = Realm.getDefaultInstance();
        }
        return realmChannelProfile;
    }

    @Override
    public void OnChannelUpdateReactionStatusError() {

    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
        channelType.setValue(isPrivate ? G.context.getString(R.string.private_channel) : G.context.getString(R.string.public_channel));
    }

    @Override
    public void OnChannelUpdateReactionStatusResponse(long roomId, boolean status) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (roomId == EditChannelViewModel.this.roomId) {
                    isReactionMessage.setValue(status);
                }
                isShowLoading.setValue(false);
            }
        });
    }
}