package com.iGap.helper;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.iGap.G;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


/**
 * tashkhise inke etela'ati dakhele barname baraye eshterak gozari ersal shode va ya barname be surate mamul baz shode ast
 */

public class HelperGetDataFromOtherApp {

    public enum FileType {
        message,
        video,
        file,
        audio,
        image
    }

    private Intent intent;

    public static boolean hasSharedData = false;                    // after use intent set this to false
    public static FileType messageType;
    public static String message = "";
    public static ArrayList<Uri> messageFileAddress;
    public static ArrayList<FileType> fileTypeArray = new ArrayList<FileType>();


    public HelperGetDataFromOtherApp(Intent intent) {

        this.intent = intent;

        if (intent == null) {
            return;
        }

        checkData(intent);
    }


    /**
     * check intent data and get type and address message
     *
     * @param intent
     */
    private void checkData(Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();

        if (action == null || type == null)
            return;

        if (Intent.ACTION_SEND.equals(action)) {

            if (type.equals("text/plain")) {

                handleSendText(intent);
            } else if (type.startsWith("image/")) {

                SetOutPutSingleFile(FileType.image);

            } else if (type.startsWith("video/")) {

                SetOutPutSingleFile(FileType.video);

            } else if (type.startsWith("audio/")) {

                SetOutPutSingleFile(FileType.audio);

            } else {

                SetOutPutSingleFile(FileType.file);

            }

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {

            if (type.startsWith("image/")) {

                SetOutPutMultipleFile(FileType.image);
            } else if (type.startsWith("video/")) {

                SetOutPutMultipleFile(FileType.video);

            } else if (type.startsWith("audio/")) {

                SetOutPutMultipleFile(FileType.audio);

            } else {

                SetOutPutMultipleFile(FileType.file);
            }
        }

    }


    //*****************************************************************************************************

    private void SetOutPutSingleFile(FileType type) {

        Uri fileAddressUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (fileAddressUri != null) {
            hasSharedData = true;
            messageType = type;
            messageFileAddress = new ArrayList<Uri>();
            messageFileAddress.add(fileAddressUri);
        }

    }


    //*****************************************************************************************************

    private void SetOutPutMultipleFile(FileType type) {

        ArrayList<Uri> fileAddressUri = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileAddressUri != null) {

            hasSharedData = true;
            messageType = type;
            messageFileAddress = fileAddressUri;

            for (int i = 0; i < messageFileAddress.size(); i++) {
                FileType fileType = getMimeType(fileAddressUri.get(i));
                fileTypeArray.add(fileType);
            }

        }
    }


    //*****************************************************************************************************

    void handleSendText(Intent intent) {

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {

            hasSharedData = true;
            messageType = FileType.message;
            message = sharedText;
        } else {
            SetOutPutSingleFile(FileType.file);
        }

    }


    //*****************************************************************************************************

    public static FileType getMimeType(Uri uri) {
        String extension;
        FileType fileType = FileType.file;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(G.context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        extension = extension.toLowerCase();

        if (extension.endsWith("jpg") || extension.endsWith("jpeg") || extension.endsWith("png") || extension.endsWith("bmp") || extension.endsWith(".tiff"))
            fileType = FileType.image;
        else if (extension.endsWith("mp3") || extension.endsWith("ogg") || extension.endsWith("wma") || extension.endsWith("m4a") || extension.endsWith("amr") || extension.endsWith("wav") || extension.endsWith(".mid") || extension.endsWith(".midi"))
            fileType = FileType.audio;
        else if (extension.endsWith("mp4") || extension.endsWith("3gp") || extension.endsWith("avi") || extension.endsWith("mpg") || extension.endsWith("flv") || extension.endsWith("wmv") || extension.endsWith("m4v") || extension.endsWith(".mpeg"))
            fileType = FileType.video;

        return fileType;
    }

    //*****************************************************************************************************


    /**
     * get every data in bundle from intent
     */
    private void getAllDAtaInIntent(Intent intent) {

        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            while (it.hasNext()) {
                String key = it.next();
                Log.i("LOG", key + "=" + bundle.get(key));
            }
        }
    }


    public ArrayList<Uri> getInfo() {
        return messageFileAddress;
    }

}
