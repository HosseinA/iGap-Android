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

import net.iGap.eventbus.EventManager;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoWalletPaymentInit;

public class WalletPaymentInitResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public WalletPaymentInitResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder builder = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) message;
        builder.getToken();
        builder.getPublicKey();
        EventManager.getInstance().postEvent(EventManager.ON_INIT_PAY, builder);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        EventManager.getInstance().postEvent(EventManager.ON_INIT_PAY, null);
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        EventManager.getInstance().postEvent(EventManager.ON_INIT_PAY, null);
    }
}


