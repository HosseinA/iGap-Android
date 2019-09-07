package net.iGap.fragments;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentDailNumberBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.DailNumberViewModel;

public class DailNumberFragment extends Fragment {

    private FragmentDailNumberBinding binding;
    private DailNumberViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dail_number, container, false);

        viewModel = new DailNumberViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create().setContext(getContext()).setLeftIcon(R.string.back_icon).setLogoShown(true).setListener(new ToolbarListener() {
            @Override
            public void onLeftIconClickListener(View view) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        binding.toolbar.addView(t.getView());
    }
}
