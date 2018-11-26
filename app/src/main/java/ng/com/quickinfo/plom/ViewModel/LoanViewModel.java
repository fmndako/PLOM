package ng.com.quickinfo.plom.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;

public class LoanViewModel extends AndroidViewModel {
    /*Class that helps with view activities*/

    //instantiate repo classes
    private LoanRepo mLoanRepo;
    private UserRepo mUserRepo;
    //private OffsetRepo mOffsetRepo

    private LiveData<List<Loan>> mAllLoans;
    private LiveData<List<User>> mAllUsers;

    public LoanViewModel (Application application) {
        super(application);
        //instantiate
        mUserRepo = new UserRepo(application);
        mLoanRepo = new LoanRepo(application);

        mAllLoans = mLoanRepo.getAllLoans();
        mAllUsers = mUserRepo.getAllUsers();

    }
    //loans
    //get allloans
    public LiveData<List<Loan>> getAllLoans() { return mAllLoans; }
    //getloanbyuser

    public LiveData<List<Loan>> getLoanByUserId(long user_id) {
        return mLoanRepo.getLoanByUserId(user_id);
    }
    //get loan by loan id
    public Loan getLoan(long id){
        return mLoanRepo.getLoan(id);

    }

    public void insert(Loan loan) { mLoanRepo.insert(loan); }

    //users
    public void insert(User user){mUserRepo.insert(user);}

    public User getUser(String email){
        //repo first returns the user with the email address without asynctask
        //return mUserRepo.getSimpleUser(email);
        //with async task
        return mUserRepo.getUserByEmail(email);


    }
    public LiveData<List<User>> getAllUsers() { return mAllUsers; }

}
