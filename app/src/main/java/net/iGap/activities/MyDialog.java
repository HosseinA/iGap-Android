/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoGlobal;

public class MyDialog {

    public static void showDialogMenuItemRooms(final Context context, final ProtoGlobal.Room.Type mType, boolean isMute, final String role, final OnComplete complete) {

        //final Dialog dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setContentView(R.layout.chat_popup_dialog);
        //dialog.setCancelable(true);

        final MaterialDialog dialog = new MaterialDialog.Builder(context).customView(R.layout.chat_popup_dialog, true).build();

        View v = dialog.getCustomView();

        //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.copyFrom(dialog.getWindow().getAttributes());
        //// layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.gravity = Gravity.CENTER;

        DialogAnimation.animationDown(dialog);

        dialog.show();
        //dialog.getWindow().setAttributes(layoutParams);

        TextView txtMuteNotification = null;

        txtMuteNotification = (TextView) v.findViewById(R.id.cm_txt_mute_notification);
        MaterialDesignTextView iconMuteNotification = (MaterialDesignTextView) v.findViewById(R.id.cm_icon_mute_notification);
        //        iconMuteNotification.setTypeface();
        TextView txtClearHistory = (TextView) v.findViewById(R.id.cm_txt_clear_history);
        MaterialDesignTextView iconClearHistory = (MaterialDesignTextView) v.findViewById(R.id.cm_icon_clear_history);
        TextView txtDeleteChat = (TextView) v.findViewById(R.id.cm_txt_delete_chat);
        MaterialDesignTextView iconDeleteChat = (MaterialDesignTextView) v.findViewById(R.id.cm_icon_delete_chat);
        //TextView txtCancel = (TextView) dialog.findViewById(R.id.cm_txt_cancle);

        if (isMute) {
            txtMuteNotification.setText(context.getString(R.string.unmute));
            iconMuteNotification.setText(context.getString(R.string.md_muted));
        } else {
            txtMuteNotification.setText(context.getString(R.string.mute));
            iconMuteNotification.setText(context.getString(R.string.md_unMuted));
        }

        //        txtMuteNotification.setText(isMute ? context.getString(R.string.unmute_notification)
        //                : context.getString(R.string.mute_notification));

        txtMuteNotification.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (complete != null) complete.complete(true, "txtMuteNotification", "");
                dialog.dismiss();
            }
        });

        txtClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (complete != null) complete.complete(true, "txtClearHistory", "");
                dialog.dismiss();
            }
        });

        if (mType == ProtoGlobal.Room.Type.CHAT) {
            txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.chat));
        } else if (mType == ProtoGlobal.Room.Type.GROUP) {
            if (role.equals("OWNER")) {

                txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.group));
            } else {

                txtDeleteChat.setText(context.getString(R.string.left) + " " + context.getString(R.string.group));
                iconDeleteChat.setText(context.getString(R.string.md_go_back_left_arrow));
            }
        } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {

            txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.channel));

            if (role.equals("OWNER")) {

                txtDeleteChat.setText(context.getString(R.string.delete_item_dialog) + " " + context.getString(R.string.channel));
            } else {

                txtDeleteChat.setText(context.getString(R.string.left) + " " + context.getString(R.string.channel));
                iconDeleteChat.setText(context.getString(R.string.md_go_back_left_arrow));
            }
        }

        txtDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String str0 = "";
                String str = "";
                if (mType == ProtoGlobal.Room.Type.CHAT) {
                    str0 = context.getString(R.string.do_you_want_delete_this);
                    str = context.getString(R.string.chat);
                } else if (mType == ProtoGlobal.Room.Type.GROUP) {
                    str = context.getString(R.string.group);
                    if (role.equals("OWNER")) {
                        str0 = context.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = context.getString(R.string.do_you_want_left_this);
                    }
                } else if (mType == ProtoGlobal.Room.Type.CHANNEL) {

                    str = context.getString(R.string.channel);
                    if (role.equals("OWNER")) {
                        str0 = context.getString(R.string.do_you_want_delete_this);
                    } else {
                        str0 = context.getString(R.string.do_you_want_left_this);
                    }
                }

                showDialogNotification(context, str0, complete, "txtDeleteChat");

                dialog.dismiss();
            }
        });

        //txtCancel.setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View view) {
        //         dialog.dismiss();
        //    }
        //});
    }

    public static void showDialogNotification(Context context, String Message, final OnComplete complete, final String result) {

        new MaterialDialog.Builder(context).title(G.context.getResources().getString(R.string.igap))
            .titleColor(G.context.getResources().getColor(R.color.toolbar_background))
            .content(Message)
            .positiveText(G.context.getResources().getString(R.string.B_ok))
            .negativeText(G.context.getResources().getString(R.string.B_cancel))
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (complete != null) complete.complete(true, result, "yes");

                    dialog.dismiss();
                }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            })
            .show();
    }
}
