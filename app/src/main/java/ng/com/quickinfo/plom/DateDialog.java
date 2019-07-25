package ng.com.quickinfo.plom;
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.widget.AutoCompleteTextView;
//import android.widget.DatePicker;
//
//import java.util.Calendar;
//import java.util.Date;
//
//import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;
//
//
//public class DateDialog extends DialogFragment {
//        public AutoCompleteTextView dateView;
//        Context mContext;
//        int id;
//        //Listener
//
//    @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar calendar = Calendar.getInstance();
//            int yy = calendar.get(Calendar.YEAR);
//            int mm = calendar.get(Calendar.MONTH);
//            int dd = calendar.get(Calendar.DAY_OF_MONTH);
//
//
//            mContext = getActivity();
//
//
//            //get the R.id of the textview to set the date from args
//            //id = getArguments().getInt("key");
//            //dateView = getActivity().findViewById(id);
//
//            return new DatePickerDialog(mContext, (DatePickerDialog.OnDateSetListener) getActivity(), yy, mm, dd);
//        }
//
//
//
//
//
//}
//
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import customfonts.MyEditText;

import static ng.com.quickinfo.plom.Utils.Utilities.log;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public MyEditText dateView;
    Context context;
    int id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        context = getActivity();

        //get the R.id of the textview to set the date from args
        id = getArguments().getInt("key");



        return new DatePickerDialog(context, this, yy, mm, dd);


    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }

    // populate date
    public void populateSetDate(int year, int month, int day) {
        String date = singleToDoubleDigit(day)+"/"+ singleToDoubleDigit(month)+"/"+year;
        dateView = getActivity().findViewById(id);
        if (dateView == null){
            log("DateDialog", "dateview null");
            sendResults(date);
            }
        else{

            dateView.setText(date);

            }
         }

    //returning activity on result
    private void sendResults(String date) {

        if(getTargetFragment() == null){
            return;
        }
       Intent intent = newIntent(date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

    }

    //intent to return activity on result
    public static Intent newIntent(String message){
        Intent intent = new Intent();
        intent.putExtra(OffsetDialog.SELECTED_DATE, message);
        return intent;


    }
    //static instantiate datedialog
    public static DateDialog getInstance(){
        DateDialog frag = new DateDialog();
        return frag;

    }

    public String singleToDoubleDigit(int value){
        String valueString = String.valueOf(value);
        if (valueString.length() == 1){
            valueString = "0"+valueString;
        }
        return valueString;
    }

 public void fragmentDate(){

 }


}
