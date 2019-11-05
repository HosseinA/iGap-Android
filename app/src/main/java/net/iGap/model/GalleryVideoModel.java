package net.iGap.model;


import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class GalleryVideoModel {

    private String id ;
    private String caption = "";
    private String path ;
    private Bitmap cover ;

    public GalleryVideoModel() {
    }

    public GalleryVideoModel(String id, String caption , String path, Bitmap cover) {
        this.id = id;
        this.caption = caption;
        this.path = path;
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryVideoModel){
            return ((GalleryVideoModel) obj).getId().equals(this.id) ;
        }
        return super.equals(obj);
    }
}
