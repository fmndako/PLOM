package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.Utils.FilterUtils.activeLoans;
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateFilterList;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getItemCount;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalSum;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.showProgress;

public class HomeActivity extends LifecycleLoggingActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvUserGone)
    MyTextView tvUserGone;
    @BindView(R.id.tvEmail)
    MyTextView tvEmail;
    @BindView(R.id.tvCountBorrows)
    MyTextView tvCountBorrows;
    @BindView(R.id.tvSumBorrows)
    MyTextView tvSumBorrows;
    @BindView(R.id.llBorrows)
    LinearLayout llBorrows;
    @BindView(R.id.tvCountLends)
    MyTextView tvCountLends;
    @BindView(R.id.tvSumLends)
    MyTextView tvSumLends;
    @BindView(R.id.llLends)
    LinearLayout llLends;
    @BindView(R.id.tvDeficit)
    MyTextView tvDeficit;

    @BindView(R.id.tvSumAll)
    MyTextView tvSumAll;
    @BindView(R.id.tvCountDueSoon)
    MyTextView tvCountDueSoon;
    @BindView(R.id.tvSumDueSoon)
    MyTextView tvSumDueSoon;
    @BindView(R.id.llDueSoon)
    LinearLayout llDueSoon;
    @BindView(R.id.tvCountDue)
    MyTextView tvCountDue;
    @BindView(R.id.tvSumDue)
    MyTextView tvSumDue;
    @BindView(R.id.llDue)
    LinearLayout llDue;
    @BindView(R.id.tvCountOverdue)
    MyTextView tvCountOverdue;
    @BindView(R.id.tvSumOverdue)
    MyTextView tvSumOverdue;
    @BindView(R.id.tvActiveCount)
    MyTextView tvActiveCount;
    @BindView(R.id.tvClearedCount)
    MyTextView tvClearedCount;
    @BindView(R.id.llOverDue)
    LinearLayout llOverDue;
    @BindView(R.id.hlinear)
    LinearLayout hlinear;
    @BindView(R.id.tvOverallCount)
    MyTextView tvOverallCount;

    private String TAG = getClass().getSimpleName();

    //user
    private User mUser;
    //shared pref
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    String currency;
    int reminderDays;



    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    UserViewModel mUserViewModel;
    long id;
    Context mContext;

    public static final String userAddAction = "package ng.com.quickinfo.plom.USER_ADDED";
    public static final String userUpdateAction = "package ng.com.quickinfo.plom.USER_UPDATED";
    public static final String userDeleteAction = "package ng.com.quickinfo.plom.USER_DELETED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inprogress);
        ButterKnife.bind(this);

        //handle nav
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //handles viewmodel
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //context
        mContext = getApplicationContext();
        
        //shared pref
        sharedPref = Utilities.MyPref.getSharedPref(mContext);
        editor = sharedPref.edit();
        currency = sharedPref.getString("currency","N" );
        reminderDays = sharedPref.getInt("reminderDays", 7);
        //get id from intent
    
        id = getIntent().getLongExtra("id", 0);
        makeToast(mContext, id + "");
        showProgress(true, progressBar, mContext);
       //user live datat
        UserViewModel userViewModel = ViewModelProviders.of(this).get(
                UserViewModel.class
        );

        userViewModel.getUserById(id).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable final User user) {
                mUser = user;
                updateUI();
            }
        });
    }

    private void updateUI() {
        //user components
        tvEmail.setText(mUser.getEmail());
        if(!mUser.getUserName().isEmpty()){
            tvUserGone.setVisibility(View.VISIBLE);
            tvUserGone.setText(mUser.getUserName());
        }

        //loan components
        getLoans();


    }

    private void viewLoans(int selection) {
        //go to listActivity
        //TODO uncomment all below
        Intent listIntent = new Intent(this, ListActivity.class);
        //listIntent.putExtra("user_id", mUser.getUserId());
        //listIntent.putExtra("email", mUser.getEmail());
        //listIntent.putExtra("loanType", selection);
        startActivity(listIntent);
    }


    private void getLoans() {
        //observer
        mLoanViewModel.getLoansByUserId(id).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.

                //loans by user
                int countAllLoans = getItemCount(loans);


                
                List<Loan> activeLoans = activeLoans(loans);

                //lends
                List<List<Loan>> loanType = FilterUtils.loanType(activeLoans);
                int lendCount = getItemCount(loanType.get(0));
                int lendTotal = getTotalSum(loanType.get(0));
                tvSumLends.setText(currency + String.valueOf(lendTotal));
                tvCountLends.setText(String.valueOf(lendCount));
                log(TAG, "lends:" + lendCount + ":" +
                        lendTotal);

                //borrow
                int borrowCount = getItemCount(loanType.get(1));
                int borrowTotal = getTotalSum(loanType.get(1));
                tvSumBorrows.setText(currency + String.valueOf(borrowTotal));
                tvCountBorrows.setText(String.valueOf(borrowCount));
                log(TAG, "Borrows:" + borrowCount + ":" + borrowTotal);

                //active count and sum
                int activeCount = borrowCount + lendCount;
                int deficit = borrowTotal - lendTotal;

                //TODO
                //TODO
                tvActiveCount.setText(activeCount+ " Active Loans");
                //tvCountActive.setText(String.valueOf(allCount));

                //cleared
                tvClearedCount.setText((countAllLoans - activeCount) + " Cleared Loans");
                //deficit
                tvDeficit.setText(deficit+"");
                //total loans
                tvOverallCount.setText(countAllLoans + "Total");
                tvSumAll.setText(currency + borrowTotal+lendTotal + "");

                //datelist
                List<List<Loan>> byDateLoans = dateFilterList(activeLoans, reminderDays);

                //duesoon

                int dueSoonCount = getItemCount(byDateLoans.get(0));
                if(dueSoonCount!=0){
                    llDueSoon.setVisibility(View.VISIBLE);
                int dueSoonTotal = getTotalSum(byDateLoans.get(0));
                tvSumDueSoon.setText( currency + String.valueOf(dueSoonTotal));
                tvCountDueSoon.setText(String.valueOf(dueSoonCount));
                log(TAG, "DueSoon:" + dueSoonCount + ":" + dueSoonTotal);}

                //due
                int dueCount = getItemCount(byDateLoans.get(1));
                if(dueCount!= 0){
                    llDue.setVisibility(View.VISIBLE);
                int dueTotal = getTotalSum(byDateLoans.get(1));
                tvSumDue.setText( currency + String.valueOf(dueTotal));
                tvCountDue.setText(String.valueOf(dueCount));
                log(TAG, "Due:" + dueCount + ":" + dueTotal);}

                //overdue
                int overDueCount = getItemCount(byDateLoans.get(2));
                if(overDueCount!=0) {
                    llOverDue.setVisibility(View.VISIBLE);
                    int overDueTotal = getTotalSum(byDateLoans.get(2));
                    tvSumOverdue.setText(currency + String.valueOf(overDueTotal));
                    tvCountOverdue.setText(String.valueOf(overDueCount));
                    log(TAG, "overDue:" + overDueCount + ":" + overDueTotal);
                }

                //TODO update other UI
                Date date = Calendar.getInstance().getTime();
                log(TAG, Utilities.dateToString(date));

            }
        });
        //stop progress bar
        showProgress(false, progressBar, mContext);
        log(TAG, "stopprogress");
    }

    @OnClick({R.id.llBorrows,
            R.id.tvSumLends,R.id.navigation_notifications, R.id.navigation_settings, R.id.llLends, R.id.tvDeficit, R.id.tvSumAll, R.id.llDueSoon, R.id.llDue, R.id.llOverDue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llBorrows:
                viewLoans(2);
                break;
            case R.id.llLends:

                viewLoans(1);
                break;
            case R.id.navigation_settings:
                startActivity(new Intent(HomeActivity.this, ActivitySettings.class));

                break;
            case R.id.navigation_notifications:
                viewLoans(7);
                break;

            case R.id.tvDeficit:
                break;
            case R.id.tvSumAll:
                viewLoans(1);
                break;
            case R.id.llDueSoon:
                viewLoans(4);
                break;
            case R.id.llDue:
                viewLoans(5);
                break;
            case R.id.llOverDue:
                viewLoans(6);
                break;
        }
    }
}
