package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Model.LoanRepo.LoanAsyncTask;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.View.OffsetAdapter;

import static ng.com.quickinfo.plom.Utils.Utilities.HomeIntent;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString1;
import static ng.com.quickinfo.plom.Utils.Utilities.intentToLoan;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.drawable.Drawable;
//import android.support.v4.graphics.drawable.DrawableCompat;

public class DetailActivity extends LifecycleLoggingActivity implements
        OffsetDialog.OffsetDialogListener, ListDialog.ListDialogListener,
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
    @BindView(R.id.llDetailOffsetBalance)
    LinearLayout llDetailOffsetBalance;
    @BindView(R.id.tvDetailAmountBalanceValue)
    MyTextView tvDetailAmountBalanceValue;
    @BindView(R.id.pbDetail)
    ProgressBar pbDetail;
    @BindView(R.id.llDetailMain)
    LinearLayout llDetailMain;



    private Context mContext;

    //Receivers
    DetailReceiver myReceiver;

    IntentFilter offsetAddFilter;

    public static final String loanInsertAction = "package ng.com.quickinfo.plom.LOAN_INSERTED";
    public static final String loanDetailGetAction = 
            "package ng.com.quickinfo.plom.detail_activity_get_loan";

    public static final String loanClearedAction = "package ng.com.quickinfo.plom.LOAN_CLEARED";
    public static final String loanUpdateAction = "package ng.com.quickinfo.plom.LOAN_EDITED";
    public static final String loanDeleteAction = "package ng.com.quickinfo.plom.LOAN_DELETED";
    public static final String offsetAddAction = "package ng.com.quickinfo.plom.OFFSET_ADDED";
    public static final String offsetDeleteAction = "package ng.com.quickinfo.plom.OFFSET_DELETED";
    public static final String offsetUpdateAction = "package ng.com.quickinfo.plom.OFFSET_UPDATED";

    private static final int UPDATE_ACTIVITY_REQUEST_CODE = 13;


    //adapter

    private ExpandableHeightListView listview;
    private ArrayList<Offset> Offset;
    private OffsetAdapter baseAdapter;

    //toolbar and menu
    Menu mMenu;

    private SharedPreferences myPref;
    private SharedPreferences.Editor editor;

    //loan and current offset
    private Loan mLoan;
    private Offset mOffset;


    private String userEmail;
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
        //llDetailMain.setVisibility(View.INVISIBLE);
        //showProgress(true, pbDetail, mContext);


        //create broadcast receivers
        myReceiver = new DetailReceiver();
        //add filters
        offsetAddFilter = new IntentFilter(offsetAddAction);
        offsetAddFilter.addAction(offsetDeleteAction);
        offsetAddFilter.addAction(offsetUpdateAction);
        offsetAddFilter.addAction(loanDeleteAction);
        offsetAddFilter.addAction(loanUpdateAction);
        offsetAddFilter.addAction(loanClearedAction);
        //register
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, offsetAddFilter);


        //set loan view model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        myPref = Utilities.MyPref.getSharedPref(mContext);
        editor = Utilities.MyPref.getEditor();
        userEmail = myPref.getString("email", "null");

        //        ********LISTVIEW***********
        listview = (ExpandableHeightListView)findViewById(R.id.listview);
        //setlistener

        //get loan id from intent
        long mLoanId = getIntent().getLongExtra("loan_id", 16L);
        // loan data observer
        mLoanViewModel.getLoan(mLoanId).observe(this,
                new Observer<Loan>()
        {

            @Override
            public void onChanged(@Nullable final Loan loan) {
                // Update the cached copy of the loans in the adapter.
                mLoan = loan;
                updateUI();
                //showProgress(false, pbDetail, mContext);
                //llDetailMain.setVisibility(View.VISIBLE);

            }
        });
        //loadRV(mLoanId);
    }

    private void loadRV(long id) {
        //loads the RV
        //observer
        listview.setClickable(true);

        mLoanViewModel.getOffsetByLoanId(id).observe(this, new Observer<List<Offset>>() {
            @Override
            public void onChanged(@Nullable final List<Offset> offsets) {
                // Update the cached copy of the loans in the adapter.
                baseAdapter = new OffsetAdapter(mContext, offsets) {
                };
                listview.setAdapter(baseAdapter);

                tvDetailOffsetTotalValue.setText(baseAdapter.getTotal()+"");
                if(listview.getCount() != 0){

                llDetailOffsetBalance.setVisibility(View.VISIBLE);
                tvDetailAmountBalanceValue.setText((mLoan.getAmount()- baseAdapter.getTotal())+ "");}



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOffset = offsets.get(position);
                loadOffsetListDialog();

            }
        });
            }
        });

    }

    private void loadOffsetListDialog() {
        //handles click of offset list
        Bundle args = new Bundle();
        args.putInt("title", R.string.offset_list_dialog_title);
        args.putInt("items", R.array.offset_dialog_list);
        ListDialog listDialog = new ListDialog();
        listDialog.setArguments(args);
        listDialog.show(getSupportFragmentManager(), "ListDialog");

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
            makeToast(mContext, "Loan name"+mLoan.getName() + mLoan.getId());
            tvDetailAmountValue.setText(mLoan.getAmount() + "");
            //dates
            tvDetailDateTakenValue.setText(dateToString1(mLoan.getDateTaken()));
            //loan type
            if (mLoan.getLoanType() != 0) {
                tvDetailLoanTypeValue.setText(R.string.loan_type_borrow);
                tvDetailLoanTypeValue.setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.rectangle_red));
            }
            //personal details
            tvDetailNameValue.setText(mLoan.getName());
            tvDetailNumberValue.setText(mLoan.getNumber());
            tvDetailEmailValue.setText(mLoan.getEmail());

            //repayment details
            tvDetailDateToRepayValue.setText(mLoan.getDateToRepay().toString());
            if (mLoan.getRepaymentOption() != 0) {
                tvDetailRepaymentOptionValue.setText(R.string.repayment_option_several);
            }
            tvDetailRemarksValue.setText(mLoan.getRemarks() + "");

            //offset
            loadRV(mLoan.getId());
                if (mLoan.getClearStatus() != 0) {

                    updateClearedStatus();
                        }

            }
    }
    //TODO separate concern
    public void updateClearedStatus() {

        tvDetailClearedStatusValue.setText(R.string.cleared_status_cleared);
        //change to date
        tvDetailDateClearedValue.setText(dateToString1(mLoan.getDateCleared()));
        removeClearedMenuItem();

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
                //pass action to offset dialog to know what is to be done, either
                //insert or update
                Bundle args = new Bundle();
                args.putString("action", offsetAddAction);
                OffsetDialog offsetDialog = new OffsetDialog();
                offsetDialog.setArguments(args);
                offsetDialog.show(getSupportFragmentManager(), "OffsetDialog");
                return;

            case R.string.action_clear:
                DialogFragment clearAllDialog = new ClearAllDialog();
                clearAllDialog.show(getSupportFragmentManager(), "ClearAllDialog");
                return;
        }
    }

