package ng.com.quickinfo.plom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;

public class HomeActivity extends LifecycleLoggingActivity implements
        LoanListAdapter.OnHandlerInteractionListener {

    //intent for signout and revoke access
    SignOutReceiver signoutReceiver;
    IntentFilter intentfilter;

    public static String ACTION_USER_SIGN_IN = "ng.com.quickinfo.plom.ACTION_USER_SIGN_IN";
    public static String ACTION_SIGN_OUT = "ng.com.quickinfo.loanmanager.ACTION_SIGN_OUT";
    public static String ACTION_DELETE_ACCOUNT = "ng.com.quickinfo.loanmanager.ACTION_DELETE_ACCOUNT";
    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;

    //context
    private Context mContext;

    //UI ref
    private View mProgressView;
    private View mRVView;

    //user
    private User mUser;

    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    String mEmail;
    //required for start new activity
    public static final int NEW_LOAN_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //load View Model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //context
        mContext = getApplicationContext();
        //intent
        mEmail = getIntent().getStringExtra("email");

        //get user id fromm db
        getUser();
        //register receiver
        registerMyReceivers();
        //set collapsing tool bar
        setToolBar(mEmail);
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

    private void getUser() {
        //get user from database using getUserAsyncTask
        GetUserAsyncTAsk task = new GetUserAsyncTAsk();
        task.execute(mEmail);

        Utilities.log(TAG, "user id = ");

    }

    public void onHandlerInteraction(long total) {

        Utilities.makeToast(this, "" + total);
        Utilities.log(TAG, "" + "viewkink");
    }

    private void setToolBar(String mEmail) {
        //set toolbar string to mEmail

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

//            mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mSignInView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            //mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void loadRV(long user_id) {
        //loads the RV
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final LoanListAdapter adapter = new LoanListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //observer
        mLoanViewModel.getLoanByUserId(user_id).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                adapter.setLoans(loans);
                //TODO update other UI
                Utilities.log(TAG, adapter.getItemCount()+"");
                Utilities.log(TAG, adapter.getTotalLends()+"");
                Date date = Calendar.getInstance().getTime();
                Utilities.log(TAG, Utilities.dateToString(date));
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

            String name = data.getStringExtra(AddLoanActivity.EXTRA_REPLY);
            Integer amount = 33;
            Integer loanType = data.getIntExtra("loanType", 0);
            String remarks = "loan";
            String number = "090";
            Integer clearStatus = 0;
            Integer offset = 1;
            String email = "email";
            Date dateTaken = stringToDate("11/11/1111");
            Date dateToRepay = stringToDate(data.getStringExtra("dateToRepay"));
            long user_id = mUser.getUserId();

            Loan loan = new Loan(name, number, email, amount, dateTaken, dateToRepay, loanType,
                    remarks, clearStatus, offset, user_id);
            mLoanViewModel.insert(loan);
            makeToast(this, "loan saved");

        }

        if (requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            User user = new User(data.getStringExtra("user"),
                    "34354354", data.getStringExtra("email"), ";lsfl;f");
            mLoanViewModel.insert(user);
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
        intentfilter = new IntentFilter(HomeActivity.ACTION_USER_SIGN_IN);
        //intentfilter.addAction(HomeActivity.ACTION_DELETE_ACCOUNT);

        //registers receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(signoutReceiver, intentfilter);
    }

    private void unRegisterMyReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(signoutReceiver);

    }

    public class SignOutReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());
            if (intent.getAction().equals(HomeActivity.ACTION_USER_SIGN_IN)) {
                Log.d("Sup", "user in");
                //loadRV();
                //signOut();

            } else if (intent.getAction().equals(HomeActivity.ACTION_DELETE_ACCOUNT)) {
                Log.d(TAG, "delete account");
                //revokeAccess();

            }


        }
    }

    private class GetUserAsyncTAsk extends AsyncTask<String, Void, User>{

        @Override
        protected void onPreExecute(){
            Utilities.log(TAG, "preexecute");
            showProgress(true);
        }

        protected User doInBackground(final String... params) {
          return mLoanViewModel.getUser(params[0]);



        }


        protected void onPostExecute(User result){

            Utilities.log(TAG, "postexecute");
            //save result as mUser
            mUser = result;
            //stop progress bar
            showProgress(false);
            Utilities.makeToast(mContext, mUser.getUserId()+"");
            //load recycler view
            loadRV(mUser.getUserId());
        }

    }

}
