package ng.com.quickinfo.plom;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;


public class ActivitySettings extends LifecycleLoggingActivity {


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
    @BindView(R.id.tvSignOut)
    MyTextView tvSignOut;
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.tvDeleteAccount)
    MyTextView tvDeleteAccount;

    private Context mContext;
    //ViewModel
    private UserViewModel mUserViewModel;
    public GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static String Pref_Keeper = "ng.com.quickinfo.PLOM.keep_me_in";
    public static String Pref_User = "ng.com.quickinfo.PLOM.user_id";

    public static String Pref_ReminderDays = "ng.com.quickinfo.PLOM.reminder_days";
    public static String Pref_Currency = "ng.com.quickinfo.PLOM.currency";
    public static String Pref_Currency_sp = "ng.com.quickinfo.PLOM.currency";
    public static String Pref_Notification = "ng.com.quickinfo.PLOM.notification";
    boolean keepMeIn;
    boolean notification;
    String currency;
    int days;
    long mUserId;
    User mUser;


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

        mUserId = sharedPref.getLong(Pref_User, 0) ;
        updateUI();
        setListener();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(
                        "255241179759-ekpv13bpufhdg2fr04e9csmmj7k5ja6b.apps.googleusercontent.com")
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }
    private void setListener() {
        //reminder days
        tvReminderDays.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                editor.putInt(Pref_ReminderDays, Integer.valueOf(s.toString()));
                editor.commit();
            }
        });
    //switch notification
        sNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    llReminderDays.setVisibility(View.VISIBLE);

                } else{
                    llReminderDays.setVisibility(View.GONE);

                }

                editor.putBoolean(Pref_Notification, isChecked);
                editor.commit();
            }
        });
        final String[] array = getResources().getStringArray(R.array.currency_array);

        //spinner
        //String [] countries = {"NG", "US", "CA", "MX", "GB", "DE", "PL", "RU", "JP", "CN" };

        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //setCurrency(i);
                editor.putString(Pref_Currency, getCurrency(array[i]));
                editor.putInt(Pref_Currency_sp, i);
                editor.commit();
                makeToast(mContext, getCurrency(array[i]));




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public static String getCurrency(String s) {
            Locale locale = new Locale("EN", s);
            Currency currency = Currency.getInstance(locale);
            String symbol = currency.getSymbol(locale);
            return symbol;


    }

    private void updateUI() {
        getUser();

        if(sharedPref.getBoolean(Pref_Notification, true)){
            sNotifications.setChecked(true);
            llReminderDays.setVisibility(View.VISIBLE);
            tvReminderDays.setText(sharedPref.getInt(Pref_ReminderDays, 7) + "");


        }
        //TODO  set real value
        spCurrency.setSelection(0);

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
                        //prompt delete of databas
                        //new UserRepo.UserAsyncTask(mUserViewModel, HomeActivity.userDeleteAction).execute();
                        //then delete database
                        //exit app
                    }
                });
    }



    @OnClick({R.id.tvViewProfile, R.id.tvSignOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvViewProfile:
                SignupDialog profile = new SignupDialog();
                Bundle bundle = new Bundle();
                bundle.putLong(Pref_User, mUserId );
                profile.setArguments(bundle);
                profile.show(getSupportFragmentManager(), "Sign Up Fragment");

                break;
            case R.id.tvSignOut:
                logOut();
                break;
            case R.id.tvDeleteAccount:
                deleteAccount();
        }
    }

    private void logOut() {
        if(!mUser.getUserName().isEmpty()){
            logOutUser();
        }else{
            //google sign out
            //showProgress();
            signOut();

        }
    }
    private void logOutUser(){
        editor.putBoolean(Pref_Keeper, false);
        editor.commit();
        startActivity(new Intent(this, SignInActivity.class));
    }
    private void deleteAccount() {
        if(!mUser.getUserName().isEmpty()){
            deleteUser();
            logOutUser();

        }else{
            //google delete account
            revokeAccess();
            deleteUser();
            logOutUser();

        }

    }

    private void deleteUser() {
        new UserRepo.UserAsyncTask(mUserViewModel,
                HomeActivity.userDeleteAction).execute(mUser);
    }

    public void getUser() {

        mUserViewModel.getUserById(mUserId).observe(this, new android.arch.lifecycle.Observer <User>()  {
            @Override
            public void onChanged(@Nullable final User user) {
               mUser = user;

            }
        });


    }

//"""""""""listeners
public void onSignUp(DialogFragment dialog, User user){

        //nothing
}
}


