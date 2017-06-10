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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vicmikhailau.maskededittext.MaskedEditText;
import io.realm.Realm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterDialog;
import net.iGap.fragments.FragmentSecurityRecovery;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnSmsReceive;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.module.AppUtils;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.IncomingSms;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.enums.Security;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationVerifyPassword;
import net.iGap.request.RequestWrapper;

public class ActivityRegister extends ActivityEnhanced implements OnSecurityCheckPassword, OnRecoverySecurityPassword {

    static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;
    public static MaskedEditText edtPhoneNumber;
    public static String isoCode = "IR";
    public static TextView btnOk;
    public static Dialog dialogChooseCountry;
    public static int positionRadioButton = -1;
    ArrayList<StructCountry> structCountryArrayList = new ArrayList();
    private SoftKeyboard softKeyboard;
    private Button btnStart;
    private TextView txtAgreement_register, txtTitleToolbar, txtTitleRegister, txtDesc;
    private ProgressBar rg_prg_verify_connect, rg_prg_verify_sms, rg_prg_verify_generate, rg_prg_verify_register;
    private TextView rg_txt_verify_connect, rg_txt_verify_sms, rg_txt_verify_generate, rg_txt_verify_register, txtTimer;
    private ImageView rg_img_verify_connect, rg_img_verify_sms, rg_img_verify_generate, rg_img_verify_register;
    private ViewGroup layout_verify;
    private String phoneNumber;
    //Array List for Store List of StructCountry Object
    private String regex;
    private String userName;
    private String authorHash;
    private String token;
    private String regexFetchCodeVerification;
    private long userId;
    private boolean newUser;
    private ArrayList<StructCountry> items = new ArrayList<>();
    private AdapterDialog adapterDialog;
    private Dialog dialogVerifyLandScape;
    private IncomingSms smsReceiver;
    private CountDownTimer countDownTimer;
    private SearchView edtSearchView;
    private Dialog dialog;
    private int digitCount;
    private MaterialDialog dialogWait;
    private Typeface titleTypeface;
    private TextView txtTimerLand;
    private String verifyCode;
    private boolean isRecoveryByEmail = false;
    private EditText editCheckPassword;
    private TextView txtRecovery;
    private TextView txtOk;
    private ViewGroup vgMainLayout;
    private ViewGroup vgCheckPassword;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPasswordHint = "";
    private boolean hasConfirmedRecoveryEmail;
    private String unconfirmedEmailPattern;


    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(final String phoneNumber, final String message) {
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        rg_txt_verify_sms.setText(message);
                        receiveVerifySms(message);
                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        try {
            HelperPermision.getSmsPermision(ActivityRegister.this, new OnGetPermission() {
                @Override
                public void Allow() {
                    registerReceiver(smsReceiver, filter);
                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        G.onSecurityCheckPassword = this;
        G.onRecoverySecurityPassword = this;

        edtCodeNumber = (EditText) findViewById(R.id.rg_edt_CodeNumber);
        btnChoseCountry = (Button) findViewById(R.id.rg_btn_choseCountry);
        edtPhoneNumber = (MaskedEditText) findViewById(R.id.rg_edt_PhoneNumber);
        txtAgreement_register = (TextView) findViewById(R.id.txtAgreement_register);

        findViewById(R.id.ar_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));


        int portrait = getResources().getConfiguration().orientation;
        if (portrait == 1) {

            txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
        }
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            edtCodeNumber.setText(savedInstanceState.getString(KEY_SAVE_CODENUMBER));
            edtPhoneNumber.setMask(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_MASK));
            edtPhoneNumber.setText(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_NUMBER));
            btnChoseCountry.setText(savedInstanceState.getString(KEY_SAVE_NAMECOUNTRY));
            txtAgreement_register.setText(savedInstanceState.getString(KEY_SAVE_AGREEMENT));
            regex = (savedInstanceState.getString(KEY_SAVE_REGEX));
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                isoCode = extras.getString("ISO_CODE");
                edtCodeNumber.setText("+" + extras.getInt("CALLING_CODE"));
                btnChoseCountry.setText(extras.getString("COUNTRY_NAME"));
                String pattern = extras.getString("PATTERN");
                if (!pattern.equals("")) {
                    edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
                }
                regex = extras.getString("REGEX");
                String body = extras.getString("TERMS_BODY");
                if (body != null & txtAgreement_register != null) { //TODO [Saeed Mozaffari] [2016-09-01 9:28 AM] - txtAgreement_register !=null is wrong. change it
                    txtAgreement_register.setText(Html.fromHtml(body));
                }
            }
        }
        int getHeight = G.context.getResources().getDisplayMetrics().heightPixels;

