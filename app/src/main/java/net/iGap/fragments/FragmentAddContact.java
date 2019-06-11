/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAddContact;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.OnCountryCallBack;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.CountryReader;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserContactImport;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.G.context;
import static net.iGap.module.Contacts.showLimitDialog;

public class FragmentAddContact extends BaseFragment implements ToolbarListener {

    public static final String NAME = "name";
    public static final String PHONE = "PHONE";

    public static OnCountryCallBack onCountryCallBack;
    private EditText edtFirstName;
    private EditText edtLastName;
    private MaskedEditText edtPhoneNumber;
    private ViewGroup parent;
    private EditText txtCodeCountry;
    private HelperToolbar mHelperToolbar;

    public static FragmentAddContact newInstance() {
        return new FragmentAddContact();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_add_contact, container, false));
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtFirstName = view.findViewById(R.id.ac_edt_firstName);
        edtLastName = view.findViewById(R.id.ac_edt_lastName);
        edtPhoneNumber = view.findViewById(R.id.ac_edt_phoneNumber);
        txtCodeCountry = view.findViewById(R.id.ac_txt_codeCountry);
        parent = view.findViewById(R.id.ac_layoutParent);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String contactName = bundle.getString(NAME);
            edtFirstName.setText(contactName);
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initComponent(view);
    }

    private void initComponent(final View view) {

        setupToolbar(view);

        String phoneFromUrl = "";
        String countryCode = "";
        try {
            phoneFromUrl = getArguments().getString(PHONE);
            if (phoneFromUrl != null && phoneFromUrl.length() > 0) {

                if (phoneFromUrl.startsWith("+")) {
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneFromUrl, "");
                        phoneFromUrl = numberProto.getNationalNumber() + "";
                        countryCode = numberProto.getCountryCode() + "";
                    } catch (NumberParseException e) {
                        phoneFromUrl = phoneFromUrl.substring(1);
                        ;
                    }
                } else if (phoneFromUrl.startsWith("0")) {
                    phoneFromUrl = phoneFromUrl.substring(1);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        parent.setOnClickListener(view1 -> {

        });

        txtCodeCountry.setOnClickListener(v -> {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentChooseCountry()).setReplace(false).load();
                closeKeyboard(v);
            }
        });

        //when user clicked on edit text keyboard wont open with this code
        txtCodeCountry.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                closeKeyboard(txtCodeCountry);

            }
        });

        if (phoneFromUrl != null && phoneFromUrl.length() > 0) {
            edtPhoneNumber.setText(phoneFromUrl);
        }

        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                isEnableSetButton();
            }
        });
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEnableSetButton();
            }
        });

        G.fragmentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        onCountryCallBack = (nameCountry, code, mask) -> G.handler.post(() -> {
            txtCodeCountry.setText(nameCountry);
            txtCodeCountry.setText("+" + code);
            if (!mask.equals(" ")) {
                edtPhoneNumber.setMask(mask.replace("X", "#").replace(" ", "-"));

            } else {
                edtPhoneNumber.setMask("##################");
            }
        });

        if (countryCode.length() > 0) {
            txtCodeCountry.setText("+" + countryCode);
            CountryReader countryReade = new CountryReader();
            StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.fragmentActivity);
            String listArray[] = fileListBuilder.toString().split("\\r?\\n");

            for (String aListArray : listArray) {
                String listItem[] = aListArray.split(";");
                if (countryCode.equals(listItem[0])) {
                    txtCodeCountry.setText(listItem[2]);
                    if (listItem.length > 3) {
                        if (!listItem[3].equals(" ")) {
                            edtPhoneNumber.setMask(listItem[3].replace("X", "#").replace(" ", "-"));
                        }
                    }
                    break;
                }
            }
        }
    }

    private void setupToolbar(View view) {

        ViewGroup toolbarLayout = view.findViewById(R.id.frg_add_contact_toolbar);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setDefaultTitle(getString(R.string.menu_add_contact))
                .setLogoShown(true)
                .setListener(this);

        toolbarLayout.addView(mHelperToolbar.getView());
    }

    private void isEnableSetButton() {

        if ((edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0) && edtPhoneNumber.getText().toString().length() > 0) {
            mHelperToolbar.getRightButton().setEnabled(true);
        } else {
            mHelperToolbar.getRightButton().setEnabled(false);
        }
    }

    private void changePage(View view) {
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        removeFromBaseFragment(FragmentAddContact.this);
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        if (RealmUserInfo.isLimitImportContacts()) {
            showLimitDialog();
            return;
        }

        String _phone = edtPhoneNumber.getText().toString();
        String codeCountry = txtCodeCountry.getText().toString();

        String saveNumber;

        if (edtPhoneNumber.getText().toString().startsWith("0")) {
            saveNumber = codeCountry + _phone.substring(1, _phone.length());
        } else {
            saveNumber = codeCountry + _phone;
        }

        List<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = edtFirstName.getText().toString();
        contact.lastName = edtLastName.getText().toString();
        contact.phone = saveNumber;

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void addToContactList(View view) {
        if (edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0) {
            if (edtPhoneNumber.getText().toString().length() > 0) {

                final String phone = edtPhoneNumber.getText().toString();
                final String firstName = edtFirstName.getText().toString();
                final String lastName = edtLastName.getText().toString();
                final String codeNumber = txtCodeCountry.getText().toString();
                String displayName = firstName + " " + lastName;
                HelperAddContact.addContact(displayName, codeNumber, phone);

                changePage(view);
            } else {
                Toast.makeText(G.context, R.string.please_enter_phone_number, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(G.context, R.string.please_enter_firstname_or_lastname, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onLeftIconClickListener(View view) {
        changePage(view);

    }

    @Override
    public void onRightIconClickListener(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.add_to_list_contact).content(R.string.text_add_to_list_contact).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                addContactToServer();
                final int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
                if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermission.getContactPermision(G.fragmentActivity, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    addToContactList(view);
                }
            }
        }).negativeText(R.string.no).onNegative((dialog, which) -> {
            addContactToServer();
            dialog.dismiss();
            G.fragmentActivity.onBackPressed();
        }).show();
    }

    public EditText getEdtFirstName() {
        return edtFirstName;
    }

    public EditText getEdtLastName() {
        return edtLastName;
    }

    public MaskedEditText getEdtPhoneNumber() {
        return edtPhoneNumber;
    }
}
