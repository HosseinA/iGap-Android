package net.iGap.fragments.giftStickers.buyStickerCompleted;

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
import net.iGap.databinding.FragmentBuyGiftStickerComletedBinding;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.giftStickers.GiftStickerMainFragment;

public class BuyGiftStickerCompletedFragment extends Fragment {
    private StructIGSticker structIGSticker;
    private BuyGiftStickerCompletedBottomSheet.Delegate delegate;

    public static BuyGiftStickerCompletedFragment getInstance(StructIGSticker structIGSticker, BuyGiftStickerCompletedBottomSheet.Delegate delegate) {
        BuyGiftStickerCompletedFragment completedFragment = new BuyGiftStickerCompletedFragment();
        completedFragment.delegate = delegate;
        completedFragment.structIGSticker = structIGSticker;
        return completedFragment;
    }

    private BuyGiftStickerCompletedViewModel viewModel;
    private FragmentBuyGiftStickerComletedBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(BuyGiftStickerCompletedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_gift_sticker_comleted, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.stickerView.loadSticker(structIGSticker);

        if (delegate == null) {
            binding.positiveButton.setText(R.string.back_to_menu);
            binding.negativeButton.setVisibility(View.GONE);
        }

        binding.negativeButton.setOnClickListener(v -> {
            if (delegate != null) {
                delegate.onNegativeButton(structIGSticker);
                dismiss();
            } else {
                if (getParentFragment() instanceof GiftStickerMainFragment){
                    ((GiftStickerMainFragment) getParentFragment()).loadReceivedGiftStickerPage();
                }
            }
        });

        binding.positiveButton.setOnClickListener(v -> {
            if (delegate != null) {
                delegate.onPositiveButton(structIGSticker);
                dismiss();
            } else {
                if (getParentFragment() instanceof GiftStickerMainFragment){
                    ((GiftStickerMainFragment) getParentFragment()).goToHomePage();
                }
            }
        });

    }

    private void dismiss() {
        if (getParentFragment() instanceof BuyGiftStickerCompletedBottomSheet) {
            ((BuyGiftStickerCompletedBottomSheet) getParentFragment()).dismiss();
        }
    }
}
