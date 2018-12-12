package ng.com.quickinfo.plom.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;


import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static android.content.Context.MODE_PRIVATE;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;

public class DatabaseUtils {
    /*contains basic tools*/

    //        ******************** get laon async task ******************
    public static class GetLoanAsyncTask extends AsyncTask<Long, Void, Loan> {
        LoanViewModel loanViewModel;
        String mAction;
        Context mContext;

        public GetLoanAsyncTask(LoanViewModel lv, String action){
            loanViewModel = lv;

            mAction = action;
            mContext=lv.getApplication().getApplicationContext();
        }


        @Override
        protected Loan doInBackground(Long... params) {

            return loanViewModel.getLoan(params[0]);


        }

        protected void onPostExecute(Loan result) {
           // /extract data into intent
            //send braodcast
            log("DetailActivity repo", "post execute");
            Intent intent = new Intent();
            intent.putExtra("name", result.getName());
            intent.putExtra("number", result.getNumber());
            intent.putExtra("email", result.getEmail());
            intent.putExtra("amount", result.getAmount());
            intent.putExtra("loan_type", result.getLoanType());
            intent.putExtra("date_taken", dateToString(result.getDateTaken()));
            intent.putExtra("date_promised", dateToString(result.getDateToRepay()));
            intent.putExtra("repayment_option", result.getRepaymentOption());
            intent.putExtra("notify", result.getNotify());
            intent.putExtra("remarks", result.getRemarks());
            intent.putExtra("id", result.getId());
            if(mAction == DetailActivity.offsetAddAction) {
                intent.putExtra("cleared_status", result.getClearStatus());
                intent.putExtra("offset", result.getOffset());
                if (result.getClearStatus() != 0) {

                    intent.putExtra("date_cleared", dateToString(result.getDateCleared()));
                }

            }

            intent.setAction(mAction);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

// ************************   Loan insert async task *************
    public static class InsertLoanAsyncTask extends AsyncTask<Loan, Void, Void> {

        String mAction;

        private LoanViewModel loanViewModel;

        public InsertLoanAsyncTask(LoanViewModel lv, String action) {
            loanViewModel = lv;
            mAction = action;
        }

        @Override
        protected Void doInBackground(Loan... params) {
            if (mAction.equals(DetailActivity.loanDeleteAction)) {
                loanViewModel.delete(params[0]);
            } else {
                loanViewModel.insert(params[0]);


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            log("Database Utils", mAction);
            Intent intent = new Intent();
            intent.setAction(mAction);
            LocalBroadcastManager.getInstance(
                    loanViewModel.getApplication().getApplicationContext()).sendBroadcast(intent);

        }

    }

}
