package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosLoginBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosLoginVM;
import net.iGap.libs.bottomNavigation.Util.Utils;


public class KuknosLoginFrag extends BaseFragment {

    private FragmentKuknosLoginBinding binding;
    private KuknosLoginVM kuknosLoginVM;
    private HelperToolbar mHelperToolbar;


    public static KuknosLoginFrag newInstance() {
        KuknosLoginFrag kuknosLoginFrag = new KuknosLoginFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosLoginVM = ViewModelProviders.of(this).get(KuknosLoginVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_login, container, false);
        binding.setViewmodel(kuknosLoginVM);
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
                        kuknosLoginVM.getNextPage().setValue(false);
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosIdToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onErrorObserver();
        onNext();
        progressState();
    }

    private void onErrorObserver() {

        kuknosLoginVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    //TODO clear Log
                    if (errorM.getMessage().equals("0")) {
                        binding.fragKuknosIdUserIDHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosIdUserID.requestFocus();
                    }
                    else if (errorM.getMessage().equals("1")){
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosLoginContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
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

    private void onNext() {

        kuknosLoginVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosEntryOptionFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosEntryOptionFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    //fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                }
            }
        });

    }

    private void progressState() {
        kuknosLoginVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                    binding.fragKuknosIdSubmit.setEnabled(false);
                    binding.fragKuknosIdUserID.setEnabled(false);
                    binding.fragKuknosLProgressV.setVisibility(View.VISIBLE);
                }
                else {
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_submit_str));
                    binding.fragKuknosIdSubmit.setEnabled(true);
                    binding.fragKuknosIdUserID.setEnabled(true);
                    binding.fragKuknosLProgressV.setVisibility(View.GONE);
                }
            }
        });
    }

}
