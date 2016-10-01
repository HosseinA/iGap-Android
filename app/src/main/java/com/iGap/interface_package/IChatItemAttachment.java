package com.iGap.interface_package;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public interface IChatItemAttachment<VH extends RecyclerView.ViewHolder> {
    void onLoadFromLocal(VH holder, String localPath);

    void onRequestDownloadFile(int offset, int progress);

    void onRequestDownloadThumbnail();
}
