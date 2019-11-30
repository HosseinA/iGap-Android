package net.iGap.dialog.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.AccountHelper;
import net.iGap.AccountManager;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.helper.avatar.AvatarHandler;

import org.paygear.RaadApp;

public class AccountsDialog extends BottomSheetDialogFragment {

    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;

    public AccountsDialog setData(AvatarHandler avatarHandler, AccountDialogListener listener) {
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        binding.bottomSheetList.setAdapter(new AccountsDialogAdapter(mAvatarHandler, (isAssigned, id) -> {
            if (isAssigned) {
                if (getActivity() instanceof ActivityMain && AccountManager.getInstance().getCurrentUser().getId() != id) {
                    new AccountHelper().changeAccount(id);
                    Log.wtf(this.getClass().getName(), "updateUiForChangeAccount");
                    ((ActivityMain) getActivity()).updateUiForChangeAccount();
                    RaadApp.onCreate(getContext());
                    Log.wtf(this.getClass().getName(), "updateUiForChangeAccount");
                }
                dismiss();
            } else {
                if (getActivity() != null) {
                    new AccountHelper().addAccount();
                    RaadApp.onCreate(getContext());
                    // WebSocketClient.connectNewAccount();
                    Intent intent = new Intent(getActivity(), ActivityRegistration.class);
                    intent.putExtra("add account", true);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        }));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}
