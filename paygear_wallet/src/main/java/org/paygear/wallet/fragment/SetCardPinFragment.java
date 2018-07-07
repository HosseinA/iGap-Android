package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SetCardPinFragment extends Fragment {

    private RaadToolBar appBar;
    private EditText currentPass;
    private EditText newPass;
    private EditText confirmPass;
    private TextView button;
    private ProgressBar progressBar;


    public SetCardPinFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_card_pin, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        appBar = view.findViewById(R.id.app_bar);
        appBar.setTitle(getString(R.string.paygear_card_pin));
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape,true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor),PorterDuff.Mode.SRC_IN));
        appBar.showBack();

        ViewGroup root_current = view.findViewById(R.id.root_current);
        root_current.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));

        TextView currentPassTitle = view.findViewById(R.id.current_pass_title);
        TextView newPassTitle = view.findViewById(R.id.new_pass_title);
        TextView confirmPassTitle = view.findViewById(R.id.confirm_pass_title);

        currentPass = view.findViewById(R.id.current_pass);
        newPass = view.findViewById(R.id.new_pass);
        confirmPass = view.findViewById(R.id.confirm_pass);

        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }
        progressBar = view.findViewById(R.id.progress);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColor), PorterDuff.Mode.SRC_IN);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD, currentPassTitle, newPassTitle, confirmPassTitle, button);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_REGULAR, currentPass, newPass, confirmPass);


        confirmPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startSavePin();
                }
                return false;
            }
        });

        currentPassTitle.setVisibility(RaadApp.paygearCard.isProtected ? View.VISIBLE : View.GONE);
        currentPass.setVisibility(RaadApp.paygearCard.isProtected ? View.VISIBLE : View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSavePin();
            }
        });


        return view;
    }

    private void startSavePin() {
        String[] data = new String[]{currentPass.getText().toString(),
                newPass.getText().toString(),
                confirmPass.getText().toString()};
        if ((RaadApp.paygearCard.isProtected && TextUtils.isEmpty(data[0])) || TextUtils.isEmpty(data[1]) || TextUtils.isEmpty(data[2])) {
            Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!data[1].equals(data[2])) {
            Toast.makeText(getContext(), R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, String> map = new HashMap<>();
        if (RaadApp.paygearCard.isProtected)
            map.put("old_password", data[0]);
        map.put("new_password", data[1]);

        Web.getInstance().getWebService().setCreditCardPin(RaadApp.paygearCard.token, PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(SetCardPinFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    if (currentPass.getVisibility() == View.VISIBLE)
                        Toast.makeText(getContext(), R.string.card_pin_changed, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), R.string.card_pin_saved, Toast.LENGTH_SHORT).show();

                    RaadApp.paygearCard.isProtected = true;
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(SetCardPinFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        currentPass.setEnabled(!loading);
        newPass.setEnabled(!loading);
        confirmPass.setEnabled(!loading);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(R.string.ok));
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (loading)
            Utils.hideKeyboard(getContext(), button);
    }

}
