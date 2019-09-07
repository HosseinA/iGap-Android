package net.iGap.igasht.historylocation;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtHistoryPlaceBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.barcodescaner.FragmentIgashtBarcodeScan;
import net.iGap.interfaces.ToolbarListener;

public class IGashtHistoryPlaceListFragment extends IGashtBaseView {

    private FragmentIgashtHistoryPlaceBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtHistoryPlaceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_history_place, container, false);
        binding.setViewModel((IGashtHistoryPlaceViewModel) viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.order_history))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        binding.favoriteList.setAdapter(new PlaceHistoryAdapter(position -> ((IGashtHistoryPlaceViewModel) viewModel).onClickHistoryItem(position)));
        binding.favoriteList.addItemDecoration(new DividerItemDecoration(binding.favoriteList.getContext(), DividerItemDecoration.VERTICAL));
        binding.favoriteList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.getLayoutManager() != null) {
                    ((IGashtHistoryPlaceViewModel)viewModel).loadMoreItems(recyclerView.getLayoutManager().getItemCount(), ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition());
                }
            }
        });

        ((IGashtHistoryPlaceViewModel) viewModel).getHistoryList().observe(getViewLifecycleOwner(), data -> {
            if (data != null && binding.favoriteList.getAdapter() instanceof PlaceHistoryAdapter) {
                ((PlaceHistoryAdapter) binding.favoriteList.getAdapter()).setItems(data);
            }
        });

        ((IGashtHistoryPlaceViewModel) viewModel).getGoToTicketDetail().observe(getViewLifecycleOwner(), voucherNumber -> {
            if (getActivity() != null && voucherNumber != null) {
                Fragment fragment = new FragmentIgashtBarcodeScan();
                Bundle bundle = new Bundle();
                bundle.putString("voucher_number", voucherNumber);
                fragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(true);
            }
        });
    }
}