// ********************* Listeners **********************************************************

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the DialogListener interface

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Date date) {
        // User touched cleared dialog's positive button
        makeToast(mContext, "clear positive offset");
        mLoan.setClearedStatus(date);
        new LoanAsyncTask(mLoanViewModel, loanClearedAction).execute(mLoan);


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Offset offset, String action) {
        // User touched the offset dialog's positive button

        //makeToast(mContext, action);
        if(action.equals(offsetUpdateAction)){
            //makeToast(mContext, "updateing");
            mOffset.setAmount(offset.getAmount());
            mOffset.setDateOffset(offset.getDateOffset());
            mOffset.setRemarks(offset.getRemarks());
            mLoanViewModel.insert(mOffset, action);

        }else{
            //makeToast(mContext, "inserting");

        offset.setLoan_id(mLoan.getId());
        mLoanViewModel.insert(offset, action);
        }
        dialog.dismiss();
}

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int action) {
        // User touched the delete dialog's positive button
        LoanAsyncTask task = new LoanAsyncTask(
                mLoanViewModel, loanDeleteAction
        );
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
// ********************* offset item selected **********************8

    @Override
    public void onItemSelected(DialogFragment dialog, int title, int item){
        dialog.dismiss();
        makeToast(mContext, item + "");
        if (item == 0){
           deleteOffset();
        }else{
            updateOffset();
        }
    }

    public void deleteOffset(){
        log(TAG, "deleteOffset");
        mLoanViewModel.deleteOffset(mOffset);

    }
    public void updateOffset(){
        Bundle args = new Bundle();
        args.putString("action", offsetUpdateAction);
        args.putString("amount", mOffset.getAmount().toString());
        args.putString("remarks", mOffset.getRemarks().toString());
        args.putString("date", dateToString(mOffset.getDateOffset()));
        OffsetDialog offsetDialog = new OffsetDialog();
        offsetDialog.setArguments(args);
        offsetDialog.show(getSupportFragmentManager(), "OffsetDialog");

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




        return super.onCreateOptionsMenu(menu);
    }

    //TODO remove menu if cleared
    public void removeClearedMenuItem(){
        if (mMenu != null) {
            //remove clear action button and update
            mMenu.findItem(R.id.action_clear).setVisible(false);
            mMenu.findItem(R.id.action_update).setVisible(false);
            mMenu.findItem(R.id.action_offset).setVisible(false);
        }

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

                startActivityForResult(updateIntent, UPDATE_ACTIVITY_REQUEST_CODE);
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


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == UPDATE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            log(TAG, "back to detail activity");
            Loan loan = intentToLoan(intent);
            loan.updateLoan(mLoan.getId(), mLoan.getOffset(), mLoan.getClearStatus(), mLoan.getDateCleared(), mLoan.getUser_id());
            //use database utils async task
            new LoanAsyncTask(mLoanViewModel,
                    DetailActivity.loanUpdateAction).execute(loan);
            log(TAG, "tru with detailactivity onActivityResult");
            ///makeToast(this, "loan saved");

        }

    }


    // *********** Register and unregister receivers
    //register receiver when app resumes and unregister when app pauses
    //register on create then unregister on Destroy


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregistering using local broadcast manager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        //null the receivers to prevent ish
        myReceiver = null;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //unregistering using local broadcast manager

    }

    //******************** SignInReceiver ********************
    public class DetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            makeToast(mContext, "intent received" + intent.getAction());
            String action = "";
            switch (intent.getAction()){
                case offsetAddAction:
                    action = "Offset inserted";

                    break;
                case offsetUpdateAction:
                    action = "Offset updated";
                    break;
                case offsetDeleteAction:
                    action = "Offset deleted";
                    break;
                case loanUpdateAction:
                    action = "Loan Updated";
                    break;
                case loanDeleteAction:
                    makeToast(mContext, "Loan Deleted");
                    //startActivity(new Intent(DetailActivity.this, ListActivity.class));
                    onBackPressed();
                    break;
                case loanClearedAction:
                    action = "Loan cleared";
                    break;
            }

            makeToast(mContext, action);
            log(TAG, action + "offset");

            //
        }
    }


}

