package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import java.util.List;

public class UnreadMessage extends AbstractMessage<UnreadMessage, UnreadMessage.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public UnreadMessage(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override public int getType() {
        return R.id.cslum_txt_unread_message;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layot_unread_message;
    }

    @Override public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setTextIfNeeded(holder.txtUnreadMessage, mMessage.messageText);
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override void OnDownLoadFileFinish(ViewHolder holder, String path) {

    }

    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtUnreadMessage;

        public ViewHolder(View view) {
            super(view);

            txtUnreadMessage = (TextView) view.findViewById(R.id.cslum_txt_unread_message);
            txtUnreadMessage.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                }
            });

            txtUnreadMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
    }
}
