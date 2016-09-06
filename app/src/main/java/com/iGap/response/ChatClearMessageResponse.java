package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatClearMessage;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;

public class ChatClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        ProtoChatClearMessage.ChatClearMessageResponse.Builder chatClearMessage = (ProtoChatClearMessage.ChatClearMessageResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(chatClearMessage.getResponse());
        Log.i("SOC", "ChatClearMessageResponse response.getId() : " + response.getId());
        Log.i("SOC", "ChatClearMessageResponse response.getTimestamp() : " + response.getTimestamp());

        G.clearMessagesUtil.onChatClearMessage(chatClearMessage.getRoomId(), chatClearMessage.getClearId(), chatClearMessage.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatClearMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatClearMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatClearMessageResponse response.minorCode() : " + minorCode);
    }
}


