package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class UserRepo {

    private UserDao mUserDao;
    private LiveData<List<User>> mAllUsers;
    private User mUser;

    public UserRepo(Application application) {
        LoanRoomDatabase db = LoanRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsers = mUserDao.getAllUsers();

    }

    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }
    public User getUser(String email){
       new getUserAsyncTask( mUserDao).execute(email);
        return getUserAsyncTask
    }

    public void insert (User user) {
        new insertAsyncTask(mUserDao).execute(user);
    }


    private static class getUserAsyncTask extends AsyncTask<String, Void, Void>{
        private UserDao mAsyncTaskDao;
        getUserAsyncTask(UserDao dao){mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(final String... params){
            mAsyncTaskDao.getUserbyEmail(params[0]);
            return null;
        }

    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.addUser(params[0]);
            //mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
