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

            return new DatePickerDialog(mContext, listethis, yy, mm, dd);
        }





}
