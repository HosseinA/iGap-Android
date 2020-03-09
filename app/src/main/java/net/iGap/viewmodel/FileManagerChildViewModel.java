package net.iGap.viewmodel;


import android.os.Environment;

import com.google.common.collect.Ordering;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperMimeType;
import net.iGap.module.FileUtils;
import net.iGap.module.structs.StructExplorerItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManagerChildViewModel extends BaseViewModel {

    private List<StructExplorerItem> mItems = new ArrayList<>();
    private List<String> mSelectedList = new ArrayList<>();

    public void setSelectedList(List<String> mSelectedList) {
        this.mSelectedList = mSelectedList;
    }

    public List<StructExplorerItem> getRootItems() {

        List<String> storageList = FileUtils.getSdCardPathList();

        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) {
            addItemToList(
                    R.string.internal_storage,
                    null ,
                    R.drawable.ic_fm_internal,
                    Environment.getExternalStorageDirectory().getAbsolutePath(),
                    R.string.internal_desc,
                    null ,
                    R.drawable.shape_file_manager_file_bg,
                    true
            );
        }

        for (String sdPath : storageList) {
            if (new File(sdPath).exists()) {
                addItemToList(
                        R.string.external_storage,
                        null ,
                        R.drawable.ic_fm_memory,
                        sdPath + "/",
                        R.string.external_desc,
                        null ,
                        R.drawable.shape_file_manager_folder_bg,
                        true
                );
            }
        }

        if (!G.DIR_SDCARD_EXTERNAL.equals("")) {
            if (new File(G.DIR_SDCARD_EXTERNAL).exists()) {
                addItemToList(
                        R.string.external_storage,
                        null ,
                        R.drawable.ic_fm_folder,
                        G.DIR_SDCARD_EXTERNAL + "/",
                        R.string.file_manager_app_desc,
                        null ,
                        R.drawable.shape_file_manager_folder_bg,
                        true
                );
            }
        }

        if (new File(G.DIR_APP).exists()) {
            addItemToList(
                    R.string.app_name,
                    null ,
                    R.drawable.ic_fm_folder,
                    G.DIR_APP + "/",
                    R.string.file_manager_app_desc,
                    null ,
                    R.drawable.shape_file_manager_file_bg,
                    true
            );
        }

        addItemToList(
                R.string.images,
                null ,
                R.drawable.ic_fm_image,
                null,
                R.string.file_manager_image_desc,
                null ,
                R.drawable.shape_file_manager_file_1_bg,
                false
        );

        addItemToList(
                R.string.videos,
                null ,
                R.drawable.ic_fm_video,
                null,
                R.string.file_manager_video_desc,
                null ,
                R.drawable.shape_file_manager_file_1_bg,
                false
        );

        addItemToList(
                R.string.audios,
                null ,
                R.drawable.ic_fm_audio,
                G.DIR_APP + "/",
                R.string.file_manager_music,
                null ,
                R.drawable.shape_file_manager_file_2_bg,
                false
        );

        return mItems;
    }

    public void getFoldersSubItems(String folder , FolderResultCallback callback){
        new Thread(() -> {

            File file = new File(folder);
            if (file.isDirectory()) {
                String[] items = file.list();
                for (String item : items) {

                    //ignore hidden and temp files
                    if (item.startsWith(".")) continue;
                    if (item.endsWith(".tmp")) continue;

                    String address = folder + "/" + item;
                    File subFile = new File(address);

                    addItemToList(
                            0 ,
                            item,
                            subFile.isDirectory() ? R.drawable.ic_fm_folder : HelperMimeType.getMimeResource(address),
                            address,
                            subFile.isDirectory() ? R.string.folder : 0,
                            subFile.isDirectory() ? null : getFileDescription(subFile),
                            subFile.isDirectory() ? R.drawable.shape_file_manager_folder_bg : R.drawable.shape_file_manager_file_bg,
                            true
                    );
                }

                Collections.sort(mItems, Ordering.from(new FileManager.SortFolder()).compound(new FileManager.SortFileName()));
                checkListHasSelectedBefore();
                callback.onResult(mItems);
            }

        }).start();

    }

    public void sortList(Boolean isDate){
        if(isDate == null){
            Collections.sort(mItems, Ordering.from(new FileManager.SortFolder()).compound(new FileManager.SortFileName()));
        }else if(isDate){
            Collections.sort(mItems, new FileManager.SortFileDate());
        }else {
            Collections.sort(mItems, new FileManager.SortFileName());
        }
    }

    private void addItemToList(int title ,String titleStr, int image, String path, int desc , String descStr, int background, boolean isFolderOrFile) {
        StructExplorerItem item = new StructExplorerItem();
        item.name = title;
        item.nameStr = titleStr;
        item.image = image;
        item.path = path;
        item.backColor = background;
        item.description = desc;
        item.descriptionStr = descStr;
        item.isFolderOrFile = isFolderOrFile;
        mItems.add(item);
    }

    private String getFileDescription(File file) {
        float kb, mb;
        kb = file.length() / 1024;
        mb = kb / 1024;
        if(mb == 0) {
            return (new DecimalFormat("##.##").format(kb)) + " kb";
        } else {
            return (new DecimalFormat("##.##").format(mb)) + " mb";
        }
    }

    private void checkListHasSelectedBefore(){
        for (int i = 0 ; i < mItems.size() ; i++){
            for(int j = 0 ; j < mSelectedList.size() ; j++){
                if(mItems.get(i).path.equals(mSelectedList.get(j)))
                    mItems.get(i).isSelected = true;
            }
        }
    }

    public List<StructExplorerItem> getItems() {
        return mItems;
    }

    public interface FolderResultCallback{
        void onResult(List<StructExplorerItem> items);
    }
}
