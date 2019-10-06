package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.gson.Gson;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.showProgress;

public class ResetDialog extends DialogFragment {
    //context
    Context mContext;

    // Use this instance of the interface to deliver action events
    ResetDialogListener mListener;
    static final String PREF_USER_TOKEN = "ng.com.quickinfo.plom.pref_user_token";
    //View Model
    UserViewModel userViewModel;
    @BindView(R.id.tvResetEmail)
    TextView tvEmail;
    @BindView(R.id.etForgotTokenUser)
    EditText etForgotTokenUser;
    @BindView(R.id.etForgotTokenToken)
    EditText etForgotTokenToken;
    @BindView(R.id.etForgotTokenPwd)
    EditText etForgotTokenPwd;
    @BindView(R.id.etForgotTokenConfirm)
    EditText etForgotTokenConfirm;
    @BindView(R.id.dlgconfirm)
    TextView dlgconfirm;
    @BindView(R.id.dlgcancel)
    TextView dlgcancel;
    Unbinder unbinder1;
    @BindView(R.id.llcontent)
    LinearLayout llcontent;
    @BindView(R.id.llprogressbar)
    LinearLayout llprogressbar;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    Context context;
    String mAction;

    Unbinder unbinder;


    //email
    String mEmail;
    User mUser;
    ResetReceiver myReceiver;
    IntentFilter intentFilter;

    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //get from args
        Bundle bundle = getArguments();
        mAction = bundle.getString("action");

        View view = inflater.inflate(R.layout.dialog_forgotten_password_token, null);
        //butterknife
        builder.setView(view);

        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(getActivity().getApplicationContext());
        editor = sharedPref.edit();

        ButterKnife.bind(this, view);
        //create broadcast receivers
        myReceiver = new ResetReceiver();
        intentFilter= new IntentFilter(HomeActivity.userUpdateAction);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver, intentFilter);
        if (mAction.equals(SignInActivity.userResetPasswordAction)) {
            //action from settings-- update user
            log(this.getTag(), "oncreateview: to reset user");
//            setProfile(bundle.getLong(ActivitySettings.Pref_User, 0));
            showToken(false);

        } else {
            showToken(true);
        }
        setTextListener();

        return builder.create();
    }

    private void showToken(Boolean show) {
        if (!show) {
            etForgotTokenToken.setVisibility(View.GONE);
            etForgotTokenPwd.setVisibility(View.GONE);
            etForgotTokenConfirm.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            dlgconfirm.setText("RESET");
        } else {
            etForgotTokenToken.setVisibility(View.VISIBLE);
            etForgotTokenPwd.setVisibility(View.VISIBLE);
            etForgotTokenConfirm.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            dlgconfirm.setText("CREATE");
        }

    }

    private void setTextListener() {

        etForgotTokenConfirm.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!s.toString().equals(etForgotTokenPwd.getText().toString())) {
                    etForgotTokenConfirm.setError("Passwords do not match");

                }
            }
        });
        etForgotTokenPwd.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String confirmpass = etForgotTokenConfirm.getText().toString();
                String pass = etForgotTokenPwd.getText().toString();
                if ((!confirmpass.isEmpty()) && (!s.toString().equals(confirmpass))) {
                    etForgotTokenConfirm.setError("Passwords do not match");

                }
            }
        });
    }

    // Override the Fragment.onAttach() method to instantiate the OffsetDialogListener
    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the OffsetDialogListener so we can send events to the host
            mListener = (ResetDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement resetDialogListener");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    @OnClick({R.id.dlgconfirm, R.id.dlgcancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlgconfirm:
                String username = etForgotTokenUser.getText().toString();

                if(username.equals("")){
                    etForgotTokenUser.setError("enter username");
                    etForgotTokenUser.requestFocus();}
                else{
                showProgress(true);
                if (dlgconfirm.getText().equals("RESET")) {
                    resetPassword();
                } else {
                    confirmToken(username);
                }}
                break;
            case R.id.dlgcancel:
                if (mAction == SignInActivity.userTokenAction) {
//                TODO    set pref as false;
                }
                dismiss();
                break;
        }
    }
    private void resetPassword() {
        //setUI
        String user = etForgotTokenUser.getText().toString();
        getUser(user, "RESET");


    }

    void showProgress(Boolean show){
        if (show){llcontent.setVisibility(View.GONE);
        llprogressbar.setVisibility(View.VISIBLE);}
        else{
            llcontent.setVisibility(View.VISIBLE);
            llprogressbar.setVisibility(View.GONE);
        }
    }


    private void confirmToken(String username) {
        String storedToken = sharedPref.getString(PREF_USER_TOKEN + username, "");
        if (storedToken.equals("")){
            showProgress(false);
            tvEmail.setText("Invalid user password reset");
        }   else{
            verifyToken(username, storedToken);
        }



    }

    private void getUser(String username, String action) {
        new UserListAsyncTask(userViewModel, action).execute(username);


    }

    private void sendEmail( User user) {
        final String token = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        final String username = user.getUserName();
        log("RESET", "sending mail");
        BackgroundMail.newBuilder(mContext)
                .withUsername(Token.EMAIL)
                .withPassword(Token.PWD)
                .withMailto(user.getEmail())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("PLOM password reset")
                .withProcessVisibility(false)
                .withBody("Username: " + username +"Token: " + token)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        editor.putBoolean(SignInActivity.PREF_PASSWORD_RESET, true);
                        editor.putString(PREF_USER_TOKEN + username, token);
                        editor.commit();
                        makeToast(mContext, "Token sent to your email");
                        showToken(true);
                        showProgress(false);


                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        showProgress(false);

                    }
                })
                .send();
    }


    private void verifyToken(String username, String savedToken) {
        //the user has passwords and other fields and the fields need to be validated
        boolean cancel = false;

        String enterToken = etForgotTokenToken.getText().toString();
        String newpass = etForgotTokenPwd.getText().toString();
        if (!savedToken.equals(enterToken)) {
            etForgotTokenToken.setError("Incorrect Token");
            etForgotTokenToken.requestFocus();
            cancel = true;

        }
        if (!isPasswordValid(newpass)) {
            //update
            etForgotTokenPwd.setError("Invalid Password");
            etForgotTokenPwd.requestFocus();
            cancel = true;

        }

        if (!cancel) {
            // update user
            getUser(username, "CONFIRM");


        }

    }



    private void updateUser(User user) {
        user.setPassword(etForgotTokenPwd.getText().toString());
        //add user to database after credentials have been checked
        UserRepo.UserAsyncTask task = new UserRepo.UserAsyncTask(
                userViewModel, HomeActivity.userUpdateAction
        );

        task.execute(user);
        dismiss();


    }


