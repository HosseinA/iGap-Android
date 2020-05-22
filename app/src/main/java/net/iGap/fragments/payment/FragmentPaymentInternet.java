package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryPackage;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.api.ChargeApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.OperatorType;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.module.Contacts;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRegisteredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.helper.HelperString.isNumeric;
import static net.iGap.model.OperatorType.Type.HAMRAH_AVAL;
import static net.iGap.model.OperatorType.Type.IRANCELL;
import static net.iGap.model.OperatorType.Type.RITEL;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MCI;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MTN;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.RIGHTEL;

public class FragmentPaymentInternet extends BaseFragment implements HandShakeCallback {

    private static final String SIM_TYPE_CREDIT = "CREDIT";
    private static final String SIM_TYPE_PERMANENT = "PERMANENT";
    private static final String SIM_TYPE_TD_LTE_CREDIT = "CREDIT_TD_LTE";
    private static final String SIM_TYPE_TD_LTE_PERMANENT = "PERMANENT_TD_LTE";
    private static final String SIM_TYPE_DATA = "DATA";

    private View frameHistory;
    private View frameHamrah;
    private View frameIrancel;
    private View frameRightel;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private AppCompatEditText numberEditText;
    private OperatorType.Type currentOperator;
    private RadioButton rbCredit;
    private RadioButton rbPermanent;
    private RadioButton rbTdLteCredit;
    private RadioButton rbTdLtePermanent;
    private RadioButton rbData;
    private String currentSimType = SIM_TYPE_CREDIT;
    private ChargeApi chargeApi;
    private FavoriteNumber historyNumber;
    private View progressBar;

