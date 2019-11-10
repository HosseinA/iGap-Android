package net.iGap.helper;

import android.content.Context;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import net.iGap.R;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryItemModel;
import net.iGap.model.GalleryVideoModel;

import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static void getDevicePhotoFolders(Context context, FetchListener<List<GalleryAlbumModel>> callback) {

        new Thread(() -> {

            List<GalleryAlbumModel> albums = new ArrayList<>();
            if (context == null) {
                callback.onFetch(albums);
                return;
            }

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            };

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            if (cursor != null) {

                final int COLUMN_ID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                final int COLUMN_BUCKET_NAME = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryAlbumModel album = new GalleryAlbumModel();
                        album.setId(cursor.getString(COLUMN_ID));
                        if (!ids.contains(album.getId())) {
                            album.setCaption(cursor.getString(COLUMN_BUCKET_NAME));
                            album.setCover(cursor.getString(COLUMN_DATA));
                            if (!album.getCover().contains(".gif")) {
                                //check and add ALL for first item
                                if (albums.size() == 0) {
                                    albums.add(new GalleryAlbumModel("-1", context.getString(R.string.all), album.getCover()));
                                }
                                albums.add(album);
                                ids.add(album.getId());
                            }
                        }//else could be counter
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(albums);

        }).start();

    }

    public static void getFolderPhotosById(Context context, String folderId, FetchListener<List<GalleryItemModel>> callback) {

        new Thread(() -> {

            List<GalleryItemModel> photos = new ArrayList<>();
            if (context == null) {
                callback.onFetch(photos);
                return;
            }

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.DATE_TAKEN
            };

            boolean isAllPhoto = folderId.equals("-1");

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    isAllPhoto ? null : MediaStore.Images.Media.BUCKET_ID + " = ?",
                    isAllPhoto ? null : new String[]{folderId},
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryItemModel photo = new GalleryItemModel();
                        photo.setId(photos.size());
                        photo.setAddress(cursor.getString(COLUMN_DATA));
                        if (photo.getAddress() != null && !photo.getAddress().contains(".gif")) {
                            photos.add(photo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(photos);

        }).start();
    }

    public static void getFolderVideosById(Context context, String folderId, FetchListener<List<GalleryVideoModel>> callback) {

        new Thread(() -> {

            List<GalleryVideoModel> photos = new ArrayList<>();
            if (context == null) {
                callback.onFetch(photos);
                return;
            }

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Video.Media.DATA
            };

            boolean isAllPhoto = folderId.equals("-1");

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    isAllPhoto ? null : MediaStore.Video.Media.BUCKET_ID + " = ?",
                    isAllPhoto ? null : new String[]{folderId},
                    MediaStore.Video.Media.DATE_TAKEN + " DESC"
            );

            if (cursor != null) {

                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryVideoModel video = new GalleryVideoModel();
                        video.setId(photos.size() + "");
                        video.setPath(cursor.getString(COLUMN_DATA));
                        photos.add(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(photos);

        }).start();

    }

    public static void getDeviceVideoFolders(Context context, FetchListener<List<GalleryVideoModel>> callback) {

        new Thread(() -> {

            List<GalleryVideoModel> albums = new ArrayList<>();
            if (context == null) {
                callback.onFetch(albums);
                return;
            }

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            };

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    MediaStore.Video.Media.DATE_TAKEN + " DESC");

            ArrayList<String> ids = new ArrayList<>();
            if (cursor != null) {

                final int COLUMN_ID = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID);
                final int COLUMN_BUCKET_NAME = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
                final int COLUMN_DATA = cursor.getColumnIndex(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    try {
                        GalleryVideoModel album = new GalleryVideoModel();
                        album.setId(cursor.getString(COLUMN_ID));
                        if (!ids.contains(album.getId())) {
                            album.setCaption(cursor.getString(COLUMN_BUCKET_NAME));
                            album.setPath(cursor.getString(COLUMN_DATA));

                            //check and add ALL for first item
                            if (albums.size() == 0) {
                                albums.add(new GalleryVideoModel("-1", context.getString(R.string.all), album.getPath()));
                            }
                            albums.add(album);
                            ids.add(album.getId());

                        }//else could be counter
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
            }
            callback.onFetch(albums);

        }).start();
    }

    public interface FetchListener<T> {
        void onFetch(T result);
    }
}
