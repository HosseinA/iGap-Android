/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.interfaces;

import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.realm.RealmRoomMessage;

import java.util.List;

public interface OnMessageReceive {

    /**
     * message that reached from server
     */
    void onMessage(long roomId, long startMessageId, long endMessageId, List<RealmRoomMessage> list, boolean gapReached, boolean jumpOverLocal, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction);

    void onError(int majorCode, int minorCode, long messageIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction);

}
