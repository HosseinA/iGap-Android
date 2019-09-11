package net.iGap.kuknos.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosViewRecoveryEpBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosViewRecoveryEPVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosViewRecoveryEPFrag extends BaseFragment {

    private FragmentKuknosViewRecoveryEpBinding binding;
    private KuknosViewRecoveryEPVM kuknosViewRecoveryEPVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosViewRecoveryEPFrag newInstance() {
        KuknosViewRecoveryEPFrag kuknosLoginFrag = new KuknosViewRecoveryEPFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosViewRecoveryEPVM = ViewModelProviders.of(this).get(KuknosViewRecoveryEPVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_view_recovery_ep, container, false);
        binding.setViewmodel(kuknosViewRecoveryEPVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosVRToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onNextPage();
        onError();
        onProgress();
        entryListener();
    }


    private void onError() {
        kuknosViewRecoveryEPVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true && errorM.getMessage().equals("0")) {
                    binding.fragKuknosVRPassHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosVRPassHolder.requestFocus();
                } else if (errorM.getState() == true && errorM.getMessage().equals("1")) {
                    showDialog(errorM.getResID());
                }
            }
        });
    }

    private void showDialog(int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosViewRecoveryEPVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosVRProgressV.setVisibility(View.VISIBLE);
                    binding.fragKuknosVRPass.setEnabled(false);
                    binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_viewRecoveryEP_load));
                } else {
                    binding.fragKuknosVRProgressV.setVisibility(View.GONE);
                    binding.fragKuknosVRPass.setEnabled(true);
                    binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_viewRecoveryEP_btn));
                }
            }
        });
    }

    private void entryListener() {
        binding.fragKuknosVRPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosVRPassHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onNextPage() {
        kuknosViewRecoveryEPVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    popBackStackFragment();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosShowRecoveryKeySFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosShowRecoveryKeySFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        });
    }

}