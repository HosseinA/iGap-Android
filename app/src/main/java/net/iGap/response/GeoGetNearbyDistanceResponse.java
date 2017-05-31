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

import net.iGap.proto.ProtoGeoGetNearbyDistance;

public class GeoGetNearbyDistanceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GeoGetNearbyDistanceResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder builder = (ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder) message;
        for (ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Result result : builder.getResultList()) {
            result.getUserId();
            result.getHasComment();
            result.getDistance();
        }
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


