package com.iGap.request;

import com.iGap.proto.ProtoGroupGetMemberList;

public class RequestGroupGetMemberList {

    public void getMemberList(long roomId) {

        ProtoGroupGetMemberList.GroupGetMemberList.Builder builder = ProtoGroupGetMemberList.GroupGetMemberList.newBuilder();
        builder.setRoomId(roomId);
        builder.setFilterRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL);

        RequestWrapper requestWrapper = new RequestWrapper(317, builder, roomId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
