// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

public interface OnGroupCreate {

    void onGroupCreate(long roomId);

    void onError(int majorCode, int minorCode);


}
