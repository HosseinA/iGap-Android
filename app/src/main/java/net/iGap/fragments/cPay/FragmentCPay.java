package net.iGap.fragments.cPay;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.adapter.cPay.AdapterPlaqueList;
import net.iGap.databinding.FragmentCpayBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCPay;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSeePayViewModel;

import java.util.ArrayList;


public class FragmentCPay extends BaseFragment implements ToolbarListener {

    private FragmentSeePayViewModel viewModel ;
    private FragmentCpayBinding binding ;
    private AdapterPlaqueList adapter ;
    private ArrayList<String> plaqueList = new ArrayList<>();

    public FragmentCPay() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentSeePayViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_cpay, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initRecyclerView();
        initCallBacks();

        HelperCPay.getPlaqueCode("الف");
        HelperCPay.getPlaqueCode("ب");
        HelperCPay.getPlaqueCode("ی");

    }

    private void initCallBacks() {

        viewModel.onAddClickListener.observe(getViewLifecycleOwner() , isOpen -> {
            openEditOrAddFragment(null);
        });

        viewModel.onInquiryClickListener.observe(getViewLifecycleOwner() , isOpen -> {
            if (getActivity() == null) return;

            if (adapter.getSelectedPlaqueList().size() == 0){
                Toast.makeText(getContext(), getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new HelperFragment(getActivity().getSupportFragmentManager() , FragmentCPayInquiry.getInstance(adapter.getSelectedPlaqueList().get(0)))
                    .setReplace(false)
                    .load();
        });
    }

    private void initRecyclerView() {

        plaqueList.add("110114566");
        plaqueList.add("183226588");

        adapter = new AdapterPlaqueList(getActivity());
        adapter.setPlaqueList(plaqueList);
        binding.rvPlaques.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPlaques.setAdapter(adapter);

        adapter.onEditClickListener.observe(getViewLifecycleOwner() , plaque -> {
            if (plaque != null){
               openEditOrAddFragment(plaque);
            }
        });
    }

    private void openEditOrAddFragment(String plaque) {
        if (getActivity() == null) return;

        new HelperFragment(getActivity().getSupportFragmentManager() , FragmentCPayEdit.getInstance(plaque))
                .setReplace(false)
                .load();
    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.see_pay_title))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
                .setListener(this);

        binding.fspToolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onRightIconClickListener(View view) {

        if (getActivity() == null) return;

        new HelperFragment(getActivity().getSupportFragmentManager() , new FragmentCPayHistory())
                .setReplace(false)
                .load();

    }
}
