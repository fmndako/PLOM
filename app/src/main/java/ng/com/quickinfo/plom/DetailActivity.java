package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.ViewModel.OffsetListAdapter;

import static ng.com.quickinfo.plom.Utils.Utilities.HomeIntent;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString1;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.drawable.Drawable;
//import android.support.v4.graphics.drawable.DrawableCompat;

public class DetailActivity extends LifecycleLoggingActivity implements
        OffsetListAdapter.OnHandlerInteractionListener, OffsetDialog.OffsetDialogListener,
        DeleteDialog.DeleteDialogListener, ClearAllDialog.ClearAllDialogListener {
    @BindView(R.id.tvDetailNameValue)
    MyTextView tvDetailNameValue;
    @BindView(R.id.tvDetailAmountValue)
    MyTextView tvDetailAmountValue;
    @BindView(R.id.tvDetailLoanTypeValue)
    MyTextView tvDetailLoanTypeValue;
    @BindView(R.id.tvDetailDateTakenValue)
    MyTextView tvDetailDateTakenValue;
    @BindView(R.id.tvDetailNumberValue)
    MyTextView tvDetailNumberValue;
    @BindView(R.id.tvDetailEmailValue)
    MyTextView tvDetailEmailValue;
    @BindView(R.id.tvDetailDateToRepayValue)
    MyTextView tvDetailDateToRepayValue;
    @BindView(R.id.tvDetailRepaymentOptionValue)
    MyTextView tvDetailRepaymentOptionValue;
    @BindView(R.id.tvDetailRemarksValue)
    MyTextView tvDetailRemarksValue;
    @BindView(R.id.tvDetailClearedStatusValue)
    MyTextView tvDetailClearedStatusValue;
    @BindView(R.id.tvDetailDateClearedValue)
    MyTextView tvDetailDateClearedValue;
    @BindView(R.id.ivDetailCall)
    ImageView ivDetailCall;
    @BindView(R.id.ivDetailEmail)
    ImageView ivDetailEmail;
    @BindView(R.id.ivDetailMessage)
    ImageView ivDetailMessage;
    @BindView(R.id.tvDetailOffsetTotalValue)
    MyTextView tvDetailOffsetTotalValue;
    @BindView(R.id.offsetrv)
    RecyclerView offsetrv;
    @BindView(R.id.OffsetRecyclerview)
    RecyclerView OffsetRecyclerview;
    private Context mContext;

    //Receivers
    DetailReceiver myReceiver;
    IntentFilter loanDeleteFilter;

    IntentFilter offsetAddFilter;

    public static final String loanClearedAction = "package ng.com.quickinfo.plom.LOAN_CLEARED";
    public static final String loanOffsetAction = "package ng.com.quickinfo.plom.LOAN_OFFSET";
    public static final String loanUpdateAction = "package ng.com.quickinfo.plom.LOAN_EDIT";
    public static final String loanDeleteAction = "package ng.com.quickinfo.plom.LOAN_CLEARED";
    public static final String offsetAddAction = "package ng.com.quickinfo.plom.OFFSET_ADDED";
    public static final String offsetDeleteAction = "package ng.com.quickinfo.plom.OFFSET_UPDATED";
    public static final String offsetUpdateAction = "package ng.com.quickinfo.plom.LOAN_DELETED";


    //adapter
    //loads the RV
    private RecyclerView recyclerView;
    private OffsetListAdapter adapter;

    //toolbar and menu
    Menu mMenu;

    private SharedPreferences myPref;
    private SharedPreferences.Editor editor;

    //loan
    private List<Loan> mLoans;
    private Loan mLoan;


    private String userEmail;
    //initiate viewmodel
    LoanViewModel mLoanViewModel;

    //TAG
    public String TAG = getClass().getSimpleName();
//    @BindView(R.id.progressBar2)
//    ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);

        //context
        mContext = getApplicationContext();

        //create broadcast receivers
        myReceiver = new DetailReceiver();
        loanDeleteFilter = new IntentFilter(loanDeleteAction);
        offsetAddFilter = new IntentFilter(offsetAddAction);


        //set loan view model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        myPref = Utilities.MyPref.getSharedPref(mContext);
        editor = Utilities.MyPref.getEditor();
        userEmail = myPref.getString("email", "null");


        //recyclerView = findViewById(R.id.OffsetRecyclerview);
        adapter = new OffsetListAdapter(this);
        OffsetRecyclerview.setAdapter(adapter);
        offsetrv.setAdapter(adapter);
        OffsetRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        offsetrv.setLayoutManager(new LinearLayoutManager(this));


        //get loan id from intent
        long mLoanId = getIntent().getLongExtra("loan_id", 16L);
        getLoan(mLoanId);

        loadRV(mLoanId);


    }

    private void loadRV(long id) {
        //loads the RV

        //observer
        mLoanViewModel.getOffsetByLoanId(id).observe(this, new Observer<List<Offset>>() {
            @Override
            public void onChanged(@Nullable final List<Offset> offsets) {
                // Update the cached copy of the loans in the adapter.
                //recyclerView.setVisibility(View.VISIBLE);

                adapter.setOffsets(offsets);
                tvDetailOffsetTotalValue.setText(adapter.getItemCount() + "");


            }
        });
    }

    private void getLoan(long loan_id) {
        //get loan details
        GetLoanAsyncTask task = new GetLoanAsyncTask();
        task.execute(loan_id);


    }

    // ***************** offsetadapter listener *******************88
    public void onHandlerInteraction(long loan_id) {
        //my own listener created in the loanadapter class
        makeToast(this, "" + loan_id);

    }

