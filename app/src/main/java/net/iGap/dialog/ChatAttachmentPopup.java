package net.iGap.dialog;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BottomSheetItem;
import net.iGap.adapter.items.AdapterCamera;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnClickCamera;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPathAdapterBottomSheet;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructBottomSheet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import io.fotoapparat.Fotoapparat;

import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static net.iGap.R.string.item;
import static net.iGap.fragments.FragmentChat.listPathString;

public class ChatAttachmentPopup {

    private final String TAG = "ChatAttachmentPopup";

    private Context mContext;
    private View mRootView;
    private ChatPopupListener mPopupListener;
    private PopupWindow mPopup;
    private SharedPreferences mSharedPref;
    private FragmentActivity mFrgActivity;
    private Fragment mFragment;
    private AttachFile attachFile;
    private RecyclerView rcvBottomSheet;
    private FastItemAdapter fastItemAdapter;
    private boolean isNewBottomSheet;
    private OnClickCamera onClickCamera;
    private OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    private View btnSend;
    private TextView icoSend;
    private TextView lblSend;
    private boolean isPermissionCamera;
    private View viewRoot;
    private boolean isCameraAttached;
    private Fotoapparat fotoapparatSwitcher;
    private boolean isCameraStart;

    private ChatAttachmentPopup() {
    }

    public static ChatAttachmentPopup create() {
        return new ChatAttachmentPopup();
    }

    public ChatAttachmentPopup setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public ChatAttachmentPopup setRootView(View view) {
        this.mRootView = view;
        return this;
    }

    public ChatAttachmentPopup setListener(ChatPopupListener listener) {
        this.mPopupListener = listener;
        return this;
    }

    public ChatAttachmentPopup setSharedPref(SharedPreferences pref) {
        this.mSharedPref = pref;
        return this;
    }

    public ChatAttachmentPopup setFragmentActivity(FragmentActivity fa) {
        this.mFrgActivity = fa;
        return this;
    }

    public ChatAttachmentPopup setFragment(Fragment frg) {
        this.mFragment = frg;
        return this;
    }

    public ChatAttachmentPopup build() {

        if (mContext == null)
            throw new IllegalArgumentException(TAG + " : CONTEXT can not be null!");

        if (mRootView == null)
            throw new IllegalArgumentException(TAG + " : set root view!");


        //inflate layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        viewRoot = inflater.inflate(R.layout.bottom_sheet_new, null, false);

        attachFile = new AttachFile(mFrgActivity);
        initViews(viewRoot);

        //setup popup
        mPopup = new PopupWindow(mContext);
        mPopup.setContentView(viewRoot);
        mPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.setBackgroundDrawable(new BitmapDrawable());
        mPopup.setFocusable(true);
        mPopup.setOutsideTouchable(true);
        mPopup.setAnimationStyle(R.style.chatAttachmentAnimation);


        mPopup.setOnDismissListener(() -> {
            isNewBottomSheet = true;
            if (isPermissionCamera) {
                if (fotoapparatSwitcher != null) {
                    G.handler.postDelayed(() -> {
                        if (!isCameraStart) {
                            fotoapparatSwitcher.start();
                            isCameraStart = true;
                        }
                    }, 50);
                }
            }
        });

        return this;
    }

    public void setIsNewDialog(boolean isNew){
        this.isNewBottomSheet = isNew ;
    }

    public void show() {

        //get height of keyboard if it was gone set wrap content to popup
        int height = getKeyboardHeight();
        if (height == 0) height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mPopup.setHeight(height);

        setupAdapterRecyclerImagesAndShowPopup();
    }

