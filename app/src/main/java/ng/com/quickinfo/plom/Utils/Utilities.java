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
import ng.com.quickinfo.plom.Model.Loan;

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
            date = new SimpleDateFormat("E MMM dd yyyy").parse(dateString);

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




//    public static Intent GetIntent (Intent intent){String name = data.getStringExtra(AddLoanActivity.EXTRA_REPLY);
//
//    Integer amount = 33;
//        Integer loanType = data.getIntExtra("loanType", 0);
//        String remarks = "loan";
//        String number = "090";
//        Integer clearStatus =  0;
//        Integer offset = 1;
//        Integer notify = 0;
//        Integer repaymentOption = 0;
//        String email = "email";
//        Date dateTaken = stringToDate("11/11/1111");
//        Date dateToRepay = stringToDate(data.getStringExtra("dateToRepay"));
//        long user_id = mUserId;
//
//        Loan loan = new Loan(name, number, email, amount, dateTaken, dateToRepay, loanType,
//                remarks, clearStatus, offset, notify,repaymentOption, user_id);
//        mLoanViewModel.insert(loan);
//        makeToast(this, "loan saved");
//        return
//    }

}
