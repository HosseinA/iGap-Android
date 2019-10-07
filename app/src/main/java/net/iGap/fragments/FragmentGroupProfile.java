package net.iGap.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.ActivityGroupProfileBinding;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupCheckUsername;
import net.iGap.interfaces.OnGroupUpdateUsername;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.MEditText;
import net.iGap.proto.ProtoGroupCheckUsername;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.request.RequestGroupCheckUsername;
import net.iGap.request.RequestGroupKickAdmin;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestGroupKickModerator;
import net.iGap.request.RequestGroupUpdateUsername;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.CLIPBOARD_SERVICE;


/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */
public class FragmentGroupProfile extends BaseFragment implements OnGroupAvatarDelete {

    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private FragmentGroupProfileViewModel viewModel;
    private ActivityGroupProfileBinding binding;


    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    private String pathSaveImage;

    public static FragmentGroupProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentGroupProfile fragment = new FragmentGroupProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentGroupProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        long roomId = 0;
        boolean isNotJoin = true;
        if (getArguments() != null) {
            roomId = getArguments().getLong(ROOM_ID);
            isNotJoin = getArguments().getBoolean(IS_NOT_JOIN);
        }
        viewModel.init(this, roomId, isNotJoin);
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_group_profile, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isNeedResume = true;

        // because actionbar not in this view do that and not correct in viewModel
        imvGroupAvatar = binding.toolbarAvatar;
        imvGroupAvatar.setOnClickListener(v -> viewModel.onClickRippleGroupAvatar());

