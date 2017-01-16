package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperEditMessage;
import com.iGap.proto.ProtoChatEditMessage;
import com.iGap.proto.ProtoError;

public class ChatEditMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatEditMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChatEditMessage.ChatEditMessageResponse.Builder builder = (ProtoChatEditMessage.ChatEditMessageResponse.Builder) message;
        HelperEditMessage.editMessage(builder.getRoomId(), builder.getMessageId(), builder.getMessageVersion(), builder.getMessageType(), builder.getMessage(), builder.getResponse());
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

        G.onChatEditMessageResponse.onError(majorCode, minorCode);
    }
}


