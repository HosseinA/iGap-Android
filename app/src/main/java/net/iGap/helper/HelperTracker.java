/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;

import ir.metrix.sdk.Metrix;

public class HelperTracker {

    private static Tracker mTracker;

    private static final String CATEGORY_SETTING = "Setting@";
    private static final String CATEGORY_COMMUNICATION = "Communication@";
    private static final String CATEGORY_REGISTRATION = "Registration@";
    private static final String CATEGORY_DISCOVERY = "Discovery@";

    public static final String TRACKER_CHANGE_LANGUAGE = CATEGORY_SETTING + "TRACKER_CHANGE_LANGUAGE";

    public static final String TRACKER_CALL_PAGE = CATEGORY_COMMUNICATION + "TRACKER_CALL_PAGE";
    public static final String TRACKER_VOICE_CALL_CONNECTING = CATEGORY_COMMUNICATION + "TRACKER_VOICE_CALL_CONNECTING";
    public static final String TRACKER_VOICE_CALL_CONNECTED = CATEGORY_COMMUNICATION + "TRACKER_VOICE_CALL_CONNECTED";
    public static final String TRACKER_VIDEO_CALL_CONNECTING = CATEGORY_COMMUNICATION + "TRACKER_VIDEO_CALL_CONNECTING";
    public static final String TRACKER_VIDEO_CALL_CONNECTED = CATEGORY_COMMUNICATION + "TRACKER_VIDEO_CALL_CONNECTED";
    public static final String TRACKER_CHAT_VIEW = CATEGORY_COMMUNICATION + "TRACKER_CHAT_VIEW";
    public static final String TRACKER_GROUP_VIEW = CATEGORY_COMMUNICATION + "TRACKER_GROUP_VIEW";
    public static final String TRACKER_CHANNEL_VIEW = CATEGORY_COMMUNICATION + "TRACKER_CHANNEL_VIEW";
    public static final String TRACKER_BOT_VIEW = CATEGORY_COMMUNICATION + "TRACKER_BOT_VIEW";
    public static final String TRACKER_ROOM_PAGE = CATEGORY_COMMUNICATION + "TRACKER_ROOM_PAGE";
    public static final String TRACKER_CREATE_CHANNEL = CATEGORY_COMMUNICATION + "TRACKER_CREATE_CHANNEL";
    public static final String TRACKER_CREATE_GROUP = CATEGORY_COMMUNICATION + "TRACKER_CREATE_GROUP";
    public static final String TRACKER_INVITE_FRIEND = CATEGORY_COMMUNICATION + "TRACKER_INVITE_FRIEND";

    public static final String TRACKER_INSTALL_USER = CATEGORY_REGISTRATION + "TRACKER_INSTALL_USER";
    public static final String TRACKER_SUBMIT_NUMBER = CATEGORY_REGISTRATION + "TRACKER_SUBMIT_NUMBER";
    public static final String TRACKER_ACTIVATION_CODE = CATEGORY_REGISTRATION + "TRACKER_ACTIVATION_CODE";
    public static final String TRACKER_QR_REGISTRATION = CATEGORY_REGISTRATION + "TRACKER_QR_REGISTRATION";
    public static final String TRACKER_REGISTRATION_USER = CATEGORY_REGISTRATION + "TRACKER_REGISTRATION_USER";
    public static final String TRACKER_REGISTRATION_NEW_USER = CATEGORY_REGISTRATION + "TRACKER_REGISTRATION_NEW_USER";

    public static final String TRACKER_DISCOVERY_PAGE = CATEGORY_DISCOVERY + "TRACKER_DISCOVERY_PAGE";
    public static final String TRACKER_WALLET_PAGE = CATEGORY_DISCOVERY + "TRACKER_WALLET_PAGE";
    public static final String TRACKER_NEARBY_PAGE = CATEGORY_DISCOVERY + "TRACKER_NEARBY_PAGE";
    public static final String TRACKER_FINANCIAL_SERVICES = CATEGORY_DISCOVERY + "TRACKER_FINANCIAL_SERVICES";


