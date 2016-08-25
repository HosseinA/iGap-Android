package com.iGap.activitys;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterViewPager;
import com.iGap.helper.HelperString;
import com.iGap.interface_package.OnReceiveInfoLocation;
import com.iGap.interface_package.OnReceivePageInfoTOS;
import com.iGap.module.CustomCircleImage;
import com.iGap.proto.ProtoInfoLocation;
import com.iGap.proto.ProtoInfoPage;
import com.iGap.proto.ProtoRequest;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestWrapper;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.util.Locale;

public class ActivityIntroduce extends ActivityEnhanced {

    private ViewPager viewPager;
    private AdapterViewPager adapterViewPager;
    private int[] layout;

    private CustomCircleImage circleButton;

    private boolean isOne0 = true;
    private boolean isOne1 = true;
    private boolean isOne2 = true;
    private boolean isOne3 = true;
    private boolean isOne4 = true;

    private ImageView logoIgap, logoSecurity, logoChat, transfer, boy;

    private TextView txt_i_p1_l1, txt_p1_l1, txt_p1_l2, txt_p1_l3, txt_p2_l1, txt_p2_l2, txt_p3_l1, txt_p3_l2, txt_p4_l1, txt_p4_l2, txt_p5_l1, txt_p5_l2;

    private Button btnStart;

    private ViewGroup layout_test;

    private JustifiedTextView justifiedTextView;

