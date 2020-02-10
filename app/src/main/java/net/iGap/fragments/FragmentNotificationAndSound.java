package net.iGap.fragments;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationAndSoundBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentNotificationAndSoundViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {
    private FragmentNotificationAndSoundBinding binding;
    private FragmentNotificationAndSoundViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentNotificationAndSoundViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE), getResources().getStringArray(R.array.sound_message));
            }
        }).get(FragmentNotificationAndSoundViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_and_sound, container, false);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setFragmentNotificationAndSoundViewModel(viewModel);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setDefaultTitle(getString(R.string.notificaion_and_sound))
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        setupResetNotification();
        showLEDDialog();
        showVibrationDialog();
        showPopupNotification();

        showMessageSound();
        showGroupSound();
    }

    @Override
    public void onPause() {
        super.onPause();
        HelperNotification.getInstance().updateSettingValue();
    }

    private void setupResetNotification() {
        binding.llResetNotifications.setOnClickListener(v -> {
            if (getActivity() != null) {
                new MaterialDialog.Builder(getActivity()).title(R.string.st_title_reset).content(R.string.st_dialog_reset_all_notification).positiveText(R.string.st_dialog_reset_all_notification_yes).negativeText(R.string.st_dialog_reset_all_notification_no).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.onResetDataInSharedPreference();
                        Toast.makeText(getActivity(), R.string.st_reset_all_notification, Toast.LENGTH_SHORT).show();
                        removeFromBaseFragment(FragmentNotificationAndSound.this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentNotificationAndSound()).setReplace(false).load();
                    }
                }).show();
            }
        });
    }

    /**
     * setup LED dialog in to notification and sound setting
     **/
    private void showLEDDialog() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.ivLedMessage.getBackground();
        gradientDrawable.setColor(viewModel.ledColorMessage);
        viewModel.showMessageLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true).positiveText(R.string.set).negativeText(R.string.DISCARD).title(R.string.st_led_color)
                        .onNegative((dialog1, which) -> dialog1.dismiss()).build();
                View view = dialog.getCustomView();
                ColorPicker picker = view.findViewById(R.id.picker);
                SVBar svBar = view.findViewById(R.id.svBar);
                OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    viewModel.setMessagePickerColor(picker.getColor());
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        viewModel.messageLedColor.observe(getViewLifecycleOwner(), integer -> {
            gradientDrawable.setColor(integer);
            binding.ivLedMessage.setBackground(gradientDrawable);

        });

        GradientDrawable gradientDrawableGroup = (GradientDrawable) binding.ivLedGroup.getBackground();
        gradientDrawableGroup.setColor(viewModel.ledColorGroup);
        viewModel.showGroupLedDialog.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_colorpicker, true).positiveText(R.string.set).negativeText(R.string.DISCARD).title(R.string.st_led_color)
                        .onNegative((dialog1, which) -> dialog1.dismiss()).build();
                View view = dialog.getCustomView();
                ColorPicker picker = view.findViewById(R.id.picker);
                SVBar svBar = view.findViewById(R.id.svBar);
                OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                picker.setOldCenterColor(picker.getColor());
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                    viewModel.setGroupPickerColor(picker.getColor());
                    dialog.dismiss();
                });
                dialog.show();
            }
        });
        viewModel.groupLedColor.observe(getViewLifecycleOwner(), integer -> {
            gradientDrawableGroup.setColor(integer);
            binding.ivLedGroup.setBackground(gradientDrawableGroup);
        });
    }

    /**
     * setup VIbration dialog in to notification and sound setting
     **/
    private void showVibrationDialog() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
        viewModel.showMessageVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(getContext()).title(R.string.st_vibrate).items(R.array.vibrate).negativeText(R.string.B_cancel).itemsCallback((dialog, view, which, text) -> {
                viewModel.setMessageVibrateTime(which);
            }).show();
        });
        viewModel.showGroupVibrationDialog.observe(getViewLifecycleOwner(), isShow -> {
            new MaterialDialog.Builder(getContext()).title(R.string.st_vibrate).items(R.array.vibrate).negativeText(R.string.B_cancel).itemsCallback((dialog, view, which, text) -> {
                viewModel.setGroupVibrateTime(which);
            }).show();
        });
        viewModel.startVibration.observe(getViewLifecycleOwner(), vibrationTime -> {
            if (getContext() != null && vibrationTime != null) {
                if (vibrationTime == -1) {
                    switch (audioManager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                            break;
                    }
                } else {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(vibrationTime);
                }
            }
        });
    }

    /**
     * setup PopupNotification in to notification and sound setting
     **/
    private void showPopupNotification() {
        viewModel.showMessagePopupNotification.observe(getViewLifecycleOwner(), list -> {
            int pop = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 1);
            new MaterialDialog.Builder(getContext()).title(R.string.st_popupNotification)
                    .items(R.array.popup_Notification).negativeText(R.string.B_cancel).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(pop, (dialog, itemView, which, text) -> {
                viewModel.saveMessagePopUpNotification(which);
                return false;
            }).show();
        });
        viewModel.showGroupPopupNotification.observe(getViewLifecycleOwner(), isShow -> {
            int po = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
            new MaterialDialog.Builder(getContext()).title(R.string.st_popupNotification)
                    .items(R.array.popup_Notification).negativeText(R.string.B_cancel).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, (dialog, itemView, which, text) -> {
                viewModel.setGroupPopUpNotification(which);
                return false;
            }).show();
        });

    }

    /**
     * setup Message Sound in to notification and sound setting
     **/
    private void showMessageSound() {
        viewModel.showMessageSound.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null & isShow) {
                int messageDialogSoundMessage = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                new MaterialDialog.Builder(getContext()).title(R.string.Ringtone).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(messageDialogSoundMessage, (dialog, view, which, text) -> {
                            viewModel.getSoundMessagePosition(which);
                            return true;
                        }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                        .onPositive((dialog, which) -> {
                            viewModel.setChooseSound();
                        }).show();
            }
        });

        viewModel.playSound.observe(getViewLifecycleOwner(), soundRes -> {
            if (getContext() != null && soundRes != null) {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundRes);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release());
            }
        });

    }

    /**
     * setup Group Sound in to notification and sound setting
     **/
    private void showGroupSound() {
        viewModel.showGroupSound.observe(getViewLifecycleOwner(), isShow -> {
            int getGroupSoundSelected = viewModel.getSharedPreferences().getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
            new MaterialDialog.Builder(getContext()).title(R.string.Ringtone).titleGravity(GravityEnum.START).items(R.array.sound_message).alwaysCallSingleChoiceCallback()
                    .itemsCallbackSingleChoice(getGroupSoundSelected, (dialog, view, which, text) -> {
                        viewModel.getSoundGroupPosition(which);
                        return true;
                    }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                    .onPositive((dialog, which) -> {
                        viewModel.chooseSound();
                    }).show();
        });
        viewModel.playSound.observe(getViewLifecycleOwner(), musicId -> {
            if (getContext() != null && musicId != null) {
                MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), musicId);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                });
            }
        });
    }

}
