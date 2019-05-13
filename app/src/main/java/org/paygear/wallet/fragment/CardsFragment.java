package org.paygear.wallet.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.databinding.FragmentCardsBinding;

import org.paygear.wallet.RaadApp;
import org.paygear.wallet.RefreshLayout;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.MerchantsResult;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.model.SearchedAccount;
import org.paygear.wallet.utils.SettingHelper;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;
import org.paygear.wallet.widget.BankCardView;

import java.util.ArrayList;

import ir.radsense.raadcore.OnFragmentInteraction;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.JWT;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;

import ir.radsense.raadcore.web.WebBase;
import ir.radsense.raadcore.widget.CircleImageTransform;
import ir.radsense.raadcore.widget.ProgressLayout;
import ir.radsense.raadcore.widget.RecyclerRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardsFragment extends Fragment implements OnFragmentInteraction, RefreshLayout {

    private static final int COLLAPSE = 60;

    private RaadToolBar appBar;
    private ImageView appBarImage;
    private TextView appBarTitle;
    private RecyclerRefreshLayout mRefreshLayout;
    private ProgressLayout progress;
    LinearLayout cardsLayout;
    ScrollView scrollView;
    ArrayList<CardView> viewItems;
    private ArrayList<SearchedAccount> merchantsList = new ArrayList<>();

    private ArrayList<Card> mCards;

    private Payment mPayment;

    FragmentCardsBinding mBinding;

    public CardsFragment() {
    }

    public static CardsFragment newInstance(Payment payment) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putSerializable("Payment", payment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPayment = (Payment) getArguments().getSerializable("Payment");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (WalletActivity.refreshLayout != null)
            WalletActivity.refreshLayout = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        mBinding = FragmentCardsBinding.bind(view);
        WalletActivity.refreshLayout = this;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));


