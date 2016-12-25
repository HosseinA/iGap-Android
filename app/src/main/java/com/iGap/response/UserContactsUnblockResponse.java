package com.iGap.response;

import com.iGap.proto.ProtoUserContactsUnblock;

public class UserContactsUnblockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsUnblockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder builder = (ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder) message;
        builder.getUserId();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


