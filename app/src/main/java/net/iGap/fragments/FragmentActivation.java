package net.iGap.fragments;

import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentActivationBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.module.SmsRetriver.SMSReceiver;
import net.iGap.viewmodel.FragmentActivationViewModel;

public class FragmentActivation extends BaseFragment {

    private final static String TAG = FragmentActivation.class.getName();

    private FragmentActivationBinding binding;
    private FragmentActivationViewModel viewModel;
    private SMSReceiver smsReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activation, container, false);
        viewModel = new FragmentActivationViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        if (getArguments() != null) {
            viewModel.setUserData(
                    getArguments().getString("userName"),
                    getArguments().getLong("userId"),
                    getArguments().getString("authorHash"),
                    getArguments().getString("phoneNumber")
            );
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startSMSListener();

        binding.timerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                binding.timerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                Log.wtf("activation", "width: " + binding.timerView.getWidth());
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.timerPosition.getId(), binding.timerView.getId(), binding.timerView.getWidth() / 2, 0);
                set.applyTo(binding.root);
            }
        });

        viewModel.verifyCode.observe(this, s -> {
            if (s != null) {
                setActivationCode(s);
                binding.loginButton.performClick();
            }
        });
        viewModel.showEnteredCodeError.observe(this, aBoolean -> {
            if (aBoolean != null && getContext() != null) {
                new DefaultRoundDialog(getContext()).setTitle(R.string.Enter_Code).setMessage(R.string.Toast_Enter_Code).setPositiveButton(R.string.B_ok, null).show();
            }
        });
        viewModel.currentTimePosition.observe(this, integer -> {
            if (integer != null) {
                ConstraintSet set1 = new ConstraintSet();
                set1.clone(binding.root);
                set1.constrainCircle(binding.timerPosition.getId(), binding.timerView.getId(), binding.timerView.getWidth() / 2, -integer);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.root);
                    set1.applyTo(binding.root);
                } else {
                    set1.applyTo(binding.root);
                }
            }
        });
        viewModel.isNewUser.observe(this, aBoolean -> {
            WelcomeFragment fragment = new WelcomeFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("newUser", aBoolean);
            bundle.putLong("userId", viewModel.userId);
            fragment.setArguments(bundle);
            G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
            G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentActivation.this).commitAllowingStateLoss();
        });

        initialActivationCodeEditor();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver();
        viewModel.cancelTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            G.fragmentActivity.registerReceiver(smsReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(new SMSReceiver.OTPReceiveListener() {
                @Override
                public void onOTPReceived(String message) {

                    try {
                        if (message != null && message.length() > 0) {
                            viewModel.receiveVerifySms(message);
                        }
                    } catch (Exception e1) {
                        e1.getStackTrace();
                    }

                    unregisterReceiver();
                }

                @Override
                public void onOTPTimeOut() {
                    Log.e(TAG, "OTP Time out");
                }

                @Override
                public void onOTPReceivedError(String error) {
                    Log.e(TAG, error);
                }
            });

            SmsRetrieverClient client = SmsRetriever.getClient(getActivity());

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e(TAG, "sms API successfully started   ");
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "sms Fail to start API   ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterReceiver() {
        try {
            if (smsReceiver != null) {
                G.fragmentActivity.unregisterReceiver(smsReceiver);
                smsReceiver = null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void setActivationCode(String code) {
        if (code.length() == 5) {
            binding.activationCodeEditText1.setText(code.charAt(0));
            binding.activationCodeEditText2.setText(code.charAt(1));
            binding.activationCodeEditText3.setText(code.charAt(2));
            binding.activationCodeEditText4.setText(code.charAt(3));
            binding.activationCodeEditText5.setText(code.charAt(4));
        }
    }

    private void initialActivationCodeEditor() {
        binding.activationCodeEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText2.requestFocus();
                }
            }
        });

        binding.activationCodeEditText2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText1.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText3.requestFocus();
                } else {
                    binding.activationCodeEditText1.requestFocus();
                }
            }
        });

        binding.activationCodeEditText3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText2.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText4.requestFocus();
                } else {
                    binding.activationCodeEditText2.requestFocus();
                }
            }
        });

        binding.activationCodeEditText4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText3.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.activationCodeEditText5.requestFocus();
                } else {
                    binding.activationCodeEditText3.requestFocus();
                }
            }
        });
        binding.activationCodeEditText5.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.activationCodeEditText4.requestFocus();
                    return true;
                }
            }
            return false;
        });
        binding.activationCodeEditText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    String message = binding.activationCodeEditText1.getEditableText().toString() +
                            binding.activationCodeEditText2.getEditableText().toString() +
                            binding.activationCodeEditText3.getEditableText().toString() +
                            binding.activationCodeEditText4.getEditableText().toString() +
                            binding.activationCodeEditText5.getEditableText().toString();
                    Log.wtf("main", "value: " + message);
                    viewModel.receiveVerifySms(message);
                } else {
                    binding.activationCodeEditText4.requestFocus();
                }
            }
        });
    }
}