package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserGetDeleteToken;

public class UserGetDeleteTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserGetDeleteTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserGetDeleteToken.UserGetDeleteTokenResponse.Builder builder =
                (ProtoUserGetDeleteToken.UserGetDeleteTokenResponse.Builder) message;

        G.smsNumbers = builder.getSmsNumberList();
        if (G.onUserGetDeleteToken != null)
        G.onUserGetDeleteToken.onUserGetDeleteToken(builder.getResendDelay(),
                builder.getTokenRegex(), builder.getTokenLenght());
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
        int getWait = errorResponse.getWait();

        if (G.onUserGetDeleteToken != null) ;
        G.onUserGetDeleteToken.onUserGetDeleteError(majorCode, minorCode, getWait);

    }
}
