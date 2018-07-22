package org.paygear.wallet.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.databinding.FragmentSettingWalletBinding;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.web.Web;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */
public class FragmentSettingWallet extends Fragment {

    private Payment mPayment;
    private Card mCard;

    private FragmentSettingWalletBinding fragmentSettingWalletBinding;

    public FragmentSettingWallet() {
    }

    public static FragmentSettingWallet newInstance() {
        FragmentSettingWallet fragment = new FragmentSettingWallet();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mCard = (Card) getArguments().getSerializable("Card");
            mPayment = (Payment) getArguments().getSerializable("Payment");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        fragmentSettingWalletBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_wallet, container, false);
        return fragmentSettingWalletBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RaadToolBar appBar = fragmentSettingWalletBinding.appBar;
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        appBar.setTitle(getString(R.string.settings));
        fragmentSettingWalletBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        ViewGroup btnSetPassword = fragmentSettingWalletBinding.btnSetNewPassword;
        ViewGroup btnForgotPassword = fragmentSettingWalletBinding.btnForgotPassword;
        TextView txtSetPassword = fragmentSettingWalletBinding.txtSetPassword;


        Drawable mDrawableSetPassword = getResources().getDrawable(R.drawable.button_blue_selector_24dp);
        mDrawableSetPassword.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnSetPassword.setBackground(mDrawableSetPassword);
            btnForgotPassword.setBackground(mDrawableSetPassword);
        }


        btnSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((NavigationBarActivity) getActivity()).replaceFullFragment(
                        new SetCardPinFragment(), "SetCardPinFragment", true);
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.text_forgot_title)
                        .content(R.string.text_forgot)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String id = Auth.getCurrentAuth().getId();
                                String token = RaadApp.paygearCard.token;

                                Web.getInstance().getWebService().getForgotPassword(token, id).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {


                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                    }
                                });
                            }
                        })
                        .show();



            }
        });
    }

}
