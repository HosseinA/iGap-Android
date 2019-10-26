package net.iGap.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;

public abstract class BaseCPayViewModel<T> extends BaseAPIViewModel implements ResponseCallback<T> {

    private MutableLiveData<Integer> msgToUser = new MutableLiveData<>();
    private MutableLiveData<String> msgToUserString = new MutableLiveData<>();
    private MutableLiveData<Boolean> loaderListener = new MutableLiveData<>();

    public MutableLiveData<Integer> getMessageToUser() {
        return msgToUser;
    }

    public MutableLiveData<String> getMessageToUserText() {
        return msgToUserString;
    }

    public MutableLiveData<Boolean> getLoaderListener() {
        return loaderListener;
    }

    @Override
    public void onError(ErrorModel error) {
        msgToUserString.setValue(error.getMessage());
        loaderListener.setValue(false);
    }

    @Override
    public void onFailed(boolean handShakeError) {
        msgToUser.setValue(R.string.server_do_not_response);
        loaderListener.setValue(false);
    }
}