    public static FragmentPaymentInternet newInstance() {
        return new FragmentPaymentInternet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout toolbar = view.findViewById(R.id.toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
        View frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        numberEditText = view.findViewById(R.id.phoneNumber);
        MaterialButton goNextButton = view.findViewById(R.id.btn_nextpage);
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancel = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        rbCredit = view.findViewById(R.id.rbCredit);
        RadioGroup radioGroup = view.findViewById(R.id.rdGroup);
        rbPermanent = view.findViewById(R.id.rbPermanent);
        rbTdLteCredit = view.findViewById(R.id.rbTdLteCredit);
        rbTdLtePermanent = view.findViewById(R.id.rbTdLtePermanent);
        rbData = view.findViewById(R.id.rbData);
        progressBar = view.findViewById(R.id.loadingView);
        MaterialDesignTextView btnRemoveSearch = view.findViewById(R.id.btnRemoveSearch);

        chargeApi = new RetrofitFactory().getChargeRetrofit();
        numberEditText.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null) {
                numberEditText.setText(userInfo.getPhoneNumber());
                String number = userInfo.getPhoneNumber();
                numberEditText.setText(number
                        .replace("98", "0")
                        .replace("+98", "0")
                        .replace("0098", "0")
                        .replace(" ", "")
                        .replace("-", ""));
                onPhoneNumberInput();
                numberEditText.setSelection(numberEditText.getText() == null ? 0 : numberEditText.getText().length());
            }
        });

        toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.buy_internet_package_title))
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        btnRemoveSearch.setOnClickListener(v -> {
            numberEditText.setText(null);
            btnRemoveSearch.setVisibility(View.INVISIBLE);
        });

        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && btnRemoveSearch.getVisibility() == View.INVISIBLE) {
                    btnRemoveSearch.setVisibility(View.VISIBLE);
                }
                if (s.length() == 0)
                    btnRemoveSearch.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numberEditText.getText() != null && numberEditText.getText().length() == 11) {
                    String number = numberEditText.getText().toString().substring(0, 4);
                    OperatorType.Type opt = new OperatorType().getOperation(number);
                    if (opt != null) {
                        changeOperator(opt);
                    }
                }
            }
        });

        frameContact.setOnClickListener(v -> onContactNumberButtonClick());
        frameHistory.setOnClickListener(v -> onHistoryNumberButtonClick());
        frameHamrah.setOnClickListener(v -> changeOperator(HAMRAH_AVAL));
        frameRightel.setOnClickListener(v -> changeOperator(RITEL));
        frameIrancel.setOnClickListener(v -> changeOperator(IRANCELL));

        goNextButton.setOnClickListener(v -> {
            if (currentOperator != null) {
                if (numberEditText.getText() == null) {
                    numberEditText.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                String phoneNumber = numberEditText.getText().toString().trim();
                if (!isNumeric(phoneNumber) || phoneNumber.length() < 11) {
                    numberEditText.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentInternetPackage.newInstance(phoneNumber, convertOperatorToString(currentOperator), currentSimType)).setReplace(false).load();
            } else {
                showError(getResources().getString(R.string.sim_type_not_choosed));
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> changeSimType());


    }

    private void changeSimType() {
        if (rbCredit.isChecked()) {
            currentSimType = SIM_TYPE_CREDIT;
        } else if (rbPermanent.isChecked()) {
            currentSimType = SIM_TYPE_PERMANENT;
        } else if (rbTdLteCredit.isChecked()) {
            currentSimType = SIM_TYPE_TD_LTE_CREDIT;
        } else if (rbTdLtePermanent.isChecked()) {
            currentSimType = SIM_TYPE_TD_LTE_PERMANENT;
        } else if (rbData.isChecked()) {
            currentSimType = SIM_TYPE_DATA;
        }
    }

    private void onPhoneNumberInput() {
        if (numberEditText.getText() != null && numberEditText.getText().length() == 4 || numberEditText.getText().length() == 11) {
            String number = numberEditText.getText().toString().substring(0, 4);
            OperatorType.Type opt = new OperatorType().getOperation(number);
            if (opt != null) {
                changeOperator(opt);
            }
        }

        if (numberEditText.getText().length() == 11) {
            hideKeyboard();
        }
    }


    private void changeOperator(OperatorType.Type operator) {
        if (currentOperator == operator)
            return;

        currentOperator = operator;

        radioButtonHamrah.setChecked(currentOperator == OperatorType.Type.HAMRAH_AVAL);
        frameHamrah.setSelected(currentOperator == OperatorType.Type.HAMRAH_AVAL);

        radioButtonIrancell.setChecked(currentOperator == OperatorType.Type.IRANCELL);
        frameIrancel.setSelected(currentOperator == OperatorType.Type.IRANCELL);

        radioButtonRightel.setChecked(currentOperator == OperatorType.Type.RITEL);
        frameRightel.setSelected(currentOperator == OperatorType.Type.RITEL);

        rbCredit.setVisibility(View.VISIBLE);
        rbPermanent.setVisibility(View.VISIBLE);

        rbCredit.setChecked(true);
        currentSimType = SIM_TYPE_CREDIT;

        if (currentOperator == RITEL) {
            rbTdLteCredit.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbData.setVisibility(View.VISIBLE);
        } else if (currentOperator == IRANCELL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.VISIBLE);
            rbTdLteCredit.setVisibility(View.VISIBLE);
        } else if (currentOperator == HAMRAH_AVAL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbTdLteCredit.setVisibility(View.GONE);
        }
    }

    private String convertOperatorToString(OperatorType.Type opt) {
        switch (opt) {
            case RITEL:
                return RIGHTEL;
            case IRANCELL:
                return MTN;

            case HAMRAH_AVAL:
                return MCI;
        }
        return MTN;
    }

    private void showError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void onContactNumberButtonClick() {
        try {
            HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, true).build();
                    View contactDialogView = dialog.getCustomView();

                    if (contactDialogView != null) {
                        RecyclerView contactRecyclerView = contactDialogView.findViewById(R.id.rv_contact);
                        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        AdapterContactNumber adapterContact = new AdapterContactNumber();
                        contactRecyclerView.setAdapter(adapterContact);

                        new Contacts().getAllPhoneContactForPayment(contactNumbers -> {
                            if (contactNumbers.size() == 0) {
                                HelperError.showSnackMessage(getResources().getString(R.string.no_number_found), false);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                adapterContact.setContactNumbers(contactNumbers);
                            }
                        });

                        contactDialogView.findViewById(R.id.btn_dialog1).setOnClickListener(v15 -> {
                            if (adapterContact.getSelectedPosition() == -1) {
                                return;
                            }

                            ContactNumber contactNumber = adapterContact.getContactNumbers().get(adapterContact.getSelectedPosition());
                            numberEditText.setText(contactNumber.getPhone().replace(" ", "").replace("-", "").replace("+98", "0"));

                            dialog.dismiss();
                        });

                        contactDialogView.findViewById(R.id.closeView).setOnClickListener(v12 -> dialog.dismiss());
                    }
                    dialog.show();
                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onHistoryNumberButtonClick() {
        frameHistory.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        chargeApi.getFavoriteInternetPackage().enqueue(new Callback<GetFavoriteNumber>() {
            @Override
            public void onResponse(@NotNull Call<GetFavoriteNumber> call, @NotNull Response<GetFavoriteNumber> response) {
                frameHistory.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<FavoriteNumber> numbers = response.body().getData();
                    if (numbers.size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
                    } else {
                        progressBar.setVisibility(View.GONE);

                        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                        View historyDialogView = dialog.getCustomView();

                        if (historyDialogView != null) {
                            AdapterHistoryPackage adapterHistory = new AdapterHistoryPackage(numbers);

                            RecyclerView rvHistory = historyDialogView.findViewById(R.id.rv_history);
                            rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            rvHistory.setAdapter(adapterHistory);

                            historyDialogView.findViewById(R.id.btn_dialog2).setOnClickListener(v13 -> {
                                if (adapterHistory.getSelectedPosition() == -1) {
                                    return;
                                }

                                historyNumber = adapterHistory.getHistoryNumberList().get(adapterHistory.getSelectedPosition());
                                numberEditText.setText(historyNumber.getPhoneNumber());

                                dialog.dismiss();
                            });

                            historyDialogView.findViewById(R.id.iv_close2).setOnClickListener(v12 -> dialog.dismiss());
                        }

                        dialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetFavoriteNumber> call, @NotNull Throwable t) {
                frameHistory.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
            }
        });
    }
}