        binding.toolbarMore.setOnClickListener(v -> viewModel.onClickRippleMenu());
        binding.toolbarBack.setOnClickListener(v -> popBackStackFragment());
        binding.toolbarEdit.setOnClickListener(v -> {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), EditGroupFragment.newInstance(viewModel.roomId)).setReplace(false).load();
            }
        });

        viewModel.groupName.observe(getViewLifecycleOwner(), s -> {
            binding.toolbarTxtNameCollapsed.setText(s);
            binding.toolbarTxtNameExpanded.setText(s);
        });

        viewModel.groupNumber.observe(getViewLifecycleOwner(), s -> binding.toolbarTxtStatusExpanded.setText(String.format("%s %s", s, getString(R.string.member))));

        viewModel.showMoreMenu.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null) {
                binding.toolbarMore.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.showEditButton.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null) {
                binding.toolbarEdit.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.goToShearedMediaPage.observe(getViewLifecycleOwner(), model -> {
            if (getActivity() != null && model != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShearedMedia.newInstance(model)).setReplace(false).load();
            }
        });

        viewModel.goToShowAvatarPage.observe(getViewLifecycleOwner(), roomId -> {
            if (getActivity() != null && roomId != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group)).setReplace(false).load();
            }
        });

        viewModel.showMenu.observe(getViewLifecycleOwner(), menuList -> {
            if (getActivity() != null && menuList != null) {
                new TopSheetDialog(getActivity()).setListDataWithResourceId(menuList, -1, position -> {
                    if (menuList.get(position) == R.string.clear_history) {
                        new MaterialDialog.Builder(getActivity()).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (FragmentChat.onComplete != null) {
                                    FragmentChat.onComplete.complete(false, viewModel.roomId + "", "");
                                }
                            }
                        }).negativeText(R.string.no).show();
                    } else if (menuList.get(position) == R.string.group_title_convert_to_public || menuList.get(position) == R.string.group_title_convert_to_private) {
                        viewModel.convertMenuClick();
                    }
                }).show();
            }
        });

        viewModel.goToShowMemberPage.observe(getViewLifecycleOwner(), type -> {
            if (getActivity() != null && type != null) {
                FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), G.userId, type, viewModel.isNeedgetContactlist, true);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.showDialogConvertToPublic.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(getString(R.string.group_title_convert_to_public)).content(getString(R.string.group_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        setUsername();
                    }
                }).negativeText(R.string.no).show();
            }
        });

        viewModel.showDialogConvertToPrivate.observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(R.string.group_title_convert_to_private).content(R.string.group_text_convert_to_private).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.sendRequestRemoveGroupUsername();
                    }
                }).negativeText(R.string.no).show();
            }
        });

        viewModel.showRequestError.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(getString(errorMessage), false);
            }
        });

        viewModel.goToShowCustomListPage.observe(getViewLifecycleOwner(), listItem -> {
            if (getActivity() != null && listItem != null) {
                Fragment fragment = ShowCustomList.newInstance(listItem, (result, message, countForShowLastMessage, list) -> {
                    for (int i = 0; i < list.size(); i++) {
                        new RequestGroupAddMember().groupAddMember(viewModel.roomId, list.get(i).peerId, RealmRoomMessage.findCustomMessageId(viewModel.roomId, countForShowLastMessage));
                    }
                });

                Bundle bundle = new Bundle();
                bundle.putBoolean("DIALOG_SHOWING", true);
                bundle.putLong("COUNT_MESSAGE", 0);
                fragment.setArguments(bundle);

                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), isGoBack -> {
            if (isGoBack != null && isGoBack) {
                popBackStackFragment();
            }
        });

        binding.description.setMovementMethod(LinkMovementMethod.getInstance());

        viewModel.groupDescription.observe(getViewLifecycleOwner(), groupDescription -> {
            if (getActivity() != null && groupDescription != null) {
                binding.description.setText(HelperUrl.setUrlLink(getActivity(), groupDescription, true, false, null, true));
            }
        });

        viewModel.goToRoomListPage.observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() instanceof ActivityMain && isGo != null) {
                Log.wtf(this.getClass().getName(), "goToRoomListPage observe");
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(2);*/
            }
        });

        viewModel.showDialogCopyLink.observe(getViewLifecycleOwner(), link -> {
            if (getActivity() != null && link != null) {

                LinearLayout layoutGroupLink = new LinearLayout(getActivity());
                layoutGroupLink.setOrientation(LinearLayout.VERTICAL);
                View viewRevoke = new View(getActivity());
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                TextInputLayout inputGroupLink = new TextInputLayout(getActivity());
                MEditText edtLink = new MEditText(getActivity());
                edtLink.setHint(getString(R.string.group_link_hint_revoke));
                edtLink.setTypeface(ResourcesCompat.getFont(edtLink.getContext() , R.font.main_font));
                edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dp14));
                edtLink.setText(link);
                edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtLink.setPadding(0, 8, 0, 8);
                edtLink.setEnabled(false);
                edtLink.setSingleLine(true);
                inputGroupLink.addView(edtLink);
                inputGroupLink.addView(viewRevoke, viewParams);

                TextView txtLink = new AppCompatTextView(getActivity());
                txtLink.setText(Config.IGAP_LINK_PREFIX);
                txtLink.setTextColor(getResources().getColor(R.color.gray_6c));

                viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutGroupLink.addView(inputGroupLink, layoutParams);
                layoutGroupLink.addView(txtLink, layoutParams);

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(R.string.group_link)
                        .positiveText(R.string.array_Copy)
                        .customView(layoutGroupLink, true)
                        .widgetColor(Color.parseColor(G.appBarColor))
                        .negativeText(R.string.no)
                        .onPositive((dialog1, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("LINK_GROUP", link);
                            clipboard.setPrimaryClip(clip);
                        })
                        .build();

                dialog.show();
            }
        });

        viewModel.goToCustomNotificationPage.observe(getViewLifecycleOwner(), roomId -> {
            if (getActivity() != null && roomId != null) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentNotification).setReplace(false).load();
            }
        });

        viewModel.showDialogLeaveGroup.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                groupLeft();
            }
        });

        initComponent();

        attachFile = new AttachFile(getActivity());
        G.onGroupAvatarDelete = this;

        initialToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        showAvatar();
    }

    private void initialToolbar() {

        binding.toolbarAppbar.addOnOffsetChangedListener((appBarLayout, offset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(offset) / (float) maxScroll;

            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);
        });
        startAlphaAnimation(binding.toolbarTxtNameCollapsed, 0, View.INVISIBLE);

    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, 100, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        if (visibility == View.VISIBLE) v.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void groupLeft() {
        String text;
        int title;
        text = G.fragmentActivity.getResources().getString(R.string.do_you_want_to_leave_this_group);
        title = R.string.left_group;

        new MaterialDialog.Builder(G.fragmentActivity).title(title).content(text).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                viewModel.leaveGroup();
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).show();
    }

    /**
     * ************************************** methods **************************************
     */


    private void initComponent() {

        AppUtils.setProgresColler(binding.loading);

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                avatarHandler.avatarDelete(new ParamWithAvatarType(imvGroupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM), mAvatarId);
            }
        };

        showAvatar();
    }


    private void showAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imvGroupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.loading != null && getActivity() != null) {
                    binding.loading.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                binding.loading.setVisibility(View.GONE);
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    //TODO: change this and create custom dialog and handle request in it and ...
    //TODO: because no time be this and this code is not correct and should be change it
    public void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(getContext());
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(getContext());
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(getContext());
        final MEditText edtUserName = new MEditText(getContext());
        edtUserName.setHint(getString(R.string.group_title_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(ResourcesCompat.getFont(edtUserName.getContext() , R.font.main_font));
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.dp14));
        //TODO: fixed this and this will set viewModel
        if (viewModel.isPopup) {
            edtUserName.setText(Config.IGAP_LINK_PREFIX);
        } else {
            edtUserName.setText(Config.IGAP_LINK_PREFIX + viewModel.linkUsername);
        }

        edtUserName.setTextColor(getContext().getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getContext().getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getContext().getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getContext().getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);
        ProgressBar progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams progParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progParams);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        layoutUserName.addView(progressBar);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(getContext()).title(R.string.st_username).positiveText(R.string.save).customView(layoutUserName, true).widgetColor(Color.parseColor(G.appBarColor)).negativeText(R.string.B_cancel).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        edtUserName.setSelection((edtUserName.getText().toString().length()));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtUserName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    if (G.userLogin) {
                        String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                        new RequestGroupCheckUsername().GroupCheckUsername(viewModel.roomId, userName, new OnGroupCheckUsername() {
                            @Override
                            public void onGroupCheckUsername(final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.AVAILABLE) {

                                            positive.setEnabled(true);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError("");
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.INVALID) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError(getString(R.string.INVALID));
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.TAKEN) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError(getString(R.string.TAKEN));
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError("" + getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                if (majorCode == 5) {
                                    positive.setEnabled(false);
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError(getString(R.string.network_error));
                                } else {
                                    positive.setEnabled(false);
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError(getString(R.string.server_error));
                                }
                            }
                        });
                    } else {
                        positive.setEnabled(false);
                        inputUserName.setErrorEnabled(true);
                        inputUserName.setError(getString(R.string.network_error));
                    }
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError(getString(R.string.INVALID));
                }
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                if (G.userLogin) {
                    progressBar.setVisibility(View.VISIBLE);
                    positive.setEnabled(false);
                    new RequestGroupUpdateUsername().groupUpdateUsername(viewModel.roomId, userName, new OnGroupUpdateUsername() {
                        @Override
                        public void onGroupUpdateUsername(final long roomId, final String username) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    positive.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    viewModel.isPrivate = false;
                                    dialog.dismiss();
                                    viewModel.linkUsername = username;
                                    viewModel.setTextGroupLik();
                                }
                            });
                        }

                        @Override
                        public void onError(final int majorCode, int minorCode, final int time) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    positive.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    switch (majorCode) {
                                        case 5:
                                            HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                                        case 368:
                                            if (dialog.isShowing()) dialog.dismiss();
                                            dialogWaitTime(R.string.GROUP_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                                            break;
                                    }
                                }
                            });

                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewUserName.setBackgroundColor(getContext().getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        // check each word with server
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyboard();
            }
        });

        dialog.show();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.wtf(this.getClass().getName(), "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.wtf(this.getClass().getName(), "onDestroy");
    }

    /**
     * ************************************** Callbacks **************************************
     */

    @Override
    public void onDeleteAvatar(long roomId, long avatarId) {
        hideProgressBar();
        avatarHandler.avatarDelete(new ParamWithAvatarType(imvGroupAvatar, roomId).avatarType(AvatarHandler.AvatarType.ROOM), avatarId);
    }

    @Override
    public void onDeleteAvatarError(int majorCode, int minorCode) {

    }


    @Override
    public void onTimeOut() {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                HelperError.showSnackMessage(getString(R.string.time_out), false);
            }
        });
    }


    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    new RequestGroupKickAdmin().groupKickAdmin(viewModel.roomId, memberID);
                }
            }).show();
        }
    }

    /**
     * delete this member from list of member group
     */
    public void kickMember(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickMember().groupKickMember(viewModel.roomId, memberID);
                }
            }).show();
        }
    }

    public void kickModerator(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickModerator().groupKickModerator(viewModel.roomId, memberID);
                }
            }).show();
        }
    }
}

