/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.realm.RealmCallConfig;

import static net.iGap.G.needGetSignalingConfiguration;

public class SignalingGetConfigurationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public SignalingGetConfigurationResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        needGetSignalingConfiguration = false;
        ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.Builder builder = (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.Builder) message;
        RealmCallConfig.updateSignalingConfiguration(builder);
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


