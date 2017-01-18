package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;

public class UserContactsImportResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsImportResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        if (G.onContactImport != null) G.onContactImport.onContactImport();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorReponse = (ProtoError.ErrorResponse.Builder) message;
        errorReponse.getMajorCode();
        errorReponse.getMinorCode();
    }
}


