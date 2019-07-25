package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import customfonts.MyEditText;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static android.app.Activity.RESULT_OK;
import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;


public class OffsetDialog extends DialogFragment {
    //context
    Context mContext;
    LoanViewModel loanViewModel;
    // Use this instance of the interface to deliver action events
    OffsetDialogListener mListener;
    @BindView(R.id.etOffsetAmount)
    EditText etOffsetAmount;
    @BindView(R.id.etOffsetDate)
    MyEditText etOffsetDate;
    @BindView(R.id.etOffsetRemarks)
    EditText etOffsetRemarks;

    Unbinder unbinder;

    String mAction;
    @BindView(R.id.dlgoffset)
    TextView dlgoffset;
    @BindView(R.id.dlgcancel)
    TextView dlgcancel;

    public static final String SELECTED_DATE = "ng.com.quickinfo.plom.date_selected";
    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_offset, null);


        //butterknife
        unbinder = ButterKnife.bind(this, view);
        new DateInputMask(etOffsetDate);
        etOffsetDate.setText(dateToString(Calendar.getInstance().getTime()));
        etOffsetDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){

                }
                else{
                    setArgs(R.id.etOffsetDate);
                }
            }
        });
        //get from args
        Bundle bundle = getArguments();
        mAction = bundle.getString("action");
        this.setCancelable(false);
        //this.setFinishOnTouchOutside(false);

        if (mAction.equals(DetailActivity.offsetUpdateAction)) {
            //setUI
            etOffsetAmount.setText(bundle.getString("amount", ""));
            etOffsetRemarks.setText(bundle.getString("remarks", ""));
            etOffsetDate.setText(bundle.getString("date", ""));
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        return builder.create();
    }

    private Offset getValues(String amount) {


        return new Offset(Integer.valueOf(amount), stringToDate(etOffsetDate.getText().toString()), etOffsetRemarks.getText().toString(), 0);
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
        dateFragment.setTargetFragment(OffsetDialog.this, R.id.etOffsetDate);
        dateFragment.show(getFragmentManager(), "Offset DatePicker");

    }

    //on activity result from date dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == R.id.etOffsetDate && resultCode == RESULT_OK) {
            etOffsetDate.setText(intent.getStringExtra(SELECTED_DATE));

        }

    }
    // Override the Fragment.onAttach() method to instantiate the OffsetDialogListener
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the OffsetDialogListener so we can send events to the host
            mListener = (OffsetDialogListener) context;
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

    @OnClick(R.id.etOffsetDate)
    public void onViewClicked() {

    }

    public static OffsetDialog getInstance(){
        OffsetDialog frag = new OffsetDialog();
        return frag;

    }



    @OnClick({R.id.dlgoffset, R.id.dlgcancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlgoffset:
               //Send the positive button event back to the host activity
                    //add offset
                String amount =  etOffsetAmount.getText().toString();

                if (!amount.equals("")){
                    Offset offset = getValues(amount);
                    mListener.onDialogPositiveClick(OffsetDialog.this, offset, mAction);

                }else{ etOffsetAmount.setError("enter a valid input");}

               break;
            case R.id.dlgcancel:
                dismiss();
                break;
        }
    }


    //*********** interface *************
    public interface OffsetDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, Offset offset, String action);

        public void onDialogNegativeClick(DialogFragment dialog, int action);
    }



}

