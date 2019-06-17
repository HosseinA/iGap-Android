/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingGetLog;

public class RequestSignalingGetLog {

    public void signalingGetLog(int offset, int limit) {

        ProtoSignalingGetLog.SignalingGetLog.Builder builder = ProtoSignalingGetLog.SignalingGetLog.newBuilder();
        ProtoGlobal.Pagination.Builder pagination = ProtoGlobal.Pagination.newBuilder();
        pagination.setOffset(offset);
        pagination.setLimit(limit);
        builder.setPagination(pagination);

        RequestWrapper requestWrapper = new RequestWrapper(907, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void signalingGetLog(int offset, int limit , ProtoSignalingGetLog.SignalingGetLog.Filter status) {

        ProtoSignalingGetLog.SignalingGetLog.Builder builder = ProtoSignalingGetLog.SignalingGetLog.newBuilder();
        ProtoGlobal.Pagination.Builder pagination = ProtoGlobal.Pagination.newBuilder();
        pagination.setOffset(offset);
        pagination.setLimit(limit);
        builder.setPagination(pagination);
        builder.setFilter(status);

        RequestWrapper requestWrapper = new RequestWrapper(907, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
