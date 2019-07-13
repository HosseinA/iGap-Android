package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vanniktech.emoji.EmojiPopup;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentEditChannelBinding;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnChannelCheckUsername;
import net.iGap.interfaces.OnChannelRemoveUsername;
import net.iGap.interfaces.OnChannelUpdateUsername;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MEditText;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChannelRemoveUsername;
import net.iGap.request.RequestChannelRevokeLink;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.viewmodel.EditChannelViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditChannelFragment extends BaseFragment {

    private static final String ROOM_ID = "RoomId";

    private FragmentEditChannelBinding binding;
    private EditChannelViewModel viewModel;
    private boolean isInitEmoji = false;
    private boolean isEmojiShow = false;
    private EmojiPopup emojiPopup;

    public static EditChannelFragment newInstance(long channelId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, channelId);
        EditChannelFragment fragment = new EditChannelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_channel, container, false);
        viewModel = new EditChannelViewModel(getArguments() != null ? getArguments().getLong(ROOM_ID) : -1);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarHandler.getAvatar(new ParamWithAvatarType(binding.channelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getContext().getResources().getString(R.string.tab_edit))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.setData(binding.channelNameEditText.getEditableText().toString(), binding.groupDescriptionEditText.getEditableText().toString());
                        hideKeyboard();
                    }
                });
        binding.toolbar.addView(mHelperToolbar.getView());


        viewModel.goToMembersPage.observe(this, b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
            }
        });

        viewModel.goToAdministratorPage.observe(this, b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            }
        });

        viewModel.goToModeratorPage.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
            }
        });

        viewModel.initEmoji.observe(this, aBoolean -> {
            if (aBoolean != null) {
                if (!isInitEmoji) {
                    setUpEmojiPopup();
                    isInitEmoji = true;
                }
                emojiPopup.toggle();
            }
        });

        viewModel.showDialogLeaveGroup.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                /*groupLeft();*/
            }
        });

        viewModel.showSelectImageDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialogSelectPicture();
            }
        });
        viewModel.showConvertChannelDialog.observe(this, aBoolean -> {
            if (aBoolean != null) {
                showPopUp(aBoolean);
            }
        });
        viewModel.showDeleteChannelDialog.observe(this, aBoolean -> {
            if (aBoolean != null) {
                deleteChannel(aBoolean);
            }
        });

        viewModel.goBack.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                popBackStackFragment();
            }
        });

        viewModel.goToChatRoom.observe(this, go -> {
            if (getActivity() instanceof ActivityMain && go != null && go) {
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(3);*/
            }
        });

        viewModel.onSignClickListener.observe(this, isClicked -> {
            binding.signedMessage.setChecked(!binding.signedMessage.isChecked());
        });

        viewModel.onReactionMessageClickListener.observe(this, isClicked -> {
            binding.rateMessage.setChecked(!binding.rateMessage.isChecked());
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();

            if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
            if (FragmentEditImage.itemGalleryList != null)
                FragmentEditImage.itemGalleryList.clear();

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    if (getActivity() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image

                            FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
                        } else {
                            ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image

                            FragmentEditImage.insertItemList(AttachFile.imagePath, false);
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0)).setReplace(false).load();
                        }
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    if (getActivity() != null) {
                        ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true); //rotate image
                        FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
                    }
                    break;
            }
        }
    }

    private void startDialogSelectPicture() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.from_camera))) {
                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        try {
                            HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    // this dialog show 2 way for choose image : gallery and camera
                                    dialog.dismiss();
                                    useCamera();
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(G.fragmentActivity, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new AttachFile(G.fragmentActivity).requestOpenGalleryForImageSingleSelect(EditChannelFragment.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(EditChannelFragment.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(EditChannelFragment.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showListForCustomRole(String SelectedRole) {
        if (getActivity() != null) {
            FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), G.userId, SelectedRole, false, false);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void showPopUp(boolean isPrivate) {
        List<String> items = new ArrayList<>();
        if (isPrivate) {
            items.add(G.fragmentActivity.getString(R.string.channel_title_convert_to_public));
        } else {
            items.add(G.fragmentActivity.getString(R.string.channel_title_convert_to_private));
        }
        new BottomSheetFragment().setData(items, -1, position -> {
            if (isPrivate) {
                convertToPublic();
            } else {
                convertToPrivate();
            }
        }).show(getFragmentManager(), "bottom sheet");
    }

    private void convertToPublic() {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getString(R.string.channel_title_convert_to_public)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                setUsername();
            }
        }).negativeText(R.string.no).show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtUserName = new MEditText(G.fragmentActivity);
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.channel_title_channel_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));

        /*if (isPopup) {*/
        edtUserName.setText(Config.IGAP_LINK_PREFIX);
        /*} else {
            edtUserName.setText(Config.IGAP_LINK_PREFIX + linkUsername);
        }*/

        edtUserName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_username)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(Color.parseColor(G.appBarColor)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onChannelCheckUsername = new OnChannelCheckUsername() {
            @Override
            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {
                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        edtUserName.setSelection((edtUserName.getText().toString().length()));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtUserName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(edtUserName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().startsWith(Config.IGAP_LINK_PREFIX)) {
                    edtUserName.setText(Config.IGAP_LINK_PREFIX);
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                } else {
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }


                if (HelperString.regexCheckUsername(editable.toString().replace(Config.IGAP_LINK_PREFIX, ""))) {
                    String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                    new RequestChannelCheckUsername().channelCheckUsername(viewModel.roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                }
            }
        });


        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {
                G.handler.post(() -> {
                    viewModel.setPrivate(false);
                    dialog.dismiss();
                    viewModel.linkUsername = username;
                    /*setTextChannelLik();*/
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {
                switch (majorCode) {
                    case 457:
                        G.handler.post(() -> {
                            if (dialog.isShowing()) dialog.dismiss();
                            dialogWaitTime(R.string.limit_for_set_username, time, majorCode);
                        });
                        break;
                }
            }

            @Override
            public void onTimeOut() {

            }
        };

        positive.setOnClickListener(view -> {
            String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
            new RequestChannelUpdateUsername().channelUpdateUsername(viewModel.roomId, userName);
        });

        edtUserName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                viewUserName.setBackgroundColor(Color.parseColor(G.appBarColor));
            } else {
                viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
            }
        });
        // check each word with server
        dialog.setOnDismissListener(dialog1 -> AndroidUtils.closeKeyboard(binding.getRoot()));
        dialog.show();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialog.getCustomView();

        final TextView remindTime = v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void convertToPrivate() {
        G.onChannelRemoveUsername = new OnChannelRemoveUsername() {
            @Override
            public void onChannelRemoveUsername(final long roomId) {
                G.handler.post(() -> {
                    viewModel.setPrivate(true);
                    if (viewModel.inviteLink == null || viewModel.inviteLink.isEmpty() || viewModel.inviteLink.equals("https://")) {
                        new RequestChannelRevokeLink().channelRevokeLink(roomId);
                    } else {
                        /*setTextChannelLik();*/
                    }
                    RealmRoom.setPrivate(roomId);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_private)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_private)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelRemoveUsername().channelRemoveUsername(viewModel.roomId);
            }
        }).negativeText(R.string.no).show();
    }

    private void deleteChannel(boolean isOwner) {
        String deleteText = "";
        int title;
        if (isOwner) {
            deleteText = getString(R.string.do_you_want_delete_this_channel);
            title = R.string.channel_delete;
        } else {
            deleteText = getString(R.string.do_you_want_leave_this_channel);
            title = R.string.channel_left;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(title).content(deleteText).positiveText(R.string.yes).onPositive((dialog, which) -> {
            if (isOwner) {
                new RequestChannelDelete().channelDelete(viewModel.roomId);
            } else {
                new RequestChannelLeft().channelLeft(viewModel.roomId);
            }
            binding.loading.setVisibility(View.VISIBLE);
            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }).negativeText(R.string.no).show();
    }

    /*private void showDialog() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.show_message_count).items(R.array.numberCountGroup).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                switch (which) {
                    case 0:
                        viewModel.setChatHistoryStatus(0);
                        break;
                    case 1:
                        viewModel.setChatHistoryStatus(-1);
                        break;
                    case 2:
                        viewModel.setChatHistoryStatus(50);
                        break;
                    case 3:
                        dialog.dismiss();
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.customs).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).alwaysCallInputCallback().widgetColor(G.context.getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (dialog.getInputEditText() != null && dialog.getInputEditText().getEditableText() != null) {
                                    if (dialog.getInputEditText().getEditableText().length() < 5) {
                                        viewModel.setChatHistoryStatus(Integer.parseInt(dialog.getInputEditText().getEditableText().toString()));
                                    } else {
                                        viewModel.setChatHistoryStatus(0);
                                    }
                                } else {
                                    viewModel.setChatHistoryStatus(0);
                                }
                            }
                        }).inputType(InputType.TYPE_CLASS_NUMBER).input(G.fragmentActivity.getResources().getString(R.string.count_of_show_message), null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NotNull MaterialDialog dialog, CharSequence input) {

                            }
                        }).show();
                        break;
                }
            }
        }).show();
    }*/

    private void setUpEmojiPopup() {
        switch (G.themeColor) {
            case Theme.BLUE_GREY_COMPLETE:
            case Theme.INDIGO_COMPLETE:
            case Theme.BROWN_COMPLETE:
            case Theme.GREY_COMPLETE:
            case Theme.TEAL_COMPLETE:
            case Theme.DARK:
                setEmojiColor(G.getTheme2BackgroundColor(), G.textTitleTheme, G.textTitleTheme);
                break;
            default:
                setEmojiColor(Color.parseColor("#eceff1"), "#61000000", "#61000000");
        }
    }

    private void setEmojiColor(int BackgroundColor, String iconColor, String dividerColor) {
        emojiPopup = EmojiPopup.Builder.fromRootView(binding.root)
                .setOnEmojiBackspaceClickListener(v -> {

                }).setOnEmojiPopupShownListener(() -> isEmojiShow = true)
                .setOnSoftKeyboardOpenListener(keyBoardHeight -> {
                }).setOnEmojiPopupDismissListener(() -> isEmojiShow = false)
                .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
                .setBackgroundColor(BackgroundColor)
                .setIconColor(Color.parseColor(iconColor))
                .setDividerColor(Color.parseColor(dividerColor))
                .build(binding.channelNameEditText);
    }

    /*private void groupLeft() {
        String text = "";
        int title;
        if (viewModel.role == GroupChatRole.OWNER) {
            text = G.fragmentActivity.getResources().getString(R.string.do_you_want_to_delete_this_group);
            title = R.string.delete_group;
        } else {
            text = G.fragmentActivity.getResources().getString(R.string.do_you_want_to_leave_this_group);
            title = R.string.left_group;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(title).content(text).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                viewModel.leaveGroup();
                viewModel.showLoading.setValue(true);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).show();
    }*/
}
