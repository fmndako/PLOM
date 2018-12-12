package ng.com.quickinfo.plom.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ng.com.quickinfo.plom.AddLoanActivity;
import ng.com.quickinfo.plom.DetailActivity;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static android.content.Context.MODE_PRIVATE;

public class Utilities {
    /*contains basic tools*/

    public static Intent HomeIntent(Context mContext, Class mClass, String email){
        Intent intent = new Intent(mContext, mClass);
        intent.putExtra("email", email);
        return intent;

    }
    public static Date stringToDate(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
            //TODO parse in today as default date taken
            date = Calendar.getInstance().getTime();
        }
        return date;
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        return strDate;

    }

    public static String dateToString1(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd yyyy");
        String strDate = dateFormat.format(date);
        return strDate;

    }

    public static void makeToast(Context context, String string){
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    public static void log(String TAG, String text){
        Log.d(TAG, text);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final ProgressBar mRegisterProgress, Context mContext) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);
//            mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mSignInView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegisterProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            //mSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    //shared preference
    public static class MyPref{
        private static SharedPreferences sharedPref;
        private static  SharedPreferences.Editor editor;


        public static SharedPreferences getSharedPref(Context context){
            sharedPref = context.getSharedPreferences("myPref", MODE_PRIVATE);
            editor = sharedPref.edit();
            return sharedPref;
        }

        public static SharedPreferences.Editor getEditor(){
            return editor;
        }



    }

    // *************************   get user async task ***************
    public static Loan intentToLoan(Intent intent){
        String name, number, email, date_taken, date_promised, date_cleared, remarks;
        int amount, loan_type, repayment_option, notify, offset, clearStatus;
        long user_id, loan_id;
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        email= intent.getStringExtra("email");
        amount= intent.getIntExtra("amount",0);
        loan_type= intent.getIntExtra("loan_type", 0);
        date_taken = intent.getStringExtra("date_taken");
        date_promised = intent.getStringExtra("date_promised");
        repayment_option = intent.getIntExtra("repayment_option", 0);
        notify = intent.getIntExtra("notify", 0);
        remarks = intent.getStringExtra("remarks");
        offset = intent.getIntExtra("offset", 0);
        clearStatus = intent.getIntExtra("clearStatus", 0);
        user_id = intent.getLongExtra("user_id", -1);
        loan_id = intent.getLongExtra("loan_id", -1);
        date_cleared = intent.getStringExtra("date_cleared");


        Loan loan = new Loan(name, number, email, amount, stringToDate(date_taken), stringToDate(date_promised), loan_type,
                remarks, clearStatus,offset, notify,repayment_option, user_id);
        if (loan_id != -1){
            loan.setId(loan_id);
        }
        if (clearStatus != 0){
            loan.setClearedStatus(stringToDate(date_cleared));
        }
        return loan;
    }
}
