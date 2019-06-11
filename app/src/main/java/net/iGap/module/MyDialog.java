/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.interfaces.OnComplete;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyDialog {


    /**
     * create custom dialog for main page
     */
    public static void showDialogMenuItemRooms(FragmentActivity activity, final String itemName, final ProtoGlobal.Room.Type mType, boolean isMute, final String role, long peerId, RealmRoom mInfo, final OnComplete complete, boolean isPinned) {

        Realm realm = Realm.getDefaultInstance();
        RealmResults realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_PINNED, true).findAll();
        int pinCount = realmRoom.size();
        realm.close();

        List<String> items = new ArrayList<>();
        if (mInfo != null && !RealmRoom.isPromote(mInfo.getId())) {
            if (isPinned) {
                items.add(activity.getString(R.string.Unpin_to_top));
            } else if (pinCount < 5) {
                items.add(activity.getString(R.string.pin_to_top));
            }
        }
        if (isMute) {
            items.add(activity.getString(R.string.unmute));
        } else {
            items.add(activity.getString(R.string.mute));
        }
        items.add(activity.getString(R.string.clear_history));
        if (mInfo != null && !RealmRoom.isPromote(mInfo.getId())) {
            if (mType == ProtoGlobal.Room.Type.CHAT) {
                items.add(activity.getString(R.string.delete_item_dialog) + " " + activity.getString(R.string.chat));
            } else if (mType == ProtoGlobal.Room.Type.GROUP) {
                if (role.equals("OWNER")) {
                    items.add(activity.getString(R.string.delete_item_dialog) + " " + activity.getString(R.string.group));
                } else {
                    items.add(activity.getString(R.string.left) + " " + activity.getString(R.string.group));
                }
            } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {
                items.add(activity.getString(R.string.delete_item_dialog) + " " + activity.getString(R.string.channel));
                if (role.equals("OWNER")) {
                    items.add(activity.getString(R.string.delete_item_dialog) + " " + activity.getString(R.string.channel));
                } else {
                    items.add(activity.getString(R.string.left) + " " + activity.getString(R.string.channel));
                }
            }
        }

        new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(activity.getString(R.string.Unpin_to_top)) || items.get(position).equals(activity.getString(R.string.pin_to_top))){
                if (complete != null) complete.complete(true, "pinToTop", "");
            }
            else if (items.get(position).equals(activity.getString(R.string.unmute)) || items.get(position).equals(activity.getString(R.string.mute))){
                if (complete != null) complete.complete(true, "txtMuteNotification", "");
            }
            else if (items.get(position).equals(activity.getString(R.string.clear_history))){
                new MaterialDialog.Builder(activity).title(itemName).titleColor(G.context.getResources().getColor(R.color.toolbar_background)).content(activity.getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (complete != null) complete.complete(true, "txtClearHistory", "");
                        dialog.dismiss();
                    }
                }).onNegative((dialog, which) -> dialog.dismiss()).show();
            }
            else if (items.get(position).contains(activity.getString(R.string.delete_item_dialog)) || items.get(position).contains(activity.getString(R.string.left))){
                String str0 = "";
                String str = "";
                if (mType == ProtoGlobal.Room.Type.CHAT) {
                    str0 = activity.getString(R.string.do_you_want_delete_this);
                    str = activity.getString(R.string.chat);
                } else if (mType == ProtoGlobal.Room.Type.GROUP) {
                    str = activity.getString(R.string.group);
                    if (role.equals("OWNER")) {
                        str0 = activity.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = activity.getString(R.string.do_you_want_left_this);
                    }
                } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {

                    str = activity.getString(R.string.channel);
                    if (role.equals("OWNER")) {
                        str0 = activity.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = activity.getString(R.string.do_you_want_left_this);
                    }
                }

                showDialogNotification(activity, itemName, str0, complete, "txtDeleteChat");
            }
        }).show(activity.getSupportFragmentManager(),"bottom sheet");
    }


    public static void showDialogNotification(Context context, String title, String Message, final OnComplete complete, final String result) {

        new MaterialDialog.Builder(context).title(title)
                .content(Message).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (complete != null) complete.complete(true, result, "yes");

                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
