package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.igasht.BaseIGashtResponse;
import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.SingleLiveEvent;

import java.util.List;

import static net.iGap.G.context;

public class IGashtBuyTicketViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationService>> {

    private ObservableInt totalPrice = new ObservableInt(0);
    private ObservableInt viewBackground = new ObservableInt(R.drawable.shape_igasht_lightgray);
    private SingleLiveEvent<Boolean> registerVoucher = new SingleLiveEvent<>();
    private MutableLiveData<List<IGashtLocationService>> serviceList = new MutableLiveData<>();
    private SingleLiveEvent<Integer> showErrorMessage = new SingleLiveEvent<>();

    private IGashtRepository repository;

    public IGashtBuyTicketViewModel() {
        repository = IGashtRepository.getInstance();
        if(G.isDarkTheme){
            viewBackground.set(R.drawable.shape_igasht_buy_ticket);
        }
        getTicketData();
    }

    public ObservableInt getTotalPrice() {
        return totalPrice;
    }

    public ObservableInt getViewBackground() {
        return viewBackground;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public SingleLiveEvent<Boolean> getRegisterVoucher() {
        return registerVoucher;
    }

    public MutableLiveData<List<IGashtLocationService>> getServiceList() {
        return serviceList;
    }

    public SingleLiveEvent<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationService> data) {
        serviceList.setValue(data.getData());
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
    }

    public void onAddPlaceClick() {
        /*List<String> tmp = new ArrayList<>();
        for (int i = 0; i < serviceList.size(); i++) {
            tmp.add(serviceList.get(i).getSeviceNameWithLanguage());
        }
        showDialogSelectService.setValue(tmp);*/
    }

    public void onRetryClick() {
        getTicketData();
    }

    public void onPayClick() {
        if (serviceList.getValue() != null) {
            if (checkEntranceTicketCount(serviceList.getValue())) {
                repository.createVoucherList(serviceList.getValue());
                registerVoucher.setValue(repository.hasVoucher());
            } else {
                showErrorMessage.setValue(R.string.error);
            }
        }
    }

    private boolean checkEntranceTicketCount(List<IGashtLocationService> list) {
        int tmp = findEntranceLocation(list);
        if (tmp != -1) {
            return findMaxTicketCount(list,tmp) <= list.get(tmp).getCount();
        } else {
            return false;
        }
    }

    private int findEntranceLocation(List<IGashtLocationService> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getModelId().equals("1")) {
                return i;
            }
        }
        return -1;
    }

    private int findMaxTicketCount(List<IGashtLocationService> list, int entrancePosition) {
        int t = -1;
        for (int i = 0; i < list.size(); i++) {
            if (i != entrancePosition) {
                if (list.get(i).getCount() > t) {
                    t = list.get(i).getCount();
                }
            }
        }
        return t;
    }

    private void getTicketData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getServiceList(this);
    }
}
