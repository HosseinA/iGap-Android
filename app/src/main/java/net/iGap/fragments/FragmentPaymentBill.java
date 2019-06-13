package net.iGap.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPaymentBillBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.IBackHandler;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentPaymentBillViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import static net.iGap.activities.ActivityMain.requestCodeBarcode;
import static net.iGap.viewmodel.FragmentPaymentBillViewModel.getCompany;


public class FragmentPaymentBill extends BaseFragment {

    private FragmentPaymentBillBinding fragmentPaymentBillBinding;

    public static FragmentPaymentBill newInstance(int resTitleId) {
        return FragmentPaymentBill.newInstance(resTitleId, null, null);
    }

    public static FragmentPaymentBill newInstance(int resTitleId, String PID, String BID) {
        Bundle args = new Bundle();
        args.putInt("title", resTitleId);
        args.putString("PID", PID);
        args.putString("BID", BID);
        FragmentPaymentBill fragmentPaymentBill = new FragmentPaymentBill();
        fragmentPaymentBill.setArguments(args);
        return fragmentPaymentBill;
    }

    public static FragmentPaymentBill newInstance(int resTitleId, JSONObject jsonObject) {
        String PID = null;
        String BID = null;
        try {
            PID = jsonObject.getString("PID");
            BID = jsonObject.getString("BID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newInstance(resTitleId, PID, BID);
    }

    public FragmentPaymentBill() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentBillBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_bill, container, false);
        return attachToSwipeBack(fragmentPaymentBillBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int resTitleId = getArguments().getInt("title");
        String PID = getArguments().getString("PID");
        String BID = getArguments().getString("BID");

        FragmentPaymentBillViewModel fragmentPaymentBillViewModel = new FragmentPaymentBillViewModel(FragmentPaymentBill.this, fragmentPaymentBillBinding, resTitleId, PID, BID);
        fragmentPaymentBillBinding.setFragmentPaymentBillViewModel(fragmentPaymentBillViewModel);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(resTitleId))
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        fragmentPaymentBillBinding.fpbLayoutToolbar.addView(toolbar.getView());

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeBarcode) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            if (result.getContents() != null) {
                String barCode = result.getContents();
                if (barCode.length() == 26) {
                    String billId = barCode.substring(0, 13);
                    String payId = barCode.substring(13, 26);
                    String company_type = barCode.substring(11, 12);
                    String price = barCode.substring(13, 21);

                    while (payId.startsWith("0")) {
                        payId = payId.substring(1);
                    }

                    fragmentPaymentBillBinding.fpbEdtBillId.setText(billId);
                    fragmentPaymentBillBinding.fpbEdtPayId.setText(payId);
                    fragmentPaymentBillBinding.fpbEdtPrice.setText(addCommasToNumericString((Integer.parseInt(price) * 1000) + ""));
                    fragmentPaymentBillBinding.fpbImvCompany.setImageResource(getCompany(company_type));

                    fragmentPaymentBillBinding.getFragmentPaymentBillViewModel().observeAmount.set(true);

                }
            }
        }


    }

    public static String addCommasToNumericString(String digits) {
        String result = "";
        int len = digits.length();
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result = digits.charAt(i) + result;
            nDigits++;
            if (((nDigits % 3) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        return (result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onMplResult = null;
    }

}