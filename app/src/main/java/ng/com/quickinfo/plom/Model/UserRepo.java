package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.HomeActivity;
import ng.com.quickinfo.plom.ListActivity;
import ng.com.quickinfo.plom.SignInActivity;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

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
    // get user

    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    public LiveData<User> getUserByEmail(String email){
        return mUserDao.getUserByEmail(email);
    }

    public LiveData<User> getUserById(long id){
        return mUserDao.getUserById(id);
    }

    public LiveData<User> getUserByName(String user){
        return mUserDao.getUserByName(user);
    }
    //insert user
    public long insert (User user) {
        return mUserDao.addUser(user);
    }
    //delete user

    public void delete(User user){
        mUserDao.deleteUser(user);
    }
    //*************** async task ****************
    public static class UserAsyncTask extends AsyncTask<User, Void ,Long> {

        String mAction;

        private UserViewModel userViewModel;

        public UserAsyncTask(UserViewModel lv, String action) {
            userViewModel = lv;
            mAction = action;
        }

        @Override
        protected Long doInBackground(User... params) {
            if (mAction.equals(HomeActivity.userDeleteAction)) {
                userViewModel.delete(params[0]);
                return null;
            } else {
                return userViewModel.insert(params[0]);


            }


        }

        @Override
        protected void onPostExecute(Long result) {
            log("Database Utils", mAction);
            Intent intent = new Intent();
            intent.setAction(mAction);
            intent.putExtra("id", result);
            LocalBroadcastManager.getInstance(
                    userViewModel.getApplication().getApplicationContext()).sendBroadcast(intent);

        }

    }
}
