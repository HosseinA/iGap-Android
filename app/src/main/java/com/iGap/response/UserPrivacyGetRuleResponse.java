package com.iGap.response;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserPrivacyGetRule;
import com.iGap.realm.RealmPrivacy;

public class UserPrivacyGetRuleResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserPrivacyGetRuleResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserPrivacyGetRule.UserPrivacyGetRuleResponse.Builder builder = (ProtoUserPrivacyGetRule.UserPrivacyGetRuleResponse.Builder) message;

        RealmPrivacy.updateRealmPrivacy(ProtoGlobal.PrivacyType.valueOf(identity), builder.getLevel());

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


