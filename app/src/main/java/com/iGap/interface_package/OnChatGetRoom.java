// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interface_package;

public interface OnChatGetRoom {

    void onChatGetRoom(long roomId);

    void onChatGetRoomTimeOut();

    void onChatGetRoomError();

}
