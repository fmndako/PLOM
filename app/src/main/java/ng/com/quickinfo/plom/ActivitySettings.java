package ng.com.quickinfo.plom;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;


public class ActivitySettings extends LifecycleLoggingActivity {

    public String keepLoginName = "Keeper";
    //keep signed in
    int keepSignIn;
    @BindView(R.id.tvProfile)
    MyTextView tvProfile;
    @BindView(R.id.tvViewProfile)
    MyTextView tvViewProfile;
    @BindView(R.id.tvCurrency)
    MyTextView tvCurrency;
    @BindView(R.id.spCurrency)
    Spinner spCurrency;
    @BindView(R.id.tvNotification)
    MyTextView tvNotification;
    @BindView(R.id.sNotifications)
    Switch sNotifications;
    @BindView(R.id.tvSelectDaysText)
    MyTextView tvSelectDaysText;
    @BindView(R.id.tvReminderDays)
    EditText tvReminderDays;
    @BindView(R.id.llReminderDays)
    LinearLayout llReminderDays;
    @BindView(R.id.tvKeepMein)
    MyTextView tvKeepMein;
    @BindView(R.id.sKeepMeIn)
    Switch sKeepMeIn;
    @BindView(R.id.tvSignOut)
    MyTextView tvSignOut;
    @BindView(R.id.linear)
    LinearLayout linear;

    private Context mContext;
    //ViewModel
    private UserViewModel mUserViewModel;
    public GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static String Pref_Keeper = "ng.com.quickinfo.PLOM.keep_me_in";
    public static String Pref_ReminderDays = "ng.com.quickinfo.PLOM.reminder_days";
    public static String Pref_Currency = "ng.com.quickinfo.PLOM.currency";
    public static String Pref_Notification = "ng.com.quickinfo.PLOM.notification";
    boolean keepMeIn;
    boolean notification;
    String currency;
    int days;


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //view model

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d(TAG, "account is not  null" + account.getObfuscatedIdentifier());
        }
        //continue
        //updateUI();
        Log.d(TAG, "account is  null, load signin fragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        mContext = getApplicationContext();
        //load View Model
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();

        updateUI();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(
                        "255241179759-ekpv13bpufhdg2fr04e9csmmj7k5ja6b.apps.googleusercontent.com")
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void updateUI() {

        keepMeIn = sharedPref.getBoolean(Pref_Keeper, true);
        if (sharedPref.getBoolean(Pref_Keeper, true)) {
            sKeepMeIn.setChecked(false);
        }else{sKeepMeIn.setChecked(true);}

        if(sharedPref.getBoolean(Pref_Notification, true)){
            sNotifications.setChecked(true);
            llReminderDays.setVisibility(View.VISIBLE);
            tvReminderDays.setText(sharedPref.getInt(Pref_ReminderDays, 7));


        }

        spCurrency.setSelection(sharedPref.getInt(Pref_Currency, 0));

    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        //TODO go back to home menu
                        startActivity(new Intent(ActivitySettings.this, SignInActivity.class));
                        finish();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        //prompt delete of database
                        //then delete database
                        //exit app
                    }
                });
    }



    @OnClick({R.id.tvViewProfile, R.id.tvSignOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvViewProfile:
                break;
            case R.id.tvSignOut:
                signOut();
                break;
        }
    }
}

