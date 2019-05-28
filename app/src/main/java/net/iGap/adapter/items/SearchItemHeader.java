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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.nostra13.universalimageloader.utils.L;

import net.iGap.G;
import net.iGap.R;

import java.util.List;

public class SearchItemHeader extends AbstractItem<SearchItemHeader, SearchItemHeader.ViewHolder> {
    public String text;

    public SearchItemHeader setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int getType() {
        return R.id.sfslh_txt_header_text;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout_header;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        if (holder.getAdapterPosition() == 0 ){
            holder.root.setPadding(0 , 22 , 0 , 0 );
        }else {
            holder.root.setPadding(0 , 8 , 0 , 0 );
        }

        if (G.isDarkTheme){
            holder.txtHeader.setTextColor(G.context.getResources().getColor(R.color.white));
        }
        holder.txtHeader.setText(text);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtHeader;
        protected LinearLayout root ;

        public ViewHolder(View view) {
            super(view);
            txtHeader = (TextView) view.findViewById(R.id.sfslh_txt_header_text);
            root =  view.findViewById(R.id.sfslh_header_layout);
        }
    }
}