    private void initViews(View view) {

        View camera, photo, video, music, file, contact, location;

        camera = view.findViewById(R.id.camera);
        photo = view.findViewById(R.id.picture);
        video = view.findViewById(R.id.video);
        music = view.findViewById(R.id.music);
        file = view.findViewById(R.id.file);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        btnSend = view.findViewById(R.id.close);
        icoSend = view.findViewById(R.id.txtSend);
        lblSend = view.findViewById(R.id.txtNumberItem);


        btnSend.setOnClickListener(v -> {


            if (FragmentEditImage.textImageList.size() > 0) {
                mPopup.dismiss();
                clearRecyclerAdapter();
                //send.setImageResource(R.mipmap.ic_close);
                lblSend.setText(mFrgActivity.getString(R.string.close_icon));
                lblSend.setText(mFrgActivity.getString(R.string.navigation_drawer_close));

                mPopupListener.onAttachPopupSendSelected();

            } else {
                mPopup.dismiss();
            }

        });

        camera.setOnClickListener(v -> {
            mPopup.dismiss();

            if (mSharedPref.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                attachFile.showDialogOpenCamera(v, null, mFragment);
            } else {
                attachFile.showDialogOpenCamera(v, null, mFragment);
            }
        });

        photo.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestOpenGalleryForImageMultipleSelect(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        video.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestOpenGalleryForVideoMultipleSelect(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        music.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestPickAudio(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        file.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestPickFile(selectedPathList -> {
                    mPopupListener.onAttachPopupFilePicked(selectedPathList);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        contact.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestPickContact(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        location.setOnClickListener(v -> {
            mPopup.dismiss();
            try {
                attachFile.requestGetPosition((result, messageOne, MessageTow) -> {
                    mPopupListener.onAttachPopupLocation(messageOne);
                }, mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //init local pictures
        fastItemAdapter = new FastItemAdapter();


        onClickCamera = () -> {
            try {
                mPopup.dismiss();
                new AttachFile(mFrgActivity).requestTakePicture(mFragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        onPathAdapterBottomSheet = (path, isCheck, isEdit, mList, id) -> {

            if (isEdit) {
                mPopup.dismiss();
                new HelperFragment(mFrgActivity.getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, id)).setReplace(false).load();
            } else {
                if (isCheck) {
                    StructBottomSheet item = new StructBottomSheet();
                    item.setPath(path);
                    item.setText("");
                    item.setId(id);
                    FragmentEditImage.textImageList.put(path, item);
                } else {
                    FragmentEditImage.textImageList.remove(path);
                }
                if (FragmentEditImage.textImageList.size() > 0) {
                    icoSend.setText(mFrgActivity.getString(R.string.md_send_button));
                    lblSend.setText("" + FragmentEditImage.textImageList.size() + " " + mFrgActivity.getString(item));
                } else {
                    icoSend.setText(mFrgActivity.getString(R.string.close_icon));
                    lblSend.setText(mFrgActivity.getString(R.string.navigation_drawer_close));
                }
            }
        };


        rcvBottomSheet = view.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(mFrgActivity, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setItemViewCacheSize(100);
        rcvBottomSheet.setAdapter(fastItemAdapter);


        rcvBottomSheet.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(final View view) {
                if (isPermissionCamera) {

                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = true;
                    }
                    if (isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher.start();
                                        }
                                    }, 50);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fotoapparatSwitcher = Fotoapparat.with(mFrgActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.start();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(final View view) {

                if (isPermissionCamera) {
                    if (rcvBottomSheet.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = false;
                    }
                    if (!isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            //                    if (isCameraStart && ( rcvBottomSheet.getChildAdapterPosition(view)> 4  || rcvBottomSheet.computeHorizontalScrollOffset() >200)){
                            if (isCameraStart) {

                                try {
                                    fotoapparatSwitcher.stop();
                                    isCameraStart = false;
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = false;
                                try {
                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            fotoapparatSwitcher = Fotoapparat.with(mFrgActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                                    .photoSize(biggestSize())   // we want to have the biggest photo possible
                                                    .lensPosition(back())       // we want back camera
                                                    .build();

                                            fotoapparatSwitcher.stop();
                                        }
                                    }, 100);
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    public void dismiss(){
        if (mPopup == null) return;
        mPopup.dismiss();
    }

    public void notifyRecyclerView() {
        if (fastItemAdapter == null) return;
        fastItemAdapter.notifyAdapterDataSetChanged();
    }

    public void clearRecyclerAdapter() {
        if (fastItemAdapter == null) return;
        fastItemAdapter.clear();
    }

    public void addItemToRecycler(IItem item) {
        if (fastItemAdapter == null || item == null) return;
        fastItemAdapter.add(item);
    }

    private void setupAdapterRecyclerImagesAndShowPopup() {

        clearRecyclerAdapter();

        if (isNewBottomSheet || FragmentEditImage.itemGalleryList.size() <= 1) {

            if (listPathString != null) {
                listPathString.clear();
            } else {
                listPathString = new ArrayList<>();
            }

            FragmentEditImage.itemGalleryList.clear();
            if (isNewBottomSheet) {
                FragmentEditImage.textImageList.clear();
            }

            try {
                HelperPermission.getStoragePermision(mFrgActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        FragmentEditImage.itemGalleryList = getAllShownImagesPath(mFrgActivity);
                        if (rcvBottomSheet != null) rcvBottomSheet.setVisibility(View.VISIBLE);
                        checkCameraAndLoadImage();
                    }

                    @Override
                    public void deny() {
                        loadImageGallery();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            checkCameraAndLoadImage();
        }


    }

    private void checkCameraAndLoadImage() {
        boolean isCameraButtonSheet = mSharedPref.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        if (isCameraButtonSheet) {
            try {
                HelperPermission.getCameraPermission(mFrgActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                addItemToRecycler(new AdapterCamera("", onClickCamera).withIdentifier(99));
                                for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                                    addItemToRecycler(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
                                }
                                isPermissionCamera = true;
                            }
                        });
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showPopup();
                            }
                        }, 100);
                    }

                    @Override
                    public void deny() {

                        loadImageGallery();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadImageGallery();
        }
    }

    private void showPopup() {

        if (FragmentEditImage.textImageList != null && FragmentEditImage.textImageList.size() > 0) {
            //icoSend.setText(R.mipmap.send2);
            if (icoSend != null)
                icoSend.setText(mFrgActivity.getResources().getString(R.string.md_send_button));
            if (lblSend != null)
                lblSend.setText("" + FragmentEditImage.textImageList.size() + " " + mFrgActivity.getResources().getString(item));
        } else {
            //icoSend.setImageResource(R.mipmap.ic_close);
            if (icoSend != null)
                icoSend.setText(mFrgActivity.getResources().getString(R.string.close_icon));
            if (lblSend != null)
                lblSend.setText(mFrgActivity.getResources().getString(R.string.navigation_drawer_close));
        }

        if (isPermissionCamera) {

            if (fotoapparatSwitcher != null) {
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isCameraStart) {
                            fotoapparatSwitcher.start();
                            isCameraStart = true;
                        }
                    }
                }, 50);
            }
        }


        if (HelperPermission.grantedUseStorage()) {
            rcvBottomSheet.setVisibility(View.VISIBLE);
        } else {
            rcvBottomSheet.setVisibility(View.GONE);
        }

        mPopup.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);

        //animate views after popup showed -> delay set base on xml animation
        G.handler.postDelayed(() -> animateViews(viewRoot), 250);
    }

    private void loadImageGallery() {

        G.handler.post(() -> {
            for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                addItemToRecycler(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
            }
        });

        G.handler.postDelayed(() -> showPopup(), 100);

    }

    /**
     * get images for show in bottom sheet
     */
    public ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {
        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        };

        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                StructBottomSheet item = new StructBottomSheet();
                item.setId(listOfAllImages.size());
                item.setPath(absolutePathOfImage);
                item.isSelected = true;
                listOfAllImages.add(0, item);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    private void animateViews(View view) {

        View lytButtons = view.findViewById(R.id.lyt_buttons);
        animateViewWithCircularReveal(lytButtons);

    }

    private int getKeyboardHeight() {
        try {
            final InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            final Class inputMethodManagerClass = imm.getClass();
            final Method visibleHeightMethod = inputMethodManagerClass.getDeclaredMethod("getInputMethodWindowVisibleHeight");
            visibleHeightMethod.setAccessible(true);
            return (int) visibleHeightMethod.invoke(imm);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private void animateViewWithCircularReveal(View myView) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = myView.getMeasuredWidth() / 2;
            int cy = myView.getMeasuredHeight() / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

            anim.setDuration(500);

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            anim.start();

        } else {
            myView.setVisibility(View.VISIBLE);
        }

    }

    public interface ChatPopupListener {

        void onAttachPopupImageSelected();

        void onAttachPopupShowed();

        void onAttachPopupDismiss();

        void onAttachPopupLocation(String message);

        void onAttachPopupFilePicked(ArrayList<String> selectedPathList);

        void onAttachPopupSendSelected();
    }

    public enum ChatPopupAction {
        PHOTO, VIDEO, CAMERA, MUSIC, FILE, CONTACT, LOCATION, CLOSE
    }
}
