/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentNotification;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.OnUserContactEdit;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.module.structs.StructMessageAttachment;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsEdit;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;

import static net.iGap.G.context;

public class ActivityContactsProfile extends ActivityEnhanced implements OnUserUpdateStatus {
    private long userId = 0;
    private long roomId;
    private String phone = "0";
    private String displayName = "";
    private String username = "";
    private String firstName;
    private String lastName;
    private long lastSeen;
    private String mPhone = "";
    private String initials;
    private String color;
    private String enterFrom;
    private String userStatus;
    private boolean isBlockUser = false;
    RealmRegisteredInfo rrg;
    private long shearedId = -2;

    TextView txtCountOfShearedMedia;

    private boolean showNumber = true;

    private AppBarLayout appBarLayout;

    private TextView txtUserName, titleToolbar, titleLastSeen, txtBlockContact, txtClearChat, txtPhoneNumber, txtNotifyAndSound;
    private EmojiTextViewE txtNickname;
    private EmojiTextViewE txtLastSeen;
    private ViewGroup vgPhoneNumber, vgSharedMedia, layoutNickname;
    private net.iGap.module.CircleImageView imgUser;
    private MaterialDesignTextView imgMenu, txtBack;

    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private PopupWindow popupWindowPhoneNumber;
    private int screenWidth;

    private String avatarPath;
    private RealmList<RealmAvatar> avatarList;


    @Override
    protected void onPause() {
        super.onPause();

        if (G.onUpdateUserStatusInChangePage != null) {
            G.onUpdateUserStatusInChangePage.updateStatus(userId, userStatus, lastSeen);
        }
    }

    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;



