package net.iGap.news.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;

import java.util.List;

public class NewsGroupListVM extends ViewModel {

    private MutableLiveData<NewsGroup> mGroups;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsGroupListVM() {
        mGroups = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
    }

    public void getData() {
        repo.getNewsGroups(new ApiResponse<NewsGroup>() {
            @Override
            public void onResponse(NewsGroup newsGroup) {
                mGroups.setValue(newsGroup);
            }

            @Override
            public void onFailed(String error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public MutableLiveData<NewsGroup> getmGroups() {
        return mGroups;
    }

    public void setmGroups(MutableLiveData<NewsGroup> mGroups) {
        this.mGroups = mGroups;
    }

    public MutableLiveData<NewsError> getError() {
        return error;
    }

    public void setError(MutableLiveData<NewsError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
