package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

public class DetailActivity extends LifecycleLoggingActivity implements
        LoanListAdapter.OnHandlerInteractionListener {
    @BindView(R.id.tvDetailNameValue)
    MyTextView tvDetailNameValue;
    @BindView(R.id.tvDetailAmountValue)
    MyTextView tvDetailAmountValue;
    @BindView(R.id.tvDetailLoanTypeValue)
    MyTextView tvDetailLoanTypeValue;
    @BindView(R.id.tvDetailDateTakenValue)
    MyTextView tvDetailDateTakenValue;
    @BindView(R.id.tvDetailNumberValue)
    MyTextView tvDetailNumberValue;
    @BindView(R.id.tvDetailEmailValue)
    MyTextView tvDetailEmailValue;
    @BindView(R.id.tvDetailDateToRepayValue)
    MyTextView tvDetailDateToRepayValue;
    @BindView(R.id.tvDetailRepaymentOptionValue)
    MyTextView tvDetailRepaymentOptionValue;
    @BindView(R.id.tvDetailRemarksValue)
    MyTextView tvDetailRemarksValue;
    @BindView(R.id.tvDetailClearedStatusValue)
    MyTextView tvDetailClearedStatusValue;
    @BindView(R.id.tvDetailDateClearedValue)
    MyTextView tvDetailDateClearedValue;
    @BindView(R.id.ivDetailCall)
    ImageView ivDetailCall;
    @BindView(R.id.ivDetailEmail)
    ImageView ivDetailEmail;
    @BindView(R.id.ivDetailMessage)
    ImageView ivDetailMessage;
    @BindView(R.id.tvDetailOffsetTotalValue)
    MyTextView tvDetailOffsetTotalValue;
    private Context mContext;

    //adapter
    //loads the RV
    private RecyclerView recyclerView;
    private LoanListAdapter adapter;

    //loan
    private List<Loan> mLoans;
    private Loan mLoan;

    //initiate viewmodel
    LoanViewModel mLoanViewModel;

    //TAG
    public String TAG = getClass().getSimpleName();
//    @BindView(R.id.progressBar2)
//    ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);

        //context
        mContext = getApplicationContext();

        //set loan view model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);


        //get loan id from intent
        long mLoanId = getIntent().getLongExtra("loan_id", 16L);
        getLoan(mLoanId);

        loadRV();


    }

    private void loadRV() {
        //loads the RV
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new LoanListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //observer
        mLoanViewModel.getLoanByUserId(1).observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(@Nullable final List<Loan> loans) {
                // Update the cached copy of the loans in the adapter.
                //recyclerView.setVisibility(View.VISIBLE);

                adapter.setLoans(loans);
                tvDetailOffsetTotalValue.setText(adapter.getItemCount() + "");


            }
        });
    }

    private void getLoan(long loan_id) {
        //get loan details
        GetLoanAsyncTask task = new GetLoanAsyncTask();
        task.execute(loan_id);


    }

    public void onHandlerInteraction(long loan_id) {
        //my own listener created in the loanadapter class
        Utilities.makeToast(this, "" + loan_id);

    }

    private void updateUI() {
        //update UI components


        if (mLoan != null) {
            Utilities.makeToast(mContext, mLoan.getName());
            tvDetailNameValue.setText(mLoan.getName());
            tvDetailAmountValue.setText(mLoan.getAmount() + "");
            //loan type
            int loanTypeColor = R.color.green;
            int loanTypeText = R.string.loan_name;
            if(mLoan.getLoanType() != 0){
                loanTypeColor = R.color.red;
                loanTypeText = R.string.loan_amount;

            }
            tvDetailLoanTypeValue.setText(loanTypeText);
            tvDetailLoanTypeValue.setBackgroundColor(loanTypeColor);

            //dates
            tvDetailDateTakenValue.setText(mLoan.getDateTaken().toString());

            //personal details
            tvDetailNumberValue.setText(mLoan.getNumber());
            tvDetailEmailValue.setText(mLoan.getEmail());

            //repayment details
            tvDetailDateToRepayValue.setText(mLoan.getDateToRepay().toString());
            if (mLoan.getRepaymentOption() != 0){
                tvDetailRepaymentOptionValue.setText(R.string.repayment_option_several);}
            tvDetailRemarksValue.setText(mLoan.getRemarks() + "");


            //cleared status
            if (mLoan.getClearStatus() != 0){
                tvDetailClearedStatusValue.setText(R.string.cleared_status_cleared);
                tvDetailDateClearedValue.setText(mLoan.getDateCleared().toString());
            }

            //offset
            if (mLoan.getOffset()!=0){
                //load offset rv and set total and Balance

                loadRV();

            }




        }
    }

    // **************** contact lender ********************
    @OnClick({R.id.ivDetailCall, R.id.ivDetailEmail, R.id.ivDetailMessage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDetailCall:
                //startcallintent
                break;
            case R.id.ivDetailEmail:
                //startmailintent
                break;
            case R.id.ivDetailMessage:
                //startmessageintent
                break;
        }
    }

    private class GetLoanAsyncTask extends AsyncTask<Long, Void, Loan> {

        @Override
        protected void onPreExecute() {
            Utilities.log(TAG, "preexecute");
            //TODO implement a progress bar
            // Utilities.showProgress(true, progressBar2, mContext);
        }

        @Override
        protected Loan doInBackground(Long... params) {
            Utilities.log(TAG, "DOINBACK" + params.length + ":" + params[0]);

            Loan mloan = mLoanViewModel.getLoan(params[0]);
            Utilities.log(TAG, "DOINBACK");
            return mloan;

        }

        protected void onPostExecute(Loan result) {
            Utilities.log(TAG, "postexecute");
            //save result as mUser
            mLoan = result;
            //stop progress bar
            //Utilities.showProgress(false, progressBar2, mContext);

            //load loans
            updateUI();
        }
    }
}
