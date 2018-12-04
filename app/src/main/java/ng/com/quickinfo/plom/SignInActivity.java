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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.getStatusCodeString;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

public class SignInActivity extends LifecycleLoggingActivity {

    //register Broadcast receivers
    public static String asyncTaskUser = "ng.com.quickinfo.plom.Sign_in";
    SignInReceiver myReceiver;
    IntentFilter myFilter;

    private Context mContext;
    //ViewModel
    LoanViewModel mLoanViewModel;
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
    protected void onStart(){
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //view model
        //load View Model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            Log.d(TAG, "account is not  null"+account.getObfuscatedIdentifier());
            //updateUI(account.getEmail());
           // registerMyReceivers();
            //start next activity with email
            loadAccount(account.getEmail());
           //startActivity(new Intent(this, ListActivity.class));
            //register receivers

        }
            //continue
            //updateUI();
            Log.d(TAG, "account is  null, load signin fragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        mContext = getApplicationContext();
        //create broadcast receivers
        myReceiver = new SignInReceiver();
        myFilter = new IntentFilter(asyncTaskUser);

        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();


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
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //listener for signin button
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.sign_in_button:
                            //TODO uncomment signin and comment updateui
                            //showProgress(true);
                            updateUI("timatme@gnnmail.com");
                            // signIn();
                            break;
                    }
                    }
        });
        mSignInView = findViewById(R.id.signin_form);
        mProgressView = findViewById(R.id.signin_progress);
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
                mEmail = account.getEmail();
                Log.d(TAG, mEmail);

                // Signed in successfully, show authenticated UI.
                showProgress(false);
                //updateUI(account.getEmail());
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + getStatusCodeString(e.getStatusCode()));
                //updateUI("timatme@hhhh.com");
                //showProgress(false);
                //TODO remove after successful login

                //updateUI("timatme@h4545hhhl.com");

            }


    }

    private void updateUI(String email ){
        //if first timer
        boolean userFirstLogin = sharedPref.getBoolean(email, true);
        if (userFirstLogin) {
            //register
            //start sign up dialog and sign up
            registerUser(email);
            //change first timer = false
            Utilities.log(TAG, "user first login");
            //save user to shared pref
        }
        //else
        else {
            Utilities.log(TAG, "not user first login");
            //enter system
            loadAccount(email);
            //change sharedpreff user to email
            editor.putString("email", email);
            editor.apply();
        }

    }

    private void registerUser(String email) {
        User user = new User("username", "333", email, "jjjj");
        mLoanViewModel.insert(user, mContext);

    }

    private void registerUserSuccesful(){
        makeToast(this, "Registration successful");
        //change shared pref to false if successfull
        editor.putBoolean(mEmail, false);
        editor.putString("email", mEmail);
        editor.apply();
        loadAccount(mEmail);
    }

    private void loadAccount(String email){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
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
    public void onDestroy(){
        super.onDestroy();
    }

    public static LiveData getAllUsers(LoanViewModel mLoanViewModel, String TAG) {
        LiveData<List<User>> mAllUsers = mLoanViewModel.getAllUsers();
        List<User> allUsers = mAllUsers.getValue();
        if (mAllUsers == null){Log.d(TAG, "allusers is null obj");}
        else {Log.d(TAG, "allusers is not null obj");}
        return  mAllUsers;
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

    //register receiver when app resumes and unregister when app pauses
    //register on create then unregister on Destroy
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, myFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregistering using local broadcast manager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
       //null the receivers to prevent ish
        myReceiver = null;
    }

    //******************** SignInReceiver ********************
    public class SignInReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            showProgress(false);
            makeToast(context, "user added");
            //
        }
    }
}

