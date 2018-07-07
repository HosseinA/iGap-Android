package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.os.Bundle;
import android.view.View;

import net.iGap.R;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.fragments.FragmentPaymentCharge;
import net.iGap.fragments.FragmentPaymentInquiry;
import net.iGap.helper.HelperFragment;


public class FragmentPaymentViewModel {

    public FragmentPaymentViewModel(Bundle arguments) {

    }

    public void onClickCharge(View v) {
        new HelperFragment(FragmentPaymentCharge.newInstance()).setReplace(false).load();
    }

    public void onClickBill(View v) {
        new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
    }

    public void onClickBillTraffic(View v) {
        new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
    }

    public void onClickInquiryMci(View v) {
        new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.mci)).setReplace(false).load();
    }

    public void onClickInquiryTelecom(View v) {
        new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.telecome)).setReplace(false).load();
    }

}
