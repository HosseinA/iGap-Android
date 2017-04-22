/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.iGap.R;
import com.iGap.module.StructSessionsGetActiveList;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import java.util.List;


public class AdapterActiveSessionsHeader extends AbstractItem<AdapterActiveSessionsHeader, AdapterActiveSessionsHeader.ViewHolder> {

    public StructSessionsGetActiveList item;

    private List<StructSessionsGetActiveList> itemList;

    public List<StructSessionsGetActiveList> getItem() {
        return itemList;
    }

    public AdapterActiveSessionsHeader(List<StructSessionsGetActiveList> item) {
        itemList = item;
    }

    public void setItem(List<StructSessionsGetActiveList> item) {
        this.itemList = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.adph_rootLayout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_active_sessions_header;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

//        holder.root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
//                for (int i = 0; i < itemList.size(); i++) {
//                    if (!itemList.get(i).isCurrent()) {
//                        new RequestUserSessionTerminate().userSessionTerminate(itemList.get(i).getSessionId());
//
//                    }
//                }
//            }
//        });

    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup root;

        public ViewHolder(View view) {
            super(view);

            root = (ViewGroup) view.findViewById(R.id.adph_rootLayout);
        }
    }

    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }
}