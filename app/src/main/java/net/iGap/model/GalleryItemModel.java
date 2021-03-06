package net.iGap.model;

import androidx.annotation.Nullable;

public class GalleryItemModel {

    private long id;
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryItemModel) {
            return ((GalleryItemModel) obj).getId() == this.id;
        }
        return super.equals(obj);
    }
}
