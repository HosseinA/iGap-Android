package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentMyGiftStickerBuyBinding;
import net.iGap.helper.HelperError;

public class MyGiftStickerBuyFragment extends Fragment {

    private FragmentMyGiftStickerBuyBinding binding;
    private MyGiftStickerBuyViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MyGiftStickerBuyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_gift_sticker_buy, container, false);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.giftStickerList.setAdapter(new MyStickerListAdapter());

        viewModel.getLoadStickerList().observe(getViewLifecycleOwner(), stickerList -> {
            if (binding.giftStickerList.getAdapter() instanceof MyStickerListAdapter && stickerList != null) {
                ((MyStickerListAdapter) binding.giftStickerList.getAdapter()).setItems(stickerList);
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });
    }
}