//    // drawable changer
//    public static Drawable changeDrawableColor(Context context,int icon, int newColor) {
//        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
//        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
//        return mDrawable;
//    }


    private void updateUI() {
        //update UI components


        if (mLoan != null) {
            makeToast(mContext, mLoan.getName());
            tvDetailNameValue.setText(mLoan.getName());
            tvDetailAmountValue.setText(mLoan.getAmount() + "");

            //loan type

            if (mLoan.getLoanType() != 0) {


                tvDetailLoanTypeValue.setText(R.string.loan_type_borrow);
                tvDetailLoanTypeValue.setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.rectangle_red));
            }
            //dates
            tvDetailDateTakenValue.setText(dateToString1(mLoan.getDateTaken()));

            //personal details
            tvDetailNumberValue.setText(mLoan.getNumber());
            tvDetailEmailValue.setText(mLoan.getEmail());

            //repayment details
            tvDetailDateToRepayValue.setText(mLoan.getDateToRepay().toString());
            if (mLoan.getRepaymentOption() != 0) {
                tvDetailRepaymentOptionValue.setText(R.string.repayment_option_several);
            }
            tvDetailRemarksValue.setText(mLoan.getRemarks() + "");


            //offset
            //TODO corrext logic
            if (mLoan.getOffset() != 0) {
                //load offset rv and set total and Balance
                makeToast(mContext, "offset not equal to 0");
                loadRV(mLoan.getId());

            }


        }
    }

    public void updateClearedStatus() {

        tvDetailClearedStatusValue.setText(R.string.cleared_status_cleared);
        //TODO change to date

        tvDetailDateClearedValue.setText("today");
        log(TAG, "looking for menu");
        if (mMenu != null) {
            //remove clear action button and update
            mMenu.findItem(R.id.action_clear).setVisible(false);
            mMenu.findItem(R.id.action_update).setVisible(false);
            mMenu.findItem(R.id.action_offset).setVisible(false);
        }
    }


    // ************ offset dlg ************************88
    public void showDialogs(int action) {
        // Create an instance of the dialog fragment and show it
        switch (action) {
            case R.string.action_delete:
                DialogFragment deleteDialog = new DeleteDialog();
                deleteDialog.show(getSupportFragmentManager(), "DeleteDialog");
                return;
            case R.string.action_offset:
                DialogFragment offsetDialog = new OffsetDialog();
                offsetDialog.show(getSupportFragmentManager(), "OffsetDialog");
                return;

            case R.string.action_clear:
                DialogFragment clearAllDialog = new ClearAllDialog();
                clearAllDialog.show(getSupportFragmentManager(), "ClearAllDialog");
                return;
        }
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Date date) {
        // User touched cleared dialog's positive button
        makeToast(mContext, "clear positive offset");
        mLoan.setClearedStatus(date);
        mLoanViewModel.insert(mLoan);
        getLoan(mLoan.getId());


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Offset offset) {
        // User touched the offset dialog's positive button
        makeToast(mContext, "positive offset");
        offset.setLoan_id(mLoan.getId());
        mLoanViewModel.insert(offset);
        dialog.dismiss();


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int action) {
        // User touched the dialog's positive button
        //delete
        DeleteAsyncTask task = new DeleteAsyncTask();
        task.execute(mLoan);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int action) {
        // User touched the dialog's negative button

        switch (action) {
            case R.string.action_offset:
                makeToast(mContext, "negative offset");
                return;
            case R.string.action_delete:
                return;
            case R.string.action_clear:
                return;
        }

    }


    // **************** contact lender ********************
    @OnClick({R.id.ivDetailCall, R.id.ivDetailEmail, R.id.ivDetailMessage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDetailCall:
                startCallIntent();
                break;
            case R.id.ivDetailEmail:
                startMailIntent();
                break;
            case R.id.ivDetailMessage:
                startSmsIntent();
                break;
        }
    }

    private void startSmsIntent() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", mLoan.getNumber());
        smsIntent.putExtra("sms_body", "body");
        startActivity(smsIntent);
    }

    private void startMailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "abc@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void startCallIntent() {
        String phone = mLoan.getNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    //********************** action bar ***************88
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        mMenu = menu;
        log(TAG, "menu created");

        //cleared status (if cleared)
        if (mLoan != null) {
            if (mLoan.getClearStatus() != 0) {

                updateClearedStatus();
            }
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_share:
                startShareIntent();

                return true;
            case R.id.action_offset:
                showDialogs(R.string.action_offset);
                return true;
            case R.id.action_update:
                Intent updateIntent = new Intent(mContext, AddLoanActivity.class);
                updateIntent.putExtra("loan_id", mLoan.getId());
                startActivity(updateIntent);
                return true;
            case R.id.action_delete:
                // delete lend
                showDialogs(R.string.action_delete);
                return true;
            case R.id.action_clear:
                showDialogs(R.string.action_clear);
                return true;
            case R.id.action_home:

                startActivity(HomeIntent(this, HomeActivity.class, userEmail));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // *************** share **********************8
    public void startShareIntent() {
        String subj = "Personal Loan Manager";
        String body = "Amount N: " + mLoan.getAmount() + ", Date taken: " +
                dateToString1(mLoan.getDateTaken()) + ", Promised Return Date: " +
                dateToString1(mLoan.getDateToRepay());
        shareText(subj, body);


    }

    public void shareText(String subject, String body) {
        Intent txtIntent = new Intent(Intent.ACTION_SEND);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        txtIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(txtIntent, "Share"));
    }

    //        ******************** get laon async task ******************
    private class GetLoanAsyncTask extends AsyncTask<Long, Void, Loan> {

        @Override
        protected void onPreExecute() {
            log(TAG, "preexecute");
            //TODO implement a progress bar
            // Utilities.showProgress(true, progressBar2, mContext);
        }

        @Override
        protected Loan doInBackground(Long... params) {
            log(TAG, "DOINBACK" + params.length + ":" + params[0]);

            Loan mloan = mLoanViewModel.getLoan(params[0]);
            log(TAG, "DOINBACK");
            return mloan;

        }

        protected void onPostExecute(Loan result) {
            log(TAG, "postexecute");
            //save result as mUser
            mLoan = result;
            //stop progress bar
            //Utilities.showProgress(false, progressBar2, mContext);

            //load loans
            updateUI();
        }
    }


    private class DeleteAsyncTask extends AsyncTask<Loan, Void, Void> {


        @Override
        protected Void doInBackground(Loan... params) {
            mLoanViewModel.delete(params[0]);
            return null;

        }

        protected void onPostExecute(Void Void) {
            Intent onDeleteLoanIntent = new Intent(loanDeleteAction);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(onDeleteLoanIntent);
            //onBackPressed();
            //else start ListActivity and pass back to it Listactivity_loantype
            //load loans

        }
    }

    // *********** Register and unregister receivers
    //register receiver when app resumes and unregister when app pauses
    //register on create then unregister on Destroy
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, loanDeleteFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, offsetAddFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregistering using local broadcast manager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        //null the receivers to prevent ish
        myReceiver = null;
    }

    //******************** SignInReceiver ********************
    public class DetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            //showProgress(false);
            makeToast(context, "offset added hhh");
            loadRV(mLoan.getId());
            //
        }
    }

}

