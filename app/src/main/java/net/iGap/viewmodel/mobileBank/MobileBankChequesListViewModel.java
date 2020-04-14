package net.iGap.viewmodel.mobileBank;

import android.util.Pair;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.model.mobileBank.BankBlockCheque;
import net.iGap.model.mobileBank.BankChequeSingle;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MobileBankRepository;

import java.util.ArrayList;
import java.util.List;

public class MobileBankChequesListViewModel extends BaseMobileBankViewModel {

    private ObservableInt noItemVisibility = new ObservableInt(View.GONE);
    private ObservableInt progressVisibility = new ObservableInt(View.GONE);
    private SingleLiveEvent<Pair<String, Integer>> registerChequeListener = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> registerChequeLoader = new SingleLiveEvent<>();
    private MutableLiveData<List<BankChequeSingle>> responseListener = new MutableLiveData<>();
    private String deposit, bookNumber;

    public void init() {
        getCheques(0);
    }

    public void getCheques(int offset) {
        noItemVisibility.set(View.GONE);
        progressVisibility.set(View.VISIBLE);
        MobileBankRepository.getInstance().getChequeList(deposit, bookNumber,
                100, offset, null, null,
                this, new ResponseCallback<BaseMobileBankResponse<List<BankChequeSingle>>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<List<BankChequeSingle>> data) {
                        if (data.getData() == null || data.getData().size() == 0) {
                            noItemVisibility.set(View.VISIBLE);
                            return;
                        }
                        responseListener.setValue(data.getData());
                        progressVisibility.set(View.GONE);
                    }

                    @Override
                    public void onError(String error) {
                        progressVisibility.set(View.GONE);
                        showRequestErrorMessage.setValue(error);
                    }

                    @Override
                    public void onFailed() {
                        progressVisibility.set(View.GONE);
                    }
                });
    }

    public void blockCheques(String chequeNumber) {
        // TODO: 1/27/2020 delete this line in final version
        chequeNumber = "";
        progressVisibility.set(View.VISIBLE);
        List<String> cheques = new ArrayList<>();
        cheques.add(chequeNumber);
        MobileBankRepository.getInstance().blockCheque(cheques, deposit, "", this, new ResponseCallback<BaseMobileBankResponse<BankBlockCheque>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankBlockCheque> data) {
                progressVisibility.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibility.set(View.GONE);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                progressVisibility.set(View.GONE);
            }
        });
    }

    public void getRegisterCheque(String number, long amount, int position) {
        registerChequeLoader.postValue(true);
        MobileBankRepository.getInstance().getRegisterCheque(number, deposit, amount, this, new ResponseCallback<BaseMobileBankResponse>() {
            @Override
            public void onSuccess(BaseMobileBankResponse data) {
                registerChequeLoader.postValue(false);
                registerChequeListener.postValue(new Pair<>(data.getMessage(), position));

            }

            @Override
            public void onError(String error) {
                registerChequeLoader.postValue(false);
                registerChequeListener.postValue(new Pair<>(error, -1));
            }

            @Override
            public void onFailed() {
                registerChequeLoader.postValue(false);
            }
        });
    }

    public ObservableInt getNoItemVisibility() {
        return noItemVisibility;
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public MutableLiveData<List<BankChequeSingle>> getResponseListener() {
        return responseListener;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public SingleLiveEvent<Boolean> getRegisterChequeLoader() {
        return registerChequeLoader;
    }

    public SingleLiveEvent<Pair<String, Integer>> getRegisterChequeListener() {
        return registerChequeListener;
    }
}