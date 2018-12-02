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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class OffsetDialog extends DialogFragment {
    //context
    Context mContext;
    // Use this instance of the interface to deliver action events
    OffsetDialogListener mListener;
    @BindView(R.id.etOffsetAmount)
    EditText etOffsetAmount;
    @BindView(R.id.etOffsetDate)
    EditText etOffsetDate;
    @BindView(R.id.etOffsetRemarks)
    EditText etOffsetRemarks;
    Unbinder unbinder;

    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.offset_dialog, null))
                .setPositiveButton(R.string.action_offset, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(OffsetDialog.this, R.string.action_offset);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(OffsetDialog.this, R.string.action_offset);
                    }
                });
        return builder.create();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.etOffsetDate)
    public void onViewClicked() {
        Bundle args = new Bundle();
        args.putInt("key", R.id.actvDatePromised);
        pickDate(args);

    }

    //date
    public void pickDate(Bundle args){
        DialogFragment dateFragment = new DateDialog();
        dateFragment.setArguments(args);
        dateFragment.show(getFragmentManager(), "DatePicker");

    }


    //*********** interface *************
    public interface OffsetDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int action);

        public void onDialogNegativeClick(DialogFragment dialog, int action);
    }
}
