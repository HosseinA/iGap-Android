package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.LastSeenTimeUtil;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import io.realm.Realm;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;

    public ContactItem setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        Log.i("WWW", "1");
        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        holder.title.setText(mContact.displayName);
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mContact.peerId).findFirst();
        if (realmRegisteredInfo != null) {

            if (realmRegisteredInfo.getStatus() != null) {
                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    holder.subtitle.setText(LastSeenTimeUtil.computeTime(mContact.peerId, realmRegisteredInfo.getLastSeen(), false));
                } else {
                    holder.subtitle.setText(realmRegisteredInfo.getStatus());
                }
            }

            if (HelperCalander.isLanguagePersian) {
                holder.subtitle.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.subtitle.getText().toString()));
            }


        }
        realm.close();

        setAvatar(holder);
        Log.i("WWW", "2****");
    }

    private void setAvatar(final ViewHolder holder) {

        HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            }
        });

    }



    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
        }
    }
}
