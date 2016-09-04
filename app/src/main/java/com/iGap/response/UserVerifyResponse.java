package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoResponse;
import com.iGap.proto.ProtoUserVerify;

public class UserVerifyResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserVerifyResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        Log.i("SOC_RES", "UserRegisterResponse handler");

        ProtoUserVerify.UserVerifyResponse.Builder userVerifyResponse = (ProtoUserVerify.UserVerifyResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(userVerifyResponse.getResponse());
        Log.i("SOC_RES", "userVerifyResponse response.getId() : " + response.getId());
        Log.i("SOC_RES", "userVerifyResponse response.getTimestamp() : " + response.getTimestamp());

        Log.i("SOC_RES", "userVerifyResponse getToken : " + userVerifyResponse.getToken());
        Log.i("SOC_RES", "userVerifyResponse getNewUser : " + userVerifyResponse.getNewUser());

        G.onUserVerification.onUserVerify(userVerifyResponse.getToken(), userVerifyResponse.getNewUser(), "handler");

    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        G.onUserVerification.onUserVerify("", false, "error");
    }
}
