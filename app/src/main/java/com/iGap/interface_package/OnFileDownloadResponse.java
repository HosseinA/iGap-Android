package com.iGap.interface_package;

import com.iGap.proto.ProtoFileDownload;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/28/2016.
 */
public interface OnFileDownloadResponse {
    void onFileDownload(String token, int offset, ProtoFileDownload.FileDownload.Selector selector, int progress);
}
