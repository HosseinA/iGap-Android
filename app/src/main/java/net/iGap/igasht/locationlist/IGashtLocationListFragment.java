package net.iGap.igasht.locationlist;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.favoritelocation.IGashtFavoritePlaceListFragment;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationdetail.IGashtLocationDetailFragment;
import net.iGap.interfaces.ToolbarListener;

public class IGashtLocationListFragment extends IGashtBaseView {

    private FragmentIgashtLocationBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location, container, false);
        binding.setViewModel((IGashtLocationViewModel) viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.score_star_icon, R.string.history_icon)
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
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtFavoritePlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }
                }).getView());

        binding.locationListView.addItemDecoration(new DividerItemDecoration(binding.locationListView.getContext(), DividerItemDecoration.VERTICAL));
        binding.locationListView.setAdapter(new IGashtLocationListAdapter());

        ((IGashtLocationViewModel) viewModel).getLocationList().observe(getViewLifecycleOwner(), data -> {
            if (binding.locationListView.getAdapter() instanceof IGashtLocationListAdapter && data != null) {
                ((IGashtLocationListAdapter) binding.locationListView.getAdapter()).setItems(data, new IGashtLocationListAdapter.onLocationItemClickListener() {
                    @Override
                    public void addToFavorite(int position) {
                        ((IGashtLocationViewModel) viewModel).addToFavorite(position);
                    }

                    @Override
                    public void buyTicket(int position) {
                        ((IGashtLocationViewModel) viewModel).buyTicket(position);
                    }

                    @Override
                    public void onItem(int position) {
                        ((IGashtLocationViewModel) viewModel).buyTicket(position);
                    }
                });
            }
        });

        ((IGashtLocationViewModel) viewModel).getGoToLocationDetail().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null) {
                if (isGo) {
                    new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(new IGashtLocationDetailFragment()).setReplace(false).load(true);
                } else {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((IGashtLocationViewModel) viewModel).getAddToFavorite().observe(getViewLifecycleOwner(), aBoolean -> Toast.makeText(getContext(), "add to favorite", Toast.LENGTH_SHORT).show());
    }
}