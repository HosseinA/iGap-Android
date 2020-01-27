package net.iGap.mobileBank.repository;

import net.iGap.DbManager;
import net.iGap.api.MobileBankApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.MobileBankApiInitializer;
import net.iGap.api.apiService.MobileBankExpiredTokenCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankBlockCheque;
import net.iGap.mobileBank.repository.model.BankCardBalance;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankChequeBookListModel;
import net.iGap.mobileBank.repository.model.BankChequeSingle;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BankServiceLoanDetailModel;
import net.iGap.mobileBank.repository.model.BankShebaModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.model.LoanListModel;
import net.iGap.mobileBank.repository.model.LoginResponse;
import net.iGap.realm.RealmUserInfo;

import java.util.List;

public class MobileBankRepository {

    private static MobileBankRepository instance ;
    private MobileBankApi bankApi = new RetrofitFactory().getMobileBankRetrofit();
    private String accessToken;
    private static String TOKEN_PREFIX = "Bearer  ";
    RealmUserInfo userInfo;

    private MobileBankRepository() {
        //use instance
    }

    public static MobileBankRepository getInstance(){
        if (instance == null) instance = new MobileBankRepository();
        return instance;
    }

    public void mobileBankLogin(String authentication, HandShakeCallback callback, ResponseCallback<BaseMobileBankResponse<LoginResponse>> responseCallback) {
        new ApiInitializer<BaseMobileBankResponse<LoginResponse>>().initAPI(new RetrofitFactory().getMobileBankLoginRetrofit().mobileBankLogin(authentication), callback, responseCallback);
    }

    public void getMobileBankCards(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankCardModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankCardModel>>>().initAPI(bankApi.getUserCards(getAccessToken() ,null, null, null, null), callback, responseCallback);
    }

    public void getChequeBookList(String deposit, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankChequeBookListModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankChequeBookListModel>>>().initAPI(bankApi.getChequesBookList(getAccessToken(), deposit), callback, responseCallback);
    }

    public void getChequeList(String depositNumber, String chequeBookNumber, Integer length, Integer offset, String chequeNumber, String status, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankChequeSingle>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankChequeSingle>>>().initAPI(bankApi.getChequesList(getAccessToken(), depositNumber, chequeBookNumber, length, offset, chequeNumber, status), callback, responseCallback);
    }

    public void getLoanList(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<LoanListModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<LoanListModel>>>().initAPI(bankApi.getLoansList(getAccessToken()), callback, responseCallback);
    }

    public void getMobileBankAccounts(MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankAccountModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankAccountModel>>>().initAPI(bankApi.getUserDeposits(getAccessToken(), null), callback, responseCallback);
    }

    public void getShebaNumber(String pan, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<String>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<String>>>().initAPI(bankApi.getShebaNumber(getAccessToken(), pan), callback, responseCallback);
    }

    public void getShebaNumberByDeposit(String deposit, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankShebaModel>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankShebaModel>>().initAPI(bankApi.getShebaNumberByDeposit(getAccessToken(), deposit), callback, responseCallback);
    }

    public void getAccountHistory(String depositNumber, Integer offset, String startDate, String endDate, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<List<BankHistoryModel>>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<List<BankHistoryModel>>>().initAPI(bankApi.getAccountHistory(getAccessToken(), depositNumber, 30, offset, startDate, endDate), callback, responseCallback);
    }

    public void getCardBalance(String cardNumber, String cardData, String depositNumber, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankCardBalance>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankCardBalance>>().initAPI(bankApi.getCardBalance(getAccessToken(), cardNumber, cardData, depositNumber), callback, responseCallback);
    }

    public void getLoanDetail(String loanNumber, Integer offset, Integer length, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankServiceLoanDetailModel>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankServiceLoanDetailModel>>().initAPI(bankApi.getLoanDetail(getAccessToken(), loanNumber, true, offset, length), callback, responseCallback);
    }

    public void hotCard(String cardNumber, String reason, String auth, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse>().initAPI(bankApi.hotCard(getAccessToken(), cardNumber, reason, auth), callback, responseCallback);
    }

    public void blockCheque(List<String> chequeNumbers, String depositNumber, String reason, MobileBankExpiredTokenCallback callback, ResponseCallback<BaseMobileBankResponse<BankBlockCheque>> responseCallback) {
        new MobileBankApiInitializer<BaseMobileBankResponse<BankBlockCheque>>().initAPI(bankApi.blockCheque(getAccessToken(), chequeNumbers, depositNumber, reason), callback, responseCallback);
    }

    public void getOTP(String cardNumber, MobileBankExpiredTokenCallback callback, ResponseCallback<ErrorModel> responseCallback) {
        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
            String phone = userInfo.getUserInfo().getPhoneNumber();
            if (phone.startsWith("98")) {
                phone = phone.replaceFirst("98", "0");
            }
            new MobileBankApiInitializer<ErrorModel>().initAPI(new RetrofitFactory().getMobileBankOTPRetrofit().getOTP(cardNumber, phone), callback, responseCallback);
        });
    }

    public String getAccessToken() {
        return TOKEN_PREFIX + accessToken;
    }

    public void setAccessToken(String token) {
        accessToken = token;
    }
}
