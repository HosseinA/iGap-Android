package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MyAppBarLayout;

public abstract class FragmentToolBarBack extends BaseFragment {

    public static int numberOfVisible = 0;
    protected MyAppBarLayout appBarLayout;
    protected TextView titleTextView;
    protected TextView menu_item1;
    protected boolean isSwipeBackEnable = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        numberOfVisible++;

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.toolbar_back_fragment, container, false);
        onCreateViewBody(inflater, view, savedInstanceState);
        if (isSwipeBackEnable) {
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    public abstract void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numberOfVisible--;
        if (G.fragmentActivity instanceof ActivityMain) {
            ((ActivityMain) G.fragmentActivity).openNavigation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (G.fragmentActivity instanceof ActivityMain) {
            ((ActivityMain) G.fragmentActivity).lockNavigation();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.title);
        titleTextView.setTypeface(G.typeface_IRANSansMobile);
        appBarLayout = view.findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        menu_item1 = view.findViewById(R.id.menu_item1);
        menu_item1.setVisibility(View.GONE);

        RippleView rippleBackButton = (RippleView) view.findViewById(R.id.chl_ripple_back_Button);
        rippleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackButtonClicked(view);
            }
        });
    }

    protected void onBackButtonClicked(View view) {
        closeKeyboard(view);
        popBackStackFragment();
    }
}
