package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import ng.com.quickinfo.plom.MainActivity2;

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
        try{
       return new getUserAsyncTask( mUserDao).execute(email).get();}
       catch (Exception e){
            return null;

       }
    }

    public void insert (User user, Context context) {
        new insertAsyncTask(mUserDao, context).execute(user);
    }


    private static class getUserAsyncTask extends AsyncTask<String, Void, User>{
        private UserDao mAsyncTaskDao;
        getUserAsyncTask(UserDao dao){mAsyncTaskDao = dao;}

        @Override
        protected User doInBackground(final String... params){
            mAsyncTaskDao.getUserbyEmail(params[0]);
            return null;
        }

    }

    private static class insertAsyncTask extends AsyncTask<User, Void,Long> {

        private UserDao mAsyncTaskDao;
        //mine added
        private Context mContext;

        insertAsyncTask(UserDao dao, Context context) {
            mAsyncTaskDao = dao;
            mContext = context;
        }

        @Override
        protected Long doInBackground(final User... params) {
            mAsyncTaskDao.addUser(params[0]);
            //mAsyncTaskDao.insert(params[0]);
            return null;
        }

        protected void onPostExecute(){
            Intent intent = new Intent();
            intent.setAction(MainActivity2.ACTION_USER_SIGN_IN);
            Log.d("Repo", "sending intent");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

}
