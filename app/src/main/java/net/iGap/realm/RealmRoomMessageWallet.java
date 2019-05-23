/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.net_iGap_realm_RealmRoomMessageWalletRealmProxy;

import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;

@Parcel(implementations = {net_iGap_realm_RealmRoomMessageWalletRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessageWallet.class})
public class RealmRoomMessageWallet extends RealmObject {

    @PrimaryKey
    private long id;
    private String type;
    private RealmRoomMessageWalletCardToCard realmRoomMessageWalletCardToCard;
    private RealmRoomMessageWalletPayment realmRoomMessageWalletPayment;
    private RealmRoomMessageWalletMoneyTransfer realmRoomMessageWalletMoneyTransfer;

    public static RealmRoomMessageWallet put(final ProtoGlobal.RoomMessageWallet input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageWallet messageWallet;
        messageWallet = realm.createObject(RealmRoomMessageWallet.class, AppUtils.makeRandomId());

        messageWallet.setType(input.getType().toString());

        if (input.getType() == CARD_TO_CARD) {
            messageWallet.setRealmRoomMessageWalletCardToCard(RealmRoomMessageWalletCardToCard.put(input.getCardToCard()));
        } else if (input.getType() == MONEY_TRANSFER) {
            messageWallet.setRealmRoomMessageWalletMoneyTransfer(RealmRoomMessageWalletMoneyTransfer.put(input.getMoneyTransfer()));
        } else if (input.getType() == PAYMENT ) {
            messageWallet.setRealmRoomMessageWalletPayment(RealmRoomMessageWalletPayment.put(input.getMoneyTransfer()));
        } else {

        }

        realm.close();

        return messageWallet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmRoomMessageWalletCardToCard getRealmRoomMessageWalletCardToCard() {
        return realmRoomMessageWalletCardToCard;
    }

    public void setRealmRoomMessageWalletCardToCard(RealmRoomMessageWalletCardToCard realmRoomMessageWalletCardToCard) {
        this.realmRoomMessageWalletCardToCard = realmRoomMessageWalletCardToCard;
    }

    public RealmRoomMessageWalletPayment getRealmRoomMessageWalletPayment() {
        return realmRoomMessageWalletPayment;
    }

    public void setRealmRoomMessageWalletPayment(RealmRoomMessageWalletPayment realmRoomMessageWalletPayment) {
        this.realmRoomMessageWalletPayment = realmRoomMessageWalletPayment;
    }

    public RealmRoomMessageWalletMoneyTransfer getRealmRoomMessageWalletMoneyTransfer() {
        return realmRoomMessageWalletMoneyTransfer;
    }

    public void setRealmRoomMessageWalletMoneyTransfer(RealmRoomMessageWalletMoneyTransfer realmRoomMessageWalletMoneyTransfer) {
        this.realmRoomMessageWalletMoneyTransfer = realmRoomMessageWalletMoneyTransfer;
    }
}
