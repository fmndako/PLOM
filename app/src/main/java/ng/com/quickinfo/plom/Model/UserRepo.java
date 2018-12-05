package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import ng.com.quickinfo.plom.ListActivity;
import ng.com.quickinfo.plom.SignInActivity;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class UserRepo {

    private UserDao mUserDao;
    private LiveData<List<User>> mAllUsers;
    private User mUser;
    private Context mContext;

    public UserRepo(Application application) {
        LoanRoomDatabase db = LoanRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsers = mUserDao.getAllUsers();
        mContext = application.getBaseContext();
    }

    //get all users
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    //insert user
    public void insert(User User, Context context) {
        new insertAsyncTask(mUserDao, context).execute(User);
    }

    //insert asynctask class
    private class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao, Context context) {
            mAsyncTaskDao = dao;
            mContext = context;
        }


        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.addUser(params[0]);
            // mAsyncTaskDao.insert(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            log(mContext.getPackageName(), "onPostExecute User Added");
            if (mContext!=null){
                log("PostExecute", mContext.getPackageName() +
                        mContext.getApplicationContext().toString());
            }
            Intent intent = new Intent(SignInActivity.userRegisteredAction);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);



        }
    }

    //get a  particular user without asynctask
    public User getUserByEmail(String email) {
        return mUserDao.getUserbyEmail(email);
    }




}