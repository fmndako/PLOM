package ng.com.quickinfo.plom.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Model.OffsetRepo;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;
import ng.com.quickinfo.plom.SignInActivity;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class LoanViewModel extends AndroidViewModel {
    /*Class that helps with view activities*/

    //instantiate repo classes
    private LoanRepo mLoanRepo;
    private UserRepo mUserRepo;
    private OffsetRepo mOffsetRepo;

    private LiveData<List<Loan>> mAllLoans;
    private LiveData<List<User>> mAllUsers;

    private Context context;

    public LoanViewModel(Application application) {
        super(application);
        //instantiate

        context = application.getBaseContext();
        mUserRepo = new UserRepo(application);
        mLoanRepo = new LoanRepo(application);
        mOffsetRepo = new OffsetRepo(application);

        mAllLoans = mLoanRepo.getAllLoans();
        mAllUsers = mUserRepo.getAllUsers();



    }

    //*********************** loans ******************************
    //get allloans
    public LiveData<List<Loan>> getAllLoans() {
        return mAllLoans;
    }
    //getloanbyuser

    public LiveData<List<Loan>> getLoanByUserId(long user_id) {
        return mLoanRepo.getLoanByUserId(user_id);
    }

    //get loan by loan id
    public Loan getLoan(long id) {
        return mLoanRepo.getLoan(id);

    }

    public void insert(Loan loan) {
        mLoanRepo.insert(loan);
    }

    //delete
    public void delete(Loan loan){
        mLoanRepo.delete(loan);
    }
// ********************* users **********************
    public void insert(User user, Context context) {


        mUserRepo.insert(user, context);
    }

    public User getUser(String email) {
        //repo first returns the user with the email address without asynctask
        //return mUserRepo.getSimpleUser(email);
        //with async task
        return mUserRepo.getUserByEmail(email);


    }

    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }


//*********************** offset ******************************

    public LiveData<List<Offset>> getOffsetByLoanId(long loan_id) {
        return mOffsetRepo.getOffsetsByLoanId(loan_id);
    }

    //get loan by loan id
    public Offset getOffset(long id) {
        return mOffsetRepo.getOffset(id);

    }

    public void insert(Offset offset) {
        mOffsetRepo.insert(offset);
    }
}