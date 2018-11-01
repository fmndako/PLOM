package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;

public class MainActivity2 extends LifecycleLoggingActivity {

    //intent for signout and revoke access
    SignOutReceiver signoutReceiver;
    IntentFilter intentfilter;

    public static String ACTION_USER_SIGN_IN = "ng.com.quickinfo.plom.ACTION_USER_SIGN_IN";
    public static String ACTION_SIGN_OUT = "ng.com.quickinfo.loanmanager.ACTION_SIGN_OUT";
    public static String ACTION_DELETE_ACCOUNT = "ng.com.quickinfo.loanmanager.ACTION_DELETE_ACCOUNT";

    //UI ref
    private View mProgressView;
    private View mRVView;


    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    long userID;
    String mEmail;
    //required for start new activity
    public static final int NEW_LOAN_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //load View Model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //intent
        userID = getIntent().getLongExtra("user_id", -1L);
        mEmail = getIntent().getStringExtra("email");
        //register receiver
        registerMyReceivers();
        loginOrSignup(mEmail, userID);
        ////unregister receivers
        //        unRegisterMyReceivers();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();


            }
        });


    }

    private void loginOrSignup(String email, long user_id) {
        //if user is present
        if (user_id != -1L) {

            //set adapter
            loadRV();
        } else {
            Utilities.makeToast(this, "Email not registered, Sign Up");
            startSignUpActivity(email);


        }
    }

    private void startSignUpActivity(String mEmail) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("email", mEmail);
        startActivityForResult(intent, NEW_USER_ACTIVITY_REQUEST_CODE);

    }

    private void goToHome() {
        Intent intent = new Intent(this, AddLoanActivity.class);
        startActivityForResult(intent, NEW_LOAN_ACTIVITY_REQUEST_CODE);
    }

    private void loadRV() {
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final LoanListAdapter adapter = new LoanListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLoanViewModel.getAllLoans().observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the words in the adapter.
                adapter.setLoans(loans);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_LOAN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Loan loan = new Loan(data.getStringExtra(AddLoanActivity.EXTRA_REPLY), "222",
                    "ti@dsf", 22, stringToDate("11/11/1111"), stringToDate("11/11/1111"),
                    1, "terms", 0, 0, 2);
            mLoanViewModel.insert(loan);
            makeToast(this, "loan saved");
        }

        if (requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            User user = new User(data.getStringExtra("user"),
                    "34354354", data.getStringExtra("email"), ";lsfl;f");
            mLoanViewModel.insert(user, this);
            makeToast(this, "User successfully added");
            //getuser id of registered U
            //getUserID()
            //wait(11);ser
            //getUser(data.getStringExtra("email"));


        }
       /* else{
            Toast.makeText(getApplicationContext(), "user not saved", Toast.LENGTH_LONG).show();
        }*/
    }

    private void registerMyReceivers() {
        signoutReceiver = new SignOutReceiver();
        intentfilter = new IntentFilter(MainActivity2.ACTION_USER_SIGN_IN);
        //intentfilter.addAction(MainActivity2.ACTION_DELETE_ACCOUNT);

        //registers receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(signoutReceiver, intentfilter);
    }
    private void unRegisterMyReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(signoutReceiver);

    }
    public class SignOutReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent){
            Log.d(TAG, intent.getAction());
            if (intent.getAction().equals(MainActivity2.ACTION_USER_SIGN_IN)){
                Log.d("Sup", "user in");
                loadRV();
                //signOut();

            }else if (intent.getAction().equals(MainActivity2.ACTION_DELETE_ACCOUNT)){
                Log.d(TAG, "delete account");
                //revokeAccess();

            }


        }
    }

}
