package net.iGap.mobileBank.viewmoedel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.Config;
import net.iGap.R;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankCardAuth;
import net.iGap.mobileBank.repository.model.BankDateModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.util.RSACipher;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MobileBankCardBalanceViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<String> complete = new MutableLiveData<>();

    private ObservableField<String> cardNumber = new ObservableField<>("6221061052382498");
    private ObservableField<Integer> cardNumberError = new ObservableField<>(0);
    private ObservableField<String> password = new ObservableField<>("65546545");
    private ObservableField<Integer> passwordError = new ObservableField<>(0);
    private ObservableField<String> CVV = new ObservableField<>("123");
    private ObservableField<Integer> CVVError = new ObservableField<>(0);
    private ObservableField<String> date = new ObservableField<>("9909");
    private ObservableField<Integer> dateError = new ObservableField<>(0);
    private ObservableField<Integer> progress = new ObservableField<>(R.string.inquiry);

    public MobileBankCardBalanceViewModel() {

    }

    private boolean checkData() {
        // check cardNumber
        if (cardNumber.get() != null) {
            if (!cardNumber.get().isEmpty()) {
                if (cardNumber.get().length() < 19) {
                    cardNumberError.set(R.string.mobile_bank_balance_cardLengthError);
                    return false;
                }
            } else {
                cardNumberError.set(R.string.mobile_bank_balance_cardError);
                return false;
            }
        } else {
            cardNumberError.set(R.string.mobile_bank_balance_cardError);
            return false;
        }
        // check password
        if (password.get() != null) {
            if (!password.get().isEmpty()) {
                if (password.get().length() < 6) {
                    passwordError.set(R.string.mobile_bank_balance_passwordLengthError);
                    return false;
                }
            } else {
                passwordError.set(R.string.mobile_bank_balance_passwordError);
                return false;
            }
        } else {
            passwordError.set(R.string.mobile_bank_balance_passwordError);
            return false;
        }
        // check CVV
        if (CVV.get() != null) {
            if (!CVV.get().isEmpty()) {
                if (CVV.get().length() < 3) {
                    CVVError.set(R.string.mobile_bank_balance_CVVLengthError);
                    return false;
                }
            } else {
                CVVError.set(R.string.mobile_bank_balance_CVVError);
                return false;
            }
        } else {
            CVVError.set(R.string.mobile_bank_balance_CVVError);
            return false;
        }
        // check date
        if (date.get() != null) {
            if (!date.get().isEmpty()) {
                if (date.get().length() != 5) {
                    dateError.set(R.string.mobile_bank_balance_dateError);
                    return false;
                } else
                    return true;
            } else {
                dateError.set(R.string.mobile_bank_balance_dateError);
                return false;
            }
        } else {
            dateError.set(R.string.mobile_bank_balance_dateError);
            return false;
        }
    }

    private void sendData() {
        progress.set(R.string.news_add_comment_load);
        String tempAuth = null;
        BankCardAuth auth = new BankCardAuth(CVV.get(), date.get().replace("/", ""), password.get(), "EPAY", "");
        try {
            RSACipher cipher = new RSACipher();
            tempAuth = cipher.encrypt(new Gson().toJson(auth), RSACipher.stringToPublicKey(Config.PUBLIC_PARSIAN_KEY_CLIENT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            showRequestErrorMessage.setValue("Bad Encryption");
            return;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            showRequestErrorMessage.setValue("Bad Encryption");
            return;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            showRequestErrorMessage.setValue("Bad Encryption");
            return;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            showRequestErrorMessage.setValue("Bad Encryption");
            return;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            showRequestErrorMessage.setValue("Bad Encryption");
            return;
        }
        // TODO: 1/22/2020 return model is not available must be changed and show the result;
        MobileBankRepository.getInstance().getCardBalance(cardNumber.get().replace("-", ""), tempAuth, null, this, new ResponseCallback<BaseMobileBankResponse<BankDateModel>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankDateModel> data) {
                DecimalFormat df = new DecimalFormat(",###");
                complete.setValue(compatibleUnicode(df.format(Double.parseDouble("20000"))));
                progress.set(R.string.inquiry);
            }

            @Override
            public void onError(String error) {
                progress.set(R.string.inquiry);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                progress.set(R.string.inquiry);
            }
        });
    }

    private String compatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    public void onContinueBtnClick() {
        if (!checkData())
            return;
        sendData();
    }

    public ObservableField<Integer> getProgress() {
        return progress;
    }

    public void setProgress(ObservableField<Integer> progress) {
        this.progress = progress;
    }

    public String getCardNumber() {
        return cardNumber.get();
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber.set(cardNumber);
    }

    public ObservableField<Integer> getCardNumberError() {
        return cardNumberError;
    }

    public void setCardNumberError(ObservableField<Integer> cardNumberError) {
        this.cardNumberError = cardNumberError;
    }

    public ObservableField<String> getPassword() {
        return password;
    }

    public void setPassword(ObservableField<String> password) {
        this.password = password;
    }

    public ObservableField<Integer> getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(ObservableField<Integer> passwordError) {
        this.passwordError = passwordError;
    }

    public ObservableField<String> getCVV() {
        return CVV;
    }

    public void setCVV(ObservableField<String> CVV) {
        this.CVV = CVV;
    }

    public ObservableField<Integer> getCVVError() {
        return CVVError;
    }

    public void setCVVError(ObservableField<Integer> CVVError) {
        this.CVVError = CVVError;
    }

    public MutableLiveData<String> getComplete() {
        return complete;
    }

    public void setComplete(MutableLiveData<String> complete) {
        this.complete = complete;
    }

    public ObservableField<String> getDate() {
        return date;
    }

    public ObservableField<Integer> getDateError() {
        return dateError;
    }
}
