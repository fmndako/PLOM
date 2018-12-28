package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class LoanRepo {

    private Context mContext;
    private LoanDao mLoanDao;
    private LiveData<List<Loan>> mAllLoans;

    public LoanRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mLoanDao = db.loanDao();
        mContext = application.getApplicationContext();
    }

    //get loan by loan id
    public LiveData<Loan> getLoan(long id){return mLoanDao.getItembyId(id);}

    //return all loans by user_id
    public LiveData<List<Loan>> getLoanByUserId(long user_id){
        return mLoanDao.getItembyUserId(user_id);
    }


    public void insert (Loan Loan) {
        mLoanDao.insert(Loan);
    }

    //delete
    public void delete(Loan loan){
        mLoanDao.deleteLoan(loan);
    }

    //service
    public List<Loan> getLoans(long id){
        return mLoanDao.getLoans(id);
    }


    // ************************   Loan insert async task *************
    public static class LoanAsyncTask extends AsyncTask<Loan, Void, Void> {

        String mAction;

        private LoanViewModel loanViewModel;

        public LoanAsyncTask(LoanViewModel lv, String action) {
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
