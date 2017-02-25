package com.iGap.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.StructSessionsGetActiveList;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserProfileGetSelfRemove;
import com.iGap.request.RequestUserProfileSetSelfRemove;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.R.id.st_layoutParent;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends Fragment {

    int poSelfRemove;
    private SharedPreferences sharedPreferences;
    private int poRbDialogSelfDestruction = 0;
    private int selfRemove;
    private ArrayList<StructSessionsGetActiveList> itemSessionsgetActivelist = new ArrayList<StructSessionsGetActiveList>();
    private TextView txtDestruction;

    private Realm mRealm;
    RealmChangeListener<RealmModel> userInfoListener;
    RealmUserInfo realmUserInfo;

    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_and_security, container, false);
    }

    @Override public void onResume() {
        super.onResume();

        if (realmUserInfo != null) {
            if (userInfoListener != null) {
                realmUserInfo.addChangeListener(userInfoListener);
            }
            selfRemove = realmUserInfo.getSelfRemove();
            setTextSelfDestructs();
        }
    }

    @Override public void onPause() {
        super.onPause();

        if (realmUserInfo != null) realmUserInfo.removeChangeListeners();
    }

    @Override public void onDestroy() {
        super.onDestroy();

        if (mRealm != null) mRealm.close();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fpac_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        mRealm = Realm.getDefaultInstance();

        realmUserInfo = mRealm.where(RealmUserInfo.class).findFirst();
        userInfoListener = new RealmChangeListener<RealmModel>() {
            @Override public void onChange(RealmModel element) {

                selfRemove = ((RealmUserInfo) element).getSelfRemove();
                setTextSelfDestructs();
            }
        };



        RelativeLayout parentPrivacySecurity = (RelativeLayout) view.findViewById(R.id.parentPrivacySecurity);
        parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stps_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentPrivacyAndSecurity.this).commit();
            }
        });

        TextView txtActiveSessions = (TextView) view.findViewById(R.id.stps_activitySessions);
        txtActiveSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentActiveSessions fragmentActiveSessions = new FragmentActiveSessions();

                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(st_layoutParent, fragmentActiveSessions, null).commit();


            }
        });

        TextView txtBlockedUser = (TextView) view.findViewById(R.id.stps_txt_blocked_user);
        txtBlockedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentBlockedUser fragmentBlockedUser = new FragmentBlockedUser();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.parentPrivacySecurity, fragmentBlockedUser, null).commit();
            }
        });


        txtDestruction = (TextView) view.findViewById(R.id.stps_txt_Self_destruction);

        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);
        ViewGroup ltSelfDestruction = (ViewGroup) view.findViewById(R.id.stps_layout_Self_destruction);
        ltSelfDestruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDestructs();
            }
        });



        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();

    }

    private void selfDestructs() {

        new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.self_destructs)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {
                        txtDestruction.setText(getResources().getString(R.string.month_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                        break;
                    }
                    case 1: {
                        txtDestruction.setText(getResources().getString(R.string.month_3));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                        break;
                    }
                    case 2: {

                        txtDestruction.setText(getResources().getString(R.string.month_6));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                        break;
                    }
                    case 3: {

                        txtDestruction.setText(getResources().getString(R.string.year_1));
                        new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                        break;
                    }
                }
                return false;
            }
        }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
    }

    private void setTextSelfDestructs() throws IllegalStateException {
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    txtDestruction.setText(getResources().getString(R.string.month_1));
                    poSelfRemove = 0;
                    break;
                case 3:
                    txtDestruction.setText(getResources().getString(R.string.month_3));
                    poSelfRemove = 1;
                    break;
                case 6:
                    txtDestruction.setText(getResources().getString(R.string.month_6));
                    poSelfRemove = 2;
                    break;
                case 12:
                    txtDestruction.setText(getResources().getString(R.string.year_1));
                    poSelfRemove = 3;
                    break;
            }
        } else {
            txtDestruction.setText(getResources().getString(R.string.month_6));
        }
    }
}
