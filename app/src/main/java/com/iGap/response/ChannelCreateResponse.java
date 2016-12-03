package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelCreate;
import com.iGap.proto.ProtoError;

public class ChannelCreateResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelCreateResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelCreate.ChannelCreateResponse.Builder builder = (ProtoChannelCreate.ChannelCreateResponse.Builder) message;
        G.onChannelCreate.onChannelCreate(builder.getRoomId(), builder.getInviteLink());

    }

    @Override
    public void timeOut() {
        super.timeOut();

        G.onChannelCreate.onTimeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onChannelCreate.onError(majorCode, minorCode);
    }
}


