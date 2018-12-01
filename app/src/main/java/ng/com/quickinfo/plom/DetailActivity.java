package ng.com.quickinfo.plom;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

public class DetailActivity extends LifecycleLoggingActivity {
    private Context mContext;

    Loan mLoan;
    //viewmodel
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




    }

    private void getLoan(long loan_id) {
        //get loan details
        GetLoanAsyncTask task = new GetLoanAsyncTask();
        task.execute(loan_id);


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
