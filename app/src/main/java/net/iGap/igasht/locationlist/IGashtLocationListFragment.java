package net.iGap.igasht.locationlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationdetail.IGashtLocationDetailFragment;
import net.iGap.interfaces.ToolbarListener;

public class IGashtLocationListFragment extends IGashtBaseView {

    private FragmentIgashtLocationBinding binding;
    private IGashtLocationViewModel iGashtLocationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iGashtLocationViewModel = ViewModelProviders.of(this).get(IGashtLocationViewModel.class);
        viewModel = iGashtLocationViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location, container, false);
        binding.setViewModel(iGashtLocationViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(/*R.string.score_star_icon,*/ R.string.history_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                 /*   @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }*/
                }).getView());

        binding.locationListView.addItemDecoration(new DividerItemDecoration(binding.locationListView.getContext(), DividerItemDecoration.VERTICAL));
        binding.locationListView.setAdapter(new IGashtLocationListAdapter(iGashtLocationViewModel.getSelectedProvinceName(), new IGashtLocationListAdapter.onLocationItemClickListener() {
            @Override
            public void buyTicket(int position) {
                iGashtLocationViewModel.buyTicket(position);
            }

            @Override
            public void onItem(int position) {
                iGashtLocationViewModel.buyTicket(position);
            }
        }));

        iGashtLocationViewModel.getLocationList().observe(getViewLifecycleOwner(), data -> {
            if (binding.locationListView.getAdapter() instanceof IGashtLocationListAdapter && data != null) {
                ((IGashtLocationListAdapter) binding.locationListView.getAdapter()).setItems(data);
            }
        });


        iGashtLocationViewModel.getGoToLocationDetail().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null) {
                if (isGo) {
                    new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(new IGashtLocationDetailFragment()).setReplace(false).load(true);
                } else {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        ((IGashtLocationViewModel) viewModel).getAddToFavorite().observe(getViewLifecycleOwner(), aBoolean -> Toast.makeText(getContext(), "add to favorite", Toast.LENGTH_SHORT).show());

    }

}
