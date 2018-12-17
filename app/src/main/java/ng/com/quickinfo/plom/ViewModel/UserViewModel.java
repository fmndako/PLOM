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


    public UserViewModel(Application application) {
        super(application);
        //instantiate
        mUserRepo = new UserRepo(application);

    }

    public long insert(User user) {
        return mUserRepo.insert(user);
    }

    public LiveData<User> getUserById(long id ) {
        return mUserRepo.getUserById(id);
    }

    public LiveData<User> getUserByEmail(String email) {
        return mUserRepo.getUserByEmail(email);
    }
    public LiveData<User> getUserByName(String name) {
        return mUserRepo.getUserByName(name);
    }

    public  void delete(User user){mUserRepo.delete(user);}
}