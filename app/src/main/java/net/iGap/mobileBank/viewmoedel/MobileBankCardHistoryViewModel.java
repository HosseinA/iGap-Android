package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankDateModel;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.module.CalendarShamsi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MobileBankCardHistoryViewModel extends BaseMobileBankViewModel {

    private ObservableField<String> balance = new ObservableField<>("1,000,000,000");
    private ObservableField<String> income = new ObservableField<>("1,000,000,000");
    private ObservableField<String> withdraw = new ObservableField<>("1,000,000,000");
    private MutableLiveData<List<BankDateModel>> calender;
    private MutableLiveData<List<BankHistoryModel>> bills;

    private ObservableInt progressVisibility = new ObservableInt(View.INVISIBLE);
    private MutableLiveData<ErrorModel> errorM;

    private static final String TAG = "CardHistoryViewModel";

    public MobileBankCardHistoryViewModel() {
        calender = new MutableLiveData<>();
        bills = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
    }

    public void init() {
        getCurrentTime();
    }

    private void getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long monthDiff = null;
        try {
            Date startM = sdf.parse("2020-01-01 00:00:00");
            Date endM = sdf.parse("2020-02-01 00:00:00");
            monthDiff = endM.getTime() - startM.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeNow = new Date().getTime();
        String temp = HelperCalander.getPersianCalander(timeNow);
        String[] timeParts = temp.split("/");
        List<BankDateModel> data = new ArrayList<>();
        CalendarShamsi monthName = new CalendarShamsi(new Date(timeNow));
        data.add(new BankDateModel(monthName.strMonth, timeParts[1], timeParts[0], true));
        // add future;
        for (int i = 2; i >= 1; i--) {
            String temp1 = HelperCalander.getPersianCalander((timeNow + monthDiff * i));
            String[] timeParts1 = temp1.split("/");
            monthName = new CalendarShamsi(new Date(timeNow + monthDiff * i));
            data.add(2 - i, new BankDateModel(monthName.strMonth, timeParts1[1], timeParts1[0], false));
        }
        // add past
        for (int i = 1; i <= 12; i++) {
            String temp1 = HelperCalander.getPersianCalander((timeNow - monthDiff * i));
            String[] timeParts1 = temp1.split("/");
            monthName = new CalendarShamsi(new Date(timeNow - monthDiff * i));
            data.add(new BankDateModel(monthName.strMonth, timeParts1[1], timeParts1[0], false));
        }
        calender.postValue(data);
        getAccountDataForMonth(data.get(2));
    }

    public void getAccountDataForMonth(BankDateModel date) {
        // set bills
        MobileBankRepository.getInstance().getAccountHistory("47000012780603", 0, this, new ResponseCallback<BaseMobileBankResponse<List<BankHistoryModel>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<BankHistoryModel>> data) {
                bills.setValue(data.getData());
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public ObservableField<String> getBalance() {
        return balance;
    }

    public ObservableField<String> getIncome() {
        return income;
    }

    public ObservableField<String> getWithdraw() {
        return withdraw;
    }

    public MutableLiveData<List<BankHistoryModel>> getBills() {
        return bills;
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public MutableLiveData<List<BankDateModel>> getCalender() {
        return calender;
    }
}
