package net.iGap.rx;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;

public abstract class ObserverFragment<T extends ObserverViewModel> extends BaseAPIViewFrag<T> implements EventListener {
    protected T viewModel;

    public abstract T getObserverViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        viewModel = getObserverViewModel();

        setViewModel(viewModel);

        if (viewModel == null)
            throw new NullPointerException("You must set observerViewModel with getObserverViewModel() method");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.onFragmentViewCreated();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.subscribe();
        EventManager.getInstance().addEventListener(EventManager.IG_ERROR, this);
        Log.e(getClass().getName(), "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onDestroyView();
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        Log.e(getClass().getName(), "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        Log.e(getClass().getName(), "onDestroy: ");
    }

    public void onResponseError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.IG_ERROR) {
            try {
                onResponseError((Throwable) message[0]);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
