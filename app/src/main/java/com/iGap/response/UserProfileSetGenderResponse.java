package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileGender;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;

public class UserProfileSetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        final ProtoUserProfileGender.UserProfileSetGenderResponse.Builder userProfileGenderResponse = (ProtoUserProfileGender.UserProfileSetGenderResponse.Builder) message;

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                if (userInfo != null) {
                    userInfo.setGender(userProfileGenderResponse.getGender());
                }
            }
        });

        realm.close();

        G.onUserProfileSetGenderResponse.onUserProfileGenderResponse(userProfileGenderResponse.getGender(), userProfileGenderResponse.getResponse());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserProfileSetGenderResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "UserProfileSetGenderResponse response.minorCode() : " + minorCode);
        G.onUserProfileSetGenderResponse.Error(majorCode, minorCode);

    }
}


