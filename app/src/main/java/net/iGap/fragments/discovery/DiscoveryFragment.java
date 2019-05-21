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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.helper.HelperError;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends FragmentToolBarBack {
    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int page;
    private View view;
    private boolean isInit = false;
    DiscoveryAdapter adapterDiscovery;

    public static DiscoveryFragment newInstance(int page) {
        DiscoveryFragment discoveryFragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        discoveryFragment.setArguments(bundle);
        if (page == 0) {
            discoveryFragment.isSwipeBackEnable = false;
        }
        return discoveryFragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isInit) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 400);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerView.Adapter adapter = rcDiscovery.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_discovery, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        page = getArguments().getInt("page");
        if (page == 0) {
            appBarLayout.setVisibility(View.GONE);
        }

        init();
    }

    private void init() {
        if (view == null) {
            return;
        }
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        rcDiscovery = view.findViewById(R.id.rcDiscovery);

        if (!getUserVisibleHint()) {
            if (!isInit) {
                setRefreshing(true);
            }
            return;
        }
        isInit = true;
//        setRefreshing(false);

        adapterDiscovery = new DiscoveryAdapter(getActivity(), new ArrayList<>());
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDiscovery.setLayoutManager(layoutManager);
        rcDiscovery.setAdapter(adapterDiscovery);
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
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();

        if (!isSend && page == 0) {

            loadOfflinePageZero();

            if (count < 3) {
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryToUpdateOrFetchRecycleViewData(count + 1);
                    }
                }, 1000);
            } else {
                setRefreshing(false);
            }
        } else if (!isSend) {
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
