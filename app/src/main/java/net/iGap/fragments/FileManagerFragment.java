package net.iGap.fragments;

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
import net.iGap.databinding.FileManagerFragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.IPickFile;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FileManagerViewModel;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.fragments.FileManagerChildFragment.ROOT_FILE_MANAGER;

public class FileManagerFragment extends BaseFragment implements ToolbarListener {

    private static String LISTENER_KEY = "Listener";
    private FileManagerViewModel mViewModel;
    private FileManagerFragmentBinding binding;
    private HelperToolbar mHelperToolbar;
    private IPickFile mListener;

    public static FileManagerFragment newInstance(IPickFile listener) {
        FileManagerFragment fragment = new FileManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LISTENER_KEY, listener);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.file_manager_fragment, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FileManagerViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        getDataFromArguments();
        setupListeners();
        loadFragment(FileManagerChildFragment.newInstance(ROOT_FILE_MANAGER), FileManagerChildFragment.class.getName());
    }

    private void setupListeners() {
        mViewModel.getSendClickListener().observe(getViewLifecycleOwner(), state -> {
            //todo: fill items
            mListener.onPick(new ArrayList<>());
        });
    }

    private void getDataFromArguments() {
        if (getArguments() != null) {
            mListener = (IPickFile) getArguments().getSerializable(LISTENER_KEY);
        }
    }

    private void setupToolbar() {
        mHelperToolbar = HelperToolbar.create()
                .setContext(getActivity())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.search_icon, R.string.sort_icon)
                .setListener(this);
        binding.toolbar.addView(mHelperToolbar.getView());
    }

    public void setToolbarTitle(String title) {
        if (mHelperToolbar != null) mHelperToolbar.setDefaultTitle(title);
    }

    private void onSortClicked() {
        if (getActivity() == null) return;
        Fragment fragment = getTopFragment();
        if (fragment instanceof FileManagerChildFragment) {
            ((FileManagerChildFragment) fragment).onSortClicked(true);
        }
    }

    private void onToolbarClicked() {
        if (getActivity() == null) return;
        Fragment fragment = getTopFragment();
        if (fragment instanceof FileManagerChildFragment) {
            ((FileManagerChildFragment) fragment).onToolbarClicked();
        }
    }

    private void onSearchClicked() {

    }

    private boolean onBackClicked() {
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    private Fragment getTopFragment() {
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            return getChildFragmentManager().getFragments().get(count - 1);
        }
        return null;
    }

    public void loadFragment(Fragment fragment, String tag) {
        if (getActivity() == null) return;
        getChildFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .add(R.id.frmFolders, fragment, tag)
                .commit();
    }

    void sendResult(List<String> items) {
        if (mListener != null) {
            mListener.onPick(items);
        }
    }

    void closeFileManager() {
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (!onBackPressed()) popBackStackFragment();
    }

    @Override
    public void onRightIconClickListener(View view) {
        onSearchClicked();
    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        onSortClicked();
    }

    @Override
    public void onToolbarTitleClickListener(View view) {
        onToolbarClicked();
    }

    @Override
    public boolean onBackPressed() {
        return onBackClicked();
    }

}