        txtTitleRegister = (TextView) findViewById(R.id.rg_txt_title_register);
        txtDesc = (TextView) findViewById(R.id.rg_txt_text_descRegister);

        txtTitleToolbar = (TextView) findViewById(R.id.rg_txt_titleToolbar);

        if (!HelperCalander.isLanguagePersian) {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        } else {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }

        txtTitleToolbar.setTypeface(titleTypeface);

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("0")) {
                    Toast.makeText(ActivityRegister.this, getResources().getString(R.string.Toast_First_0), Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setText("");
                }
            }
        });

        layout_verify = (ViewGroup) findViewById(R.id.rg_layout_verify_and_agreement);

        /**
         * list of country
         */

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", this);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String listArray[] = list.split("\\r?\\n");
        final String countryNameList[] = new String[listArray.length];
        //Convert array
        for (int i = 0; listArray.length > i; i++) {
            StructCountry structCountry = new StructCountry();

            String listItem[] = listArray[i].split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        for (int i = 0; i < structCountryArrayList.size(); i++) {

            countryNameList[i] = structCountryArrayList.get(i).getName();
            StructCountry item = new StructCountry();
            item.setId(i);
            item.setName(structCountryArrayList.get(i).getName());
            item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
            item.setPhonePattern(structCountryArrayList.get(i).getPhonePattern());
            item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
            items.add(item);
        }

        btnChoseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChooseCountry = new Dialog(ActivityRegister.this);
                dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogChooseCountry.setContentView(R.layout.rg_dialog);

                int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
                dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
                //
                final TextView txtTitle = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
                edtSearchView = (SearchView) dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

                txtTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        edtSearchView.setIconified(false);
                        edtSearchView.setIconifiedByDefault(true);
                        txtTitle.setVisibility(View.GONE);
                    }
                });

                edtSearchView.setOnCloseListener(new SearchView.OnCloseListener() { // close SearchView and show title again
                    @Override
                    public boolean onClose() {

                        txtTitle.setVisibility(View.VISIBLE);

                        return false;
                    }
                });

                final ViewGroup root = (ViewGroup) dialogChooseCountry.findViewById(android.R.id.content);
                InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
                softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                    @Override
                    public void onSoftKeyboardHide() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (edtSearchView.getQuery().toString().length() > 0) {
                                    edtSearchView.setIconified(false);
                                    edtSearchView.clearFocus();
                                    txtTitle.setVisibility(View.GONE);
                                } else {
                                    edtSearchView.setIconified(true);
                                    txtTitle.setVisibility(View.VISIBLE);
                                }
                                adapterDialog.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onSoftKeyboardShow() {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                txtTitle.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                final ListView listView = (ListView) dialogChooseCountry.findViewById(R.id.lstContent);
                adapterDialog = new AdapterDialog(ActivityRegister.this, items);
                listView.setAdapter(adapterDialog);

                final View border = (View) dialogChooseCountry.findViewById(R.id.rg_borderButton);
                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {

                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                        if (i > 0) {
                            border.setVisibility(View.VISIBLE);
                        } else {
                            border.setVisibility(View.GONE);
                        }
                    }
                });

                AdapterDialog.mSelectedVariation = positionRadioButton;

                adapterDialog.notifyDataSetChanged();

                edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        adapterDialog.getFilter().filter(s);
                        return false;
                    }
                });

                btnOk = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_okDialog);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        G.onInfoCountryResponse = new OnInfoCountryResponse() {
                            @Override
                            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtCodeNumber.setText("+" + callingCode);
                                        edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
                                        regex = regexR;
                                        btnStart.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                                        btnStart.setEnabled(true);
                                    }
                                });
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                //empty
                            }
                        };

                        new RequestInfoCountry().infoCountry(isoCode);

                        edtPhoneNumber.setText("");
                        dialogChooseCountry.dismiss();
                    }
                });

                dialogChooseCountry.show();
            }
        });

        //=============================================================================================================== click button for start verify

        final Animation trans_x_in = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_in);
        final Animation trans_x_out = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_out);
        btnStart = (Button) findViewById(R.id.rg_btn_start); //check phone and internet connection
        btnStart.setBackgroundColor(Color.parseColor(G.appBarColor));
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPhoneNumber.getText().length() > 0 && !regex.equals("") && edtPhoneNumber.getText().toString().replace("-", "").matches(regex)) {

                    phoneNumber = edtPhoneNumber.getText().toString();

                    MaterialDialog dialog = new MaterialDialog.Builder(ActivityRegister.this).customView(R.layout.rg_mdialog_text, true).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_edit)).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            int portaret_landscope = getResources().getConfiguration().orientation;

                            if (portaret_landscope == 1) {//portrait
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());

                                txtAgreement_register.setVisibility(View.GONE);
                                txtAgreement_register.startAnimation(trans_x_out);
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        btnStart.setBackgroundColor(getResources().getColor(R.color.rg_background_verify));
                                        btnStart.setTextColor(getResources().getColor(R.color.rg_border_editText));
                                        btnChoseCountry.setEnabled(false);
                                        btnChoseCountry.setTextColor(getResources().getColor(R.color.rg_border_editText));
                                        edtPhoneNumber.setEnabled(false);
                                        edtPhoneNumber.setTextColor(getResources().getColor(R.color.rg_border_editText));

                                        edtCodeNumber.setEnabled(false);
                                        edtCodeNumber.setTextColor(getResources().getColor(R.color.rg_border_editText));

                                        layout_verify.setVisibility(View.VISIBLE);
                                        layout_verify.startAnimation(trans_x_in);

                                        checkVerify();
                                    }
                                }, 600);
                            } else {

                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                                dialogVerifyLandScape = new Dialog(ActivityRegister.this);

                                btnStart.setBackgroundColor(getResources().getColor(R.color.rg_border_editText));
                                btnChoseCountry.setTextColor(getResources().getColor(R.color.rg_border_editText));
                                btnChoseCountry.setEnabled(false);
                                edtPhoneNumber.setTextColor(getResources().getColor(R.color.rg_border_editText));
                                edtPhoneNumber.setEnabled(false);

                                dialogVerifyLandScape.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialogVerifyLandScape.setContentView(R.layout.rg_dialog_verify_land);
                                dialogVerifyLandScape.setCanceledOnTouchOutside(false);
                                dialogVerifyLandScape.show();

                                checkVerify();
                            }
                        }
                    }).build();

                    View view = dialog.getCustomView();
                    assert view != null;
                    TextView phone = (TextView) view.findViewById(R.id.rg_dialog_txt_number);
                    phone.setText(edtCodeNumber.getText().toString() + "" + edtPhoneNumber.getText().toString());
                    dialog.show();
                } else {

                    if (regex.equals("")) {
                        new MaterialDialog.Builder(ActivityRegister.this).title(R.string.phone_number).content("regex.equals(\"\")").positiveText(R.string.B_ok).show();
                    } else if (edtPhoneNumber.getText().toString().replace("-", "").matches(regex)) {
                        new MaterialDialog.Builder(ActivityRegister.this).title(R.string.phone_number).content("matches(regex)").positiveText(R.string.B_ok).show();
                    } else {

                        new MaterialDialog.Builder(ActivityRegister.this).title(R.string.phone_number).content(R.string.Toast_Enter_Phone_Number).positiveText(R.string.B_ok).show();
                    }
                }
            }
        });
        // enable scroll text view

        int portrait_landscape = getResources().getConfiguration().orientation;
        if (portrait_landscape == 1) {//portrait

            if (getHeight > 480) {

                int marginLeft = (int) getResources().getDimension(R.dimen.dp32);
                int marginRight = (int) getResources().getDimension(R.dimen.dp32);
                int marginTopStart = (int) getResources().getDimension(R.dimen.dp20);
                int marginTopChooseCountry = 0;
                int marginBottomChooseCountry = (int) getResources().getDimension(R.dimen.dp8);
                int marginBottomStart = 0;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnChoseCountry.getLayoutParams();
                params.setMargins(marginLeft, marginTopChooseCountry, marginRight, marginBottomChooseCountry); //left, top, right, bottom
                btnChoseCountry.setLayoutParams(params);
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) btnStart.getLayoutParams();
                params2.setMargins(marginLeft, marginTopStart, marginRight, marginBottomStart); //left, top, right, bottom
                btnStart.setLayoutParams(params2);
            }
        }
    }

    //======= process verify : check internet and sms
    private void checkVerify() {

        setItem(); // invoke object

        rg_prg_verify_connect.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (G.socketConnection) { //connection ok
                    //                        if (checkInternet()) { //connection ok
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userRegister();
                            btnStart.setEnabled(false);
                            long time = 0;
                            if (BuildConfig.DEBUG) {
                                time = 10 * DateUtils.SECOND_IN_MILLIS;
                            } else {
                                time = Config.COUNTER_TIMER;
                            }

                            int portrait_landscape = getResources().getConfiguration().orientation;
                            if (portrait_landscape == 1) {//portrait
                                txtTimer = (TextView) findViewById(R.id.rg_txt_verify_timer);
                            } else {
                                txtTimerLand = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_timer_DialogLand);
                            }

                            countDownTimer = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) { // wait for verify sms
                                public void onTick(long millisUntilFinished) {

                                    int seconds = (int) ((millisUntilFinished) / 1000);
                                    int minutes = seconds / 60;
                                    seconds = seconds % 60;
                                    int portrait_landscape = getResources().getConfiguration().orientation;
                                    if (portrait_landscape == 1) {//portrait
                                        if (txtTimer != null) {
                                            txtTimer.setVisibility(View.VISIBLE);
                                            txtTimer.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                        }
                                    } else {
                                        if (txtTimerLand != null) {
                                            txtTimerLand.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                        }
                                    }
                                }

                                public void onFinish() {
                                    int portrait_landscape = getResources().getConfiguration().orientation;
                                    if (portrait_landscape == 1) {//portrait
                                        if (txtTimer != null) {
                                            txtTimer.setText("00:00");
                                            txtTimer.setVisibility(View.INVISIBLE);
                                        }
                                    } else {
                                        if (txtTimerLand != null) {
                                            txtTimerLand.setText("00:00");
                                            txtTimerLand.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    errorVerifySms(Reason.TIME_OUT); // open rg_dialog for enter sms code
                                }
                            };
                        }
                    });
                } else { // connection error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edtPhoneNumber.setEnabled(true);
                            rg_prg_verify_connect.setVisibility(View.GONE);
                            rg_img_verify_connect.setImageResource(R.mipmap.alert);
                            rg_img_verify_connect.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
                            rg_img_verify_connect.setVisibility(View.VISIBLE);
                            rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_error_red));
                            rg_txt_verify_connect.setText(R.string.please_check_your_connenction);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void setItem() { //invoke object

        int portrait_landscape = getResources().getConfiguration().orientation; //check for portrait & landScape
        if (portrait_landscape == 1) {//portrait
            rg_prg_verify_connect = (ProgressBar) findViewById(R.id.rg_prg_verify_connect);
            AppUtils.setProgresColler(rg_prg_verify_connect);

            rg_txt_verify_connect = (TextView) findViewById(R.id.rg_txt_verify_connect);
            rg_img_verify_connect = (ImageView) findViewById(R.id.rg_img_verify_connect);

            rg_prg_verify_sms = (ProgressBar) findViewById(R.id.rg_prg_verify_sms);
            AppUtils.setProgresColler(rg_prg_verify_sms);

            rg_txt_verify_sms = (TextView) findViewById(R.id.rg_txt_verify_sms);
            rg_img_verify_sms = (ImageView) findViewById(R.id.rg_img_verify_sms);

            rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key);
            AppUtils.setProgresColler(rg_prg_verify_generate);

            rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key);
            rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key);

            rg_prg_verify_register = (ProgressBar) findViewById(R.id.rg_prg_verify_server);
            AppUtils.setProgresColler(rg_prg_verify_register);
            rg_txt_verify_register = (TextView) findViewById(R.id.rg_txt_verify_server);
            rg_img_verify_register = (ImageView) findViewById(R.id.rg_img_verify_server);
        } else {
            rg_prg_verify_connect = (ProgressBar) dialogVerifyLandScape.findViewById(R.id.rg_prg_verify_connect_DialogLand);
            AppUtils.setProgresColler(rg_prg_verify_connect);
            rg_txt_verify_connect = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_connect_DialogLand);
            rg_img_verify_connect = (ImageView) dialogVerifyLandScape.findViewById(R.id.rg_img_verify_connect_DialogLand);

            rg_prg_verify_sms = (ProgressBar) dialogVerifyLandScape.findViewById(R.id.rg_prg_verify_sms_DialogLand);
            AppUtils.setProgresColler(rg_prg_verify_sms);

            rg_txt_verify_sms = (TextView) dialogVerifyLandScape.findViewById(R.id.rg_txt_verify_sms_DialogLand);
            rg_img_verify_sms = (ImageView) dialogVerifyLandScape.findViewById(R.id.rg_img_verify_sms_DialogLand);

            rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key_DialogLand);
            AppUtils.setProgresColler(rg_prg_verify_generate);

            rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key_DialogLand);
            rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key_DialogLand);

            rg_prg_verify_register = (ProgressBar) findViewById(R.id.rg_prg_verify_server_DialogLand);
            AppUtils.setProgresColler(rg_prg_verify_register);
            rg_txt_verify_register = (TextView) findViewById(R.id.rg_txt_verify_server_DialogLand);
            rg_img_verify_register = (ImageView) findViewById(R.id.rg_img_verify_server_DialogLand);
        }
    }

    // error verify sms and open rg_dialog for enter sms code
    private void errorVerifySms(Reason reason) { //when don't receive sms and open rg_dialog for enter code

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setImageResource(R.mipmap.alert);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
        rg_txt_verify_sms.setText(R.string.errore_verification_sms);
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_error_red));

        dialog = new Dialog(ActivityRegister.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rg_dialog_verify_code);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edtEnterCodeVerify = (EditText) dialog.findViewById(R.id.rg_edt_dialog_verifyCode); //EditText For Enter sms cod

        TextView txtShowReason = (TextView) dialog.findViewById(R.id.txt_show_reason);

        if (reason == Reason.SOCKET) {
            txtShowReason.setText(getResources().getString(R.string.verify_socket_message));
        } else if (reason == Reason.TIME_OUT) {
            txtShowReason.setText(getResources().getString(R.string.verify_time_out_message));
        } else if (reason == Reason.INVALID_CODE) {
            txtShowReason.setText(getResources().getString(R.string.verify_invalid_code_message));
        }

        TextView btnCancel = (TextView) dialog.findViewById(R.id.rg_btn_cancelVerifyCode);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int portrait_landscape = getResources().getConfiguration().orientation;
                if (portrait_landscape == 1) {//portrait
                    txtTimer.setVisibility(View.INVISIBLE);
                } else {
                    txtTimerLand.setVisibility(View.INVISIBLE);
                }
                if (!edtEnterCodeVerify.getText().toString().equals("")) {
                    verifyCode = edtEnterCodeVerify.getText().toString();
                    userVerify(userName, verifyCode);
                    dialog.dismiss();
                } else {

                    new MaterialDialog.Builder(ActivityRegister.this).title(R.string.Enter_Code).content(R.string.Toast_Enter_Code).positiveText(R.string.B_ok).show();
                }
            }
        });

        TextView btnOk = (TextView) dialog.findViewById(R.id.rg_btn_dialog_okVerifyCode);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (!isFinishing()) {
            dialog.show();
            if (dialog.isShowing()) {
                countDownTimer.cancel();
            }
        }
    }

    private void userRegister() {

        G.onUserRegistration = new OnUserRegistration() {

            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR) {
                digitCount = verifyCodeDigitCount;
                countDownTimer.start();
                regexFetchCodeVerification = regex;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int portrait_landscape = getResources().getConfiguration().orientation;
                        if (portrait_landscape == 1) {//portrait
                            txtTimer.setVisibility(View.VISIBLE);
                        } else {
                            txtTimerLand.setVisibility(View.VISIBLE);
                        }
                        userName = userNameR;
                        userId = userIdR;
                        authorHash = authorHashR;
                        G.smsNumbers = smsNumbersR;

                        if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {
                            errorVerifySms(Reason.SOCKET);
                            countDownTimer.cancel();
                        }

                        rg_prg_verify_connect.setVisibility(View.GONE);
                        rg_img_verify_connect.setVisibility(View.VISIBLE);
                        if (rg_img_verify_sms != null) rg_img_verify_sms.setVisibility(View.GONE);
                        rg_txt_verify_connect.setTextAppearance(G.context, R.style.RedHUGEText);
                        rg_txt_verify_connect.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        rg_prg_verify_sms.setVisibility(View.VISIBLE);
                        rg_txt_verify_sms.setTextAppearance(G.context, R.style.RedHUGEText);
                    }
                });
            }

            @Override
            public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                final long time = getWait;
                if (majorCode == 100 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid countryCode
                            requestRegister();
                        }
                    });
                } else if (majorCode == 100 && minorCode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                            requestRegister();
                        }
                    });
                } else if (majorCode == 101) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                            requestRegister();
                        }
                    });
                } else if (majorCode == 135) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MaterialDialog.Builder(ActivityRegister.this).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 136) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);
                        }
                    });
                } else if (majorCode == 137) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode);
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) { // timeout
                    requestRegister();
                }
            }
        };

        requestRegister();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        dialogWait = new MaterialDialog.Builder(ActivityRegister.this).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                btnStart.setBackgroundColor(getResources().getColor(R.color.green));
                btnStart.setTextColor(getResources().getColor(R.color.white));
                btnStart.setEnabled(true);
                btnChoseCountry.setEnabled(true);
                btnChoseCountry.setTextColor(getResources().getColor(R.color.black_register));
                edtPhoneNumber.setEnabled(true);
                edtPhoneNumber.setTextColor(getResources().getColor(R.color.black_register));
                edtCodeNumber.setTextColor(getResources().getColor(R.color.black_register));
                txtAgreement_register.setVisibility(View.VISIBLE);
                layout_verify.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void requestRegister() {

        if (G.socketConnection) {
            phoneNumber = phoneNumber.replace("-", "");
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setCountryCode(isoCode);
            builder.setPhoneNumber(Long.parseLong(phoneNumber));
            builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
            RequestWrapper requestWrapper = new RequestWrapper(100, builder);

            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestRegister();
                }
            }, 1000);
        }
    }

    /**
     * if the connection is established do verify otherwise start registration(step one) again
     */
    private void userVerify(final String userName, final String verificationCode) {
        if (G.socketConnection) {
            int portrait_landscape = getResources().getConfiguration().orientation;
            if (portrait_landscape == 1) {//portrait
                rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key);
                AppUtils.setProgresColler(rg_prg_verify_generate);

                rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key);
                rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key);
            } else {
                rg_prg_verify_generate = (ProgressBar) findViewById(R.id.rg_prg_verify_key_DialogLand);
                AppUtils.setProgresColler(rg_prg_verify_generate);
                rg_txt_verify_generate = (TextView) findViewById(R.id.rg_txt_verify_key_DialogLand);
                rg_img_verify_generate = (ImageView) findViewById(R.id.rg_img_verify_key_DialogLand);
            }

            if (rg_prg_verify_generate != null) rg_prg_verify_generate.setVisibility(View.VISIBLE);
            if (rg_txt_verify_generate != null) rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);

            userVerifyResponse(verificationCode);
            ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
            userVerify.setCode(Integer.parseInt(verificationCode));
            userVerify.setUsername(userName);

            RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            /**
             * return view for step one and two because now start registration again from step one
             */

            // return step one
            rg_prg_verify_connect.setVisibility(View.VISIBLE);
            rg_img_verify_connect.setVisibility(View.GONE);
            rg_txt_verify_connect.setTextAppearance(G.context, Typeface.NORMAL);
            // clear step two
            rg_prg_verify_sms.setVisibility(View.GONE);
            rg_img_verify_sms.setVisibility(View.INVISIBLE);
            rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));
            rg_txt_verify_sms.setTextAppearance(G.context, Typeface.NORMAL);

            requestRegister();
        }
    }

    private void userVerifyResponse(final String verificationCode) {
        G.onUserVerification = new OnUserVerification() {
            @Override
            public void onUserVerify(final String tokenR, final boolean newUserR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rg_txt_verify_sms.setText(getString(R.string.your_login_code_is) + verificationCode);
                        rg_prg_verify_sms.setVisibility(View.GONE);
                        rg_img_verify_sms.setVisibility(View.VISIBLE);
                        rg_img_verify_sms.setImageResource(R.mipmap.check);
                        rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_text_verify), PorterDuff.Mode.SRC_ATOP);
                        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        newUser = newUserR;
                        token = tokenR;

                        if (rg_prg_verify_generate != null) {
                            rg_prg_verify_generate.setVisibility(View.GONE);
                            rg_img_verify_generate.setVisibility(View.VISIBLE);
                            rg_txt_verify_generate.setTextColor(getResources().getColor(R.color.rg_text_verify));
                        }
                        userLogin(token);
                    }
                });
            }

            @Override
            public void onUserVerifyError(final int majorCode, int minorCode, final int time) {

                if (majorCode == 184 && minorCode == 1) {

                    checkPassword(verificationCode);

                } else if (majorCode == 102 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorVerifySms(Reason.INVALID_CODE);
                        }
                    });
                } else if (majorCode == 102 && minorCode == 2) {
                    //empty
                } else if (majorCode == 103) {
                    //empty
                } else if (majorCode == 104) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // There is no registered user with given username
                            new MaterialDialog.Builder(ActivityRegister.this).title(R.string.USER_VERIFY_GIVEN_USERNAME).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 105) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // User is blocked , You cannot verify the user

                            new MaterialDialog.Builder(ActivityRegister.this).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 106) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is invalid
                            errorVerifySms(Reason.INVALID_CODE);
                        }
                    });
                } else if (majorCode == 107) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is expired
                            new MaterialDialog.Builder(ActivityRegister.this).title(R.string.USER_VERIFY_EXPIRED).content(R.string.Toast_Number_Block).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            }).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 108) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is locked for a while due to too many tries

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    userVerify(userName, verifyCode);
                }
            }
        };
    }

    private void checkPassword(final String verificationCode) {
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        G.handler.post(new Runnable() {
            @Override
            public void run() {


                rg_txt_verify_sms.setText((getResources().getString(R.string.your_login_code_is)) + "" + verificationCode);
                rg_prg_verify_sms.setVisibility(View.GONE);
                rg_img_verify_sms.setVisibility(View.VISIBLE);
                rg_img_verify_sms.setImageResource(R.mipmap.check);
                rg_img_verify_sms.setColorFilter(getResources().getColor(R.color.rg_text_verify), PorterDuff.Mode.SRC_ATOP);
                rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));

                //newUser = newUserR;
                //token = tokenR;
                if (rg_prg_verify_generate != null) {
                    rg_prg_verify_generate.setVisibility(View.GONE);
                    rg_img_verify_generate.setVisibility(View.VISIBLE);
                    rg_txt_verify_generate.setTextColor(getResources().getColor(R.color.rg_text_verify));
                }
                //userLogin(token);

                vgMainLayout = (ViewGroup) findViewById(R.id.rg_rootMainLayout);
                vgMainLayout.setVisibility(View.GONE);
                vgCheckPassword = (ViewGroup) findViewById(R.id.rg_rootCheckPassword);
                vgCheckPassword.setVisibility(View.VISIBLE);
                editCheckPassword = (EditText) findViewById(R.id.rg_edtCheckPassword);
                txtRecovery = (TextView) findViewById(R.id.rg_txtForgotPassword);
                txtOk = (TextView) findViewById(R.id.rg_txtOk);
                txtOk.setVisibility(View.VISIBLE);
                txtRecovery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(ActivityRegister.this).title(R.string.set_recovery_question).items(R.array.securityRecoveryPassword).itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        isRecoveryByEmail = true;
                                        break;
                                    case 1:
                                        isRecoveryByEmail = false;
                                        break;
                                }

                                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("PAGE", Security.REGISTER);
                                bundle.putString("QUESTION_ONE", securityPasswordQuestionOne);
                                bundle.putString("QUESTION_TWO", securityPasswordQuestionTwo);
                                bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);
                                fragmentSecurityRecovery.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.rg_rootActivityRegister, fragmentSecurityRecovery).commit();
                            }
                        }).show();
                    }
                });
                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editCheckPassword.length() > 0) {
                            new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(editCheckPassword.getText().toString());
                        } else {
                            error(getString(R.string.please_enter_code));
                        }
                    }
                });
            }
        });
    }

    private void userLogin(final String token) {
        if (rg_prg_verify_register != null) rg_prg_verify_register.setVisibility(View.VISIBLE);
        if (rg_txt_verify_register != null) rg_txt_verify_register.setTextAppearance(G.context, R.style.RedHUGEText);
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                //RealmUserInfo userInfo = realm.where(RealmUserInfo.class).equalTo(RealmUserInfoFields.USER_INFO.ID, userId).findFirst();
                                RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                                if (userInfo == null) {
                                    userInfo = realm.createObject(RealmUserInfo.class);
                                    RealmRegisteredInfo registeredInfo = realm.createObject(RealmRegisteredInfo.class, userId);
                                    userInfo.setUserInfo(registeredInfo);
                                }
                                userInfo.getUserInfo().setUsername(userName);
                                userInfo.getUserInfo().setPhoneNumber(phoneNumber);
                                userInfo.setToken(token);
                                userInfo.setAuthorHash(authorHash);
                                userInfo.setUserRegistrationState(true);
                            }
                        });

                        if (rg_prg_verify_register != null) rg_prg_verify_register.setVisibility(View.GONE);
                        if (rg_img_verify_register != null) rg_img_verify_register.setVisibility(View.VISIBLE);
                        if (rg_txt_verify_register != null) rg_txt_verify_register.setTextColor(getResources().getColor(R.color.rg_text_verify));

                        if (newUser) {
                            Intent intent = new Intent(G.context, ActivityProfile.class);
                            intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                            startActivity(intent);
                            finish();
                        } else {
                            // get user info for set nick name and after from that go to ActivityMain
                            getUserInfo();
                            requestUserInfo();
                        }
                        realm.close();
                    }
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            HelperLogout.logout();
                        }
                    });
                } else if (majorCode == 111) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestLogin();
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    requestLogin();
                }
            }
        };

        requestLogin();
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        realmUserInfo.getUserInfo().setDisplayName(user.getDisplayName());
                        G.displayName = user.getDisplayName();

                        realmUserInfo.getUserInfo().setInitials(user.getInitials());
                        realmUserInfo.getUserInfo().setColor(user.getColor());
                        realmUserInfo.setUserRegistrationState(true);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                G.onUserInfoResponse = null;
                                Intent intent = new Intent(G.context, ActivityMain.class);
                                intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                realm.close();
            }

            @Override
            public void onUserInfoTimeOut() {
                requestUserInfo();
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
    }

    private void requestUserInfo() {
        if (G.socketConnection) {
            if (userId == 0) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    finish();
                } else {
                    userId = realmUserInfo.getUserId();
                }
                realm.close();
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestUserInfo();
                }
            }, 1000);
        }
    }

    private void requestLogin() {
        if (G.socketConnection) {
            if (token == null) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    finish();
                } else {
                    token = realmUserInfo.getToken();
                }
                realm.close();
            }
            new RequestUserLogin().userLogin(token);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLogin();
                }
            }, 1000);
        }
    }

    private void receiveVerifySms(String message) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        String verificationCode = HelperString.regexExtractValue(message, regexFetchCodeVerification);
        verifyCode = verificationCode;
        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextColor(getResources().getColor(R.color.rg_text_verify));
        userVerify(userName, verificationCode);
    }

    @Override
    protected void onStop() {

        try {
            unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(KEY_SAVE_CODENUMBER, edtCodeNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_MASK, edtPhoneNumber.getMask());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_NUMBER, edtPhoneNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_NAMECOUNTRY, btnChoseCountry.getText().toString());
        savedInstanceState.putString(KEY_SAVE_REGEX, regex);
        savedInstanceState.putString(KEY_SAVE_AGREEMENT, txtAgreement_register.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    public void getDetailPassword(final String questionOne, final String questionTwo, final String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (editCheckPassword != null) editCheckPassword.setHint(hint);
            }
        });

        securityPasswordQuestionOne = questionOne;
        securityPasswordQuestionTwo = questionTwo;
        securityPasswordHint = hint;
        this.hasConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        this.unconfirmedEmailPattern = unconfirmedEmailPattern;
    }

    @Override
    public void verifyPassword(final String tokenR) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                token = tokenR;
                vgCheckPassword.setVisibility(View.GONE);
                txtOk.setVisibility(View.GONE);
                vgMainLayout.setVisibility(View.VISIBLE);
                closeKeyboard(txtOk);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorVerifyPassword(final int wait) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogWaitTimeVerifyPassword(wait);
            }
        });
    }

    @Override
    public void errorInvalidPassword() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeKeyboard(txtOk);
                error(getResources().getString(R.string.invalid_password));
            }
        });
    }

    @Override
    public void recoveryByEmail(final String tokenR) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                token = tokenR;
                vgCheckPassword.setVisibility(View.GONE);
                txtOk.setVisibility(View.GONE);
                vgMainLayout.setVisibility(View.VISIBLE);
                userLogin(token);
            }
        });

    }

    @Override
    public void errorRecoveryByEmail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeKeyboard(txtOk);
                error(getString(R.string.invalid_email_token));
            }
        });
    }

    @Override
    public void recoveryByQuestion(final String tokenR) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                token = tokenR;
                vgCheckPassword.setVisibility(View.GONE);
                txtOk.setVisibility(View.GONE);
                vgMainLayout.setVisibility(View.VISIBLE);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorRecoveryByQuestion(String tokenR) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeKeyboard(txtOk);
                error(getString(R.string.invalid_question_token));
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void dialogWaitTimeVerifyPassword(long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(ActivityRegister.this).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
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

    private void error(String error) {
        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
        vShort.vibrate(200);
        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        });
        snack.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }

}
