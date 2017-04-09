package com.iGap.helper;

import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.OnFileUpload;
import com.iGap.interfaces.OnFileUploadStatusResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.FileUploadStructure;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAttachment;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestFileUploadOption;
import com.iGap.request.RequestFileUploadStatus;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static com.iGap.G.handler;
import static com.iGap.activities.ActivityChat.compressingFiles;
import static com.iGap.proto.ProtoGlobal.ClientAction.CHOOSING_CONTACT;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_AUDIO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_FILE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_GIF;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_IMAGE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_LOCATION;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VIDEO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VOICE;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;

/**
 * Created by android3 on 2/4/2017.
 */

public class HelperUploadFile implements OnFileUpload, OnFileUploadStatusResponse {

    public static OnFileUpload onFileUpload;
    public static OnFileUploadStatusResponse onFileUploadStatusResponse;

    private static int uploadingMaxsize = 1;
    private static Queue<StructUpload> myQueue = new LinkedList<StructUpload>();

    public interface UpdateListener {
        void OnProgress(int progress, FileUploadStructure struct);

        void OnError();
    }

    public static class StructUpload {

        public UpdateListener listener1;
        public UpdateListener listener2;

        public FileUploadStructure fileUploadStructure = null;
        public int attampOnError = 2;
        int progress = 0;
        ProtoGlobal.Room.Type chatType = null;
        public String identity = "";
    }

    public HelperUploadFile() {
        onFileUploadStatusResponse = this;
        onFileUpload = this;
    }

    //*******************************************************************************************************************

    private static ArrayMap<String, StructUpload> list = new ArrayMap<>();

    private static void startUpload(FileUploadStructure uploadStructure, String identity, UpdateListener listener, ProtoGlobal.Room.Type chatType, boolean FromChat) {

        StructUpload structUpload = null;

        if (!list.containsKey(identity)) {

            if (compressingFiles.containsKey(Long.parseLong(identity))) {
                structUpload = compressingFiles.get(Long.parseLong(identity));
            }

            if (structUpload == null) {
                structUpload = new StructUpload();
                structUpload.listener1 = listener;
            }

            structUpload.fileUploadStructure = uploadStructure;
            structUpload.chatType = chatType;
            structUpload.identity = identity;

            if (FromChat && list.size() >= uploadingMaxsize) {
                myQueue.add(structUpload);
                list.put(identity, structUpload);
            } else {
                list.put(identity, structUpload);
                new RequestFileUploadOption().fileUploadOption(uploadStructure, identity);
            }
        } else {

            structUpload = list.get(identity);
            structUpload.listener1 = listener;
        }

        updateListeners(structUpload);
        structUpload.progress = 1;
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String identity, ProtoResponse.Response response) {
        try {
            FileUploadStructure fileUploadStructure = list.get(identity).fileUploadStructure;
            // getting bytes from file as server said
            byte[] bytesFromFirst = AndroidUtils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast = AndroidUtils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileUploadStructure.fileHash, Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
        }
    }

