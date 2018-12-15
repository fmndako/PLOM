package ng.com.quickinfo.plom.Model;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import ng.com.quickinfo.plom.DetailActivity;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class OffsetRepo {

    private Context mContext;
    private OffsetDao mOffsetDao;
    private LiveData<List<Offset>> mAllOffsets;
    private Offset mOffset;

    public OffsetRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mOffsetDao = db.offsetDao();

        //TODO trial
        mContext = application.getApplicationContext();
        //mAllOffsets = mOffsetDao.getAllOffsets();
    }

    //get all Offsets by loan id
    public LiveData<List<Offset>> getOffsetsByLoanId( long id) {
        return mOffsetDao.getItembyLoanId(id);
    }

    public Offset getOffset(long id){
            return mOffsetDao.getItembyId(id);
            }
    //delete
    public void delete(Offset offset){
        new insertAsyncTask(mOffsetDao, DetailActivity.offsetDeleteAction).execute(offset);
    }

    //insert Offset
    public void insert(Offset offset, String action) {
        new insertAsyncTask(mOffsetDao,
               action).execute(offset);
    }

// **************** insert asynctask class
    private class insertAsyncTask extends AsyncTask<Offset, Void, Void> {
        String mAction;

        private OffsetDao mAsyncTaskDao;

        insertAsyncTask(OffsetDao dao, String action) {
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(final Offset... params) {
            if (mAction.equals(DetailActivity.offsetDeleteAction)){
                mAsyncTaskDao.deleteOffset(params[0]);
            }
            else {
                mAsyncTaskDao.addOffset(params[0]);;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void){
            log("DetailActivity repo", "post execute");
            Intent intent = new Intent();
            intent.setAction(mAction);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);


        }
    }






}
