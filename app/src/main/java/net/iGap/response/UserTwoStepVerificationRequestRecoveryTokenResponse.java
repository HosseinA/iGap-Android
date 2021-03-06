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

import net.iGap.G;
import net.iGap.proto.ProtoUserTwoStepVerificationRequestRecoveryToken;

public class UserTwoStepVerificationRequestRecoveryTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationRequestRecoveryTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryTokenResponse.Builder builder = (ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryTokenResponse.Builder) message;

        if (G.onRecoveryEmailToken != null) {
            G.onRecoveryEmailToken.getEmailPatern(builder.getEmailPattern());
        }

        if (G.onRecoverySecurityPassword != null) {
            G.onRecoverySecurityPassword.getEmailPatern(builder.getEmailPattern());
        }
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


