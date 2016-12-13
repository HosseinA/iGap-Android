package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelGetMessagesStats;
import com.iGap.proto.ProtoError;

public class ChannelGetMessagesStatsResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetMessagesStatsResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Builder builder = (ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Builder) message;
        if (G.onChannelGetMessagesStats != null) {
            G.onChannelGetMessagesStats.onChannelGetMessagesStats(builder.getStatsList());
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
        if (G.onChannelGetMessagesStats != null) {
            G.onChannelGetMessagesStats.onError(majorCode, minorCode);
        }
    }
}


