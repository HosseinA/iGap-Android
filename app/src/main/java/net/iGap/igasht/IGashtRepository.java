package net.iGap.igasht;

import net.iGap.DbManager;
import net.iGap.api.IgashtApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.igasht.barcodescaner.TicketQRCodeResponse;
import net.iGap.igasht.historylocation.IGashtTicketDetail;
import net.iGap.igasht.historylocation.TicketHistoryListResponse;
import net.iGap.igasht.locationdetail.RegisterTicketResponse;
import net.iGap.igasht.locationdetail.buyticket.IGashtLocationService;
import net.iGap.igasht.locationdetail.buyticket.IGashtOrder;
import net.iGap.igasht.locationdetail.buyticket.IGashtVouchers;
import net.iGap.igasht.locationlist.IGashtLocationItem;
import net.iGap.igasht.provinceselect.IGashtProvince;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IGashtRepository {

    //singleton
    private static IGashtRepository instance;
    private IgashtApi igashtApi;

    private IGashtProvince selectedProvince;
    private IGashtLocationItem selectedLocation;
    private List<IGashtVouchers> selectedServiceList;
    private List<IGashtProvince> provinceList;

    public static IGashtRepository getInstance() {
        if (instance == null) {
            instance = new IGashtRepository();
        }
        return instance;
    }

    public void clearInstance() {
        instance = null;
    }

    private IGashtRepository() {
        igashtApi = new RetrofitFactory().getIgashtRetrofit().create(IgashtApi.class);
        selectedServiceList = new ArrayList<>();
    }

    public void setSelectedProvince(IGashtProvince selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public IGashtProvince getSelectedProvince() {
        return selectedProvince;
    }

    public List<IGashtProvince> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<IGashtProvince> provinceList) {
        this.provinceList = provinceList;
    }

    public IGashtLocationItem getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(IGashtLocationItem selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void getProvinceList(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtProvince>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtProvince>>().initAPI(igashtApi.requestGetProvinceList(), handShakeCallback, callback);
    }

    public void getLocationListWithProvince(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtLocationItem>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtLocationItem>>().initAPI(igashtApi.requestGetLocationList(selectedProvince.getId()), handShakeCallback, callback);
    }

    public void getServiceList(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtLocationService>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtLocationService>>().initAPI(igashtApi.requestGetServiceList(selectedLocation.getId()), handShakeCallback, callback);
    }

    public void getHistoryList(int offset, int limit, HandShakeCallback handShakeCallback, ResponseCallback<TicketHistoryListResponse<IGashtTicketDetail>> callback) {
        new ApiInitializer<TicketHistoryListResponse<IGashtTicketDetail>>().initAPI(igashtApi.requestGetTicketList(offset, limit), handShakeCallback, callback);
    }

    public void registeredOrder(HandShakeCallback handShakeCallback, ResponseCallback<RegisterTicketResponse> callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            new ApiInitializer<RegisterTicketResponse>().initAPI(igashtApi.registerOrder(new IGashtOrder(realm.where(RealmUserInfo.class).findFirst().getUserInfo().getPhoneNumber(),
                    1,
                    selectedProvince.getId(),
                    selectedLocation.getId(),
                    selectedServiceList)), handShakeCallback, callback);
        });
    }

    public void getTicketQRCode(String voucherNumber, HandShakeCallback handShakeCallback, ResponseCallback<TicketQRCodeResponse> callback) {
        new ApiInitializer<TicketQRCodeResponse>().initAPI(igashtApi.requestGetTicketQRCode(voucherNumber), handShakeCallback, callback);
    }

    public void createVoucherList(@NotNull List<IGashtLocationService> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCount() > 0) {
                selectedServiceList.add(new IGashtVouchers(data.get(i).getPersianTicket().getVoucherinfoId(), data.get(i).getCount()));
            }
        }
    }

    public void clearSelectedServiceList() {
        selectedServiceList.clear();
    }

    public boolean hasVoucher() {
        return selectedServiceList.size() != 0;
    }
}
