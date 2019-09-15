package net.iGap.kuknos.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosShowRecoveryBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosShowRecoveryKeySVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosShowRecoveryKeySFrag extends BaseFragment {

    private FragmentKuknosShowRecoveryBinding binding;
    private KuknosShowRecoveryKeySVM kuknosShowRecoveryKeyVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosShowRecoveryKeySFrag newInstance() {
        KuknosShowRecoveryKeySFrag kuknosRestoreFrag = new KuknosShowRecoveryKeySFrag();
        return kuknosRestoreFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosShowRecoveryKeyVM = ViewModelProviders.of(this).get(KuknosShowRecoveryKeySVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_show_recovery, container, false);
        binding.setViewmodel(kuknosShowRecoveryKeyVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosSRToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onErrorObserver();
        onNextObserver();
    }

    private void onErrorObserver() {
        kuknosShowRecoveryKeyVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    if (errorM.getMessage().equals("1")) {
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosSRContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void onNextObserver() {
        kuknosShowRecoveryKeyVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    popBackStackFragment();
                }
            }
        });
    }

}
