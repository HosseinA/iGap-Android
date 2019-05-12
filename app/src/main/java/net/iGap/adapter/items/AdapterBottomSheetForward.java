/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hanks.library.AnimateCheckBox;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.structs.StructBottomSheetForward;
import net.iGap.proto.ProtoGlobal;

import java.util.HashMap;
import java.util.List;

import static net.iGap.G.context;

public class AdapterBottomSheetForward extends AbstractItem<AdapterBottomSheetForward, AdapterBottomSheetForward.ViewHolder> {


    public StructBottomSheetForward mList;
    public boolean isChecked = false;
    private AvatarHandler avatarHandler;

    public AdapterBottomSheetForward(StructBottomSheetForward mList, AvatarHandler avatarHandler) {
        this.mList = mList;
        this.avatarHandler = avatarHandler;
    }

//    @Override
//    public AdapterBottomSheetForward.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        // View v = inflater.inflate(R.layout.contact_item, viewGroup, false);
//
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_forward_bottom_sheet, viewGroup, false);
//        return new AdapterBottomSheetForward.ViewHolder(v);
//    }

    @Override
    public void bindView(final ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);

        if (mList.isContactList()) {
            setAvatarContact(viewHolder, mList.getId());
        } else {
            setAvatar(mList, viewHolder.imgSrc);
        }

        viewHolder.txtName.setText(mList.getDisplayName());

        //if (mList.isSelected) {
        //    holder.checkBoxSelect.setChecked(false);
        //} else {
        //    holder.checkBoxSelect.setChecked(true);
        //}
        viewHolder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));

        viewHolder.checkBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.checkBoxSelect.isChecked()) {
                    viewHolder.checkBoxSelect.setChecked(false);
                    viewHolder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    //FragmentChat.onPathAdapterBottomSheet.path(mList.getPath(), false);
                    if (mList.isNotExistRoom()) {
                        FragmentChat.onForwardBottomSheet.path(mList, false, true);
                    } else {
                        FragmentChat.onForwardBottomSheet.path(mList, false, false);
                    }
                    //mList.setSelected(true);
                } else {
                    viewHolder.checkBoxSelect.setChecked(true);
                    viewHolder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    //FragmentChat.onPathAdapterBottomSheet.path(mList.getPath(), true);
                    if (mList.isNotExistRoom()) {
                        FragmentChat.onForwardBottomSheet.path(mList, true, true);
                    } else {
                        FragmentChat.onForwardBottomSheet.path(mList, true, false);
                    }
                    //mList.setSelected(false);
                }
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.checkBoxSelect.isChecked()) {
                    viewHolder.checkBoxSelect.setChecked(false);
                    viewHolder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    if (mList.isNotExistRoom()) {
                        FragmentChat.onForwardBottomSheet.path(mList, false, true);
                    } else {
                        FragmentChat.onForwardBottomSheet.path(mList, false, false);
                    }
                    //mList.setSelected(false);
                } else {
                    viewHolder.checkBoxSelect.setChecked(true);
                    viewHolder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    if (mList.isNotExistRoom()) {
                        FragmentChat.onForwardBottomSheet.path(mList, true, true);
                    } else {
                        FragmentChat.onForwardBottomSheet.path(mList, true, false);
                    }
                    //mList.setSelected(true);
                }
            }
        });

    }

    private void setAvatar(final StructBottomSheetForward mInfo, CircleImageView imageView) {
        long idForGetAvatar;
        AvatarHandler.AvatarType avatarType;
        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
            idForGetAvatar = mInfo.getPeer_id();
            avatarType = AvatarHandler.AvatarType.USER;
        } else {
            idForGetAvatar = mInfo.getId();
            avatarType = AvatarHandler.AvatarType.ROOM;
        }

        avatarHandler.getAvatar(new ParamWithAvatarType(imageView, idForGetAvatar).avatarSize(R.dimen.dp52).avatarType(avatarType));
    }

    private void setAvatarContact(final ViewHolder holder, final long userId) {
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.imgSrc, userId).avatarType(AvatarHandler.AvatarType.USER));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.root_forward_bottom_sheet;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.adapter_forward_bottom_sheet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected AnimateCheckBox checkBoxSelect;
        private EmojiTextViewE txtName;
        private CircleImageView imgSrc;

        public ViewHolder(View view) {
            super(view);
            txtName = (EmojiTextViewE) view.findViewById(R.id.txtTitle_forward_bottomSheet);
            imgSrc = (CircleImageView) view.findViewById(R.id.imageView_forward_bottomSheet);
            checkBoxSelect = (AnimateCheckBox) view.findViewById(R.id.checkBox_forward_bottomSheet);
        }
    }

}


//}