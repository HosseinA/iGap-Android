package com.iGap.response;

import android.os.Handler;
import android.os.Looper;

import com.iGap.G;
import com.iGap.helper.HelperMessageResponse;
import com.iGap.proto.ProtoChannelSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class ChannelSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.identity = identity;
        this.message = protoClass;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelSendMessage.ChannelSendMessageResponse.Builder channelSendMessageResponse = (ProtoChannelSendMessage.ChannelSendMessageResponse.Builder) message;
        HelperMessageResponse.handleMessage(channelSendMessageResponse.getRoomId(), channelSendMessageResponse.getRoomMessage(), channelSendMessageResponse.getResponse(), this.identity);

    }

    @Override
    public void error() {
        super.error();
        makeFailed();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        // message failed
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