//        appBar.setToolBarColorRes();

        if (mPayment != null) {
            appBar.showBack();
            appBar.setTitle(getString(R.string.select_card));
        } else {
            setAppBar();
//            appBar.addRightButton(R.drawable.ic_action_settings, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (getActivity() instanceof NavigationBarActivity) {
//                        ((NavigationBarActivity) getActivity()).pushFullFragment(
//                                FragmentSettingWallet.newInstance(), "FragmentSettingWallet");
//                    }
//                }
//            });
        }
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCards();
            }
        });

        viewItems = new ArrayList<>();
        scrollView = view.findViewById(R.id.scroll_view);
        cardsLayout = view.findViewById(R.id.cards);
        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

        mCards = RaadApp.cards;
        if (mCards != null) {
            setAdapter();
        } else {
            load();
        }

        mBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        mBinding.settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof NavigationBarActivity) {
                    ((NavigationBarActivity) getActivity()).pushFullFragment(
                            FragmentSettingWallet.newInstance(), "FragmentSettingWallet");
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onFragmentResult(Fragment fragment, Bundle bundle) {
        if (fragment instanceof AddCardFragment ||
                fragment instanceof CardFragment) {
            load();
        }
    }


    private void setAppBar() {
        FrameLayout appBarView = appBar.getBack();

        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams titleLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleLayout.setLayoutParams(titleLayoutParams);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        appBarView.addView(titleLayout);

        appBarImage = new AppCompatImageView(context);
        int dp40 = RaadCommonUtils.getPx(40, context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dp40, dp40);
        imageParams.rightMargin = dp16;
        imageParams.leftMargin = dp16;
        appBarImage.setLayoutParams(imageParams);
        titleLayout.addView(appBarImage);

        appBarTitle = new AppCompatTextView(context);
        appBarTitle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //appBarTitle.setGravity(Gravity.CENTER);
        appBarTitle.setTextColor(Color.WHITE);
        appBarTitle.setText(getResources().getString(R.string.wallet));
        appBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        appBarTitle.setTypeface(Typefaces.get(context, Typefaces.IRAN_MEDIUM));
        titleLayout.addView(appBarTitle);

        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((NavigationBarActivity) getActivity()).pushFullFragment(
//                        new EditProfileFragment(), "EditProfileFragment");
            }
        });
    }

    private void load() {

        if (Auth.getCurrentAuth() != null && Auth.getCurrentAuth().getPublicKey() == null) {
            loadKey();
        } else {
            loadCards();
        }
    }

    private void loadKey() {

        if (mCards == null || mCards.size() == 0) {
            if (!mRefreshLayout.isRefreshing())
                progress.setStatus(0);
        } else {
            if (!mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(true);
        }

        Web.getInstance().getWebService().getPaymentKey().enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    PaymentAuth auth = response.body();
                    if (auth != null) {
                        Auth.getCurrentAuth().setPublicKey(auth.publicKey);
                        loadCards();
                        return;
                    }
                }

                if (mCards == null || mCards.size() == 0)
                    progress.setStatus(-1, getString(R.string.error));
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.network_error));
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadCards() {

        if (mCards == null || mCards.size() == 0) {
            if (!mRefreshLayout.isRefreshing())
                progress.setStatus(0);
        } else {
            if (!mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(true);
        }

        String orderId = mPayment != null ? mPayment.orderId : null;
        Web.getInstance().getWebService().getCards(orderId, false).enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mCards = RaadApp.cards = response.body();
                    if (mCards != null) {
                        int c = mCards.size();
                        int raadCardIndex = -1;
                        int defaultCardIndex = -1;
                        for (int i = 0; i < c; i++) {
                            if (mCards.get(i).isRaadCard())
                                raadCardIndex = i;
                            if (mCards.get(i).isDefault)
                                defaultCardIndex = i;
                        }

                        if (raadCardIndex > -1) {
                            /*if (mPayment != null && mPayment.orderType == Order.ORDER_TYPE_CHARGE_CREDIT) {
                                mCards.remove(raadCardIndex);
                            } else {
                                Card raadCard = mCards.get(raadCardIndex);
                                mCards.remove(raadCardIndex);
                                mCards.add(0, raadCard);
                            }*/

                            Card raadCard = mCards.get(raadCardIndex);
                            mCards.remove(raadCardIndex);
                            mCards.add(0, raadCard);
                        }

                        if (defaultCardIndex > -1) {
                            Card defaultCard = mCards.get(defaultCardIndex);
                            mCards.remove(defaultCardIndex);
                            mCards.add((raadCardIndex > -1 && c > 1) ? 1 : 0, defaultCard);
                        }
                    }

                    //collapsedItem = -1;
                    if (RaadApp.me == null)
                        loadMyAccount();
                    setAdapter();
                } else {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.error));
                }

                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.network_error));
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void GetMerchantsList(JWT jwt) {
        merchantsList.clear();

        Web.getInstance().getWebService().searchAccounts(200, 1, "admin", "finance").enqueue(new Callback<MerchantsResult>() {
            @Override
            public void onResponse(Call<MerchantsResult> call, Response<MerchantsResult> response) {

                Boolean success = response.isSuccessful();
                if (success == null)
                    return;

                if (success) {
                    ArrayList<SearchedAccount> searchedAccounts = new ArrayList<>();
                    searchedAccounts = response.body().getMerchants();
                    if (searchedAccounts != null) {
                        if (searchedAccounts.size() > 0) {
                            for (SearchedAccount item : searchedAccounts) {
                                if (item.getAccount_type() == 0) {
                                    boolean isValid = false;
                                    for (SearchedAccount.UsersBean userItem : item.getUsers()) {
                                        if (userItem.getUser_id().equals(Auth.getCurrentAuth().getId()))
                                            if (userItem.getRole().equals("finance") || userItem.getRole().equals("admin"))
                                                isValid = true;

                                    }
                                    if (isValid)
                                        merchantsList.add(item);
                                }
                            }
                        }
                    }
                    RaadApp.merchants = merchantsList;
                    loadCards();
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<MerchantsResult> call, Throwable t) {

            }
        });
    }


    private void loadMyAccount() {
        Web.getInstance().getWebService().getAccountInfo(Auth.getCurrentAuth().getId(), 1).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                WebBase.checkResponseInsideActivity((AppCompatActivity) getActivity(), call, response);
                Boolean success = response.isSuccessful();
                if (success == null)
                    return;

                if (success) {
                    RaadApp.me = response.body();
                    if(isAdded())
                    SettingHelper.putString(getActivity().getApplicationContext(), "mobile", RaadApp.me.mobile);
//                    loadCards();
                    GetMerchantsList(Auth.getCurrentAuth().getJWT());
                    //   logUser(response);

                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

                progress.setStatus(-1, getString(R.string.network_error));

            }
        });
    }

    private void setAdapter() {
        cardsLayout.removeAllViews();
        viewItems = new ArrayList<>();

        int i = 0;
        if (mCards.size() > 0 && mCards.get(0).isRaadCard()) {
            RaadApp.paygearCard = mCards.get(0);
            if (mPayment == null)
                addPayGearCard(mCards.get(0));
            addMyCardsTitle();
            i = 1;
        }

        if ((mCards.size() == 1 && mCards.get(0).isRaadCard()) || mCards.size() == 0) {
            addEmptyCard();
        }

        for (; i < mCards.size(); i++) {
            addCard(i);
        }

        if (mCards == null || mCards.size() == 0) {
            progress.setStatus(2, getString(R.string.no_item));
        } else {
            progress.setStatus(1);
        }
    }

    private void addPayGearCard(Card card) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_cards_paygear, cardsLayout, false);
        cardsLayout.addView(view);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        TextView balanceTitle = view.findViewById(R.id.balance_title);
        ImageView history = view.findViewById(R.id.history);

        TextView balance = view.findViewById(R.id.balance);
        TextView unit = view.findViewById(R.id.unit);

        ImageView imgQrCode = view.findViewById(R.id.imgQrCode);


        TextView cashableBalance = view.findViewById(R.id.chashable_balance);
        TextView cashableTitle = view.findViewById(R.id.cashable_title);
        TextView giftBalance = view.findViewById(R.id.gift_balance);
        TextView giftTitle = view.findViewById(R.id.gift_title);
        TextView cashout = view.findViewById(R.id.cashout);
        TextView charge = view.findViewById(R.id.charge);
        LinearLayout balanceLayout = view.findViewById(R.id.balance_layout);

        if (WalletActivity.isDarkTheme) {

            cashableBalance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cashableTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            giftBalance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            giftTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cashout.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            charge.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            unit.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            balance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            balanceTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }


        balanceLayout.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, unit, cashableTitle, cashableBalance, giftTitle, giftBalance);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, balanceTitle, balance, cashout, charge);


        Drawable mDrawable = getResources().getDrawable(R.drawable.button_blue_selector_24dp);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.findViewById(R.id.cashout_layout).setBackground(mDrawable);
            view.findViewById(R.id.charge_layout).setBackground(mDrawable);
        }


        imgQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        new ScannerFragment(), "ScannerFragment");
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        PaymentHistoryFragment.newInstance(0, true),
                        "PaymentHistoryFragment");
            }
        });
        view.findViewById(R.id.cashout_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CashOutFragment.newInstance(RaadApp.paygearCard, true), "CashOutFragment");
            }
        });
        view.findViewById(R.id.charge_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CashOutFragment.newInstance(RaadApp.paygearCard, false), "CashOutFragment");
            }
        });

        balance.setText(RaadCommonUtils.formatPrice(card.balance, false));
        cashableBalance.setText(RaadCommonUtils.formatPrice(card.cashOutBalance, false));
        long giftPrice = card.balance - card.cashOutBalance;
        giftBalance.setText(RaadCommonUtils.formatPrice(giftPrice, false));

        if (giftPrice == 0) {
            view.findViewById(R.id.bals_layout).setVisibility(View.GONE);
            view.findViewById(R.id.balance_layout).getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        }
    }

    private void addMyCardsTitle() {
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(titleLayoutParams);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(dp16, dp8, dp16, dp16);
        cardsLayout.addView(layout);

        TextView title2 = new AppCompatTextView(context);
        LinearLayout.LayoutParams title2Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        title2Params.weight = 1.0f;
        title2.setLayoutParams(title2Params);
        title2.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        title2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title2.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        title2.setText(R.string.my_cards);
        layout.addView(title2);


        FrameLayout addCardLayout = new FrameLayout(context);
        int dp40 = RaadCommonUtils.getPx(40, context);
        addCardLayout.setLayoutParams(new LinearLayout.LayoutParams(dp40, dp40));
        addCardLayout.setPadding(dp8, dp8, dp8, dp8);
        layout.addView(addCardLayout);

        AppCompatImageView addCard = new AppCompatImageView(context);
        addCard.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewCompat.setBackground(addCard, RaadCommonUtils.getRectShape(context, R.color.add_card_plus_back, 12, 0));
        addCard.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

        addCard.setImageResource(R.drawable.ic_action_add_white);
        int dp4 = RaadCommonUtils.getPx(4, context);
        //addCard.setPadding(dp4, dp4, dp4, dp4);
        addCardLayout.addView(addCard);
        addCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        new AddCardFragment(), "AddCardFragment");
            }
        });
    }

    private void addEmptyCard() {
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        int cardHeight = BankCardView.getDefaultCardHeight(getContext());

        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, cardHeight);
        params.setMargins(dp16, 0, dp16, dp16);
        cardView.setLayoutParams(params);
        if (WalletActivity.isDarkTheme) {
            cardView.setCardBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        }

        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6, context));
        cardView.setRadius(RaadCommonUtils.getPx(8, context));
        cardsLayout.addView(cardView);
        viewItems.add(cardView);

        TextView textView = new AppCompatTextView(context);
        CardView.LayoutParams textViewParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        textView.setText(R.string.click_here_for_adding_card);
        cardView.addView(textView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        new AddCardFragment(), "AddCardFragment");
            }
        });
    }

    private void addCard(final int position) {
        Card card = mCards.get(position);
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        int cardHeight = BankCardView.getDefaultCardHeight(getContext());

        BankCardView cardView = new BankCardView(context);
        cardView.setId(position + 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, cardHeight);
        if (position > 1) {
            //int dp160 = RaadCommonUtils.getPx(160, context);
            int collapsedDp = RaadCommonUtils.getPx(COLLAPSE, context);
            params.setMargins(dp16, -(cardHeight + dp16 - collapsedDp), dp16, dp16);
        } else {
            params.setMargins(dp16, 0, dp16, dp16);
        }
        cardView.setLayoutParams(params);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6 + (position * 6), context));
        cardsLayout.addView(cardView);
        viewItems.add(cardView);
        cardView.setCard(card, position == mCards.size() - 1);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CardFragment.newInstance(mPayment, mCards.get(position)), "CardFragment");
            }
        });
    }

    @Override
    public void setRefreshLayout(boolean refreshLayout) {
        try {
            if (isAdded() && mRefreshLayout != null)
                load();
        } catch (Exception e) {
        }

    }
}
