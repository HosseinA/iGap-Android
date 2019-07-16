package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.module.api.beepTunes.FirstPage;
import net.iGap.viewmodel.BaseViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeepTunesViewModel extends BaseViewModel {
    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();
    private MutableLiveData<FirstPage> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private String TAG = "aabolfazlBeepTunes";

    @Override
    public void onCreateViewModel() {

    }

    @Override
    public void onStart() {
        getFirsPage();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    private void getFirsPage() {
        progressMutableLiveData.postValue(true);
        apiService.getFirstPage().enqueue(new Callback<FirstPage>() {
            @Override
            public void onResponse(Call<FirstPage> call, Response<FirstPage> response) {
                if (response.isSuccessful()) {
                    firstPageMutableLiveData.postValue(response.body());
                } else {
                    // TODO: 7/15/19 exception message
                }
                progressMutableLiveData.postValue(false);
            }

            @Override
            public void onFailure(Call<FirstPage> call, Throwable t) {
                progressMutableLiveData.postValue(false);
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<FirstPage> getFirstPageMutableLiveData() {
        return firstPageMutableLiveData;
    }
}
