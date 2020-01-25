package net.iGap.fragments.giftStickers.giftCardDetail;

import androidx.lifecycle.MutableLiveData;

import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.rx.ObserverViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class MainStickerCardViewModel extends ObserverViewModel {
    private StructIGSticker structIGSticker;
    private MutableLiveData<Boolean> goNextLiveData = new MutableLiveData<>();

    public MainStickerCardViewModel(StructIGSticker structIGSticker) {
        this.structIGSticker = structIGSticker;
    }

    @Override
    public void subscribe() {
        if (structIGSticker.getGiftId() != null)
            StickerRepository.getInstance().getCardStatus(structIGSticker.getGiftId())
                    .subscribe(new IGSingleObserver<StructIGGiftSticker>(new CompositeDisposable()) {
                        @Override
                        public void onSuccess(StructIGGiftSticker giftSticker) {
                            goNextLiveData.postValue(giftSticker.isActive());
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            goNextLiveData.postValue(false);
                        }
                    });
    }

    public MutableLiveData<Boolean> getGoNextLiveData() {
        return goNextLiveData;
    }
}