    synchronized private static Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(G.context);
            mTracker = analytics.newTracker(R.xml.global_track);
        }
        return mTracker;
    }

    public static void sendTracker(String trackerTag) {
        if (BuildConfig.DEBUG) {
            return;
        }

        boolean allowSendTracker = true;

        if (trackerTag.equals(TRACKER_INSTALL_USER) && HelperPreferences.getInstance().readBoolean(SHP_SETTING.KEY_TRACKER_FILE, SHP_SETTING.KEY_TRACKER_INSTALL_USER)) {
            allowSendTracker = false;
        } else {
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.KEY_TRACKER_FILE, SHP_SETTING.KEY_TRACKER_INSTALL_USER, true);
        }

        if (allowSendTracker) {
            switch (trackerTag) {
                case TRACKER_CHANGE_LANGUAGE:
                    Metrix.getInstance().newEvent("rvwun");
                    break;
                case TRACKER_CALL_PAGE:
                    Metrix.getInstance().newEvent("mlrxn");
                    break;
                case TRACKER_VOICE_CALL_CONNECTING:
                    Metrix.getInstance().newEvent("qsjti");
                    break;
                case TRACKER_VOICE_CALL_CONNECTED:
                    Metrix.getInstance().newEvent("znfwd");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTING:
                    Metrix.getInstance().newEvent("sxkav");
                    break;
                case TRACKER_VIDEO_CALL_CONNECTED:
                    Metrix.getInstance().newEvent("dcsqk");
                    break;
                case TRACKER_CHAT_VIEW:
                    Metrix.getInstance().newEvent("rszqm");
                    break;
                case TRACKER_GROUP_VIEW:
                    Metrix.getInstance().newEvent("htwef");
                    break;
                case TRACKER_CHANNEL_VIEW:
                    Metrix.getInstance().newEvent("smkkz");
                    break;
                case TRACKER_BOT_VIEW:
                    Metrix.getInstance().newEvent("sgozq");
                    break;
                case TRACKER_ROOM_PAGE:
                    Metrix.getInstance().newEvent("hnahq");
                    break;
                case TRACKER_CREATE_CHANNEL:
                    Metrix.getInstance().newEvent("hzodo");
                    break;
                case TRACKER_CREATE_GROUP:
                    Metrix.getInstance().newEvent("szlrq");
                    break;
                case TRACKER_INVITE_FRIEND:
                    Metrix.getInstance().newEvent("kvjqi");
                    break;
                case TRACKER_INSTALL_USER:
                    Metrix.getInstance().newEvent("zwhkn");
                    break;
                case TRACKER_SUBMIT_NUMBER:
                    Metrix.getInstance().newEvent("hvxtt");
                    break;
                case TRACKER_ACTIVATION_CODE:
                    Metrix.getInstance().newEvent("jjrro");
                    break;
                case TRACKER_QR_REGISTRATION:
                    Metrix.getInstance().newEvent("uufge");
                    break;
                case TRACKER_REGISTRATION_USER:
                    Metrix.getInstance().newEvent("ooarp");
                    break;
                case TRACKER_REGISTRATION_NEW_USER:
                    Metrix.getInstance().newEvent("wthwa");

                    //REGISTRATION_UNIQUE
                    Metrix.getInstance().newEvent("npmol");
                    break;
                case TRACKER_DISCOVERY_PAGE:
                    Metrix.getInstance().newEvent("qkslv");
                    break;
                case TRACKER_WALLET_PAGE:
                    Metrix.getInstance().newEvent("yxhgb");
                    break;
                case TRACKER_NEARBY_PAGE:
                    Metrix.getInstance().newEvent("vvcid");
                    break;
                case TRACKER_FINANCIAL_SERVICES:
                    Metrix.getInstance().newEvent("dbbfk");
                    break;
            }

            String[] trackerType = trackerTag.split("@");
            String category = trackerType[0];
            String action = trackerType[1];

            FirebaseAnalytics.getInstance(G.context).logEvent(action, null);

            Tracker tracker = getDefaultTracker();
            tracker.send(new HitBuilders.EventBuilder(category, action).build());

            tracker.setScreenName(action);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
