package ng.com.quickinfo.plom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.getStatusCodeString;

public class SignInActivity extends LifecycleLoggingActivity {

    public GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9;

    //intent for signout and revoke access
    SignOutReceiver signoutReceiver;
    IntentFilter intentfilter;


    @Override
    protected void onStart(){
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            Log.d(TAG, "account is not  null"+account.getObfuscatedIdentifier());
            //updateUI(account.getEmail());
           // registerMyReceivers();
           // startActivity(new Intent(this, MainActivity.class));
            //register receivers

        }
        else {
            //updateUI();
            Log.d(TAG, "account is  null");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_layout);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken("629197794538-grvu0jtc9eiu8ab51c0qh219j12c50aj.apps.googleusercontent.com")
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
                            signIn();
                            break;
                        // ...
                    }
                    }
        });

        //register receiver
        registerMyReceivers();


    }

    private void registerMyReceivers() {
        signoutReceiver = new SignOutReceiver();
        intentfilter = new IntentFilter(MainActivity.ACTION_SIGN_OUT);
        intentfilter.addAction(MainActivity.ACTION_DELETE_ACCOUNT);

        //registers receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(signoutReceiver, intentfilter);
    }



    private void signIn() {
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
                Log.d(TAG, account.getEmail());
                // Signed in successfully, show authenticated UI.
                updateUI(account.getEmail());
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + getStatusCodeString(e.getStatusCode()));
                updateUI("timatme@hhhh.com");

            }

    }

    private void updateUI(String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);

    }

    private void signOut() {
       mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        unRegisterMyReceivers();
                    }
                });
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        unRegisterMyReceivers();
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregister receivers
        unRegisterMyReceivers();

    }

    private void unRegisterMyReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(signoutReceiver);

    }

    public class SignOutReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, intent.getAction());
            if (intent.getAction().equals(MainActivity.ACTION_SIGN_OUT)){
                Log.d(TAG, "sign out");

                signOut();

            }else if (intent.getAction().equals(MainActivity.ACTION_DELETE_ACCOUNT)){
                Log.d(TAG, "delete account");
                revokeAccess();

            }


        }
    }
}

