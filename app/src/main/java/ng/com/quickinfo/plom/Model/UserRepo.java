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

    public UserRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsers = mUserDao.getAllUsers();
    }

    //get all users
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    //insert user
    public void insert (User User, Context context) {
        new insertAsyncTask(mUserDao, context).execute(User);
    }

    //insert asynctask class
    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;
        private Context mContext;

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
        protected void onPostExecute(Void Void){
            Intent intent = new Intent(SignInActivity.userRegisteredAction);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            log("UserRepo", "sent");



        }
    }

    //get a  particular user without asynctask
    public User getUserByEmail(String email){
        return mUserDao.getUserbyEmail(email);
    }

//    //TODO remove is getUserByEmailWorks
//    public User getUser(String email){
//
//        try{
//       return new getUserAsyncTask( mUserDao).execute(email).get();}
//       catch (Exception e){
//            return null;
//
//       }
//    }

    //getuser asynctask class
    private static class getUserAsyncTask extends AsyncTask<String, Void, User>{
        private UserDao mAsyncTaskDao;
        getUserAsyncTask(UserDao dao){mAsyncTaskDao = dao;}

        @Override
        protected User doInBackground(final String... params){
            mAsyncTaskDao.getUserbyEmail(params[0]);
            return null;
        }
    }

    // insert user async task that includes a lot of other stuff(trial version)
    private static class TryerInsertAsyncTask extends AsyncTask<User, Void,Long> {

        private UserDao mAsyncTaskDao;
        //mine added
        private Context mContext;

        TryerInsertAsyncTask(UserDao dao, Context context) {
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
            intent.setAction(ListActivity.ACTION_USER_SIGN_IN);
            Log.d("Repo", "sending intent");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

}
