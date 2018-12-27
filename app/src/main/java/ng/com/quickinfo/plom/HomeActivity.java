package ng.com.quickinfo.plom;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateIsDue;
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateIsDueSoon;
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateIsOverDue;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getItemCount;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalSum;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isDueSoon;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isOverDue;
import static ng.com.quickinfo.plom.Utils.FilterUtils.isToday;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.showProgress;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;

public class HomeActivity extends LifecycleLoggingActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvUserGone)
    MyTextView tvUserGone;
    @BindView(R.id.fabHome)
    FloatingActionButton fabHome;
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
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (canTransition()) {
            // Apply activity transition

            getWindow().setEnterTransition(new Slide().setDuration(900));


            getWindow().setExitTransition(new Slide());
        }
        setContentView(R.layout.activity_home);
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
        currency = sharedPref.getString(ActivitySettings.Pref_Currency,"N" );
        reminderDays = sharedPref.getInt(ActivitySettings.Pref_ReminderDays, 7);
        //get id from intent
    
        id = getIntent().getLongExtra("id", 0);
        //makeToast(mContext, id + "");
        showProgress(true, progressBar, mContext);

        // inside your activity (if you did not enable transitions in your theme)
        // Check if we're running on Android 5.0 or higher

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
        Intent listIntent = new Intent(this, ListActivity.class);
        listIntent.putExtra("user_id", id);
        listIntent.putExtra("loanType", selection);
        if(canTransition()){
        startActivity(listIntent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());}
        else{startActivity(listIntent);

        }
    }

    public boolean canTransition(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
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
                tvCountLends.setText(String.valueOf(lendCount) + " Lend");
                log(TAG, "lends:" + lendCount + ":" +
                        lendTotal);

                //borrow
                int borrowCount = getItemCount(loanType.get(1));
                int borrowTotal = getTotalSum(loanType.get(1));
                tvSumBorrows.setText(currency + String.valueOf(borrowTotal));
                tvCountBorrows.setText(String.valueOf(borrowCount) + " Borrow");
                log(TAG, "Borrows:" + borrowCount + ":" + borrowTotal);

                //active count and sum
                int activeCount = borrowCount + lendCount;
                int deficit = borrowTotal - lendTotal;
                int loanSum = borrowTotal + lendTotal;


                tvActiveCount.setText(activeCount+ " Active Loans");
                //tvCountActive.setText(String.valueOf(allCount));

                //cleared
                tvClearedCount.setText((countAllLoans - activeCount) + " Cleared Loans");
                //deficit
                tvDeficit.setText(currency + deficit+"");
                //total loans
                tvOverallCount.setText(countAllLoans + " Total");
                tvSumAll.setText(currency + loanSum + "");


                //duesoon
                List<Loan> isDueSoon= dateIsDueSoon(activeLoans, reminderDays);

                int dueSoonCount = getItemCount(isDueSoon);
                if(dueSoonCount!=0){
                    llDueSoon.setVisibility(View.VISIBLE);
                int dueSoonTotal = getTotalSum(isDueSoon);
                tvSumDueSoon.setText( currency + String.valueOf(dueSoonTotal));
                tvCountDueSoon.setText("Due soon: "+ String.valueOf(dueSoonCount));
                log(TAG, "DueSoon:" + dueSoonCount + ":" + dueSoonTotal);}

                //due
                List<Loan> isDue = dateIsDue(activeLoans);
                int dueCount = getItemCount(isDue);
                if(dueCount!= 0){
                    llDue.setVisibility(View.VISIBLE);
                int dueTotal = getTotalSum(isDue);
                tvSumDue.setText( currency + String.valueOf(dueTotal));
                tvCountDue.setText("Due today: "+ String.valueOf(dueCount));
                log(TAG, "Due:" + dueCount + ":" + dueTotal);}

                //overdue
                List<Loan> isOverDue = dateIsOverDue(activeLoans);

                int overDueCount = getItemCount(isOverDue);
                if(overDueCount!=0) {
                    llOverDue.setVisibility(View.VISIBLE);
                    int overDueTotal = getTotalSum(isOverDue);
                    tvSumOverdue.setText(currency + String.valueOf(overDueTotal));
                    tvCountOverdue.setText("Over due: "+ String.valueOf(overDueCount));
                    log(TAG, "Over Due:" + overDueCount + ":" + overDueTotal);
                }

                }
        });
        //stop progress bar
        showProgress(false, progressBar, mContext);
        log(TAG, "stopprogress");
    }

    @OnClick({R.id.llBorrows,
            R.id.tvSumLends, R.id.fabHome, R.id.navigation_notifications, R.id.navigation_settings,
            R.id.llLends, R.id.tvOverallCount, R.id.tvSumAll, R.id.llDueSoon, R.id.llDue, R.id.llOverDue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llBorrows:
                log(TAG, "borrow");
                viewLoans(3);

                break;
            case R.id.fabHome:
                viewLoans(1);
                break;
            case R.id.llLends:

                viewLoans(2);
                break;
            case R.id.navigation_settings:
                startActivity(new Intent(HomeActivity.this, ActivitySettings.class));

                break;
            case R.id.navigation_notifications:
                viewLoans(7);
                break;

            case R.id.tvOverallCount:
                viewLoans(0);
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

    private void testingDateFilters() {
        //TODO remove
        makeToast(mContext, "testing");
        String[] dates = { "01/01/2018", "11/11/2018", "21/12/2018",
                "22/12/2018", "23/12/2018", "24/12/2018", "25/12/2018",
                "28/12/2018","29/12/2018",
                "30/12/2018", "31/12/2018", "01/01/2019"};

        log(TAG, "isDueToday: date is today");
        for(String dateS: dates){
            Date date = stringToDate(dateS);

            log(TAG, dateS + ": "+isToday(date) + "");


        }

        log(TAG, "overdue: Today is after the date of repayment");
        for(String dateS: dates){
            Date date = stringToDate(dateS);

            log(TAG, dateS + ": "+isOverDue(date) + "");


        }

        log(TAG, "duesoon: Today before the date of repayment");
        for(String dateS: dates){
            Date date = stringToDate(dateS);

            log(TAG, dateS + ": "+isDueSoon(date, 7) + "");


        }


    }
}
