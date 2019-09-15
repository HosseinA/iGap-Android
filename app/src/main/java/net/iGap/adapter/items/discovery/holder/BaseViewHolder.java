package net.iGap.adapter.items.discovery.holder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.fragments.FragmentIVandActivities;
import net.iGap.fragments.FragmentPayment;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.fragments.FragmentPaymentCharge;
import net.iGap.fragments.FragmentPaymentInquiryTelephone;
import net.iGap.fragments.FragmentUserScore;
import net.iGap.fragments.FragmentWalletAgrement;
import net.iGap.fragments.FragmentWebView;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.discovery.DiscoveryFragmentAgreement;
import net.iGap.fragments.emoji.add.FragmentSettingAddStickers;
import net.iGap.fragments.inquiryBill.FragmentPaymentInquiryMobile;
import net.iGap.fragments.mplTranaction.MplTransactionFragment;
import net.iGap.fragments.poll.PollFragment;
import net.iGap.fragments.populaChannel.PopularChannelHomeFragment;
import net.iGap.fragments.populaChannel.PopularMoreChannelFragment;
import net.iGap.helper.CardToCardHelper;
import net.iGap.helper.DirectPayHelper;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.internetpackage.BuyInternetPackageFragment;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.module.SHP_SETTING;
import net.iGap.payment.PaymentCallBack;
import net.iGap.payment.PaymentResult;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientSetDiscoveryItemClick;
import net.iGap.request.RequestGeoGetConfiguration;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.paygear.WalletActivity;

