package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

public class LoanRepo {

    private Context mContext;
    private LoanDao mLoanDao;
    private LiveData<List<Loan>> mAllLoans;

    public LoanRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
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
        mLoanDao.insert(Loan);
    }

    //delete
    public void delete(Loan loan){
        mLoanDao.deleteLoan(loan);
    }


}
