package net.iGap.mobileBank.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankTransferCtcStepOneBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.view.adapter.MobileBankSpinnerAdapter;
import net.iGap.mobileBank.viewmoedel.MobileBankTransferCTCStepOneViewModel;

import java.util.Locale;

public class MobileBankTransferCTCStepOneFragment extends BaseAPIViewFrag<MobileBankTransferCTCStepOneViewModel> {

    private MobileBankTransferCtcStepOneBinding binding;

    private static final String TAG = "MobileBankTransferCTCSt";

    public static MobileBankTransferCTCStepOneFragment newInstance() {
        return new MobileBankTransferCTCStepOneFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankTransferCTCStepOneViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_transfer_ctc_step_one, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.originCard.cardNumberField1.requestFocus();

        initial();
    }

    private void initial() {
        // activate origin card input suggestion
        binding.originSpinner.setThreshold(1);
        binding.originSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String temp = ((BankCardModel) parent.getItemAtPosition(position)).getPan();
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(temp), String.class);
            binding.originCard.cardNumberField1.setText("");
            binding.originCard.cardNumberField2.setText("");
            binding.originCard.cardNumberField3.setText("");
            binding.originCard.cardNumberField4.setText("");
            binding.originCard.cardNumberField1.append(tempArray[0]);
            binding.originCard.cardNumberField2.append(tempArray[1]);
            binding.originCard.cardNumberField3.append(tempArray[2]);
            binding.originCard.cardNumberField4.append(tempArray[3]);
        });
        // activate destination card input suggestion
        binding.destSpinner.setThreshold(1);
        binding.destSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String temp = ((BankCardModel) parent.getItemAtPosition(position)).getPan();
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(temp), String.class);
            binding.destCard.cardNumberField1.setText("");
            binding.destCard.cardNumberField2.setText("");
            binding.destCard.cardNumberField3.setText("");
            binding.destCard.cardNumberField4.setText("");
            binding.destCard.cardNumberField1.append(tempArray[0]);
            binding.destCard.cardNumberField2.append(tempArray[1]);
            binding.destCard.cardNumberField3.append(tempArray[2]);
            binding.destCard.cardNumberField4.append(tempArray[3]);
        });

        binding.suggestionSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            String temp = ((BankCardModel) parent.getItemAtPosition(position)).getPan();
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(temp), String.class);
            binding.destCard.cardNumberField1.setText("");
            binding.destCard.cardNumberField2.setText("");
            binding.destCard.cardNumberField3.setText("");
            binding.destCard.cardNumberField4.setText("");
            binding.destCard.cardNumberField1.append(tempArray[0]);
            binding.destCard.cardNumberField2.append(tempArray[1]);
            binding.destCard.cardNumberField3.append(tempArray[2]);
            binding.destCard.cardNumberField4.append(tempArray[3]);
        });
        /*if (outputCardNum!=null) {
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(outputCardNum), String.class);
            binding.destCard.cardNumberField1.setText("");
            binding.destCard.cardNumberField2.setText("");
            binding.destCard.cardNumberField3.setText("");
            binding.destCard.cardNumberField4.setText("");
            binding.destCard.cardNumberField1.append(tempArray[0]);
            binding.destCard.cardNumberField2.append(tempArray[1]);
            binding.destCard.cardNumberField3.append(tempArray[2]);
            binding.destCard.cardNumberField4.append(tempArray[3]);
            binding.destSpinner.dismissDropDown();
        }*/
        // set text change listener for every input and managing it

        binding.nextBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new MobileBankTransferCtcStep3Fragment())
                        .setReplace(false)
                        .load();
            }
        });

        textInputManagerOrigin();
        textInputManagerDest();
        textInputManagerValue();
        onDateChanged();
        // check for clipboard instance
        getClipboardData();
        // start actions for getting data from db
        viewModel.getOriginCardsDB();
        viewModel.getDestCardsDB();
    }

    private void onClipboardDataChangeListener() {
        ClipboardManager clipBoard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(() -> {
            ClipData clipData = clipBoard.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            Log.d(TAG, "getClipboardData: " + text);
            // Access your context here using YourActivityName.this
        });
    }

    private void getClipboardData() {
        ClipboardManager clipBoard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipBoard.getPrimaryClip();
        if (clipData == null)
            return;
        ClipData.Item item = clipData.getItemAt(0);
        String input = item.getText().toString();
        viewModel.extractCardNum(input);
    }

    private void onDateChanged() {
        viewModel.getOriginCards().observe(getViewLifecycleOwner(), bankCardModels -> {
            MobileBankSpinnerAdapter adapter = new MobileBankSpinnerAdapter(getContext(), R.layout.mobile_bank_preview_spinner_item, bankCardModels);
            binding.originSpinner.setAdapter(adapter);
        });
        viewModel.getDestCards().observe(getViewLifecycleOwner(), bankCardModels -> {
            MobileBankSpinnerAdapter adapter = new MobileBankSpinnerAdapter(getContext(), R.layout.mobile_bank_preview_spinner_item, bankCardModels);
            binding.destSpinner.setAdapter(adapter);
        });
        viewModel.getSuggestCards().observe(getViewLifecycleOwner(), bankCardModels -> {
            MobileBankSpinnerAdapter adapter = new MobileBankSpinnerAdapter(getContext(), R.layout.mobile_bank_preview_spinner_item, bankCardModels);
            binding.suggestionSpinner.setAdapter(adapter);
            binding.suggestionSpinner.showDropDown();
        });
        viewModel.getOriginBankLogo().observe(getViewLifecycleOwner(), res -> binding.originBankLogo.setImageResource(res));
        viewModel.getDestBankLogo().observe(getViewLifecycleOwner(), res -> binding.destBankLogo.setImageResource(res));
    }

    private void textInputManagerValue() {
        binding.amount.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;
            String mPrice;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice = s.toString().replaceAll(",", "");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSettingText) return;

                isSettingText = true;
                String s1 = null;

                try {
                    s1 = String.format(Locale.US, "%,d", Long.parseLong(mPrice));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                binding.amount.setText(s1);
                binding.amount.setSelection(binding.amount.length());

                isSettingText = false;
                // ?????????
                /*String amount = s.toString().replace(",","");
                DecimalFormat df = new DecimalFormat(",###");
                binding.amount.setText(df.format(Integer.parseInt(amount)));*/
                viewModel.setAmount(s.toString().replace(",", ""));
            }
        });
    }

    private void textInputManagerOrigin() {
        // Pin 1
        binding.originCard.cardNumberField1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.originCard.cardNumberField2.setEnabled(true);
                    binding.originCard.cardNumberField2.requestFocus();
                }
                viewModel.setOriginCard(s.toString());
                binding.originSpinner.showDropDown();
            }
        });

        // Pin 2
        binding.originCard.cardNumberField2.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField1.requestFocus();
                    binding.originCard.cardNumberField2.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNum = binding.originCard.cardNumberField1.getText().toString()
                        + binding.originCard.cardNumberField2.getText().toString();
                viewModel.setOriginCard(cardNum);
                binding.originSpinner.showDropDown();
                /*if (s.length() > 1) {
                    String cardNum = binding.originCard.cardNumberField1.getText().toString()
                            + binding.originCard.cardNumberField2.getText().toString();
                    viewModel.setOriginCard(cardNum);
                } else {
                    viewModel.setOriginCard(null);
                }*/
                if (s.length() == 4) {
                    binding.originCard.cardNumberField3.setEnabled(true);
                    binding.originCard.cardNumberField3.requestFocus();
                }
            }
        });

        // Pin 3
        binding.originCard.cardNumberField3.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField2.requestFocus();
                    binding.originCard.cardNumberField3.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.originCard.cardNumberField4.setEnabled(true);
                    binding.originCard.cardNumberField4.requestFocus();
                }
                String cardNum = binding.originCard.cardNumberField1.getText().toString()
                        + binding.originCard.cardNumberField2.getText().toString()
                        + binding.originCard.cardNumberField3.getText().toString();
                viewModel.setOriginCard(cardNum);
                binding.originSpinner.showDropDown();
            }
        });

        // Pin 4
        binding.originCard.cardNumberField4.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                String cardNum = binding.originCard.cardNumberField1.getText().toString()
                        + binding.originCard.cardNumberField2.getText().toString();
                viewModel.setCompleteOrigin(false, cardNum);
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.originCard.cardNumberField3.requestFocus();
                    binding.originCard.cardNumberField4.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.originCard.cardNumberField4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNum = binding.originCard.cardNumberField1.getText().toString()
                        + binding.originCard.cardNumberField2.getText().toString()
                        + binding.originCard.cardNumberField3.getText().toString()
                        + binding.originCard.cardNumberField4.getText().toString();
                if (s.length() == 4) {
                    // build the Pin Code
                    viewModel.setCompleteOrigin(true, cardNum);
                }
                viewModel.setOriginCard(cardNum);
                binding.originSpinner.showDropDown();
            }
        });
    }

    private void textInputManagerDest() {
        // Pin 1
        binding.destCard.cardNumberField1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.destCard.cardNumberField2.setEnabled(true);
                    binding.destCard.cardNumberField2.requestFocus();
                }
                viewModel.setDestCard(s.toString());
                binding.destSpinner.showDropDown();
            }
        });

        // Pin 2
        binding.destCard.cardNumberField2.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField1.requestFocus();
                    binding.destCard.cardNumberField2.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNum = binding.destCard.cardNumberField1.getText().toString()
                        + binding.destCard.cardNumberField2.getText().toString();
                viewModel.setDestCard(cardNum);
                binding.destSpinner.showDropDown();
                if (s.length() == 4) {
                    binding.destCard.cardNumberField3.setEnabled(true);
                    binding.destCard.cardNumberField3.requestFocus();
                }
            }
        });

        // Pin 3
        binding.destCard.cardNumberField3.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField2.requestFocus();
                    binding.destCard.cardNumberField3.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    binding.destCard.cardNumberField4.setEnabled(true);
                    binding.destCard.cardNumberField4.requestFocus();
                }
                String cardNum = binding.destCard.cardNumberField1.getText().toString()
                        + binding.destCard.cardNumberField2.getText().toString()
                        + binding.destCard.cardNumberField3.getText().toString();
                viewModel.setDestCard(cardNum);
                binding.destSpinner.showDropDown();
            }
        });

        // Pin 4
        binding.destCard.cardNumberField4.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                String cardNum = binding.destCard.cardNumberField1.getText().toString()
                        + binding.destCard.cardNumberField2.getText().toString();
                viewModel.setCompleteDest(false, cardNum);
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.destCard.cardNumberField3.requestFocus();
                    binding.destCard.cardNumberField4.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.destCard.cardNumberField4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String cardNum = binding.destCard.cardNumberField1.getText().toString()
                        + binding.destCard.cardNumberField2.getText().toString()
                        + binding.destCard.cardNumberField3.getText().toString()
                        + binding.destCard.cardNumberField4.getText().toString();
                viewModel.setDestCard(cardNum);
                binding.destSpinner.showDropDown();
                if (s.length() == 4) {
                    // build the Pin Code
                    viewModel.setCompleteDest(true, cardNum);
                    binding.destSpinner.dismissDropDown();
                }
            }
        });
    }
}
