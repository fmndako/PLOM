package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class LoanRepo {

    private Context mContext;
    private LoanDao mLoanDao;
    private LiveData<List<Loan>> mAllLoans;

    public LoanRepo(Application application) {
        LoanRoomDatabase db = LoanRoomDatabase.getDatabase(application);
        mLoanDao = db.loanDao();
        mAllLoans = mLoanDao.getAllLoanItems();
        mContext = application.getApplicationContext();
    }

    //return all loans
    public LiveData<List<Loan>> getAllLoans() {
        return mAllLoans;
    }
    //get loan by loan id
    public Loan getLoan(long id){return mLoanDao.getItembyId(id);}
    //return loan by user_id
    public LiveData<List<Loan>> getLoanByUserId(long user_id){
        return mLoanDao.getItembyUserId(user_id);
    }

    public void insert (Loan Loan) {
        new insertAsyncTask(mLoanDao).execute(Loan);
    }

    //delete
    public void delete(Loan loan){
        mLoanDao.deleteLoan(loan);
    }

    private static class insertAsyncTask extends AsyncTask<Loan, Void, Void> {

        private LoanDao mAsyncTaskDao;

        insertAsyncTask(LoanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Loan... params) {
            //mAsyncTaskDao.addLoan(params[0]);
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //class for accessing user obj
    private static class LoanAsyncTask extends AsyncTask<Long, Void, Void> {

        private LoanDao mAsyncTaskDao;

        LoanAsyncTask(LoanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.getItembyId(params[0]);
            return null;
        }

    }
}
