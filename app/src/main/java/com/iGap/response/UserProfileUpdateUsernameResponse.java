package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileUpdateUsername;

public class UserProfileUpdateUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileUpdateUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder builder = (ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder) message;

        G.onUserProfileUpdateUsername.onUserProfileUpdateUsername(builder.getUsername());
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

        G.onUserProfileUpdateUsername.Error(majorCode, minorCode);
    }
}


