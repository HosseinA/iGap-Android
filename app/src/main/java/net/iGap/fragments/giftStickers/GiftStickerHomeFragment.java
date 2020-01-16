package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.fragments.giftStickers.enterNationalCode.EnterNationalCodeFragment;

public class GiftStickerHomeFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gift_sticker_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChildFragmentManager().beginTransaction().replace(R.id.header,new EnterNationalCodeFragment(),EnterNationalCodeFragment.class.getName()).commit();

        view.findViewById(R.id.myGiftStickerPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragment() instanceof GiftStickerMainFragment){
                    ((GiftStickerMainFragment) getParentFragment()).loadBuyMySticker();
                }
            }
        });

        view.findViewById(R.id.receivedGiftStickerPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragment() instanceof GiftStickerMainFragment){
                    ((GiftStickerMainFragment) getParentFragment()).loadReceivedGiftStickerPage();
                }
            }
        });
    }
}
