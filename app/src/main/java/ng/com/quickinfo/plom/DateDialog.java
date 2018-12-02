package ng.com.quickinfo.plom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;


public class DateDialog extends DialogFragment {
        public AutoCompleteTextView dateView;
        Context mContext;
        int id;
        //Listener
        DateDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);


            mContext = getActivity();


            //get the R.id of the textview to set the date from args
            //id = getArguments().getInt("key");
            //dateView = getActivity().findViewById(id);

            return new DatePickerDialog(mContext, this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {

            populateSetDate(yy, mm+1, dd);
        }

        public void populateSetDate(int year, int month, int day) {

            mListener.onDateClick(stringToDate(month+"/"+day+"/"+year));
        }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DateDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    //*********** interface ********
    // *****
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub

    }
}