    private String isoCode = null, countryName, pattern, regex, body = null;
    private int callingCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmUserInfo userInfo = G.realm.where(RealmUserInfo.class).findFirst();
        if (userInfo != null && userInfo.getUserRegistrationState()) { // user registered before
            Intent intent = new Intent(G.context, ActivityMain.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_introduce);
        getTermsOfServiceBody();
        getInfoLocation();

        layout_test = (ViewGroup) findViewById(R.id.int_layout_test);

        layout = new int[]{
                R.layout.view_pager_introduce_1,
        };

        viewPager = (ViewPager) findViewById(R.id.int_viewPager_introduce);

        circleButton = (CustomCircleImage) findViewById(R.id.int_circleButton_introduce);
        if (circleButton != null) {
            circleButton.circleButtonCount(5);
        }

        logoIgap = (ImageView) findViewById(R.id.int_img_logo_introduce);
        txt_p1_l1 = (TextView) findViewById(R.id.int_txt_p1_l1);
        txt_i_p1_l1 = (TextView) findViewById(R.id.int_txt_i_p1_l1);
        txt_p1_l2 = (TextView) findViewById(R.id.int_txt_p1_l2);
        txt_p1_l3 = (TextView) findViewById(R.id.int_txt_p1_l3);

        if (Locale.getDefault().getLanguage().equals("en")) {
            txt_p1_l1.setTypeface(G.FONT_IGAP);
            txt_i_p1_l1.setTypeface(G.FONT_IGAP);
            txt_p1_l2.setTypeface(G.ARIAL_TEXT);
            txt_p1_l3.setTypeface(G.ARIAL_TEXT);
        } else {
            txt_p1_l1.setTypeface(G.YEKAN_BOLD);
            txt_p1_l1.setTextSize(getResources().getDimension(R.dimen.sp14));
            txt_i_p1_l1.setTypeface(G.YEKAN_BOLD);
            txt_i_p1_l1.setTextSize(getResources().getDimension(R.dimen.sp14));
            txt_p1_l2.setTypeface(G.YEKAN_FARSI);
            txt_p1_l3.setTypeface(G.YEKAN_FARSI);
        }

        txt_p1_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page1)));

        logoSecurity = (ImageView) findViewById(R.id.int_img_security_introduce);
        txt_p2_l1 = (TextView) findViewById(R.id.int_txt_p2_l1);
        txt_p2_l2 = (TextView) findViewById(R.id.int_txt_p2_l2);

        if (Locale.getDefault().getLanguage().equals("en")) {
            txt_p2_l1.setTypeface(G.HELETICBLK_TITR);
            txt_p2_l2.setTypeface(G.ARIAL_TEXT);
        } else {
            txt_p2_l1.setTextSize(getResources().getDimension(R.dimen.sp14));
            txt_p2_l1.setTypeface(G.YEKAN_BOLD);
            txt_p2_l2.setTypeface(G.YEKAN_FARSI);
        }

        txt_p2_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page2)));

        logoChat = (ImageView) findViewById(R.id.int_img_chat_introduce);
        txt_p3_l1 = (TextView) findViewById(R.id.int_txt_p3_l1);
        txt_p3_l2 = (TextView) findViewById(R.id.int_txt_p3_l2);

        if (Locale.getDefault().getLanguage().equals("en")) {
            txt_p3_l1.setTypeface(G.HELETICBLK_TITR);
            txt_p3_l2.setTypeface(G.ARIAL_TEXT);
        } else {
            txt_p3_l1.setTextSize(getResources().getDimension(R.dimen.sp14));
            txt_p3_l1.setTypeface(G.YEKAN_BOLD);
            txt_p3_l2.setTypeface(G.YEKAN_FARSI);
        }


        txt_p3_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page3)));


        transfer = (ImageView) findViewById(R.id.int_img_transfer_introduce);
        txt_p4_l1 = (TextView) findViewById(R.id.int_txt_p4_l1);
        txt_p4_l2 = (TextView) findViewById(R.id.int_txt_p4_l2);

        if (Locale.getDefault().getLanguage().equals("en")) {
            txt_p4_l1.setTypeface(G.HELETICBLK_TITR);
            txt_p4_l2.setTypeface(G.ARIAL_TEXT);
        } else {
            txt_p4_l1.setTypeface(G.YEKAN_BOLD);
            txt_p4_l1.setTextSize(getResources().getDimension(R.dimen.sp16));
            txt_p4_l2.setTypeface(G.YEKAN_FARSI);
        }


        txt_p4_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page4)));

        boy = (ImageView) findViewById(R.id.int_img_boy_introduce);
        txt_p5_l1 = (TextView) findViewById(R.id.int_txt_p5_l1);
        txt_p5_l2 = (TextView) findViewById(R.id.int_txt_p5_l2);

        btnStart = (Button) findViewById(R.id.int_btnStart);
        txt_p5_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page5)));

        if (Locale.getDefault().getLanguage().equals("en")) {
            txt_p5_l1.setTypeface(G.HELETICBLK_TITR);
            txt_p5_l2.setTypeface(G.ARIAL_TEXT);
        } else {
            btnStart.setTypeface(G.YEKAN_FARSI);
            txt_p5_l1.setTypeface(G.YEKAN_BOLD);
            txt_p5_l1.setTextSize(getResources().getDimension(R.dimen.sp14));
            txt_p5_l2.setTypeface(G.YEKAN_FARSI);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LOG", "CLICK!");
                Intent intent = new Intent(G.context, ActivityRegister.class);
                if (isoCode != null) {
                    intent.putExtra("ISO_CODE", isoCode);
                    intent.putExtra("CALLING_CODE", callingCode);
                    intent.putExtra("COUNTRY_NAME", countryName);
                    intent.putExtra("PATTERN", pattern);
                    intent.putExtra("REGEX", regex);
                    intent.putExtra("TERMS_BODY", body);
                }
                startActivity(intent);
                finish();
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { //set animation for all page

                circleButton.percentScroll(positionOffset, position);

                switch (position) {

                    case 0:
                        if (positionOffset == 0) {

                            isOne1 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {

                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }

                            if (isOne0) {
                                animationInPage1(logoIgap, layout_test, txt_p1_l2, txt_p1_l3);
                                isOne0 = false;
                            }
                        }

                        break;

                    case 1:

                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne2 = true;


                            if (logoIgap.getVisibility() == View.VISIBLE) {

                                animationOutPage1(logoIgap, layout_test, txt_p1_l2, txt_p1_l3);

                            }
                            if (logoChat.getVisibility() == View.VISIBLE) {
                                animationOut(logoChat, txt_p3_l1, txt_p3_l2);
                            }


                            if (isOne1) {

                                animationIn(logoSecurity, txt_p2_l1, txt_p2_l2);
                                isOne1 = false;
                            }

                        }
                        break;
                    case 2:

                        if (positionOffset == 0) {
                            isOne1 = true;
                            isOne3 = true;


                            if (logoSecurity.getVisibility() == View.VISIBLE) {

                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }
                            if (transfer.getVisibility() == View.VISIBLE) {
                                animationOut(transfer, txt_p4_l1, txt_p4_l2);
                            }
                            if (isOne2) {

                                animationIn(logoChat, txt_p3_l1, txt_p3_l2);
                                isOne2 = false;
                            }

                        }
                        break;
                    case 3:
                        if (positionOffset == 0) {
                            isOne2 = true;
                            isOne4 = true;
                            if (viewPager.isFocusable()) {

                                if (logoChat.getVisibility() == View.VISIBLE) {

                                    animationOut(logoChat, txt_p3_l1, txt_p3_l2);

                                } else if (boy.getVisibility() == View.VISIBLE) {
                                    animationOutBoy(boy, txt_p5_l1, txt_p5_l2, btnStart);
                                }
                                if (isOne3) {

                                    animationIn(transfer, txt_p4_l1, txt_p4_l2);
                                    isOne3 = false;
                                }
                            }
                        }
                        break;
                    case 4:
                        if (positionOffset == 0) {
                            isOne3 = true;

                            if (transfer.getVisibility() == View.VISIBLE) {

                                animationOut(transfer, txt_p4_l1, txt_p4_l2);
                            }
                            if (isOne4) {
                                animationInBoy(boy, txt_p5_l1, txt_p5_l2, btnStart);
                                isOne4 = false;
                            }

                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adapterViewPager = new AdapterViewPager(layout);
        viewPager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();


//        loop fo image city
        final ImageView backgroundOne = (ImageView) findViewById(R.id.int_background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.int_background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                assert backgroundOne != null;
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                assert backgroundTwo != null;
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    private void getInfoLocation() {

        G.onReceiveInfoLocation = new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, final int callingCodeR, final String countryNameR, String patternR, String regexR) {
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;

            }
        };

        ProtoInfoLocation.InfoLocation.Builder infoLocation = ProtoInfoLocation.InfoLocation.newBuilder();
        infoLocation.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(500, infoLocation);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getTermsOfServiceBody() {

        G.onReceivePageInfoTOS = new OnReceivePageInfoTOS() {
            @Override
            public void onReceivePageInfo(final String bodyR) {
                body = bodyR;
            }
        };

        ProtoInfoPage.InfoPage.Builder infoPage = ProtoInfoPage.InfoPage.newBuilder();
        infoPage.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoPage.setId("TOS");

        RequestWrapper requestWrapper = new RequestWrapper(503, infoPage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void animationInPage1(final ImageView logo, final ViewGroup txt1, final TextView txt2, final TextView txt3) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 0, 1);

        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX)
                .with(scaleY)
                .with(fade)
                .with(txt_scaleX1)
                .with(txt_scaleY1)
                .with(txt_scaleX2)
                .with(txt_scaleY2)
                .with(txt_scaleX3)
                .with(txt_scaleY3)
                .with(txt_fade1)
                .with(txt_fade2)
                .with(txt_fade3);
        scaleDown.setDuration(1000);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                if (txt3 != null) {
                    txt3.setVisibility(View.VISIBLE);
                }

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scaleDown.start();
            }
        }, 1000);
    }

    private void animationOutPage1(final ImageView logo, final ViewGroup txt1, final TextView txt2, final TextView txt3) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 1, 0);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 1, 0);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX)
                .with(scaleY)
                .with(fade)
                .with(txt_scaleX1)
                .with(txt_scaleY1)
                .with(txt_scaleX2)
                .with(txt_scaleY2)
                .with(txt_scaleX3)
                .with(txt_scaleY3)
                .with(txt_fade1)
                .with(txt_fade2)
                .with(txt_fade3);
        scaleDown.setDuration(1000);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                if (txt3 != null) {
                    txt3.setVisibility(View.VISIBLE);
                }


                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleDown.start();

    }


    private void animationIn(final ImageView logo, final TextView txt1, final TextView txt2) {

        if (!logo.equals(boy)) {

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
            ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);
            ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
            ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
            ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
            ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
            ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
            ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
            final AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleX)
                    .with(scaleY)
                    .with(fade)
                    .with(txt_scaleX1)
                    .with(txt_scaleY1)
                    .with(txt_scaleX2)
                    .with(txt_scaleY2)
                    .with(txt_fade1)
                    .with(txt_fade2);

            scaleDown.setDuration(1000);
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    logo.setVisibility(View.VISIBLE);
                    txt1.setVisibility(View.VISIBLE);
                    txt2.setVisibility(View.VISIBLE);


                    invisibleItems(logo);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scaleDown.start();
                }
            }, 1000);
        }
    }

    private void animationInBoy(final ImageView logo, final TextView txt1, final TextView txt2, final Button start) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        ObjectAnimator btn_scaleX1 = ObjectAnimator.ofFloat(start, "scaleX", 0, 1);
        ObjectAnimator btn_scaleY1 = ObjectAnimator.ofFloat(start, "scaleY", 0, 1);
        ObjectAnimator btn_fade1 = ObjectAnimator.ofFloat(start, "alpha", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX)
                .with(scaleY)
                .with(fade)
                .with(txt_scaleX1)
                .with(txt_scaleY1)
                .with(txt_scaleX2)
                .with(txt_scaleY2)
                .with(btn_scaleX1)
                .with(btn_scaleY1)
                .with(btn_fade1)
                .with(txt_fade1)
                .with(txt_fade2);
        scaleDown.setDuration(1000);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);

                logoIgap.setVisibility(View.INVISIBLE);
                logoSecurity.setVisibility(View.INVISIBLE);
                logoChat.setVisibility(View.INVISIBLE);
                transfer.setVisibility(View.INVISIBLE);

                layout_test.setVisibility(View.GONE);
                txt_p1_l2.setVisibility(View.GONE);
                txt_p1_l3.setVisibility(View.GONE);

                txt_p2_l1.setVisibility(View.GONE);
                txt_p2_l2.setVisibility(View.GONE);

                txt_p3_l1.setVisibility(View.GONE);
                txt_p3_l2.setVisibility(View.GONE);

                txt_p4_l1.setVisibility(View.GONE);
                txt_p4_l2.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scaleDown.start();
            }
        }, 1000);


    }

    private void animationOut(final ImageView logo, final TextView txt1, final TextView txt2) {

        viewPager.setEnabled(false);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);


        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX)
                .with(scaleY)
                .with(fade)
                .with(txt_scaleX1)
                .with(txt_scaleY1)
                .with(txt_scaleX2)
                .with(txt_scaleY2)
                .with(txt_fade1)
                .with(txt_fade2);

        scaleDown.setDuration(1000);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                invisibleItems(logo);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();

    }

    private void animationOutBoy(final ImageView logo, final TextView txt1, final TextView txt2, final Button start) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);

        ObjectAnimator fade2 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator fade3 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);
        ObjectAnimator btn_scaleX2 = ObjectAnimator.ofFloat(start, "scaleX", 1, 0);
        ObjectAnimator btn_scaleY2 = ObjectAnimator.ofFloat(start, "scaleY", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(start, "alpha", 1, 0);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX)
                .with(scaleY).with(fade)
                .with(txt_scaleX)
                .with(txt_scaleY)
                .with(fade2)
                .with(fade3)
                .with(txt_scaleX2)
                .with(txt_scaleY2)
                .with(btn_scaleX2)
                .with(btn_scaleY2)
                .with(txt_fade1);
        scaleDown.setDuration(1000);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                logoIgap.setVisibility(View.INVISIBLE);
                logoSecurity.setVisibility(View.INVISIBLE);
                logoChat.setVisibility(View.INVISIBLE);
                transfer.setVisibility(View.INVISIBLE);

                layout_test.setVisibility(View.GONE);
                txt_p1_l2.setVisibility(View.GONE);
                txt_p1_l3.setVisibility(View.GONE);

                txt_p2_l1.setVisibility(View.GONE);
                txt_p2_l2.setVisibility(View.GONE);

                txt_p3_l1.setVisibility(View.GONE);
                txt_p3_l2.setVisibility(View.GONE);

                txt_p4_l1.setVisibility(View.GONE);
                txt_p4_l2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
                start.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();

    }

    private void invisibleItems(ImageView logo) {

        if (logo.equals(logoIgap)) {

            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);
        }
        if (logo.equals(logoSecurity)) {
            logoChat.setVisibility(View.INVISIBLE);
            logoIgap.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);


            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

        }
        if (logo.equals(logoChat)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

        }
        if (logo.equals(transfer)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);


            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

        }
        if (logo.equals(boy)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);


        }
    }
}