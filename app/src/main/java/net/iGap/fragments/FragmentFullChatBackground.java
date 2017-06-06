/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFullChatBackground extends Fragment {

    private MaterialDesignTextView txtBack, txtSet;
    private ImageView imgFullImage;
    private RippleView rippleSet, rippleBack;
    private FragmentActivity mActivity;

    public FragmentFullChatBackground() {
        // Required empty public constructor
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_chat_background, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.ffcb_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        final String fullImage = getArguments().getString("IMAGE");

        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.stcbf_root);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        txtBack = (MaterialDesignTextView) view.findViewById(R.id.stcbf_txt_back);
        rippleBack = (RippleView) view.findViewById(R.id.stcbf_ripple_back);
        rippleSet = (RippleView) view.findViewById(R.id.stcbf_ripple_set);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                mActivity.getSupportFragmentManager().beginTransaction().remove(FragmentFullChatBackground.this).commit();
            }
        });

        imgFullImage = (ImageView) view.findViewById(R.id.stchf_fullImage);
        //        Bitmap b = BitmapFactory.decodeFile(fullImage);

        G.imageLoader.displayImage(AndroidUtils.suitablePath(fullImage), imgFullImage);

        txtSet = (MaterialDesignTextView) view.findViewById(R.id.stcbf_txt_set);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, fullImage);
                editor.apply();

                mActivity.getSupportFragmentManager().beginTransaction().remove(FragmentFullChatBackground.this).commit();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