//
//    private void registerUser(String mUser, String password) {
//        //register user after attempt signup
//        User user = new User(mUser, signupnumber.getText().toString(),
//                signupemail.getText().toString(), password);
//        mListener.onReset(this, user);
//    }


    private boolean isPasswordValid(String password) {
        return (password.length() > 3) && password.equals(
                etForgotTokenConfirm.getText().toString());
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
////        unbinder1 = ButterKnife.bind(this, rootView);
//        return rootView;
//    }


    //*********** interface *************
    public interface ResetDialogListener {
        public void onReset(DialogFragment dialog, User user);

    }

    public class UserListAsyncTask extends AsyncTask<String, Void ,User> {

        String mAction;

        private UserViewModel userViewModel;

        public UserListAsyncTask(UserViewModel lv, String action) {
            userViewModel = lv;
            mAction = action;
        }

        @Override
        protected User doInBackground(String... params) {

                return userViewModel.getUserByUsername(params[0]);

        }

        @Override
        protected void onPostExecute(User result) {

            try {
                String email = result.getEmail();
                if(dlgconfirm.getText().equals("RESET")){
                    String token = new Token(result.getUserName()).getToken();
                    Utilities.log("RESET", "user not null in REset" + result.getEmail());
                    if(isOnline()) {
                        sendEmail(result);
                    }else{

                        makeToast(mContext, "no internet service");
                    }
                } else {

                    updateUser(result);


                }

            }
            catch (NullPointerException err){
                etForgotTokenUser.setError("Invalid Username");
                etForgotTokenUser.requestFocus();
                showProgress(false);
                log("RESET", err.getMessage());
            }
        }

    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public class ResetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismiss();
            makeToast(mContext, "Password reset successful, Login");
        }
    }
}