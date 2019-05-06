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

import android.util.Log;

import net.iGap.G;
import net.iGap.helper.HelperLog;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserUpdateStatus;

import static net.iGap.G.userId;

public class UserUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserUpdateStatusResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder builder = (ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder) message;

        RealmRegisteredInfo.updateStatus(builder.getUserId(), builder.getResponse().getTimestamp(), builder.getStatus().toString());

        if (G.onUserUpdateStatus != null) {
            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUserId(), builder.getResponse().getTimestamp(), builder.getStatus().toString());
        }

        if (identity instanceof RequestUserUpdateStatus.onUserStatus) {
            ((RequestUserUpdateStatus.onUserStatus) identity).onUpdateUserStatus();
        } else {
            if (builder.getUserId() == userId) {
                HelperLog.setErrorLog("Wht the hel bagi");
                Log.d("bagi" , "Wht the hel bagi");
            }
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (identity instanceof RequestUserUpdateStatus.onUserStatus) {
            ((RequestUserUpdateStatus.onUserStatus) identity).onError(majorCode, minorCode);
        }
    }
}


