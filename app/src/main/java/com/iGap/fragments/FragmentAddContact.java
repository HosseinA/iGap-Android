package com.iGap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAddContact;
import com.iGap.helper.HelperPermision;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructListOfContact;
import com.iGap.request.RequestUserContactImport;
import java.io.IOException;
import java.util.ArrayList;

import static com.iGap.G.context;

public class FragmentAddContact extends android.support.v4.app.Fragment {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtPhoneNumber;
    private ViewGroup parent;
    private RippleView rippleSet;
    private MaterialDesignTextView txtSet;

    public static FragmentAddContact newInstance() {
        return new FragmentAddContact();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);


    }

    private void initComponent(final View view) {

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.ac_txt_back);
        final RippleView rippleBack = (RippleView) view.findViewById(R.id.ac_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                changePage(rippleView);
            }
        });


        txtSet = (MaterialDesignTextView) view.findViewById(R.id.ac_txt_set);
        txtSet.setTextColor(getResources().getColor(R.color.line_edit_text));

        parent = (ViewGroup) view.findViewById(R.id.ac_layoutParent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        edtFirstName = (EditText) view.findViewById(R.id.ac_edt_firstName);
        final View viewFirstName = view.findViewById(R.id.ac_view_firstName);
        edtLastName = (EditText) view.findViewById(R.id.ac_edt_lastName);
        final View viewLastName = view.findViewById(R.id.ac_view_lastName);
        edtPhoneNumber = (EditText) view.findViewById(R.id.ac_edt_phoneNumber);
        final View viewPhoneNumber = view.findViewById(R.id.ac_view_phoneNumber);

        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewFirstName.setBackgroundColor(
                            getResources().getColor(R.color.toolbar_background));
                } else {
                    viewFirstName.setBackgroundColor(
                            getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewLastName.setBackgroundColor(
                            getResources().getColor(R.color.toolbar_background));
                } else {
                    viewLastName.setBackgroundColor(
                            getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        edtPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewPhoneNumber.setBackgroundColor(
                            getResources().getColor(R.color.toolbar_background));
                } else {
                    viewPhoneNumber.setBackgroundColor(
                            getResources().getColor(R.color.line_edit_text));
                }
            }
        });

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

        //G.onContactImport = new OnUserContactImport() {
        //    @Override
        //    public void onContactImport() {
        //
        //    }
        //};

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        rippleSet = (RippleView) view.findViewById(R.id.ac_ripple_set);
        rippleSet.setEnabled(false);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(final RippleView rippleView) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.add_to_list_contact)
                        .content(R.string.text_add_to_list_contact)
                        .positiveText(R.string.B_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                addContactToServer();
                                final int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
                                if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
                                    try {
                                        HelperPermision.getContactPermision(getActivity(), null);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    addToContactList(rippleView);
                                }
                            }
                        })
                        .negativeText(R.string.B_cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //G.onContactImport = new OnUserContactImport() {
        //    @Override
        //    public void onContactImport() {
        //
        //    }
        //};

    }

    private void isEnableSetButton() {

        if ((edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0)
                && edtPhoneNumber.getText().toString().length() > 0) {

            txtSet.setTextColor(getResources().getColor(R.color.white));
            rippleSet.setEnabled(true);

        } else {
            txtSet.setTextColor(getResources().getColor(R.color.line_edit_text));
            rippleSet.setEnabled(false);
        }
    }

    private void changePage(View view) {

        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        String _phone = edtPhoneNumber.getText().toString();
        if (_phone.startsWith("0")) _phone = _phone.substring(1, _phone.length());

        String ph = "+98" + _phone.replace("+98", "");

        ArrayList<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = edtFirstName.getText().toString();
        contact.lastName = edtLastName.getText().toString();
        contact.phone = ph;


        contacts.add(contact);

        new RequestUserContactImport().contactImportAndGetResponse(contacts, true);
    }

    private void addToContactList(View view) {
        if (edtFirstName.getText().toString().length() > 0 || edtLastName.getText().toString().length() > 0) {
            if (edtPhoneNumber.getText().toString().length() > 0) {

                final String phone = edtPhoneNumber.getText().toString();
                final String firstName = edtFirstName.getText().toString();
                final String lastName = edtLastName.getText().toString();
                String displayName = firstName + " " + lastName;
                HelperAddContact.addContact(displayName, phone);

                changePage(view);

            } else {
                Toast.makeText(G.context, R.string.please_enter_phone_number, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(G.context, R.string.please_enter_firstname_or_lastname,
                    Toast.LENGTH_SHORT).show();
        }
    }

    //***************************************************************************************
}
