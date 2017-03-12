package com.iGap.request;

import com.iGap.module.FileUploadStructure;
import com.iGap.proto.ProtoFileUploadOption;

public class RequestFileUploadOption {

    public void fileUploadOption(FileUploadStructure fileUploadStructure, String identity) {

        ProtoFileUploadOption.FileUploadOption.Builder fileUploadOption = ProtoFileUploadOption.FileUploadOption.newBuilder();
        fileUploadOption.setSize(fileUploadStructure.fileSize);

        try {
            RequestWrapper requestWrapper = new RequestWrapper(700, fileUploadOption, identity);

            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
