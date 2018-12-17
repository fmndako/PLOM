package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Model.Offset;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;


public class SignupDialog extends DialogFragment {
    //context
    Context mContext;

    // Use this instance of the interface to deliver action events
    SignupDialogListener mListener;


    String mAction;

    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.signup_layout, null);

        //butterknife
        ButterKnife.bind(this, view);

        //get from args
        Bundle bundle = getArguments();
        //mAction = bundle.getString("action");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        return builder.create();
    }

    private void getValues(){

    }

    // Override the Fragment.onAttach() method to instantiate the OffsetDialogListener
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the OffsetDialogListener so we can send events to the host
            mListener = (SignupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement OffsetDialogListener");
        }
    }




    //*********** interface *************
    public interface SignupDialogListener {
        public void onSignUp(DialogFragment dialog, User user);

    }
}
