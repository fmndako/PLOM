package ng.com.quickinfo.plom.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Model.OffsetRepo;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Model.UserRepo;

public class UserViewModel extends AndroidViewModel {
    /*Class that helps with view activities*/

    //instantiate repo classes
    private UserRepo mUserRepo;

    private LiveData<List<User>> mAllUsers;

    private Context context;

    public UserViewModel(Application application) {
        super(application);
        //instantiate
        context = application.getBaseContext();
        mUserRepo = new UserRepo(application);
        mAllUsers = mUserRepo.getAllUsers();
    }

    public void insert(User user, Context context) {
        mUserRepo.insert(user, context);
    }

    public User getUser(String email) {
        return mUserRepo.getUserByEmail(email);
    }

    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }
}