    @Override
    public void OnFileUploadInit(String token, final double progress, long offset, int limit, final String identity, ProtoResponse.Response response) {
        // token needed for requesting upload
        // updating structure with new token

        StructUpload sp = list.get(identity);

        if (sp == null) {
            return;
        }

        sp.progress = (int) progress;

        FileUploadStructure fileUploadStructure = sp.fileUploadStructure;
        fileUploadStructure.token = token;

        updateListeners(sp);

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = AndroidUtils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, identity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {

                onFileUploadComplete(identity, response);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit, final String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        StructUpload sp = list.get(identity);
        if (sp == null) {
            return;
        }
        sp.progress = (int) progress;

        try {
            if (progress != 100.0) {
                FileUploadStructure fileUploadStructure = list.get(identity).fileUploadStructure;

                updateListeners(list.get(identity));

                byte[] bytes = AndroidUtils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset, nextLimit);

                // make request till uploading has finished
                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, identity);
            } else {
                onFileUploadComplete(identity, response);
            }
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
        }
    }

    @Override
    public void onFileUploadComplete(String identity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = list.get(identity).fileUploadStructure;

        new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
    }

    @Override
    public void onFileUploadTimeOut(String identity) {

        StructUpload sp = list.get(identity);
        if (sp != null) {

            sp.attampOnError--;

            if (sp.attampOnError > 0) {
                reUpload(sp.identity);
            } else {
                updateListenersError(sp);
                list.remove(identity);
                addItemFromQueue();
            }
        }
    }

    public static void reUpload(String identity) {

        if (list.containsKey(identity)) {

            new RequestFileUploadOption().fileUploadOption(list.get(identity).fileUploadStructure, identity);
        }
    }

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status, double progress, int recheckDelayMS, final String identity, ProtoResponse.Response response) {

        StructUpload sp = list.get(identity);

        if (sp == null) {
            return;
        }

        final FileUploadStructure fileUploadStructure = sp.fileUploadStructure;

        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED) {

            sp.progress = 100;
            updateListeners(sp);

            if (sp.chatType != null) {

                UploadComplete(sp.fileUploadStructure, sp.identity, sp.chatType);
            }

            // remove from selected files to prevent calling this method multiple times
            // multiple calling may occurs because of the server
            try {
                // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
                //removeFromSelectedFiles(identity);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
                e.printStackTrace();
            }

            // close file into structure
            try {
                if (fileUploadStructure != null) {
                    fileUploadStructure.closeFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            list.remove(identity);
            addItemFromQueue();
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING || (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.UPLOADING) && progress == 100D) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
                }
            }, recheckDelayMS);
        } else {

            if (list.containsKey(identity)) {
                StructUpload spl = list.get(identity);
                list.remove(identity);
                startUpload(spl.fileUploadStructure, spl.identity, null, spl.chatType, false);
            }
        }
    }

    private void UploadComplete(final FileUploadStructure uploadStructure, final String identity, ProtoGlobal.Room.Type chatType) {

        HelperSetAction.sendCancel(uploadStructure.messageId);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAttachment.updateToken(uploadStructure.messageId, uploadStructure.token);
            }
        });
        realm.close();

        /**
         * this code should exist in under of other codes in this block
         */
        if (uploadStructure.replyMessageId == 0) {
            new ChatSendMessageUtil().newBuilder(chatType, uploadStructure.messageType, uploadStructure.roomId).attachment(uploadStructure.token).message(uploadStructure.text).sendMessage(Long.toString(uploadStructure.messageId));
        } else {
            new ChatSendMessageUtil().newBuilder(chatType, uploadStructure.messageType, uploadStructure.roomId).replyMessage(uploadStructure.replyMessageId).attachment(uploadStructure.token).message(uploadStructure.text).sendMessage(Long.toString(uploadStructure.messageId));
        }
    }

    private static void updateListeners(StructUpload upload) {

        if (upload == null) {
            return;
        }

        if (upload.listener1 != null) {
            upload.listener1.OnProgress(upload.progress, upload.fileUploadStructure);
        }
        if (upload.listener2 != null) {
            upload.listener2.OnProgress(upload.progress, upload.fileUploadStructure);
        }
    }

    private static void updateListenersError(StructUpload upload) {

        if (upload == null) {
            return;
        }

        if (upload.listener1 != null) {
            upload.listener1.OnError();
        }
        if (upload.listener2 != null) {
            upload.listener2.OnError();
        }
    }

    public static void AddListener(String identity, UpdateListener listener) {

        StructUpload sp = list.get(identity);

        if (sp == null && compressingFiles.containsKey(Long.parseLong(identity))) {
            StructUpload structUpload = new StructUpload();
            structUpload.listener1 = listener;
            compressingFiles.setValueAt(compressingFiles.indexOfKey(Long.parseLong(identity)), structUpload);
        }

        if (sp != null) {
            sp.listener2 = listener;
        }

        updateListeners(sp);
    }

    public static boolean cancelUploading(String identity) {

        if (list.containsKey(identity)) {

            list.remove(identity);
            HelperDownloadFile.removeRequestQueue(identity);

            return true;
        }

        return false;
    }

    public static boolean isUploading(String identity) {

        if (list == null) return false;

        if (list.containsKey(identity)) return true;

        return false;
    }

    public static int getUploadProgress(String identity) {

        if (list.containsKey(identity)) {

            return list.get(identity).progress;
        }

        return -1;
    }

    //*******************************************************************************************************************

    public static void addItemFromQueue() {

        if (myQueue.size() > 0) {
            StructUpload sp = myQueue.poll();
            if (sp != null) {
                new RequestFileUploadOption().fileUploadOption(sp.fileUploadStructure, sp.identity);
            }
        }
    }

    public static void startUploadTaskChat(Long roomID, ProtoGlobal.Room.Type chatType, String filePath, long messageId, ProtoGlobal.RoomMessageType messageType, String messageText, long replyMessageId, UpdateListener listener) {
        new UploadTask(roomID, chatType, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filePath, messageId, messageType, roomID, messageText, replyMessageId);
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {

        private long roomID;
        ProtoGlobal.Room.Type chatType;
        UpdateListener listener;

        public UploadTask(Long roomID, ProtoGlobal.Room.Type chatType, UpdateListener listener) {
            this.roomID = roomID;
            this.chatType = chatType;
            this.listener = listener;
        }

        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long messageId = (long) params[1];
                ProtoGlobal.RoomMessageType messageType = (ProtoGlobal.RoomMessageType) params[2];
                long roomId = (long) params[3];
                String messageText = (String) params[4];
                long replyMessageId = (long) params[5];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, messageId, messageType, roomId, replyMessageId);
                fileUploadStructure.openFile(filePath);
                fileUploadStructure.text = messageText;

                byte[] fileHash = AndroidUtils.getFileHashFromPath(filePath);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);

            if (result != null) {
                if (!list.containsKey(result.messageId)) {
                    startUpload(result, Long.toString(result.messageId), listener, chatType, true);
                    HelperSetAction.setActionFiles(roomID, result.messageId, getAction(result.messageType), chatType);
                }
            }
        }
    }

    static ProtoGlobal.ClientAction getAction(ProtoGlobal.RoomMessageType type) {

        //TODO [Saeed Mozaffari] [2016-11-14 11:14 AM] - some actions need to detect

        ProtoGlobal.ClientAction action = null;

        if ((type == ProtoGlobal.RoomMessageType.IMAGE) || (type == IMAGE_TEXT)) {
            action = SENDING_IMAGE;
        } else if ((type == ProtoGlobal.RoomMessageType.VIDEO) || (type == VIDEO_TEXT)) {
            action = SENDING_VIDEO;
        } else if ((type == ProtoGlobal.RoomMessageType.AUDIO) || (type == ProtoGlobal.RoomMessageType.AUDIO_TEXT)) {
            action = SENDING_AUDIO;
        } else if (type == ProtoGlobal.RoomMessageType.VOICE) {
            action = SENDING_VOICE;
        } else if ((type == ProtoGlobal.RoomMessageType.GIF) || type == GIF_TEXT) {
            action = SENDING_GIF;
        } else if ((type == ProtoGlobal.RoomMessageType.FILE) || (type == ProtoGlobal.RoomMessageType.FILE_TEXT)) {
            action = SENDING_FILE;
        } else if (type == ProtoGlobal.RoomMessageType.LOCATION) {
            action = SENDING_LOCATION;
        } else if (type == CONTACT) {
            action = CHOOSING_CONTACT;
        }

        return action;
    }

    public static void startUploadTaskAvatar(String filePath, long avatarId, UpdateListener listener) {

        if (G.userLogin) {
            new UploadTaskAvatar(listener).execute(filePath, avatarId);
        } else {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
            if (listener != null) {
                listener.OnError();
            }
        }
    }

    private static class UploadTaskAvatar extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {

        UpdateListener listener;

        public UploadTaskAvatar(UpdateListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long avatarId = (long) params[1];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, avatarId);
                fileUploadStructure.openFile(filePath);

                byte[] fileHash = AndroidUtils.getFileHashFromPath(filePath);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);

            startUpload(result, result.messageId + "", listener, null, false);
        }
    }






}
