package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.ButterKnife;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanListAdapter;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.Model.OffsetListAdapter;

public class DetailActivity extends LifecycleLoggingActivity implements
        LoanListAdapter.OnHandlerInteractionListener {
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
        setContentView(R.layout.activity_detailnew);
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
                adapter.setLoans(loans);

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

    private void updateUI(){
        //update UI components
        if (mLoan!= null) {
            Utilities.makeToast(mContext, mLoan.getName());
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
