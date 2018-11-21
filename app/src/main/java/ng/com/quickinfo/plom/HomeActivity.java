package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

public class HomeActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private TextView mTextMessage;

    //user
    private User mUser;

    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    String mEmail;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //handle nav
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //handles viewmodel
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //get email from intent
        mEmail = getIntent().getStringExtra("email");

        //get user id fromm db
        getUser();

    }

    private void getUser() {
        //get user from database using getUserAsyncTask
        GetUserAsyncTAsk task = new GetUserAsyncTAsk();
        task.execute(mEmail);
        Utilities.log(TAG, "user id = ");
    }

    private void getLoans(long user_id){
        //observer
        mLoanViewModel.getLoanByUserId(user_id).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                LoanListAdapter.activeLoans(loans);
                //TODO update other UI
                Utilities.log(TAG, adapter.getItemCount()+"");
                Utilities.log(TAG, adapter.getTotalLends()+"");
                Date date = Calendar.getInstance().getTime();
                Utilities.log(TAG, Utilities.dateToString(date));
            }
        });
    }


    private class GetUserAsyncTAsk extends AsyncTask<String, Void, User> {

        @Override
        protected void onPreExecute(){
            Utilities.log(TAG, "preexecute");
            //TODO implement a progress bar
            //showProgress(true);
        }

        protected User doInBackground(final String... params) {
            return mLoanViewModel.getUser(params[0]);
        }

        protected void onPostExecute(User result){
            Utilities.log(TAG, "postexecute");
            //save result as mUser
            mUser = result;
            //stop progress bar
            //showProgress(false);
            //load loans
            getLoans(mUser.getUserId());
        }
    }
}
