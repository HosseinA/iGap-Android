package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.adapter.kuknos.WalletHistoryRAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosWHistoryBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosWHistoryVM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class KuknosWHistoryFrag extends BaseAPIViewFrag<KuknosWHistoryVM> {

    private FragmentKuknosWHistoryBinding binding;

    public static KuknosWHistoryFrag newInstance() {
        return new KuknosWHistoryFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosWHistoryVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_w_history, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosWHToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.kuknosWHistoryRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.kuknosWHistoryRecycler.setLayoutManager(layoutManager);

        viewModel.getDataFromServer();

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), operationResponsePage -> {
            if (operationResponsePage.getOperations().size() != 0) {
                WalletHistoryRAdapter mAdapter = new WalletHistoryRAdapter(viewModel.getListMutableLiveData().getValue(), getContext());
                binding.kuknosWHistoryRecycler.setAdapter(mAdapter);
            }
        });
    }

    private void onError() {
        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.kuknos_wHistory_dialogTitle))
                        .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                        .content(getResources().getString(R.string.kuknos_wHistory_error))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //close frag
                                popBackStackFragment();
                            }
                        })
                        .show();
            }
        });
    }

    private void onProgressVisibility() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.kuknosWHistoryProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.kuknosWHistoryProgressV.setVisibility(View.GONE);
            }
        });
    }

}
