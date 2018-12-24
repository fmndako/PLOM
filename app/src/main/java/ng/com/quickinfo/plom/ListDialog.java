package ng.com.quickinfo.plom;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


public class ListDialog extends DialogFragment {


    //title is the string resource id of title
    int title;
    int items;
    ListDialogListener mListener;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get the R.id of the textview to set the date from args
        title = getArguments().getInt("title");
        items = getArguments().getInt("items");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                                     // of the selected item
                        if(mListener != null){
                            mListener.onItemSelected(ListDialog.this, title, which);
                        }
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ListDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    public interface ListDialogListener{

        //interface to handle selections
        void onItemSelected(DialogFragment dlg, int title, int position);
    }

}
