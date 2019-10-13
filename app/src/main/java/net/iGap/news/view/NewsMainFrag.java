package net.iGap.news.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.NewsMainPageBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsMainBTN;
import net.iGap.news.view.Adapter.NewsFirstPageAdapter;
import net.iGap.news.viewmodel.NewsMainVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsMainFrag extends BaseAPIViewFrag {

    private NewsMainPageBinding binding;
    private NewsMainVM newsMainVM;

    private String specificNewsID = null;
    private String specificGroupID = null;

    public static NewsMainFrag newInstance() {
        return new NewsMainFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsMainVM = ViewModelProviders.of(this).get(NewsMainVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_main_page, container, false);
//        binding.setViewmodel(newsMainVM);
        binding.setLifecycleOwner(this);
        this.viewModel = newsMainVM;

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (specificGroupID != null && !specificGroupID.equals(""))
            openGroupNews(new NewsFPList("IGap News", specificGroupID, null));
        else if (specificNewsID != null && !specificNewsID.equals(""))
            openNewsDetail(specificNewsID);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setDefaultTitle(getResources().getString(R.string.news_mainTitle))
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.rcMain.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcMain.setLayoutManager(layoutManager);

        binding.pullToRefresh.setOnRefreshListener(() -> {
            newsMainVM.getNews();
            binding.noItemInListError.setVisibility(View.GONE);
        });

        newsMainVM.getNews();
        onErrorObserver();
        onDataChanged();
        onProgress();
    }

    private void onErrorObserver() {
        newsMainVM.getError().observe(getViewLifecycleOwner(), newsError -> {
            if (newsError.getState()) {
                //show the related text
                binding.noItemInListError.setVisibility(View.VISIBLE);
                // show error
                Snackbar snackbar = Snackbar.make(binding.Container, getString(newsError.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }

    private void onProgress() {
        newsMainVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> binding.pullToRefresh.setRefreshing(aBoolean));
    }

    private void onDataChanged() {
        newsMainVM.getMainList().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

    private void initMainRecycler(List<NewsFirstPage> data) {
        List<NewsFirstPage> temp = new ArrayList<>(data);
        NewsFirstPageAdapter adapter = new NewsFirstPageAdapter(temp);
        adapter.setCallBack(new NewsFirstPageAdapter.onClickListener() {
            @Override
            public void onButtonClick(NewsMainBTN btn) {
                // open Link
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(btn.getLink())) {
                    HelperUrl.openBrowser(btn.getLink());
                } else {
                    HelperUrl.openWithoutBrowser(btn.getLink());
                }
            }

            @Override
            public void onNewsCategoryClick(NewsFPList group) {
                openGroupNews(group);
            }

            @Override
            public void onSliderClick(NewsFPList.NewsContent slide) {
                openNewsDetail(slide.getId());
            }
        });
        binding.rcMain.setAdapter(adapter);
    }

    private void openGroupNews(NewsFPList group) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NewsGroupPagerFrag.class.getName());
        if (fragment == null) {
            fragment = NewsGroupPagerFrag.newInstance();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        Bundle args = new Bundle();
        args.putString("GroupID", group.getCatID());
        args.putString("GroupTitle", group.getCategory());
        if (group.getNews() == null)
            args.putString("GroupPic", "");
        else
            args.putString("GroupPic", group.getNews().get(0).getContents().getImage().get(0).getOriginal());
        fragment.setArguments(args);
        new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), fragment).setReplace(false).load();
    }

    private void openNewsDetail(String newsID) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(NewsDetailFrag.class.getName());
        if (fragment == null) {
            fragment = NewsDetailFrag.newInstance();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        Bundle args = new Bundle();
        args.putString("NewsID", newsID);
        fragment.setArguments(args);
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
    }

    public void setSpecificNewsID(String specificNewsID) {
        this.specificNewsID = specificNewsID;
    }

    public void setSpecificGroupID(String specificGroupID) {
        this.specificGroupID = specificGroupID;
    }
}
