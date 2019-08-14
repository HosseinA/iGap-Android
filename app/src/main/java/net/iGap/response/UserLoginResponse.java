/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import android.os.Looper;
import android.util.Log;

import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperConnectionState;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserLogin;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestWalletGetAccessToken;

import java.util.logging.Handler;

import io.realm.Realm;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private boolean isDeprecated = false;
    private boolean isUpdateAvailable = false;

    public UserLoginResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }


    @Override
    public void handler() {
        super.handler();
        HelperConnectionState.connectionState(ConnectionState.IGAP);
        ProtoUserLogin.UserLoginResponse.Builder builder = (ProtoUserLogin.UserLoginResponse.Builder) message;
        G.serverHashContact = builder.getContactHash() ;
      /*builder.getDeprecatedClient();
        builder.getSecondaryNodeName();
        builder.getUpdateAvailable();*/
        RealmUserInfo.insertAccessToken(builder.getAccessToken());

        if (builder.getUpdateAvailable() && !isUpdateAvailable) {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (G.onVersionCallBack != null) {
                        G.onVersionCallBack.isUpdateAvailable();
                        isUpdateAvailable = true;
                    }
                }
            }, 1000);


        }

        if (builder.getDeprecatedClient() && !isDeprecated) {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (G.onVersionCallBack != null) {
                        G.onVersionCallBack.isDeprecated();
                        isDeprecated = true;
                        G.isDepricatedApp = true;
                    }
                }
            }, 1000);

        } else {
            G.isDepricatedApp = false;
        }


        G.currentServerTime = builder.getResponse().getTimestamp();
        G.bothChatDeleteTime = builder.getChatDeleteMessageForBothPeriod() * 1000;
        G.userLogin = true;
        G.isMplActive = builder.getMplActive();
        G.isWalletActive = builder.getWalletActive();
        G.isWalletRegister = builder.getWalletAgreementAccepted();

        if (G.onPayment != null) {
            G.onPayment.onFinance(G.isMplActive, G.isWalletActive);
        }
        /**
         * get Signaling Configuration
         * (( hint : call following request after set G.userLogin=true ))
         */

        try (Realm realm = Realm.getDefaultInstance()) {
            if (G.needGetSignalingConfiguration || realm.where(RealmCallConfig.class).findFirst() == null) {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }

        WebSocketClient.waitingForReconnecting = false;
        WebSocketClient.allowForReconnecting = true;
        G.onUserLogin.onLogin();
        RealmUserInfo.sendPushNotificationToServer();

        if (G.isWalletActive && G.isWalletRegister) {
            new RequestWalletGetAccessToken().walletGetAccessToken();
        }


    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.isSecure) {
            try (Realm realm = Realm.getDefaultInstance()) {
                RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                if (!G.userLogin && userInfo != null && userInfo.getUserRegistrationState()) {
                    new RequestUserLogin().userLogin(userInfo.getToken());
                }
            }
        } else {
            WebSocketClient.getInstance().disconnect();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onUserLogin.onLoginError(majorCode, minorCode);
    }


}