    @Override
    protected void onResume() {

        super.onResume();

        Realm realm = Realm.getDefaultInstance();

        mRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, shearedId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {

                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!((RealmRoom) element).isValid() || ((RealmRoom) element).isDeleted()) {
                                    return;
                                }
                                String countText = ((RealmRoom) element).getSharedMediaCount();

                                if (countText == null || countText.length() == 0) {
                                    txtCountOfShearedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
                                } else {
                                    if (HelperCalander.isLanguagePersian) {
                                        txtCountOfShearedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        txtCountOfShearedMedia.setText(countText);
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
            txtCountOfShearedMedia.setText(context.getString(R.string.there_is_no_sheared_media));
        }

        realm.close();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (rrg != null) {
            rrg.removeAllChangeListeners();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);

        Realm realm = Realm.getDefaultInstance();

        G.onUserUpdateStatus = this;

        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("peerId");
        roomId = extras.getLong("RoomId");
        enterFrom = extras.getString("enterFrom");

        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
            if (realmRoom != null) {
                shearedId = realmRoom.getId();
            }
        } else {
            shearedId = roomId;
        }

        rrg = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (rrg != null) {

            isBlockUser = rrg.isBlockUser();

            rrg.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    isBlockUser = rrg.isBlockUser();
                }
            });
        }

        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();

        if (realmRegisteredInfo != null) {
            if (realmRegisteredInfo.getLastAvatar() != null) {

                String mainFilePath = realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

                if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                    avatarPath = mainFilePath;
                } else {
                    avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
                }

                avatarList = realmRegisteredInfo.getAvatars();
            }
        }

        RealmContacts realmUser = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();

        if (realmRegisteredInfo != null) {
            phone = realmRegisteredInfo.getPhoneNumber();
            displayName = realmRegisteredInfo.getDisplayName();
            firstName = realmRegisteredInfo.getFirstName();
            lastName = realmRegisteredInfo.getLastName();
            username = realmRegisteredInfo.getUsername();
            lastSeen = realmRegisteredInfo.getLastSeen();
            color = realmRegisteredInfo.getColor();
            initials = realmRegisteredInfo.getInitials();
            userStatus = realmRegisteredInfo.getStatus();
        } else if (realmUser != null) {
            phone = Long.toString(realmUser.getPhone());
            displayName = realmUser.getDisplay_name();
            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            username = realmUser.getUsername();
            lastSeen = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
        }

        RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, Long.parseLong(phone)).findFirst();

        /**
         * if this user isn't in my contacts don't show phone number
         */
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber = false;
        }

        imgUser = (net.iGap.module.CircleImageView) findViewById(R.id.chi_img_circleImage);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();

                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment;
                    if (userId == G.userId) {
                        fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                    } else {
                        fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.chat);
                    }

                    getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.chi_layoutParent, fragment).commit();
                }
                realm.close();
            }
        });

        txtBack = (MaterialDesignTextView) findViewById(R.id.chi_txt_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.chi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.chi_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();

                    if (realmRoom != null) {
                        // ActivityChat.activityChatForFinish.finish();

                        Intent intent = new Intent(context, ActivityChat.class);
                        intent.putExtra("RoomId", realmRoom.getId());
                        //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final long roomId) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  ActivityChat.activityChatForFinish.finish();

                                        Realm realm = Realm.getDefaultInstance();
                                        Intent intent = new Intent(context, ActivityChat.class);
                                        intent.putExtra("peerId", userId);
                                        intent.putExtra("RoomId", roomId);
                                        //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        realm.close();
                                        startActivity(intent);
                                        finish();

                                        G.onChatGetRoom = null;
                                    }
                                });
                            }

                            @Override
                            public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                            }

                            @Override
                            public void onChatGetRoomTimeOut() {

                            }

                            @Override
                            public void onChatGetRoomError(int majorCode, int minorCode) {

                            }
                        };

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }
                    realm.close();
                } else {
                    finish();
                }
            }
        });

        txtNickname = (EmojiTextViewE) findViewById(R.id.chi_txt_nikName);//set nickname
        if (displayName != null && !displayName.equals("")) {
            txtNickname.setText(displayName);
        } else {
            txtNickname.setText(R.string.nick_name_not_exist);
        }

        txtLastSeen = (EmojiTextViewE) findViewById(R.id.chi_txt_lastSeen_title);
        titleToolbar = (TextView) findViewById(R.id.chi_txt_titleToolbar_DisplayName);
        titleLastSeen = (TextView) findViewById(R.id.chi_txt_titleToolbar_LastSeen);
        txtUserName = (TextView) findViewById(R.id.chi_txt_userName);
        txtPhoneNumber = (TextView) findViewById(R.id.chi_txt_phoneNumber);
        vgPhoneNumber = (ViewGroup) findViewById(R.id.chi_layout_phoneNumber);
        txtClearChat = (TextView) findViewById(R.id.chi_txt_clearChat);

        if (!showNumber) {
            vgPhoneNumber.setVisibility(View.GONE);
            txtClearChat.setVisibility(View.GONE);
        } else {
            layoutNickname = (ViewGroup) findViewById(R.id.chi_layout_nickname);
            layoutNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final LinearLayout layoutNickname = new LinearLayout(ActivityContactsProfile.this);
                    layoutNickname.setOrientation(LinearLayout.VERTICAL);

                    String splitNickname[] = txtNickname.getText().toString().split(" ");
                    String firsName = "";
                    String lastName = "";
                    StringBuilder stringBuilder = null;
                    if (splitNickname.length > 1) {

                        lastName = splitNickname[splitNickname.length - 1];
                        stringBuilder = new StringBuilder();
                        for (int i = 0; i < splitNickname.length - 1; i++) {

                            stringBuilder.append(splitNickname[i]).append(" ");
                        }
                        firsName = stringBuilder.toString();
                    } else {
                        firsName = splitNickname[0];
                    }
                    final View viewFirstName = new View(ActivityContactsProfile.this);
                    viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));

                    LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                    TextInputLayout inputFirstName = new TextInputLayout(ActivityContactsProfile.this);
                    final EmojiEditTextE edtFirstName = new EmojiEditTextE(ActivityContactsProfile.this);
                    edtFirstName.setHint(R.string.first_name);
                    edtFirstName.setText(firsName);
                    edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.dp8));
                    edtFirstName.setTextColor(getResources().getColor(R.color.text_edit_text));
                    edtFirstName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                    edtFirstName.setPadding(0, 8, 0, 8);
                    edtFirstName.setSingleLine(true);
                    inputFirstName.addView(edtFirstName);
                    inputFirstName.addView(viewFirstName, viewParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtFirstName.setBackground(getResources().getDrawable(android.R.color.transparent));
                    }

                    final View viewLastName = new View(ActivityContactsProfile.this);
                    viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));

                    TextInputLayout inputLastName = new TextInputLayout(ActivityContactsProfile.this);
                    final EditText edtLastName = new EditText(ActivityContactsProfile.this);
                    edtLastName.setHint(R.string.last_name);
                    edtLastName.setText(lastName);
                    edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.dp8));
                    edtLastName.setTextColor(getResources().getColor(R.color.text_edit_text));
                    edtLastName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                    edtLastName.setPadding(0, 8, 0, 8);
                    edtLastName.setSingleLine(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtLastName.setBackground(getResources().getDrawable(android.R.color.transparent));
                    }
                    inputLastName.addView(edtLastName);
                    inputLastName.addView(viewLastName, viewParams);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 15);
                    LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lastNameLayoutParams.setMargins(0, 15, 0, 10);

                    layoutNickname.addView(inputFirstName, layoutParams);
                    layoutNickname.addView(inputLastName, lastNameLayoutParams);

                    final MaterialDialog dialog = new MaterialDialog.Builder(ActivityContactsProfile.this).title(getResources().getString(R.string.pu_nikname_profileUser)).positiveText(getResources().getString(R.string.B_ok)).customView(layoutNickname, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

                    final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                    positive.setEnabled(false);

                    edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewFirstName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                            } else {
                                viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewLastName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                            } else {
                                viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    final String finalFirsName = firsName;
                    edtFirstName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    final String finalLastName = lastName;
                    edtLastName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!edtLastName.getText().toString().equals(finalLastName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long po = Long.parseLong(mPhone);
                            String firstName = edtFirstName.getText().toString().trim();
                            String lastName = edtLastName.getText().toString().trim();
                            new RequestUserContactsEdit().contactsEdit(po, firstName, lastName);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    G.onUserContactEdit = new OnUserContactEdit() {
                        @Override
                        public void onContactEdit(final String firstName, final String lastName, final String initials) {
                            Realm realm1 = Realm.getDefaultInstance();
                            final RealmContacts realmUser = realm1.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
                            realm1.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realmUser.setFirst_name(firstName);
                                    realmUser.setLast_name(lastName);

                                    String displayName = firstName + " " + lastName;

                                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.TYPE, ProtoGlobal.Room.Type.CHAT.toString()).findAll()) {
                                        if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == userId) {
                                            realmRoom.setTitle(displayName.trim());
                                        }
                                    }

                                    RealmContacts contact = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
                                    if (contact != null) {
                                        contact.setFirst_name(firstName);
                                        contact.setLast_name(lastName);
                                        contact.setDisplay_name(displayName.trim());
                                    }

                                    RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
                                    if (registeredInfo != null) {
                                        registeredInfo.setFirstName(firstName);
                                        registeredInfo.setLastName(lastName);
                                        registeredInfo.setDisplayName(displayName.trim());
                                        registeredInfo.setInitials(initials);
                                    }

                                    setAvatar();
                                }
                            });
                            realm1.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtNickname.setText(firstName + " " + lastName);
                                }
                            });
                        }

                        @Override
                        public void onContactEditTimeOut() {

                        }

                        @Override
                        public void onContactEditError(int majorCode, int minorCode) {

                        }
                    };
                }
            });
        }

        txtCountOfShearedMedia = (TextView) findViewById(R.id.chi_txt_count_of_sharedMedia);

        txtUserName.setText(username);
        mPhone = "" + phone;

        txtPhoneNumber.setText(mPhone);

        if (HelperCalander.isLanguagePersian) {
            txtPhoneNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtPhoneNumber.getText().toString()));
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.acp_collapsing_toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        titleToolbar.setText(displayName);

        appBarLayout = (AppBarLayout) findViewById(R.id.chi_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()

        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.chi_root_circleImage);
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleLastSeen.setVisibility(View.VISIBLE);
                    titleLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleLastSeen.setVisibility(View.GONE);
                    titleLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });

        screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        imgMenu = (MaterialDesignTextView) findViewById(R.id.chi_img_menuPopup);

        RippleView rippleMenu = (RippleView) findViewById(R.id.chi_ripple_menuPopup);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showPopUp();
            }
        });

        if (userId != 134 && G.userId != userId) {

            RippleView rippleCall = (RippleView) findViewById(R.id.chi_ripple_call);

            // gone or visible view call
            RealmCallConfig callConfig = realm.where(RealmCallConfig.class).findFirst();
            if (callConfig != null) {
                if (callConfig.isVoice_calling()) {
                    rippleCall.setVisibility(View.VISIBLE);
                    rippleCall.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                        @Override
                        public void onComplete(RippleView rippleView) {

                            FragmentCall.call(userId, false);
                        }
                    });
                } else {
                    rippleCall.setVisibility(View.GONE);
                }
            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }


        vgPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    HelperPermision.getContactPermision(ActivityContactsProfile.this, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            showPopupPhoneNumber(vgPhoneNumber, mPhone);
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //popUpMenu(R.menu.chi_popup_phone_number, v);
            }
        });

        vgSharedMedia = (ViewGroup) findViewById(R.id.chi_layout_SharedMedia);

        vgSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityContactsProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", shearedId);
                startActivity(intent);
            }
        });

        //txtBlockContact = (TextView) findViewById(R.id.chi_txt_blockContact);
        //
        //txtBlockContact.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        showAlertDialog(getString(R.string.block_this_contact), getString(R.string.block), getString(R.string.cancel));
        //    }
        //});
        //


        txtClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(getString(R.string.clear_this_chat), getString(R.string.clear), getString(R.string.cancel));
            }
        });

        txtNotifyAndSound = (TextView) findViewById(R.id.chi_txtNotifyAndSound);

        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "CONTACT");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.chi_layoutParent, fragmentNotification).commit();
            }
        });

        realm.close();
        getUserInfo(); // client should send request for get user info because need to update user online timing
        setUserStatus(userStatus, lastSeen);

        setAvatar();

        ActivityShearedMedia.getCountOfSharedMedia(shearedId);
    }

    private void setAvatar() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgUser);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgUser.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    private void setUserStatus(String userStatus, long time) {
        this.userStatus = userStatus;
        this.lastSeen = time;

        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(userId, time, false);
                titleLastSeen.setText(status);
                txtLastSeen.setText(status);
            } else {
                titleLastSeen.setText(userStatus);
                txtLastSeen.setText(userStatus);
            }

            if (HelperCalander.isLanguagePersian) {
                txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
            }
        }
    }

    private void showPopupPhoneNumber(View v, String number) {

        boolean isExist = false;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] mPhoneNumberProjection = {
                ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME
        };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null) {
                isExist = cur.moveToFirst();
            }
        } finally {
            if (cur != null) cur.close();
        }

        if (isExist) {
            new MaterialDialog.Builder(this).title(R.string.phone_number).items(R.array.phone_number2).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:
                            String call = "+" + Long.parseLong(mPhone);
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {
                                ex.getStackTrace();
                            }
                            break;
                        case 1:
                            String copy;
                            copy = mPhone;
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);
                            break;
                    }
                }
            }).show();
        } else {
            new MaterialDialog.Builder(this).title(R.string.phone_number).items(R.array.phone_number).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    switch (which) {
                        case 0:

                            String name = txtNickname.getText().toString();
                            String phone = "+" + mPhone;

                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                            //------------------------------------------------------ Names

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

                            //------------------------------------------------------ Mobile Number

                            ops.add(ContentProviderOperation.
                                    newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    .build());

                            try {
                                G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                addContactToServer();
                                Toast.makeText(G.context, R.string.save_ok, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(G.context, getString(R.string.exception) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case 1:

                            String call = "+" + Long.parseLong(mPhone);
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {

                                ex.getStackTrace();
                            }
                            break;
                        case 2:

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", mPhone);
                            clipboard.setPrimaryClip(clip);

                            break;
                    }
                }
            }).show();
        }
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {
        ArrayList<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = firstName;
        contact.lastName = lastName;
        contact.phone = phone + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImportAndGetResponse(contacts, true);
    }

    private void showPopUp() {

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityContactsProfile.this).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);
        ViewGroup root3 = (ViewGroup) v.findViewById(R.id.dialog_root_item3_notification);

        TextView txtBlockUser = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtClearHistory = (TextView) v.findViewById(R.id.dialog_text_item2_notification);
        TextView txtDeleteContact = (TextView) v.findViewById(R.id.dialog_text_item3_notification);

        TextView iconBlockUser = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);

        TextView iconClearHistory = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconClearHistory.setText(getResources().getString(R.string.md_clearHistory));

        TextView iconDeleteContact = (TextView) v.findViewById(R.id.dialog_icon_item3_notification);
        iconDeleteContact.setText(getResources().getString(R.string.md_rubbish_delete_file));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);
        root3.setVisibility(View.VISIBLE);
        if (G.userId == userId) {
            root1.setVisibility(View.GONE);
            root3.setVisibility(View.GONE);
        }

        if (isBlockUser) {
            txtBlockUser.setText(getString(R.string.un_block_user));
            iconBlockUser.setText(getResources().getString(R.string.md_unblock));
        } else {
            txtBlockUser.setText(getString(R.string.block_user));
            iconBlockUser.setText(getResources().getString(R.string.md_block));
        }
        txtClearHistory.setText(getResources().getString(R.string.clear_history));
        txtDeleteContact.setText(getResources().getString(R.string.delete_contact));

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                blockOrUnblockUser();
            }
        });
        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MaterialDialog.Builder(ActivityContactsProfile.this).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (ActivityChat.onComplete != null) {
                            ActivityChat.onComplete.complete(false, roomId + "", "");
                        }
                    }
                }).negativeText(R.string.B_cancel).show();

            }
        });
        root3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                new MaterialDialog.Builder(ActivityContactsProfile.this).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        deleteContact();
                    }
                }).negativeText(R.string.B_cancel).show();

            }
        });
    }

    private void blockOrUnblockUser() {

        if (isBlockUser) {

            new RequestUserContactsUnblock().userContactsUnblock(userId);
        } else {

            new RequestUserContactsBlock().userContactsBlock(userId);
        }
    }

    private void showAlertDialog(String message, String positive, String negitive) { // alert dialog for block or clear user

        new MaterialDialog.Builder(ActivityContactsProfile.this).title(R.string.clear_history).content(message).positiveText(positive).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                clearHistory();
            }
        }).negativeText(negitive).show();
    }

    public ArrayList<StructMessageInfo> setItem() {
        ArrayList<StructMessageInfo> items = new ArrayList<>();

        ArrayList<String> currentTokenAdded = new ArrayList<>();

        for (int i = 0; i < avatarList.size(); i++) {
            if (avatarList.get(i).getFile() != null) {
                StructMessageInfo item = new StructMessageInfo();
                RealmAvatar avatar = avatarList.get(i);
                if (!currentTokenAdded.contains(avatar.getFile().getToken())) {
                    currentTokenAdded.add(avatar.getFile().getToken());
                    item.attachment = new StructMessageAttachment(avatarList.get(i).getFile());
                    items.add(item);
                }
            }
        }
        return items;
    }

    private void clearHistory() {
        RealmRoomMessage.clearHistoryMessage(shearedId);
    }

    private void deleteContact() {
        G.onUserContactdelete = new OnUserContactDelete() {
            @Override
            public void onContactDelete() {
                /**
                 * get user info after delete it for show nickname
                 */
                getUserInfo();
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
        new RequestUserContactsDelete().contactsDelete(phone);
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtNickname.setText(user.getDisplayName());
                    }
                });
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        new RequestUserInfo().userInfo(userId);
    }


    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {

        if (this.userId == userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUserStatus(AppUtils.getStatsForUser(status), time);
                }
            });
        }
    }
}


