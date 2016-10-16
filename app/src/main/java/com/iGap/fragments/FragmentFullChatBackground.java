package com.iGap.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SHP_SETTING;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFullChatBackground extends Fragment {

    private TextView txtBack, txtSet;
    private ImageView imgFullImage;
    private RippleView rippleSet, rippleBack;


    public FragmentFullChatBackground() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_chat_background, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String fullImage = getArguments().getString("IMAGE");

        txtBack = (TextView) view.findViewById(R.id.stcbf_txt_back);
        rippleBack = (RippleView) view.findViewById(R.id.stcbf_ripple_back);
        rippleSet = (RippleView) view.findViewById(R.id.stcbf_ripple_set);
        txtBack.setTypeface(G.fontawesome);

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentFullChatBackground.this).commit();
            }
        });

        imgFullImage = (ImageView) view.findViewById(R.id.stchf_fullImage);
        Bitmap b = BitmapFactory.decodeFile(fullImage);
        imgFullImage.setImageBitmap(b);

        txtSet = (MaterialDesignTextView) view.findViewById(R.id.stcbf_txt_set);
        rippleSet.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, fullImage);
                editor.apply();

                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentFullChatBackground.this).commit();
            }
        });

    }
}
