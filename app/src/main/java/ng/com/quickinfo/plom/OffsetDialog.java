package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
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
    EditText etOffsetDate;
    @BindView(R.id.etOffsetRemarks)
    EditText etOffsetRemarks;

    Unbinder unbinder;

    String mAction;
    @BindView(R.id.dlgoffset)
    TextView dlgoffset;
    @BindView(R.id.dlgcancel)
    TextView dlgcancel;

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

    private Offset getValues() {
        return new Offset(Integer.valueOf((etOffsetAmount.getText()).toString()), stringToDate(etOffsetDate.getText().toString()), etOffsetRemarks.getText().toString(), 0);
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





    @OnClick({R.id.dlgoffset, R.id.dlgcancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlgoffset:
               //Send the positive button event back to the host activity
                    //add offset
                    Offset offset = getValues();
                    mListener.onDialogPositiveClick(OffsetDialog.this, offset, mAction);

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
