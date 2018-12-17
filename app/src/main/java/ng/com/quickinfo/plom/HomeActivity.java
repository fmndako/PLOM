package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.FilterUtils;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.Utils.FilterUtils.activeLoans;
import static ng.com.quickinfo.plom.Utils.FilterUtils.dateFilterList;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getItemCount;
import static ng.com.quickinfo.plom.Utils.FilterUtils.getTotalLends;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.showProgress;

public class HomeActivity extends LifecycleLoggingActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ivAll)
    ImageView ivAll;
    @BindView(R.id.tvTotalAll)
    TextView tvTotalAll;
    @BindView(R.id.tvSizeAll)
    TextView tvSizeAll;
    @BindView(R.id.tvTotalOverdue)
    TextView tvTotalOverdue;
    @BindView(R.id.fabOverSoon)
    FloatingActionButton fabOverSoon;
    @BindView(R.id.tvSizeOverdue)
    TextView tvSizeOverdue;
    @BindView(R.id.tvTotalDue)
    TextView tvTotalDue;
    @BindView(R.id.fabDue)
    FloatingActionButton fabDue;
    @BindView(R.id.tvSizeDue)
    TextView tvSizeDue;
    @BindView(R.id.fabDueSoon)
    FloatingActionButton fabDueSoon;
    @BindView(R.id.tvSizeDueSoon)
    TextView tvSizeDueSoon;
    @BindView(R.id.tvTotalDueSoon)
    TextView tvTotalDueSoon;
    @BindView(R.id.ivBorrow)
    ImageView ivBorrow;
    @BindView(R.id.tvTotalBorrows)
    TextView tvTotalBorrows;
    @BindView(R.id.tvSizeBorrows)
    TextView tvSizeBorrows;
    @BindView(R.id.ivLends)
    ImageView ivLends;
    @BindView(R.id.tvLendsTotal)
    TextView tvLendsTotal;
    @BindView(R.id.tvSizeLends)
    TextView tvSizeLends;
    @BindView(R.id.flOverdue)
    FrameLayout flOverdue;
    @BindView(R.id.flDue)
    FrameLayout flDue;
    @BindView(R.id.flDueSoon)
    FrameLayout flDueSoon;
    @BindView(R.id.flOthers)
    FrameLayout flOthers;
    @BindView(R.id.flBorrow)
    FrameLayout flBorrow;

    private String TAG = getClass().getSimpleName();

    //user
    private User mUser;

    //initiate viewmodel
    LoanViewModel mLoanViewModel;
    UserViewModel mUserViewModel;
    long id;
    Context mContext;

    public static final String userAddAction = "package ng.com.quickinfo.plom.USER_ADDED";
    public static final String userUpdateAction = "package ng.com.quickinfo.plom.USER_UPDATED";
    public static final String userDeleteAction = "package ng.com.quickinfo.plom.USER_DELETED";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:


                    return true;
                case R.id.navigation_dashboard:
                    viewLoans(2);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        //handle nav
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //handles viewmodel
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        //context
        mContext = getApplicationContext();
        //get email from intent
        id = getIntent().getLongExtra("id", 0);
        makeToast(mContext, id + "");
        //
    //TODO get user livedata
        //getUser();


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


    private void getLoans(long user_id) {
        //observer
        mLoanViewModel.getLoansByUserId(user_id).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                List<Loan> mLoans = activeLoans(loans);

                //lends
                List<List<Loan>> loanType = FilterUtils.loanType(mLoans);
                int lendCount = getItemCount(loanType.get(0));
                int lendTotal = getTotalLends(loanType.get(0));
                tvLendsTotal.setText(String.valueOf(lendTotal));
                tvSizeLends.setText(String.valueOf(lendCount));
                log(TAG, "lends:" + lendCount + ":" +
                        lendTotal);

                //borrow
                int borrowCount = getItemCount(loanType.get(1));
                int borrowTotal = getTotalLends(loanType.get(1));
                tvTotalBorrows.setText(String.valueOf(borrowTotal));
                tvSizeBorrows.setText(String.valueOf(borrowCount));
                log(TAG, "Borrows:" + borrowCount + ":" + borrowTotal);

                //all
                int allCount = borrowCount + lendCount;
                int allTotal = lendTotal - borrowTotal;
                tvTotalAll.setText(String.valueOf(allTotal));
                tvSizeAll.setText(String.valueOf(allCount));
                log(TAG, "all:" + allCount + ":" + allTotal);

                //datelist
                List<List<Loan>> byDateLoans = dateFilterList(mLoans);

                //duesoon
                int dueSoonCount = getItemCount(byDateLoans.get(0));
                int dueSoonTotal = getTotalLends(byDateLoans.get(0));
                tvTotalDueSoon.setText(String.valueOf(dueSoonTotal));
                tvSizeDueSoon.setText(String.valueOf(dueSoonCount));
                log(TAG, "DueSoon:" + dueSoonCount + ":" + dueSoonTotal);

                //due
                int dueCount = getItemCount(byDateLoans.get(1));
                int dueTotal = getTotalLends(byDateLoans.get(1));
                tvTotalDue.setText(String.valueOf(dueTotal));
                tvSizeDue.setText(String.valueOf(dueCount));
                log(TAG, "Due:" + dueCount + ":" + dueTotal);

                //overdue
                int overDueCount = getItemCount(byDateLoans.get(2));
                int overDueTotal = getTotalLends(byDateLoans.get(2));
                tvTotalOverdue.setText(String.valueOf(overDueTotal));
                tvSizeOverdue.setText(String.valueOf(overDueCount));
                log(TAG, "overDue:" + overDueCount + ":" + overDueTotal);


                //TODO update other UI
                log(TAG, getItemCount(mLoans) + "");
                log(TAG, getTotalLends(mLoans) + "");
                Date date = Calendar.getInstance().getTime();
                log(TAG, Utilities.dateToString(date));

            }
        });
        //stop progress bar
        showProgress(false, progressBar, mContext);
        log(TAG, "stopprogress");
    }

    @OnClick({R.id.ivAll, R.id.fabOverSoon, R.id.fabDue, R.id.fabDueSoon, R.id.ivBorrow, R.id.ivLends})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAll:
                viewLoans(1);
                break;
            case R.id.fabOverSoon:
                viewLoans(6);
                break;
            case R.id.fabDue:
                viewLoans(5);
                break;
            case R.id.fabDueSoon:
                viewLoans(4);
                break;
            case R.id.ivBorrow:
                viewLoans(3);
                break;
            case R.id.ivLends:
                viewLoans(2);
                break;
        }
    }

}