import java.io.IOException;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;
import static net.iGap.viewmodel.FragmentIVandProfileViewModel.scanBarCode;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private long mLastClickTime = 0;
    private FragmentActivity activity;

    BaseViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView);
        this.activity = activity;
    }

    public static void handleDiscoveryFieldsClickStatic(DiscoveryItemField discoveryField, FragmentActivity activity, boolean haveNext) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (discoveryField.agreementSlug != null && discoveryField.agreementSlug.length() > 1) {
            if (!discoveryField.agreement) {
                new HelperFragment(activity.getSupportFragmentManager(), DiscoveryFragmentAgreement.newInstance(discoveryField, discoveryField.agreementSlug)).setReplace(false).load();
                return;
            }
        }

        new RequestClientSetDiscoveryItemClick().setDiscoveryClicked(discoveryField.id);

        switch (discoveryField.actionType) {
            case PAGE:/** tested **/
                actionPage(discoveryField.value, activity, haveNext);
                break;
            case JOIN_LINK:
                int index = discoveryField.value.lastIndexOf("/");
                if (index >= 0 && index < discoveryField.value.length() - 1) {
                    String token = discoveryField.value.substring(index + 1);
                    if (discoveryField.value.toLowerCase().contains("join")) {
                        HelperUrl.checkAndJoinToRoom(activity, token);
                    } else {
                        HelperUrl.checkUsernameAndGoToRoom(activity, token, HelperUrl.ChatEntry.profile);
                    }
                }
                break;
            case WEB_LINK:/** tested **/
                SharedPreferences sharedPreferences = activity.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
                    HelperUrl.openBrowser(discoveryField.value);
                } else {
                    HelperUrl.openWithoutBrowser(discoveryField.value);
                }
                break;
            case IVAND:
                new HelperFragment(activity.getSupportFragmentManager(), new FragmentUserScore()).setReplace(false).load();
                break;
            case IVANDQR:
                scanBarCode(activity);
                break;
            case IVANDLIST:
                new HelperFragment(activity.getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
                break;
            case WEB_VIEW_LINK:/** tested title needed**/
                if (HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
                    HelperUrl.openWithoutBrowser(discoveryField.value);
                } else {
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentWebView.newInstance(discoveryField.value, discoveryField.refresh, discoveryField.param)).setReplace(false).load();
                }
                break;
            case USERNAME_LINK:/** tested **/
                HelperUrl.checkUsernameAndGoToRoomWithMessageId(activity, discoveryField.value.replace("@", ""), HelperUrl.ChatEntry.chat, 0);
                break;
            case TOPUP_MENU:/** tested **/
                new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentCharge.newInstance()).setReplace(false).load();
                break;
            case BILL_MENU:/** tested **/
                try {
                    JSONObject jsonObject = new JSONObject(discoveryField.value);
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills, jsonObject)).setReplace(false).load();
                } catch (JSONException e) {
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
                }
                break;
            case TRAFFIC_BILL_MENU:/** tested **/
                try {
                    JSONObject jsonObject = new JSONObject(discoveryField.value);
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime, jsonObject)).setReplace(false).load();
                } catch (JSONException e) {
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
                }
                break;
            case PHONE_BILL_MENU:/** tested **/
                new HelperFragment(activity.getSupportFragmentManager(), new FragmentPaymentInquiryTelephone()).setReplace(false).load();
                break;
            case MOBILE_BILL_MENU:/** tested **/
                new HelperFragment(activity.getSupportFragmentManager(), new FragmentPaymentInquiryMobile()).setReplace(false).load();
                break;
            case FINANCIAL_MENU:/** tested **/
                new HelperFragment(activity.getSupportFragmentManager(), FragmentPayment.newInstance()).setReplace(false).load();
                break;
            case WALLET_MENU:/** tested **/
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                    String phoneNumber = userInfo.getUserInfo().getPhoneNumber();
                    if (!G.isWalletRegister) {
                        if (discoveryField.value.equals("QR_USER_WALLET")) {
                            new HelperFragment(activity.getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber.substring(2), true)).load();
                        } else {
                            new HelperFragment(activity.getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber.substring(2), false)).load();
                        }
                    } else {

                        Intent intent = new Intent(activity, WalletActivity.class);
                        intent.putExtra("Language", "fa");
                        intent.putExtra("Mobile", "0" + phoneNumber.substring(2));
                        intent.putExtra("PrimaryColor", G.appBarColor);
                        intent.putExtra("DarkPrimaryColor", G.appBarColor);
                        intent.putExtra("AccentColor", G.appBarColor);
                        intent.putExtra("IS_DARK_THEME", G.isDarkTheme);
                        intent.putExtra(WalletActivity.LANGUAGE, G.selectedLanguage);
                        intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                        intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                        intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                        intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme);
                        intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                        if (discoveryField.value.equals("QR_USER_WALLET")) {
                            intent.putExtra("isScan", true);
                        } else {
                            intent.putExtra("isScan", true);
                        }
                        G.currentActivity.startActivityForResult(intent, WALLET_REQUEST_CODE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case NEARBY_MENU:/** tested **/
                try {
                    HelperPermission.getLocationPermission(activity, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            try {
                                if (!waitingForConfiguration) {
                                    waitingForConfiguration = true;
                                    if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                                        G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                            @Override
                                            public void onGetConfiguration() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                                new HelperFragment(activity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).setReplace(false).load();

                                            }

                                            @Override
                                            public void getConfigurationTimeOut() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                            }
                                        };
                                        new RequestGeoGetConfiguration().getConfiguration();
                                    } else {
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingForConfiguration = false;
                                            }
                                        }, 2000);
                                        new HelperFragment(activity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).setReplace(false).load();
                                    }
                                }

                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PHONE:
                // this item is for bot
                break;
            case REQUEST_LOCATION:
                //this item is for bot
                break;
            case BOT_ACTION:
                //this item is for bot
                break;
            case PAY_BY_WALLET:
                // coming soon
                break;
            case PAY_DIRECT:
                try {
                    JSONObject jsonObject = new JSONObject(discoveryField.value);
                    DirectPayHelper.directPay(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CALL: /** tested **/
                dialPhoneNumber(activity, discoveryField.value, activity);
                break;
            case SHOW_ALERT:/** tested **/
                new MaterialDialog.Builder(activity).content(discoveryField.value).positiveText(R.string.dialog_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
                break;
            case STREAM_PLAY:
                // coming soon
                break;
            case STICKER_SHOP:/** tested **/
                new HelperFragment(activity.getSupportFragmentManager(), FragmentSettingAddStickers.newInstance()).setReplace(false).load();
                break;
            case CARD_TO_CARD:
                CardToCardHelper.CallCardToCard(activity);
                break;
            case IVANDSCORE:
                ActivityMain.doIvandScore(discoveryField.value, activity);
                break;
            case NONE:
                break;
            case POLL:
                new HelperFragment(activity.getSupportFragmentManager(), PollFragment.newInstance(Integer.valueOf(discoveryField.value))).setReplace(false).load();
                break;
            case UNRECOGNIZED:
                break;
            case FAVORITE_CHANNEL:
                if (discoveryField.value.equals(""))
                    new HelperFragment(activity.getSupportFragmentManager(), new PopularChannelHomeFragment()).setReplace(false).load();
                else {
                    PopularMoreChannelFragment popularMoreChannelFragment = new PopularMoreChannelFragment();
                    popularMoreChannelFragment.setId(discoveryField.value);
                    new HelperFragment(activity.getSupportFragmentManager(), popularMoreChannelFragment).setReplace(false).load();
                }

                break;
            case FINANCIAL_HISTORY:
                new HelperFragment(activity.getSupportFragmentManager(), new MplTransactionFragment()).setReplace(false).load();
                break;
            case INTERNET_PACKAGE_MENU:
                new HelperFragment(activity.getSupportFragmentManager(), new BuyInternetPackageFragment()).setReplace(false).load();
                break;
            case CHARITY:
                try {
                    JSONObject jsonObject = new JSONObject(discoveryField.value);
                    sendRequestGetCharityPaymentToken(activity, jsonObject.getString("charityId"), jsonObject.getInt("price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private static void sendRequestGetCharityPaymentToken(FragmentActivity activity, String charityId, int charityAmount) {
        new RetrofitFactory().getCharityRetrofit().sendRequestGetCharity(charityId, charityAmount).enqueue(new Callback<MciPurchaseResponse>() {
            @Override
            public void onResponse(@NotNull Call<MciPurchaseResponse> call, @NotNull Response<MciPurchaseResponse> response) {
                if (response.isSuccessful()) {
                    new HelperFragment(activity.getSupportFragmentManager()).loadPayment(activity.getString(R.string.charity_title), response.body().getToken(), new PaymentCallBack() {
                        @Override
                        public void onPaymentFinished(PaymentResult result) {

                        }
                    });
                } else {
                    try {
                        HelperError.showSnackMessage(new ErrorHandler().getError(response.code(), response.errorBody().string()).getMessage(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<MciPurchaseResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                HelperError.showSnackMessage(activity.getString(R.string.connection_error), false);
            }
        });
    }

    private static void actionPage(String value, FragmentActivity activity, boolean haveNext) {
        DiscoveryFragment discoveryFragment = DiscoveryFragment.newInstance(Integer.valueOf(value));
        discoveryFragment.setNeedToCrawl(haveNext);
        new HelperFragment(activity.getSupportFragmentManager(), discoveryFragment).setReplace(false).load(false);
    }

    public static void dialPhoneNumber(Context context, String phoneNumber, FragmentActivity activity) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public abstract void bindView(DiscoveryItem item);

    void loadImage(ImageView imageView, String url) {
        if (url.endsWith(".gif")) {
            Glide.with(imageView.getContext())
                    .asGif()
                    .load(url)
                    .apply(new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)).into(imageView);
        }
    }

    void handleDiscoveryFieldsClick(DiscoveryItemField discoveryField) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        handleDiscoveryFieldsClickStatic(discoveryField, activity, false);
    }

}
