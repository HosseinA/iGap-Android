package net.iGap.fragments.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.helper.HelperError;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends FragmentToolBarBack {
    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int page;
    DiscoveryAdapter adapterDiscovery;

    public static DiscoveryFragment newInstance(int page) {
        DiscoveryFragment discoveryFragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    public DiscoveryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerView.Adapter adapter = rcDiscovery.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        page = getArguments().getInt("page");
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        if (page == 0) {
            return view;
        } else {
            return attachToSwipeBack(view);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterDiscovery = new DiscoveryAdapter(getActivity(), new ArrayList<>());
        emptyRecycle = view.findViewById(R.id.emptyRecycle);

        if (page == 0) {
            appBarLayout.setVisibility(View.GONE);
        }

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRefreshing(true);
                boolean isSend = updateOrFetchRecycleViewData();
                if (!isSend) {
                    setRefreshing(false);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        emptyRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSend = updateOrFetchRecycleViewData();
                if (!isSend) {
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        rcDiscovery = view.findViewById(R.id.rcDiscovery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDiscovery.setLayoutManager(layoutManager);
        rcDiscovery.setAdapter(adapterDiscovery);

        setRefreshing(true);

        tryToUpdateOrFetchRecycleViewData(0);
    }

    private void setRefreshing(boolean value) {
        pullToRefresh.setRefreshing(value);
        if (value) {
            emptyRecycle.setVisibility(View.GONE);
        } else {
            if (adapterDiscovery.getItemCount() == 0) {
                emptyRecycle.setVisibility(View.VISIBLE);
            } else {
                emptyRecycle.setVisibility(View.GONE);
            }
        }
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        boolean isSend = updateOrFetchRecycleViewData();
        if (!isSend && page == 0 && count < 3) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryToUpdateOrFetchRecycleViewData(count + 1);
                }
            }, 1000);
        } else {
            setRefreshing(false);
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        boolean isSend = new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (page == 0) {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            String cache = gson.toJson(discoveryArrayList);
                            edit.putString("page0", cache).apply();
                            edit.putString("title", title).apply();
                        }
                        setAdapterData(discoveryArrayList, title);

                        setRefreshing(false);
                    }
                });
            }

            @Override
            public void onError() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (page == 0) {
                            loadOfflinePageZero();
                        }

                        setRefreshing(false);
                    }
                });
            }
        });

        if (isSend) {
            setRefreshing(true);
        } else {
            if (page == 0) {
                loadOfflinePageZero();
            }
        }

        return isSend;
    }

    private void setAdapterData(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
        adapterDiscovery.setDiscoveryList(discoveryArrayList);
        titleTextView.setText(title);
        adapterDiscovery.notifyDataSetChanged();
    }

    private void loadOfflinePageZero() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
        String json = pref.getString("page0", "");
        String title = pref.getString("title", "");
        if (json != null && !json.equals("")) {
            ArrayList<DiscoveryItem> discoveryArrayList = gson.fromJson(json, new TypeToken<ArrayList<DiscoveryItem>>() {
            }.getType());
            setAdapterData(discoveryArrayList, title);
        }
    }
}
