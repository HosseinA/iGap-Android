package com.iGap.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.iGap.G;
import com.iGap.R;

public class AdapterViewPager extends PagerAdapter {
    int[] layout;

    public AdapterViewPager(int[] layout) {
        this.layout = layout;
    }

    @Override public int getCount() {
        return 5;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {

        View view = G.inflater.inflate(R.layout.view_pager_introduce_1, container, false);
        container.addView(view);
        return view;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }
}
