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
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperUploadFile;
import net.iGap.proto.ProtoFileUpload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestFileUpload;

import io.realm.Realm;

public class FileUploadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;
    private RequestFileUpload.IdentityFileUpload identityFileUpload;

    public FileUploadResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
        identityFileUpload = ((RequestFileUpload.IdentityFileUpload) identity);
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUpload.FileUploadResponse.Builder fileUploadResponse = (ProtoFileUpload.FileUploadResponse.Builder) message;

        HelperUploadFile.onFileUpload.onFileUpload(fileUploadResponse.getProgress(), fileUploadResponse.getNextOffset(), fileUploadResponse.getNextLimit(), identityFileUpload.identify, fileUploadResponse.getResponse());
        boolean connectivityType = true;
        try {

            if (HelperCheckInternetConnection.currentConnectivityType != null) {


                if (HelperCheckInternetConnection.currentConnectivityType == HelperCheckInternetConnection.ConnectivityType.WIFI)
                    connectivityType = true;
                else
                    connectivityType = false;
            }

        } catch (Exception e) {
        }
        ;


        HelperDataUsage.progressUpload(connectivityType, fileUploadResponse.getNextLimit(), identityFileUpload.type);

        if (fileUploadResponse.getProgress() == 100)
            HelperDataUsage.insertDataUsage(HelperDataUsage.convetredUploadType, connectivityType, false);


    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        long roomId = -1L;
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identityFileUpload.identify)).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (message != null) {
                        message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                    }
                }
            });

            if (message != null) {
                roomId = message.getRoomId();
            }
        }

        if (roomId != -1L) {
            long finalRoomId = roomId;
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    G.refreshRealmUi();
                    G.chatSendMessageUtil.onMessageFailed(finalRoomId, Long.parseLong(identityFileUpload.identify));
                }
            });
        }
    }

    @Override
    public void error() {
        super.error();
        HelperUploadFile.onFileUpload.onFileUploadTimeOut(identityFileUpload.identify);
        HelperSetAction.sendCancel(Long.parseLong(identityFileUpload.identify));
        makeFailed();
    }
}


