package net.iGap.mobileBank.view;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.databinding.FragmentMobileBankHomeBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.view.adapter.BankAccountAdapter;
import net.iGap.mobileBank.view.adapter.BankCardsAdapter;
import net.iGap.mobileBank.viewmoedel.MobileBankHomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeFragment extends BaseMobileBankFragment<MobileBankHomeViewModel> {

    private FragmentMobileBankHomeBinding binding;

    public MobileBankHomeFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_bank_home, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankHomeViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupListeners();
    }

    private void setupListeners() {

        viewModel.getCardsData().observe(getViewLifecycleOwner(), this::saveCardsTodb);
        viewModel.getAccountsData().observe(getViewLifecycleOwner(), this::setupAccounts);

        viewModel.onTempPassListener.observe(getViewLifecycleOwner(), state -> {

            if (state != null && state && getActivity() != null) {
                new DialogParsian()
                        .setContext(getActivity())
                        .setTitle(getString(R.string.message))
                        .setButtonsText(getString(R.string.ok), getString(R.string.cancel))
                        .setListener(new DialogParsian.ParsianDialogListener() {
                            @Override
                            public void onActiveButtonClicked(Dialog dialog) {

                            }

                            @Override
                            public void onDeActiveButtonClicked(Dialog dialog) {

                            }
                        }).showSimpleMessage(getString(R.string.kuknos_buyP_MaxAmount));
            }

        });

        viewModel.onMoneyTransferListener.observe(getViewLifecycleOwner(), state -> {

            if (state != null && state && getActivity() != null) {
                new DialogParsian()
                        .setContext(getActivity())
                        .setTitle(getString(R.string.transfer_mony))
                        .setButtonsText(getString(R.string.ok), getString(R.string.cancel))
                        .setListener(new DialogParsian.ParsianDialogListener() {
                            @Override
                            public void onActiveButtonClicked(Dialog dialog) {

                            }

                            @Override
                            public void onTransferMoneyTypeSelected(Dialog dialog, String type) {
                                dialog.dismiss();
                            }
                        }).showMoneyTransferDialog();
            }

        });

        viewModel.onTransactionListener.observe(getViewLifecycleOwner(), state -> {

            if (state != null && state && getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new MobileBankCardHistoryFragment())
                        .setReplace(false)
                        .load();
            }

        });

        int textStyleOn, textStyleOff;
        textStyleOff = Theme.getInstance().getSubTitleColor(binding.lblPassword.getContext());
        textStyleOn = Theme.getInstance().getPrimaryTextIconColor(binding.lblPassword.getContext());

        viewModel.onTabChangeListener.observe(getViewLifecycleOwner(), isCardsEnable -> {
            if (isCardsEnable != null) {
                if (isCardsEnable) {
                    binding.lblPassword.setTextColor(textStyleOn);
                    binding.icPassword.setTextColor(textStyleOn);
                    binding.lblSheba.setTextColor(textStyleOff);
                    binding.icSheba.setTextColor(textStyleOff);
                } else {
                    binding.lblPassword.setTextColor(textStyleOff);
                    binding.icPassword.setTextColor(textStyleOff);
                    binding.lblSheba.setTextColor(textStyleOn);
                    binding.icSheba.setTextColor(textStyleOn);
                }
            }
        });
    }

    private void saveCardsTodb(List<BankCardModel> bankCardModels) {
        RealmMobileBankCards.putOrUpdate(bankCardModels, this::setupViewPager);
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.back_icon)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }

    private void setupAccounts(List<BankAccountModel> accountModels){

        if (accountModels == null) return;

        BankAccountAdapter adapter = new BankAccountAdapter();
        adapter.setItems(accountModels);
        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(binding.rvAccounts.getContext() , LinearLayoutManager.HORIZONTAL , false));
        binding.rvAccounts.setAdapter(adapter);
    }

    private void setupViewPager() {

        List<RealmMobileBankCards> cards = new ArrayList<>(RealmMobileBankCards.getCards());
        binding.vpCards.setAdapter(new BankCardsAdapter(cards));
        binding.vpCards.setOffscreenPageLimit(cards.size() - 1);

        binding.vpCards.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setEnableIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createViewPagerIndicators(cards.size());
    }

    private void createViewPagerIndicators(int size) {
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(binding.lytIndicators.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(28, 28);
            lp.setMargins(6, 6, 6, 6);
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.drawable.indicator_slider);
            if (i == 0) iv.setSelected(true);
            binding.lytIndicators.addView(iv);
        }
    }

    private void setEnableIndicator(int position) {
        int count = binding.lytIndicators.getChildCount();
        if (position > count) return;
        for (int i = 0; i < count; i++) {
            binding.lytIndicators.getChildAt(i).setSelected(i == position);
        }
    }
}
