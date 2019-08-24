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
import android.support.annotation.DrawableRes;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperNumerical;
import net.iGap.request.RequestMplGetBillToken;

import org.jetbrains.annotations.NotNull;

public class FragmentPaymentBillViewModel extends ViewModel {

    private ObservableInt billTypeImage = new ObservableInt(R.mipmap.empty);
    private ObservableInt haveAmount = new ObservableInt(View.VISIBLE);
    private ObservableInt showLoadingView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showScannerButton = new ObservableInt(View.GONE);
    private ObservableBoolean enabledPaymentButton = new ObservableBoolean(true);
    private ObservableField<String> payId = new ObservableField<>("");
    private ObservableField<String> billId = new ObservableField<>("");
    private ObservableField<String> billAmount = new ObservableField<>("");
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToScannerPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideKeyword = new MutableLiveData<>();

    private boolean isPolice;

    public FragmentPaymentBillViewModel(boolean isPolice, String PID_Str, String BID_Str) {

        this.isPolice = isPolice;

        if (isPolice) {
            haveAmount.set(View.GONE);
            billTypeImage.set(R.mipmap.trafic_police);
        } else {
            showScannerButton.set(View.VISIBLE);
        }

        if (PID_Str != null) {
            payId.set(PID_Str);
        } else {
            payId.set("");
        }

        if (BID_Str != null) {
            billId.set(BID_Str);
        } else {
            billId.set("");
        }
    }

    public ObservableInt getBillTypeImage() {
        return billTypeImage;
    }

    public ObservableInt getHaveAmount() {
        return haveAmount;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowScannerButton() {
        return showScannerButton;
    }

    public ObservableBoolean getEnabledPaymentButton() {
        return enabledPaymentButton;
    }

    public ObservableField<String> getPayId() {
        return payId;
    }

    public ObservableField<String> getBillId() {
        return billId;
    }

    public ObservableField<String> getBillAmount() {
        return billAmount;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<Boolean> getGoToScannerPage() {
        return goToScannerPage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<Boolean> getHideKeyword() {
        return hideKeyword;
    }

    public void onTextChangedBillId(String s) {
        if (!isPolice) {
            if (s.length() == 13) {
                billTypeImage.set(getCompany(s.substring(11, 12)));
            } else {
                billTypeImage.set(R.mipmap.empty);
            }
        }
    }

    public void onClickBarCode() {
        goToScannerPage.setValue(true);
    }

    public void onPayBillClick(String billId, String payId) {
        hideKeyword.setValue(true);
        if (G.userLogin) {
            if (isPolice) {
                if (billId.length() == 0) {
                    showErrorMessage.setValue(R.string.biling_id_not_valid);
                    return;
                }

            } else {
                if (billId.length() != 13) {
                    showErrorMessage.setValue(R.string.biling_id_not_valid);
                    return;
                }
            }

            if (isPolice) {
                if (payId.length() == 0) {
                    showErrorMessage.setValue(R.string.pay_id_is_not_valid);
                    return;
                }
            } else {
                if (payId.length() > 13 || payId.length() < 5) {
                    showErrorMessage.setValue(R.string.pay_id_is_not_valid);
                    return;
                }
            }


            G.onMplResult = error -> {
                showLoadingView.set(View.INVISIBLE);
                if (error) {
                    enabledPaymentButton.set(true);
                } else {
                    goBack.setValue(true);
                }

            };

            showLoadingView.set(View.VISIBLE);

            RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(billId), Long.parseLong(payId));

            enabledPaymentButton.set(false);
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    public void setDataFromBarcodeScanner(String result) {
        if (result != null) {
            if (result.length() == 26) {
                String billId = result.substring(0, 13);
                String payId = result.substring(13, 26);
                String company_type = result.substring(11, 12);
                String price = result.substring(13, 21);
                while (payId.startsWith("0")) {
                    payId = payId.substring(1);
                }
                this.billId.set(billId);
                this.payId.set(payId);
                this.billTypeImage.set(getCompany(company_type));
                this.haveAmount.set(View.VISIBLE);
                this.billAmount.set(new HelperNumerical().getCommaSeparatedPrice((Integer.parseInt(price) * 1000)));
            }
        }
    }

    private @DrawableRes
    int getCompany(@NotNull String value) {
        switch (value) {
            case "1":
                return R.drawable.bill_water_pec;
            case "2":
                return R.drawable.bill_elc_pec;
            case "3":
                return R.drawable.bill_gaz_pec;
            case "4":
                return R.drawable.bill_telecom_pec;
            case "5":
                return R.drawable.bill_mci_pec;
            case "6":
                return R.drawable.bill_shahrdari_pec;
            default:
                return R.mipmap.empty;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        G.onMplResult = null;
    }
}
