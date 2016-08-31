package com.iGap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.activitys.ActivityMain;
import com.iGap.activitys.MyDialog;
import com.iGap.module.CircleImageView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructContactInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/2/2016.
 */
public class AdapterContact extends RecyclerView.Adapter<AdapterContact.MyViewHolder> {

    private ArrayList<StructContactInfo> list;
    private Context context;
    private OnComplete complete;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imvContactPicture;
        public View vDistanceColor;
        public TextView txtContactIcon;
        public TextView txtContactName;
        public TextView txtMute;
        public TextView txtLastMessage;
        public TextView txtlastSeen;
        public TextView txtUnreadMessage;

        public MyViewHolder(View itemView, int position) {
            super(itemView);

            imvContactPicture = (CircleImageView) itemView.findViewById(R.id.cs_img_contact_picture);
            vDistanceColor = (View) itemView.findViewById(R.id.cs_view_distance_color);
            txtContactIcon = (TextView) itemView.findViewById(R.id.cs_txt_contact_icon);
            txtContactName = (TextView) itemView.findViewById(R.id.cs_txt_contact_name);
            txtLastMessage = (TextView) itemView.findViewById(R.id.cs_txt_last_message);
            txtlastSeen = (TextView) itemView.findViewById(R.id.cs_txt_contact_time);
            txtUnreadMessage = (TextView) itemView.findViewById(R.id.cs_txt_unread_message);
            txtMute = (TextView) itemView.findViewById(R.id.cs_txt_mute);

            txtContactIcon.setTypeface(G.fontawesome);
            txtContactName.setTypeface(G.arialBold);
            txtLastMessage.setTypeface(G.arial);
            txtMute.setTypeface(G.fontawesome);
            txtlastSeen.setTypeface(G.arial);
            txtUnreadMessage.setTypeface(G.arial);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ActivityMain.isMenuButtonAddShown) {
                        complete.complete(true, "closeMenuButton", "");
                    } else {

                        int position = MyViewHolder.super.getPosition();

                        Intent intent = new Intent(context, ActivityChat.class);
                        intent.putExtra("ChatType", list.get(position).contactType);
                        intent.putExtra("ContactID", list.get(position).contactID);
                        intent.putExtra("IsMute", list.get(position).muteNotification);
                        intent.putExtra("OwnerShip", list.get(position).ownerShip);
                        intent.putExtra("ContactName", list.get(position).contactName);
                        intent.putExtra("MemberCount", list.get(position).memberCount);
                        intent.putExtra("LastSeen", list.get(position).lastSeen);

                        context.startActivity(intent);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (ActivityMain.isMenuButtonAddShown) {
                        complete.complete(true, "closeMenuButton", "");
                    } else {
                        final int position = MyViewHolder.super.getPosition();
                        MyDialog.showDialogMenuItemContacts(context, list.get(position).contactType, list.get(position).muteNotification, new OnComplete() {
                            @Override
                            public void complete(boolean result, String messageOne, String MessageTow) {
                                onSelectContactMenu(messageOne, position);
                            }
                        });
                    }
                    return true;
                }
            });


            String color = list.get(position).viewDistanceColor;

            if (list.get(position).imageSource.length() > 0) {
                imvContactPicture.setImageResource(Integer.parseInt(list.get(position).imageSource));
            } else {
                imvContactPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), list.get(position).contactName, ""));
            }


            //    holder.contactPicture.setBackgroundColor(Color.parseColor("#FFBAE5F2"));

            //noinspection Range
            //   holder.distanceColor.setBackgroundColor(Color.parseColor(color));


            txtContactIcon.setText(getStringChatIcon(list.get(position).contactType));
            if (list.get(position).contactType == MyType.ChatType.singleChat) {
                txtContactIcon.setVisibility(View.GONE);
            } else {
                txtContactIcon.setVisibility(View.VISIBLE);
            }


            txtContactName.setText(list.get(position).contactName);
            txtLastMessage.setText(list.get(position).lastmessage);
            txtlastSeen.setText(list.get(position).lastSeen);

            int unread = list.get(position).unreadMessag;
            if (unread < 1) {
                txtUnreadMessage.setVisibility(View.INVISIBLE);
            } else {
                txtUnreadMessage.setVisibility(View.VISIBLE);
//            if(unread>99)
//               holder.unreadMessage.setText("+99");
//            else
                txtUnreadMessage.setText(unread + "");

                if (list.get(position).muteNotification) {
                    txtUnreadMessage.setBackgroundResource(R.drawable.oval_gray);
                } else {
                    txtUnreadMessage.setBackgroundResource(R.drawable.oval_green);
                }
            }

            if (list.get(position).muteNotification) {
                txtMute.setVisibility(View.VISIBLE);
            } else {
                txtMute.setVisibility(View.GONE);
            }


        }

    }


    public AdapterContact(ArrayList<StructContactInfo> list, Context context, OnComplete complete) {
        this.list = list;
        this.context = context;
        this.complete = complete;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_sub_layout, parent, false);
        return new MyViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (list.get(position).whatChange > 0)
            updateSomething(position, holder, list.get(position).whatChange);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private String getStringChatIcon(MyType.ChatType chatType) {

        String result = "";

        if (chatType == MyType.ChatType.singleChat)
            result = G.context.getString(R.string.fa_user);
        else if (chatType == MyType.ChatType.groupChat)
            result = G.context.getString(R.string.fa_group);
        else if (chatType == MyType.ChatType.channel)
            result = G.context.getString(R.string.fa_bullhorn);

        return result;
    }


    private void onSelectContactMenu(String message, int position) {

        if (message.equals("txtMuteNotification")) {
            muteNotification(position);
        } else if (message.equals("txtClearHistory")) {
            clearHistory(position);
        } else if (message.equals("txtDeleteChat")) {
            deleteChat(position);
        }

    }


    private void muteNotification(int position) {
        Log.e("fff", " txtMuteNotification " + position);

        notifyItemChanged(position);

        list.get(position).muteNotification = !list.get(position).muteNotification;
    }

    private void clearHistory(int position) {
        Log.e("fff", " txtClearHistory " + position);
    }

    private void deleteChat(int position) {
        Log.e("fff", " txtDeleteChat " + position);
    }


    private void updateSomething(int position, RecyclerView.ViewHolder holder, int whatUpdate) {

        switch (whatUpdate) {

            case 1:

                break;
        }

        list.get(position).whatChange = 0;


    }

}
