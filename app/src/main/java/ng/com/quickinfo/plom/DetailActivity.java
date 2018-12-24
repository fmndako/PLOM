package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

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
import ng.com.quickinfo.plom.Receivers.NotificationReceiver;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;
import ng.com.quickinfo.plom.View.OffsetAdapter;

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
    @BindView(R.id.ivImage)
    ImageView ivImage;
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
    @BindView(R.id.sDetailNotify)
    Switch sDetailNotify;



    private Context mContext;

    //Receivers
    DetailReceiver myReceiver;
    IntentFilter intentFilter;

    //TODO remove Notification receiver in production
    NotificationReceiver notificationReceiver;

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";


    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_AMOUNT = "detail:amount";


    //titles for filters
    public static final String loanInsertAction = "package ng.com.quickinfo.plom.LOAN_INSERTED";
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
    String currency;
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
        if (canTransition()) {
            //getWindow().setEnterTransition(new Slide().setDuration(600));
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //context
        mContext = getApplicationContext();
        //llDetailMain.setVisibility(View.INVISIBLE);
        //showProgress(true, pbDetail, mContext);


        //create broadcast receivers
        myReceiver = new DetailReceiver();
        //add filters
        notificationReceiver = new NotificationReceiver();

        intentFilter = new IntentFilter(offsetAddAction);
        intentFilter.addAction(offsetDeleteAction);
        intentFilter.addAction(offsetUpdateAction);
        intentFilter.addAction(loanDeleteAction);
        intentFilter.addAction(loanUpdateAction);
        intentFilter.addAction(loanClearedAction);
        //register
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, intentFilter);

        //set loan view model
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        myPref = Utilities.MyPref.getSharedPref(mContext);

        editor = Utilities.MyPref.getEditor();
        currency = myPref.getString(ActivitySettings.Pref_Currency, "N");

        //        ********LISTVIEW***********
        listview = (ExpandableHeightListView)findViewById(R.id.listview);
        //setlistener

        //get loan id from intent
        long mLoanId = getIntent().getLongExtra(EXTRA_PARAM_ID, 0L);
        // loan data observer

        // BEGIN_INCLUDE(detail_set_view_name)
        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(ivImage, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(tvDetailAmountValue, VIEW_NAME_AMOUNT);
        // END_INCLUDE(detail_set_view_name)
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

    public boolean canTransition(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
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

                tvDetailOffsetTotalValue.setText(currency + baseAdapter.getTotal()+"");
                if(listview.getCount() != 0){

                llDetailOffsetBalance.setVisibility(View.VISIBLE);
                tvDetailAmountBalanceValue.setText(currency + (mLoan.getAmount()- baseAdapter.getTotal())+ "");}



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
            tvDetailAmountValue.setText(currency + mLoan.getAmount() + "");
            //dates
            tvDetailDateTakenValue.setText(dateToString1(mLoan.getDateTaken()));
            //loan type
            if (mLoan.getLoanType() != 0) {
                ivImage.setImageResource(R.drawable.borrowing);
                tvDetailLoanTypeValue.setText(R.string.loan_type_borrow);
                tvDetailLoanTypeValue.setBackground(
                        ContextCompat.getDrawable(mContext, R.drawable.rectangle_red));
            }else{
                ivImage.setImageResource(R.drawable.giving);
            }
            //personal details
            tvDetailNameValue.setText(mLoan.getName());
            tvDetailNumberValue.setText(mLoan.getNumber());
            tvDetailEmailValue.setText(mLoan.getEmail());

            //repayment details
            tvDetailDateToRepayValue.setText(dateToString1(mLoan.getDateToRepay()));
            if (mLoan.getRepaymentOption() != 0) {
                tvDetailRepaymentOptionValue.setText(R.string.repayment_option_several);
            }
            tvDetailRemarksValue.setText(mLoan.getRemarks() + "");

            //notify
            if(mLoan.getNotify()==0){
                sDetailNotify.setChecked(false);

            }else{sDetailNotify.setChecked(true);}

            //offset
            loadRV(mLoan.getId());

            //clear
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
        if (mMenu!=null){
        removeClearedMenuItem();}

    }


    // ************ offset dlg ************************88
    public void showDialogs(int action) {
        // Create an instance of the dialog fragment and show it
        switch (action) {
            case R.string.action_delete:
                DialogFragment deleteDialog = new DeleteDialog();
                Bundle delBundle = new Bundle();
                delBundle.putString("action", DetailActivity.loanDeleteAction);
                deleteDialog.setArguments(delBundle);
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
        dialog.dismiss();
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
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int action) {
        // User touched the dialog's negative button
        dialog.dismiss();
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
                //TODO toggglebelow
                sendNotificationBroadCast();
                //startMailIntent();
                break;
            case R.id.ivDetailMessage:
                startSmsIntent();
                break;
        }
    }

    private void sendNotificationBroadCast() {
        Intent intent = new Intent();
        intent.setAction(offsetUpdateAction);
        LocalBroadcastManager.getInstance(
                this).sendBroadcast(intent);



    }

    private void startSmsIntent() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", mLoan.getNumber());
        smsIntent.putExtra("sms_body", getMessage());
        startActivity(smsIntent);
    }

    private void startMailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mLoan.getEmail(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Loan");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getMessage());
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
        if(mLoan.getClearStatus()!=0){
            removeClearedMenuItem();
        }
        //cleared status (if cleared)




        return super.onCreateOptionsMenu(menu);
    }

    public void removeClearedMenuItem(){

            //remove clear action button and update
            mMenu.findItem(R.id.action_clear).setVisible(false);
            mMenu.findItem(R.id.action_update).setVisible(false);
            mMenu.findItem(R.id.action_offset).setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(startSettings(mContext, mLoan.getUser_id()));
                return true;

            case R.id.action_share:
                shareText("Loan", getMessage());

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
                makeToast(mContext, "home clicked");
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("id", mLoan.getUser_id());
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static Intent startSettings(Context mContext, long id    ) {
        Intent intent = new Intent(mContext, ActivitySettings.class);
        intent.putExtra(ActivitySettings.Pref_User, id);
        return intent;
    }

    // *************** share **********************8

    public String getMessage(){
        //include Amount in messaging
        return "Amount: " + currency+mLoan.getAmount() + "\n"
                +  "Date Taken: " + dateToString(mLoan.getDateTaken()) + "\n"
                + "Promised to repay before " + dateToString1(mLoan.getDateToRepay()) +"\n"
                + "\n\n" + "via PLOM App";
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
        notificationReceiver = null;
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

