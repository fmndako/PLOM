package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static android.app.Activity.RESULT_OK;
import static ng.com.quickinfo.plom.OffsetDialog.SELECTED_DATE;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;


public class ClearAllDialog extends DialogFragment {
    //context
    Context mContext;
    LoanViewModel loanViewModel;
    // Use this instance of the interface to deliver action events
    ClearAllDialogListener mListener;

    @BindView(R.id.etClearDate)
    EditText etClearDate;

    @BindView(R.id.dlgpositive)
    TextView dlgpositive;
    @BindView(R.id.dlgcancel)
    TextView dlgcancel;
    Unbinder unbinder;

    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_clear_loan, null);

        //butterknife
        unbinder = ButterKnife.bind(this, view);
//        //Input mask
//        new DateInputMask(etClearDate);
        etClearDate.setText(dateToString(Calendar.getInstance().getTime()));
        //focus listener
        etClearDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){

                }
                else{
                    setArgs(R.id.etClearDate);
                }
            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        return builder.create();
    }


    //date editview
    private void setArgs(int actv) {
        Bundle args1 = new Bundle();
        args1.putInt("key", actv);
        pickDate(args1);
    }
    //date
    public void pickDate(Bundle args) {
        DialogFragment dateFragment = DateDialog.getInstance();
        dateFragment.setArguments(args);
        dateFragment.setTargetFragment(ClearAllDialog.this, R.id.etClearDate);
        dateFragment.show(getFragmentManager(), "Offset DatePicker");

    }

    //on activity result from date dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == R.id.etOffsetDate && resultCode == RESULT_OK) {
            etClearDate.setText(intent.getStringExtra(SELECTED_DATE));

        }

    }



    private Date getValues(){
        return stringToDate(etClearDate.getText().toString());
    }



    // Override the Fragment.onAttach() method to instantiate the OffsetDialogListener
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the OffsetDialogListener so we can send events to the host
            mListener = (ClearAllDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(mContext.toString()
                    + " must implement OffsetDialogListener");
        }
    }


//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//    }

    @OnClick(R.id.etClearDate)
    public void onViewClicked() {

    }

    @OnClick({R.id.dlgpositive, R.id.dlgcancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlgpositive:
                //Send the positive button event back to the host activity
                //add offset
                Date date = getValues();
                mListener.onDialogPositiveClick(ClearAllDialog.this, date);

                break;
            case R.id.dlgcancel:
                dismiss();
                break;
        }
    }


    //*********** interface *************
    public interface ClearAllDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Date date);

        public void onDialogNegativeClick(DialogFragment dialog, int action);
    }
}
