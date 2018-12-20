package ng.com.quickinfo.plom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.getStatusCodeString;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

public class SignInActivity extends LifecycleLoggingActivity implements SignupDialog.SignupDialogListener {

    //register Broadcast receivers
    public static final String userRegisteredAction = "ng.com.quickinfo.plom.Sign_in";

    SignInReceiver myReceiver;
    IntentFilter myFilter;
    @BindView(R.id.user)
    EditText etUser;
    @BindView(R.id.pass)
    EditText etPassword;

    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.signup)
    TextView signup;

    //keep signed in
    int keepSignIn;

    private Context mContext;
    //ViewModel
    private UserViewModel mUserViewModel;
    public GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    //UI referennce
    private View mProgressView;
    private View mSignInView;

    //List of all users
    LiveData<List<User>> mAllUsers;
    private String mEmail;


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //view model

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d(TAG, "account is not  null" + account.getObfuscatedIdentifier());
            checkCredentialsGoogle(account.getEmail());
        }
        //continue
        //updateUI();
        Log.d(TAG, "account is  null, load signin fragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);


        mContext = getApplicationContext();
        //load View Model
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //create broadcast receivers
        myReceiver = new SignInReceiver();
        myFilter = new IntentFilter(userRegisteredAction);
        myFilter.addAction(HomeActivity.userAddAction);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, myFilter);


        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();

        if(sharedPref.getBoolean(ActivitySettings.Pref_Keeper, false)){
            //TODO uncomment after debugging
            //goToHome(sharedPref.getLong(ActivitySettings.Pref_User, 0));
            //true
            makeToast(mContext, "keep in in");
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(
                        "255241179759-ekpv13bpufhdg2fr04e9csmmj7k5ja6b.apps.googleusercontent.com")
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);

        //listener for signin button
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.sign_in_button:
                            signIn();
                            break;
                    }
                    }
        });
        mSignInView = findViewById(R.id.llLoginView);
        mProgressView = findViewById(R.id.login_signin_progress);
    }

    private void signIn() {
        showProgress(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG, "handle sign in");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            checkCredentialsGoogle(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + getStatusCodeString(e.getStatusCode()));
            showProgress(false);
            makeToast(mContext, "Network Error");

        }


    }

    private void updateUI(String email) {
        //if first timer
        boolean userFirstLogin = sharedPref.getBoolean(email, true);
        if (userFirstLogin) {
            //register
            //start sign up dialog and sign up
            registerUser(email);
            //change first timer = false
            log(TAG, "user first login");
            //save user to shared pref
        }
        //else
        else {
            log(TAG, "not user first login");
            //change sharedpreff user to email
            editor.putString("email", email);
            editor.apply();
            //enter system
            loadAccount(email);

        }

    }
    //TODO remove all 3
    private void registerUser(String email) {
        User user = new User("username", "333", email, "jjjj");
        mEmail = email;
        log(TAG, "registerUser");
       // mUserViewModel.insert(user, mContext);

    }

    private void registerUserSuccesful() {
        log(TAG, mEmail + "registerUserSuccessful");//change shared pref to false if successfull
        editor.putBoolean(mEmail, false);
        editor.putString("email", mEmail);
        editor.apply();
        loadAccount(mEmail);
    }

    private void loadAccount(String email) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("email", email);
        //TODO
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //unregistering using local broadcast manager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        //null the receivers to prevent ish
        myReceiver = null;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignInView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    
    @OnClick({R.id.login, R.id.signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                attemptLogin();
                break;
            case R.id.signup:
                openSignUpDialog("username");
                break;
        }
    }



    private void attemptLogin() {

        // Reset errors.
        etUser.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user)) {
            etUser.setError(getString(R.string.error_field_required));
            focusView = etUser;
            cancel = true;
        } // Check for a valid email address.
        else if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            checkCredentials(user, password);

                        //showProgress(true);

        }
    }

    private void checkCredentialsGoogle(final String email) {
        //google checking credentials. signup if it doesnt exist

        if(sharedPref.getBoolean(email, true)){
            //first timer
            User newUser = new User("", "", email, "");
            mEmail = email;
            new UserRepo.UserAsyncTask(mUserViewModel,
                    HomeActivity.userAddAction).execute(newUser);

        } else {
            //not first timer
            mUserViewModel.getUserByEmail(email).observe(this, new android.arch.lifecycle.Observer <User>()  {
                @Override
                public void onChanged(@Nullable final User user) {
                    if (user != null){
                           //showprogress(false)
                            goToHome(user.getUserId());


                        }
                                    }
            });


        }
        editor.putBoolean(email, false);
        editor.putBoolean(ActivitySettings.Pref_Keeper, true);
        editor.commit();
        makeToast(mContext, "email changed; editor edited");

    }

    private void openSignUpDialog(String action) {
        SignupDialog signup = new SignupDialog();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        signup.setArguments(bundle);
        signup.show(getSupportFragmentManager(), "SignUpDialog");

    }


    private void checkCredentials(final String name, final String password) {
        //showprogess
        mUserViewModel.getUserByName(name).observe(this, new android.arch.lifecycle.Observer <User>()  {
            @Override
            public void onChanged(@Nullable final User user) {
                    if (user != null){
                        if(user.getPassword().equals(password)){
                            //showprogress(false)
                            goToHome(user.getUserId());


                        }else{
                            //TODO change
                            etUser.setError("Invalid Username or Password" + user.getPassword());
                            etUser.requestFocus();
                        }
                    }
                    else{
                        etUser.setError("Invalid Username or Password");
                        etUser.requestFocus();
                    }
                }
            });


    }


    private boolean isPasswordValid(String password) {
        return (password.length() > 3);
    }

    private void goToHome(long id) {
        showProgress(false);
        /*pass email and Id to mainActivity*/
        Intent intent = new Intent(this, HomeActivity.class);

        intent.putExtra("id", id );
        startActivity(intent);
        //finish();

        //showProgress(false);
    }
    //****************** sign up dialog listener **********

    public void onSignUp(DialogFragment dialog, User user){
        dialog.dismiss();
        showProgress(true);
        editor.putBoolean(user.getEmail(), false);
        if (!user.getUserName().isEmpty()){
            editor.putBoolean(user.getUserName(), false);

        }
        editor.commit();
        //showprogress
        new UserRepo.UserAsyncTask(mUserViewModel,
                HomeActivity.userAddAction).execute(user);

        //gotohome

    }


    //******************** SignInReceiver ********************
    public class SignInReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showProgress(false);
            switch (intent.getAction()){
                case HomeActivity.userAddAction:
                     //get id from intent and go to home screen
                    makeToast(context, "user added");
                    long id = intent.getLongExtra("id", 0);
                    if(id!=0){


                        goToHome(id);
                    }
                    break;
            }
        }
    }
}

