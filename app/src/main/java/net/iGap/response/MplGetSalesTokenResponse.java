/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoMplGetSalesToken;
import net.iGap.request.RequestMplGetSalesToken;

public class MplGetSalesTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public MplGetSalesTokenResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoMplGetSalesToken.MplGetSalesTokenResponse.Builder builder = (ProtoMplGetSalesToken.MplGetSalesTokenResponse.Builder) message;
        ((RequestMplGetSalesToken.GetSalesToken) identity).onSalesToken(builder.getToken());
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

        ((RequestMplGetSalesToken.GetSalesToken) identity).onError(majorCode, minorCode);
    }
}