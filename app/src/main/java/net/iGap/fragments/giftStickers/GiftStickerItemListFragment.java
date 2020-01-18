package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentGiftStickerItemBinding;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;

import org.jetbrains.annotations.NotNull;

public class GiftStickerItemListFragment extends Fragment {

    private GiftStickerItemListViewModel viewModel;
    private FragmentGiftStickerItemBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(GiftStickerItemListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_sticker_item, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.giftStickerItemList.setAdapter(new GiftStickerItemAdapter(position -> viewModel.onGiftStickerItemClicked(position)));

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && isGoBack != null && isGoBack) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });

        viewModel.getGoToBuyItemPage().observe(getViewLifecycleOwner(), item -> {

        });

        viewModel.getLoadData().observe(getViewLifecycleOwner(), data -> {
            if (binding.giftStickerItemList.getAdapter() instanceof GiftStickerItemAdapter && data != null) {
                ((GiftStickerItemAdapter) binding.giftStickerItemList.getAdapter()).setItems(data);
            }
        });

        viewModel.getGoToShowDetailPage().observe(getViewLifecycleOwner(),giftStickerItem->{
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && giftStickerItem != null){
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadStickerPackageItemDetailPage();
            }
        });

    }
}