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

import net.iGap.helper.HelperMember;
import net.iGap.proto.ProtoChannelKickMember;

public class ChannelKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isDeleted = false;

    public ChannelKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelKickMember.ChannelKickMemberResponse.Builder builder = (ProtoChannelKickMember.ChannelKickMemberResponse.Builder) message;
        HelperMember.kickMember(builder.getRoomId(), builder.getMemberId());
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


