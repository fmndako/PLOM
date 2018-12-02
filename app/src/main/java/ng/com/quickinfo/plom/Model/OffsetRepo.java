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

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class OffsetRepo {

    private OffsetDao mOffsetDao;
    private LiveData<List<Offset>> mAllOffsets;
    private Offset mOffset;

    public OffsetRepo(Application application) {
        LoanRoomDatabase db = LoanRoomDatabase.getDatabase(application);
        mOffsetDao = db.offsetDao();
        //mAllOffsets = mOffsetDao.getAllOffsets();
    }

    //get all Offsets by loan id
    public LiveData<List<Offset>> getOffsetsByLoanId( long id) {
        return mOffsetDao.getItembyLoanId(id);
    }

    public Offset getOffset(long id){
            return mOffsetDao.getItembyId(id);
            }

    //insert Offset
    public void insert(Offset offset) {
        new insertAsyncTask(mOffsetDao).execute(offset);
    }

    //insert asynctask class
    private static class insertAsyncTask extends AsyncTask<Offset, Void, Void> {

        private OffsetDao mAsyncTaskDao;

        insertAsyncTask(OffsetDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Offset... params) {
            mAsyncTaskDao.addOffset(params[0]);
            // mAsyncTaskDao.insert(params[0]);
            log("Add Offset", "offset added");
            return null;
        }
    }






}
