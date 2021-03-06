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

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivityManageSpaceViewModel extends ViewModel {

    private MutableLiveData<Boolean> loadFirstPage = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoadFirstPage() {
        return loadFirstPage;
    }

    public void setFragment(int backStackCount) {
        loadFirstPage.setValue(backStackCount == 0);
    }
}
