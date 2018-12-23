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
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Model.User;

import static ng.com.quickinfo.plom.Utils.Utilities.log;


public class DeleteDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    DeleteDialogListener mListener;
    @BindView(R.id.tvDeleteText)
    TextView tvDeleteText;
    @BindView(R.id.dlgpositive)
    TextView dlgpositive;
    @BindView(R.id.dlgcancel)
    TextView dlgcancel;
    Unbinder unbinder;
    @BindView(R.id.dlgllContent)
    LinearLayout dlgllContent;
    @BindView(R.id.dlgllProgress)
    LinearLayout dlgllProgress;

    //action
    String action;
    User user;

    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);

        action = getArguments().getString("action", "");


        //butterknife
        unbinder = ButterKnife.bind(this, view);

        builder.setView(view);

        if(action == HomeActivity.userDeleteAction){
            String message = "All data of user will be deleted from database. Are you sure you want to delete?";
            tvDeleteText.setText(message);
            this.setCancelable(false);
        }
        return builder.create();
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DeleteDialogListener");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dlgpositive, R.id.dlgcancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dlgpositive:
                if(action == HomeActivity.userDeleteAction){
                    deleteUser();

                }else{
            mListener.onDialogPositiveClick(DeleteDialog.this, R.string.action_delete);}
                break;
            case R.id.dlgcancel:
                this.dismiss();
                break;
        }
    }

    private void deleteUser() {
        dlgllContent.setVisibility(View.GONE);
        dlgllProgress.setVisibility(View.VISIBLE);
        log(this.getTag(), "threading" );
        try{ Thread.sleep(2000);}catch (InterruptedException e){

        }
        mListener.onDialogPositiveClick(this, R.id.tvDeleteAccount);
    }


    //*********** interface *************
    public interface DeleteDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int action);

        public void onDialogNegativeClick(DialogFragment dialog, int action);
    }
}
