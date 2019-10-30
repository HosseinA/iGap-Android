package net.iGap.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentUserProfileBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.StatusBarUtil;
import net.iGap.viewmodel.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class FragmentUserProfile extends BaseMainFragments implements FragmentEditImage.OnImageEdited {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserProfileViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE), avatarHandler);
            }
        }).get(UserProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        viewModel.init();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }
        viewModel.setCurrentFragment.observe(getViewLifecycleOwner(),isEdit->{
            if (isEdit) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentEditProfile.class.getName());
                if (fragment == null){
                    fragment = FragmentEditProfile.newInstance();
                    fragmentTransaction.addToBackStack(FragmentEditProfile.class.getName());
                }
                fragmentTransaction.replace(R.id.frame_edit, fragment, FragmentEditProfile.class.getName()).commit();
                binding.addAvatar.setVisibility(View.VISIBLE);
            } else {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentProfile.class.getName());
                if (fragment == null){
                    fragment = FragmentProfile.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.frame_edit, fragment, fragment.getClass().getName()).commit();
                binding.addAvatar.setVisibility(View.GONE);
            }
        });
        viewModel.changeUserProfileWallpaper.observe(getViewLifecycleOwner(), drawable -> {
            if (drawable != null) {
                binding.fupBgAvatar.setImageDrawable(drawable);
            } else {
                binding.fupBgAvatar.setImageResource(R.drawable.test_bg);
            }
        });

        viewModel.goToShowAvatarPage.observe(getViewLifecycleOwner(), userId -> {
            if (getActivity() != null && userId != null) {
                FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.setUserAvatar.observe(getViewLifecycleOwner(), userId -> {
            if (userId != null) {
                avatarHandler.getAvatar(new ParamWithAvatarType(binding.fupUserImage, userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }
        });

        viewModel.deleteAvatar.observe(getViewLifecycleOwner(), deleteAvatarModel -> {
            if (deleteAvatarModel != null) {
                avatarHandler.avatarDelete(new ParamWithAvatarType(binding.fupUserImage, deleteAvatarModel.getUserId())
                        .avatarType(AvatarHandler.AvatarType.USER), deleteAvatarModel.getAvatarId());
            }
        });

        viewModel.setUserAvatarPath.observe(getViewLifecycleOwner(), changeImageModel -> {
            if (changeImageModel != null) {
                if (changeImageModel.getImagePath() == null || !new File(changeImageModel.getImagePath()).exists()) {
                    binding.fupUserImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) binding.fupUserImage.getContext().getResources().getDimension(R.dimen.dp100), changeImageModel.getInitials(), changeImageModel.getColor()));
                } else {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(changeImageModel.getImagePath()), binding.fupUserImage);
                }
            }
        });

        viewModel.goToChatPage.observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new GoToChatActivity(data.getRoomId()).setPeerID(data.getPeerId()).startActivity(getActivity());
            }
        });

        viewModel.showDialogChooseImage.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialog();
            }
        });

        viewModel.showError.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateTwoPaneView().observe(getViewLifecycleOwner(), isUpdate -> {
            if (getActivity() != null && isUpdate != null && isUpdate) {
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            }
        });

        viewModel.getShowDialogSelectCountry().observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                showCountryDialog();
            }
        });



        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.wtf(this.getClass().getName(), "-------------------------------------------");
                for (int i = 0; i < getChildFragmentManager().getBackStackEntryCount(); i++) {
                    Log.wtf(this.getClass().getName(), "fragment: " + getChildFragmentManager().getBackStackEntryAt(i).getName());
                }
                Log.wtf(this.getClass().getName(), "-------------------------------------------");
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
        if (FragmentEditImage.itemGalleryList != null) FragmentEditImage.itemGalleryList.clear();


        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image
                FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
            } else {
                ImageHelper.correctRotateImage(viewModel.pathSaveImage, true); //rotate image
                FragmentEditImage.insertItemList(viewModel.pathSaveImage, false);
            }
            if (getActivity() != null) {
                FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                fragmentEditImage.setOnProfileImageEdited(this);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                if (getActivity() != null) {
                    ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);
                    FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                    FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                    fragmentEditImage.setOnProfileImageEdited(this);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                }
            }
        }
    }

    private void startDialog() {
        /*List<String> items = new ArrayList<>();
        items.add(getString(R.string.gallery));
        items.add(getString(R.string.remove));
        new SelectImageBottomSheetDialog().setData(items, 0, position -> {
            if (position == 0) {
                try {
                    HelperPermission.getStoragePermision(getContext(), new OnGetPermission() {
                        @Override
                        public void Allow() {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                        }

                        @Override
                        public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }).show(getFragmentManager(), "test");*/

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.array_From_Camera))) { // camera
                    try {
                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                dialog.dismiss();
                                useCamera();
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentUserProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() != null && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(viewModel.getImageFile()));
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCountryDialog() {
        if (getActivity() != null) {
            Dialog dialogChooseCountry = new Dialog(getActivity());
            dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogChooseCountry.setContentView(R.layout.rg_dialog);

            dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
            //
            final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);
            LinearLayout rootView = dialogChooseCountry.findViewById(R.id.country_root);
            rootView.setBackground(new Theme().tintDrawable(getResources().getDrawable(R.drawable.dialog_background), getContext(), R.attr.rootBackgroundColor));

            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            // close SearchView and show title again
            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
            AdapterDialog adapterDialog = new AdapterDialog(getContext(), viewModel.getStructCountryArrayList());
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                viewModel.setCountry(adapterDialog.getItem(position));
                dialogChooseCountry.dismiss();
            });

            ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
            softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                @Override
                public void onSoftKeyboardHide() {
                    G.handler.post(() -> {
                        if (edtSearchView.getQuery().toString().length() > 0) {
                            edtSearchView.setIconified(false);
                            edtSearchView.clearFocus();
                            txtTitle.setVisibility(View.GONE);
                        } else {
                            edtSearchView.setIconified(true);
                            txtTitle.setVisibility(View.VISIBLE);
                        }
                        adapterDialog.notifyDataSetChanged();
                    });
                }

                @Override
                public void onSoftKeyboardShow() {
                    G.handler.post(() -> txtTitle.setVisibility(View.GONE));
                }
            });

            View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (i > 0) {
                        border.setVisibility(View.VISIBLE);
                    } else {
                        border.setVisibility(View.GONE);
                    }
                }
            });

            adapterDialog.notifyDataSetChanged();

            edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterDialog.getFilter().filter(s);
                    return false;
                }
            });

            dialogChooseCountry.findViewById(R.id.rg_txt_okDialog).setOnClickListener(v -> dialogChooseCountry.dismiss());
            dialogChooseCountry.show();
        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        return viewModel.checkEditModeForOnBackPressed();
    }

    @Override
    public void scrollToTopOfList() {
        //no thing -> its for scroll list if available
    }

    @Override
    public void profileImageAdd(String path) {
        viewModel.uploadAvatar(path);
    }
}
