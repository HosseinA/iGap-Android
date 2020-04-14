package net.iGap.viewmodel.kuknos;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperCalander;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.model.kuknos.Parsian.KuknosOptionStatus;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.PanelRepo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KuknosPanelVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosBalance> kuknosWalletsM;
    private KuknosAsset asset = null;
    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> openPage;
    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<String> TandCAgree;

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> currency = new ObservableField<>();
    /**
     * different states: 0 = initial / 1 = To Rial Mode
     */
    private MutableLiveData<Integer> BAndCState = new MutableLiveData<>();
    private int position = 0;
    private boolean inRialMode = false;

    public KuknosPanelVM() {
        BAndCState.postValue(0);
        kuknosWalletsM = new MutableLiveData<>();
        //kuknosWalletsM.setValue(new AccountResponse("", Long.getLong("0")));
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        openPage = new SingleLiveEvent<>();
        openPage.setValue(-1);
        TandCAgree = new MutableLiveData<>(null);
    }

    public void initApis() {
        getAccountOptionStatus();
        getDataFromServer();
    }

    private void getDataFromServer() {
        progressState.setValue(true);
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                List<KuknosBalance.Balance> tempBalanceList = new ArrayList<>();
                for (KuknosBalance.Balance tmp : data.getData().getAssets()) {
                    if (tmp.getAsset().getType().equals("native")) {
                        tempBalanceList.add(0, tmp);
                    } else {
                        tempBalanceList.add(tmp);
                    }
                }
                KuknosBalance temp = new KuknosBalance();
                temp.setAssets(tempBalanceList);
                kuknosWalletsM.setValue(temp);
//                spinnerSelect(0);
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                BAndCState.postValue(0);
                error.setValue(new KuknosError(true, "Fail to get data", "0", 0));
                progressState.setValue(false);
                kuknosWalletsM.setValue(null);
            }

            @Override
            public void onFailed() {
                BAndCState.postValue(0);
                error.setValue(new KuknosError(true, "Fail to get data", "0", 0));
                progressState.setValue(false);
                kuknosWalletsM.setValue(null);
            }
        });
    }

    private void getAssetData(String assetCode) {
        asset = null;
        panelRepo.getAssetData(assetCode, this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                asset = data.getData();
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void getAccountOptionStatus() {
        panelRepo.getAccountOptionsStatus(this, new ResponseCallback<KuknosResponseModel<KuknosOptionStatus>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosOptionStatus> data) {
                if (!data.getData().isStatus())
                    openPage.setValue(6);
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public String convertToJSON(int position) {
        if (kuknosWalletsM.getValue() == null)
            return "";
        Gson gson = new Gson();
        return gson.toJson(kuknosWalletsM.getValue().getAssets().get(position));
    }

    public void spinnerSelect(int position) {
        this.position = position;
        KuknosBalance.Balance temp = kuknosWalletsM.getValue().getAssets().get(position);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(temp.getBalance()))) : df.format(Double.valueOf(temp.getBalance())));
        currency.set((temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode()));
        inRialMode = false;
        getAssetData(currency.get());
    }

    private void calculateToRial() {
        if (asset == null || asset.getAssets().size() == 0)
            return;
        KuknosBalance.Balance temp = kuknosWalletsM.getValue().getAssets().get(position);
        DecimalFormat df = new DecimalFormat("#,###");
        balance.set(HelperCalander.isPersianUnicode ?
                HelperCalander.convertToUnicodeFarsiNumber(
                        df.format(Double.valueOf(temp.getBalance()) * Double.valueOf(asset.getAssets().get(0).getSellRate()))) :
                (df.format(Double.valueOf(temp.getBalance()) * Double.valueOf(asset.getAssets().get(0).getSellRate()))));
        BAndCState.postValue(1);
    }

    public void togglePrice() {
        if (kuknosWalletsM.getValue() == null)
            return;
        if (inRialMode) {
            spinnerSelect(position);
            inRialMode = false;
        } else {
            calculateToRial();
            inRialMode = true;
        }
    }

    public boolean isPmnActive() {
        return currency.get().equals("PMN");
    }

    public void receiveW() {
        openPage.setValue(0);
    }

    public void sendW() {
        openPage.setValue(1);
    }

    public void historyW() {
        openPage.setValue(2);
    }

    public void goToSetting() {
        openPage.setValue(3);
    }

    public void goToTrading() {
        openPage.setValue(5);
    }

    public void goTOBuyPMN() {
        openPage.setValue(4);
    }

    public void goToKYC() {

    }

    public void goToDetail() {
        openPage.setValue(1);
    }

    public String getPrivateKeyData() {
        return panelRepo.getUserInfo();
    }

    // getter and setter

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<Integer> getOpenPage() {
        return openPage;
    }

    public ObservableField<String> getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance.set(balance);
    }

    public ObservableField<String> getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency.set(currency);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public MutableLiveData<KuknosBalance> getKuknosWalletsM() {
        return kuknosWalletsM;
    }

    public MutableLiveData<String> getTandCAgree() {
        return TandCAgree;
    }

    public void setTandCAgree(MutableLiveData<String> tandCAgree) {
        TandCAgree = tandCAgree;
    }

    public MutableLiveData<Integer> getBAndCState() {
        return BAndCState;
    }

    public void setOpenPage(Integer openPage) {
        this.openPage.setValue(openPage);
    }
}