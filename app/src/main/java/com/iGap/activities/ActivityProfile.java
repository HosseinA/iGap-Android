package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnUserAvatarResponse;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserProfileSetNickNameResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.EditTextAdjustPan;
import com.iGap.module.FileUploadStructure;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserProfileSetNickname;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.io.IOException;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;
import static com.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class ActivityProfile extends ActivityEnhanced implements OnUserAvatarResponse, OnFileUploadForActivities {

    public final static String ARG_USER_ID = "arg_user_id";
    public static boolean IsDeleteFile;
    public static Bitmap decodeBitmapProfile = null;
    private TextView txtTitle, txtTitlInformation, txtDesc, txtAddPhoto;
    private Button btnLetsGo;
    private com.iGap.module.CircleImageView btnSetImage;
    private EditTextAdjustPan edtNikName;
    private Uri uriIntent;
    private String pathImageUser;
    private File pathImageFromCamera = new File(G.imageFile.toString() + "_" + 0 + ".jpg");
    private int idAvatar;
    private int lastUploadedAvatarId;
    private ProgressBar prgWait;
    private String pathSaveImage;
    private boolean existAvatar = false;
    private Typeface titleTypeface;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        G.onUserAvatarResponse = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        delete();
        txtTitlInformation = (TextView) findViewById(R.id.pu_txt_title_information);

        txtDesc = (TextView) findViewById(R.id.pu_txt_title_desc);

        txtAddPhoto = (TextView) findViewById(R.id.pu_txt_addPhoto);
        prgWait = (ProgressBar) findViewById(R.id.prg);

        G.uploaderUtil.setActivityCallbacks(this);

        txtTitle = (TextView) findViewById(R.id.pu_titleToolbar);

        if (!HelperCalander.isLanguagePersian) {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        } else {
            titleTypeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }
        txtTitle.setTypeface(titleTypeface);

        final View lineEditText = findViewById(R.id.pu_line_below_editText);
        btnSetImage = (com.iGap.module.CircleImageView) findViewById(R.id.pu_profile_circle_image);
        btnSetImage.setOnClickListener(new View.OnClickListener() { // button for set image
            @Override
            public void onClick(View view) {
                if (!existAvatar) {

                    startDialog();
                }
            }
        });

        final TextInputLayout txtInputNickName = (TextInputLayout) findViewById(R.id.pu_txtInput_nikeName);
        //        txtInputNickName.setHint("Nickname");

        edtNikName = (EditTextAdjustPan) findViewById(R.id.pu_edt_nikeName); // edit Text for NikName

        btnLetsGo = (Button) findViewById(R.id.pu_btn_letsGo);

        edtNikName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtInputNickName.setErrorEnabled(true);
                txtInputNickName.setError("");
                txtInputNickName.setHintTextAppearance(R.style.remove_error_appearance);
                edtNikName.setTextColor(getResources().getColor(R.color.border_editText));
                lineEditText.setBackgroundColor(getResources().getColor(android.R.color.black));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnLetsGo.setOnClickListener(new View.OnClickListener() { // button for save data and go to next page
            @Override
            public void onClick(View view) {

                Realm realm = Realm.getDefaultInstance();
                final String nickName = edtNikName.getText().toString();

                if (!nickName.equals("")) {

                    showProgressBar();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            setNickName();
                        }
                    });
                    realm.close();
                } else {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {

                            txtInputNickName.setErrorEnabled(true);
                            txtInputNickName.setError(getResources().getString(R.string.Toast_Write_NickName));
                            txtInputNickName.setHintTextAppearance(R.style.error_appearance);
                            edtNikName.setTextColor(getResources().getColor(R.color.red));
                            lineEditText.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                    });
                }
            }
        });
    }

    private void setNickName() {

        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, ProtoResponse.Response response) {
                getUserInfo();
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        new RequestUserProfileSetNickname().userProfileNickName(edtNikName.getText().toString());
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                                realmUserInfo.getUserInfo().setDisplayName(user.getDisplayName());
                                realmUserInfo.getUserInfo().setInitials(user.getInitials());
                                realmUserInfo.getUserInfo().setColor(user.getColor());

                                final long userId = realmUserInfo.getUserId();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressBar();
                                        Intent intent = new Intent(context, ActivityMain.class);
                                        intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
                        realm.close();
                    }
                });

            }

            @Override
            public void onUserInfoTimeOut() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        new RequestUserInfo().userInfo(realmUserInfo.getUserId());
        realm.close();
    }

    public void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivityProfile.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(ActivityProfile.this).requestTakePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //======================================================================================================dialog for choose image

    public void useGallery() {
        //        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //        startActivityForResult(intent, IntentRequests.REQ_GALLERY);

        try {
            HelperPermision.getStoragePermision(context, new OnGetPermission() {
                @Override
                public void Allow() {
                    try {
                        new AttachFile(ActivityProfile.this).requestOpenGalleryForImageSingleSelect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDialog() {

        new MaterialDialog.Builder(this).title(getResources().getString(R.string.choose_picture)).negativeText(getResources().getString(R.string.B_cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
                                                                                                                                                                                                     @Override
                                                                                                                                                                                                     public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                                                                                                                                                                                                         switch (which) {
                                                                                                                                                                                                             case 0: {
                                                                                                                                                                                                                 useGallery();
                                                                                                                                                                                                                 dialog.dismiss();
                                                                                                                                                                                                                 break;

                                                                                                                                                                                                             }
                                                                                                                                                                                                             case 1: {
                                                                                                                                                                                                                 if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                                                                                                                                                                                                     try {

                                                                                                                                                                                                                         HelperPermision.getStoragePermision(ActivityProfile.this, new OnGetPermission() {
                                                                                                                                                                                                                             @Override
                                                                                                                                                                                                                             public void Allow() throws IOException {
                                                                                                                                                                                                                                 HelperPermision.getCameraPermission(ActivityProfile.this, new OnGetPermission() {
                                                                                                                                                                                                                                     @Override
                                                                                                                                                                                                                                     public void Allow() {
                                                                                                                                                                                                                                         // this dialog show 2 way for choose image : gallery and camera
                                                                                                                                                                                                                                         dialog.dismiss();
                                                                                                                                                                                                                                         useCamera();
                                                                                                                                                                                                                                     }

                                                                                                                                                                                                                                     @Override
                                                                                                                                                                                                                                     public void deny() {

                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                 });
                                                                                                                                                                                                                             }

                                                                                                                                                                                                                             @Override
                                                                                                                                                                                                                             public void deny() {

                                                                                                                                                                                                                             }
                                                                                                                                                                                                                         });
                                                                                                                                                                                                                     } catch (IOException e) {
                                                                                                                                                                                                                         e.printStackTrace();
                                                                                                                                                                                                                     }
                                                                                                                                                                                                                 } else {
                                                                                                                                                                                                                     final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.please_check_your_camera), Snackbar.LENGTH_LONG);

                                                                                                                                                                                                                     snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                                                                                                                                                                         @Override
                                                                                                                                                                                                                         public void onClick(View view) {
                                                                                                                                                                                                                             snack.dismiss();
                                                                                                                                                                                                                         }
                                                                                                                                                                                                                     });
                                                                                                                                                                                                                     snack.show();
                                                                                                                                                                                                                 }
                                                                                                                                                                                                                 break;
                                                                                                                                                                                                             }
                                                                                                                                                                                                         }
                                                                                                                                                                                                     }
                                                                                                                                                                                                 }

        ).

                show();
    }

    //======================================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "profile");
                intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
                startActivityForResult(intent, IntentRequests.REQ_CROP);

            } else {
                Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.imagePath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "profile");
                intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }

        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
                if (data.getData() == null) {
                    return;
                }
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(data.getData()));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", "profile");
                intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }

        } else if (requestCode == IntentRequests.REQ_CROP && resultCode == RESULT_OK) {
            if (data != null) {
                pathImageUser = data.getData().toString();
            }

            lastUploadedAvatarId = idAvatar + 1;

            new UploadTask().execute(pathImageUser, lastUploadedAvatarId);
        }
    }

    private void setImage(String path) {
        if (path != null) {

            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(path), btnSetImage);
            btnSetImage.setPadding(0, 0, 0, 0);
            //Bitmap bitmap = BitmapFactory.decodeFile(path);
            //btnSetImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pathImageFromCamera.exists()) {
                pathImageFromCamera.delete();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void delete() {
        Realm realm = Realm.getDefaultInstance();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final RealmResults<RealmAvatar> realmAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findAll();
        if (!realmAvatars.isEmpty()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmAvatars.deleteAllFromRealm();
                }
            });
        }
        realm.close();
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        Log.i("RRR", "onAvatarAdd 3");
        HelperAvatar.avatarAdd(userId, pathSaveImage, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("RRR", "onAvatarAdd 4");
                        existAvatar = true;
                        hideProgressBar();
                        setImage(avatarPath);
                    }
                });

            }
        });
    }

    @Override
    public void onAvatarAddTimeOut() {
        hideProgressBar();
    }

    @Override
    public void onAvatarError() {
        hideProgressBar();
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                int avatarId = (int) params[1];
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
            if (result != null) {
                G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
            }
        }

    }

    @Override
    public void onUploadStarted(FileUploadStructure struct) {
        showProgressBar();
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                *//*existAvatar = true;
                token = uploadStructure.token;*//*
                hideProgressBar();
                pathSaveImage = uploadStructure.filePath;
                setImage(pathSaveImage);
            }
        });*/

        pathSaveImage = uploadStructure.filePath;

        new RequestUserAvatarAdd().userAddAvatar(uploadStructure.token);
    }


    @Override
    public void onBadDownload(String token) {
        hideProgressBar();
    }

    @Override
    public void onFileTimeOut(String identity) {
        hideProgressBar();
    }

    //***Show And Hide Progress

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            ActivityCompat.finishAffinity(this);
        }

    }
}
