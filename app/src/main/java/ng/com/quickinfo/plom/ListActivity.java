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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.View.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.DetailActivity.startSettings;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getItemCount;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalSum;
import static ng.com.quickinfo.plom.Utils.Utilities.intentToLoan;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

public class ListActivity extends LifecycleLoggingActivity implements
        LoanListAdapter.OnHandlerInteractionListener {
    //implemented a listener from the adapter to handle layout clicks
    //delare variables
    //intent for signout and revoke access
    ListReceiver ListReceiver;
    IntentFilter intentfilter;
    @BindView(R.id.tvUser)
    MyTextView tvUser;
    @BindView(R.id.tvEmail)
    MyTextView tvEmail;

    @BindView(R.id.tvSizeLends)
    MyTextView tvSize;
    @BindView(R.id.tvTotalLends)
    MyTextView tvTotal;



    //adapter
    //loads the RV
    private RecyclerView recyclerView;
    private LoanListAdapter adapter;

    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    String currency;
    int reminderDays;
    //loan
    private List<Loan> mLoans;

    public static String ACTION_USER_SIGN_IN = "ng.com.quickinfo.plom.ACTION_USER_SIGN_IN";
    public static String ACTION_SIGN_OUT = "ng.com.quickinfo.loanmanager.ACTION_SIGN_OUT";
    public static String ACTION_DELETE_ACCOUNT = "ng.com.quickinfo.loanmanager.ACTION_DELETE_ACCOUNT";
    @BindView(R.id.register_progress)
    ProgressBar mRegisterProgress;

    //context
    private Context mContext;
    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    private String mEmail;
    private long mUserId;

    int loanType;
    //required for start new activity
    public static final int NEW_LOAN_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //show progress
        //load View Model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //context
        mContext = getApplicationContext();
        Utilities.showProgress(true, mRegisterProgress, mContext);

        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();
        currency = sharedPref.getString("currency","N" );
        reminderDays = sharedPref.getInt("reminderDays", 7);
        //intent
        //TODO can get the email address from shared pref
        //mEmail = getIntent().getStringExtra("email");
        mUserId = getIntent().getLongExtra("user_id", 1);
        loanType = getIntent().getIntExtra("loanType", 1);


        mEmail = Utilities.MyPref.getSharedPref(mContext).getString("email", null);
        makeToast(mContext, mEmail);

        //register receiver
        registerMyReceivers();
        //set collapsing tool bar
        setToolBar(mUserId);
        ////unregister receivers
        //        unRegisterMyReceivers();
        loadRV(loanType);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewLoan();
            }
        });

    }

    public void onHandlerInteraction(long loan_id) {
        //my own listener created in the loanadapter class
        Utilities.makeToast(this, "" + loan_id);
        startDetailActivity(loan_id);
    }

    private void startDetailActivity(long loan_id) {
        //starts detail activity
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("loan_id", loan_id);
        startActivity(detailIntent);
    }

    private void setToolBar(long id) {

        UserViewModel userViewModel = ViewModelProviders.of(this).get(
                UserViewModel.class
        );

        userViewModel.getUserById(id).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {
                tvEmail.setText(user.getEmail());
                if(!user.getUserName().isEmpty()){
                    tvUser.setText(user.getUserName());
                    tvUser.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addNewLoan() {
        Intent intent = new Intent(this, AddLoanActivity.class);
        intent.putExtra("loantype", loanType);
        startActivityForResult(intent, NEW_LOAN_ACTIVITY_REQUEST_CODE);
    }

    private void loadRV(final int loanType) {
        //loads the RV
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new LoanListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //observer
        mLoanViewModel.getLoansByUserId(mUserId).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                log(TAG, loans.size() + "size");
                mLoans = loans;
                switch (loanType) {
                    case 1:
                        mLoans = FilterUtils.activeLoans(loans);
                        break;
                    case 2:
                        mLoans = FilterUtils.loanType(loans).get(0);
                        break;
                    case 3:
                        mLoans = FilterUtils.loanType(loans).get(1);

                        break;
                    case 4:
                        mLoans = FilterUtils.dateIsDue(loans);

                        break;
                    case 5:
                        mLoans = FilterUtils.dateIsDueSoon(loans, reminderDays);
                        break;
                    case 6:
                        mLoans = FilterUtils.dateIsOverDue(loans);

                        break;
                    case 7:
                        mLoans =FilterUtils.Notifications(loans);
                        break;

                }
                //TODO after all, setLoans to mLoans
                adapter.setLoans(loans);

                tvSize.setText("" + getItemCount(loans) + " Loans");
                tvTotal.setText(currency + getTotalSum(loans));
                //TODO update other UI
                log(TAG, adapter.getItemCount() + "");
                //Utilities.log(TAG, getTotalLends(loans)+"");
                Date date = Calendar.getInstance().getTime();
                log(TAG, Utilities.dateToString(date));
            }
        });
        Utilities.showProgress(false, mRegisterProgress, mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //search view
        MenuItem searchViewItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                log(TAG, query);
                // if( ! searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                item.collapseActionView();
//                return false;

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                log(TAG, newText);
                adapter.setLoans(FilterUtils.searchLoans(mLoans, newText));
                tvSize.setText(adapter.getItemCount()+ "Loans");
                tvTotal.setText(currency + adapter.getItemSum());

                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(startSettings(mContext, mUserId));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == NEW_LOAN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Loan loan = intentToLoan(intent);//use database utils async task
            loan.setUser_id(mUserId);
            new LoanRepo.LoanAsyncTask(mLoanViewModel,
                    DetailActivity.loanInsertAction).execute(loan);

            ///makeToast(this, "loan saved");

        }

    }

    private void registerMyReceivers() {
        ListReceiver = new ListReceiver();
        intentfilter = new IntentFilter(DetailActivity.loanInsertAction);
        //intentfilter.addAction(ListActivity.ACTION_DELETE_ACCOUNT);
        //registers receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(ListReceiver, intentfilter);
    }

    private void unRegisterMyReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(ListReceiver);
    }

    public class ListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());
            switch (intent.getAction()) {
                case DetailActivity.loanInsertAction:
                    makeToast(mContext, "Loan added");
                    break;
            }


        }
    }

}
