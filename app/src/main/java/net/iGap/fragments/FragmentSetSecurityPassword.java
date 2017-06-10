package net.iGap.fragments;



import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.regex.Pattern;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.TwoStepSecurityConfirmEmail;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.request.RequestUserTwoStepVerificationResendVerifyEmail;
import net.iGap.request.RequestUserTwoStepVerificationSetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetSecurityPassword extends Fragment {

    private int page = 1;
    private EditText edtSetPassword;
    private EditText edtSetRePassword;
    private EditText edtSetHintPassword;
    private EditText edtSetQuestionPassOne;
    private EditText edtSetAnswerPassOne;
    private EditText edtSetQuestionPassTwo;
    private EditText edtSetAnswerPassTwo;
    private EditText edtSetEmail;
    private EditText edtSetConfirmEmail;
    private static String txtPassword;
    private static String oldPassword = "";
    private FragmentActivity mActivity;

    public FragmentSetSecurityPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_security_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            oldPassword = bundle.getString("OLD_PASSWORD");
        }


        view.findViewById(R.id.rootSetPasswordSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView btnBack = (RippleView) view.findViewById(R.id.setPassword_ripple_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getSupportFragmentManager().popBackStack();
                closeKeyboard(v);
            }
        });

        final TextView txtToolbar = (TextView) view.findViewById(R.id.setPassword_toolbar);

        final ViewGroup rootEnterPassword = (ViewGroup) view.findViewById(R.id.rootEnterPassword);
        final ViewGroup rootReEnterPassword = (ViewGroup) view.findViewById(R.id.rootReEnterPassword);
        final ViewGroup rootHintPassword = (ViewGroup) view.findViewById(R.id.rootHintPassword);
        final ViewGroup rootQuestionPassword = (ViewGroup) view.findViewById(R.id.rootQuestionPassword);
        final ViewGroup rootEmail = (ViewGroup) view.findViewById(R.id.rootEmail);
        final ViewGroup rootConfirmEmail = (ViewGroup) view.findViewById(R.id.rootConfirmEmail);

        TextView txtSkipConfirmEmail = (TextView) view.findViewById(R.id.txtSkipConfirmEmail);
        TextView txtResendConfirmEmail = (TextView) view.findViewById(R.id.txtResendConfirmEmail);
        txtResendConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestUserTwoStepVerificationResendVerifyEmail().ResendVerifyEmail();
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), R.string.resend_verify_email_code, Snackbar.LENGTH_LONG);
                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });


        txtSkipConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v);
                mActivity.getSupportFragmentManager().popBackStack();
                edtSetRePassword.setText("");
                edtSetHintPassword.setText("");
                edtSetQuestionPassOne.setText("");
                edtSetQuestionPassTwo.setText("");
                edtSetAnswerPassOne.setText("");
                edtSetAnswerPassTwo.setText("");
                edtSetEmail.setText("");
            }
        });


        RippleView btnOk = (RippleView) view.findViewById(R.id.setPassword_rippleOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (page == 1) {
                    if (edtSetPassword.length() >= 2) {


                        closeKeyboard(v);
                        page = 2;
                        txtToolbar.setText(getResources().getString(R.string.your_password));
                        txtPassword = edtSetPassword.getText().toString();
                        rootEnterPassword.setVisibility(View.GONE);
                        rootReEnterPassword.setVisibility(View.VISIBLE);
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Password_has_to_mor_than_character));
                    }


                } else if (page == 2) {

                    if (edtSetRePassword.length() >= 2) {
                        if (txtPassword.equals(edtSetRePassword.getText().toString())) {


                            closeKeyboard(v);
                            page = 3;
                            txtToolbar.setText(getResources().getString(R.string.password_hint));
                            rootReEnterPassword.setVisibility(View.GONE);
                            rootHintPassword.setVisibility(View.VISIBLE);
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.Password_dose_not_match));
                        }

                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Password_has_to_mor_than_character));
                    }

                } else if (page == 3) {

                    if (edtSetHintPassword.length() > 0) {

                        if (!txtPassword.equals(edtSetHintPassword.getText().toString())) {


                            closeKeyboard(v);
                            page = 4;
                            txtToolbar.setText(getResources().getString(R.string.recovery_question));
                            rootHintPassword.setVisibility(View.GONE);
                            rootQuestionPassword.setVisibility(View.VISIBLE);

                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.Hint_cant_the_same_password));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.please_set_hint));
                    }

                } else if (page == 4) {
                    closeKeyboard(v);
                    if (edtSetQuestionPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                        page = 5;
                        txtToolbar.setText(getResources().getString(R.string.recovery_email));
                        rootQuestionPassword.setVisibility(View.GONE);
                        rootEmail.setVisibility(View.VISIBLE);

                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.please_complete_all_item));
                    }
                } else if (page == 5) {

                    if (edtSetEmail.length() > 0) {
                        Pattern EMAIL_ADDRESS = patternEmail();

                        if (EMAIL_ADDRESS.matcher(edtSetEmail.getText().toString()).matches()) {
                            page = 6;
                            new RequestUserTwoStepVerificationSetPassword().setPassword(oldPassword, txtPassword, edtSetEmail.getText().toString(), edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString(), edtSetHintPassword.getText().toString());

                            txtToolbar.setText(getResources().getString(R.string.recovery_email));
                            rootEmail.setVisibility(View.GONE);
                            rootConfirmEmail.setVisibility(View.VISIBLE);
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.invalid_email));
                        }
                    } else {
                        page = 6;
                        new RequestUserTwoStepVerificationSetPassword().setPassword(oldPassword, txtPassword, edtSetEmail.getText().toString(), edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString(), edtSetHintPassword.getText().toString());
                        closeKeyboard(v);
                        mActivity.getSupportFragmentManager().popBackStack();
                        edtSetPassword.setText("");
                        edtSetRePassword.setText("");
                        edtSetHintPassword.setText("");
                        edtSetQuestionPassOne.setText("");
                        edtSetQuestionPassTwo.setText("");
                        edtSetAnswerPassOne.setText("");
                        edtSetAnswerPassTwo.setText("");
                        edtSetEmail.setText("");
                    }


                } else if (page == 6) {

                    if (edtSetConfirmEmail.length() > 0) {
                        new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtSetConfirmEmail.getText().toString());
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.enter_verify_email_code));
                    }
                    closeKeyboard(v);
                }

            }
        });


        G.twoStepSecurityConfirmEmail = new TwoStepSecurityConfirmEmail() {
            @Override
            public void confirmEmail() {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mActivity.getSupportFragmentManager().popBackStack();
                        edtSetRePassword.setText("");
                        edtSetHintPassword.setText("");
                        edtSetQuestionPassOne.setText("");
                        edtSetQuestionPassTwo.setText("");
                        edtSetAnswerPassOne.setText("");
                        edtSetAnswerPassTwo.setText("");
                        edtSetEmail.setText("");
                    }
                });

            }

            @Override
            public void errorInvalidConfirmCode() {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error(getString(R.string.invalid_verify_email_code));
                    }
                });
            }
        };


        //
        edtSetPassword = (EditText) view.findViewById(R.id.setPassword_edtSetPassword);
        edtSetRePassword = (EditText) view.findViewById(R.id.setPassword_edtSetRePassword);
        edtSetHintPassword = (EditText) view.findViewById(R.id.edtSetHintPassword);
        edtSetQuestionPassOne = (EditText) view.findViewById(R.id.edtSetQuestionPassOne);
        edtSetAnswerPassOne = (EditText) view.findViewById(R.id.edtSetAnswerPassOne);
        edtSetQuestionPassTwo = (EditText) view.findViewById(R.id.edtSetQuestionPassTwo);
        edtSetAnswerPassTwo = (EditText) view.findViewById(R.id.edtSetAnswerPassTwo);
        edtSetEmail = (EditText) view.findViewById(R.id.edtSetEmail);
        edtSetConfirmEmail = (EditText) view.findViewById(R.id.edtSetConfirmEmail);

    }

    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }

    private void closeKeyboard(View v) {

        if (mActivity != null) {
            try {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {

        if (mActivity != null) {
            try {
                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                vShort.vibrate(200);
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
