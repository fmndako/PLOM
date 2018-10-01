package ng.com.quickinfo.plom;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Model.Loan;

import static android.app.Activity.RESULT_OK;

public class AddLoanFragment extends Fragment {

    public static final String TAG = "AddLoanFragment";
    //create listener
    OnFragmentInteractionListener mListener;
    Context mContext;
    //datepicker
    //bundle for passing args
    private Bundle args;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    //butterknife assisted
    @BindView(R.id.spLoanType)
    Spinner planetsSpinner;
    Unbinder unbinder;
    //name and number field
    @BindView(R.id.actvName)
    AutoCompleteTextView actvName;
    @BindView(R.id.actvNumber)
    AutoCompleteTextView actvNumber;
    @BindView(R.id.actvAmount)

    //amount and date fields
    AutoCompleteTextView actvAmount;
    @BindView(R.id.actvDateTaken)
    AutoCompleteTextView actvDateTaken;
    @BindView(R.id.actvDatePromised)
    AutoCompleteTextView actvDatePromised;
    @BindView(R.id.actvRemarks)
    AutoCompleteTextView actvRemarks;
    @BindView(R.id.cbSetReminder)
    CheckBox cbSetReminder;
    @BindView(R.id.signUpBtn)
    Button signUpBtn;

    public AddLoanFragment() {
        //required empty constructor
    }

    public void setValidator(AutoCompleteTextView.Validator validator) {
        actvName.setValidator(validator);
    }

    public void performValidation() {
        actvName.performValidation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Fragment 1");
        }
        View view = inflater.inflate(R.layout.add_loan_fragment_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        mContext = getActivity();
        //set spinner
        setSpinner(getActivity());

        //set initial date
        setInitialDate();


        return view;
    }

    private void setInitialDate() {
        //set date to today
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        actvDateTaken.setText(dd +"/"+(mm+1)+"/"+yy);
        actvDatePromised.setText(dd +"/"+(mm+1)+"/"+yy);
        }

    //date
    public void pickDate(Bundle args){
        DialogFragment dateFragment = new DateDialog();
        dateFragment.setArguments(args);
        dateFragment.show(getFragmentManager(), "DatePicker");

    }
    public void setSpinner(Context context) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        planetsSpinner.setAdapter(adapter);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.actvName, R.id.actvDateTaken, R.id.actvDatePromised, R.id.cbSetReminder, R.id.signUpBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.actvName:
                pickContact(view);
                break;
            case R.id.actvDateTaken:
                args = new Bundle();
                args.putInt("key", R.id.actvDateTaken);
                pickDate(args);
                break;
            case R.id.actvDatePromised:
                args = new Bundle();
                args.putInt("key", R.id.actvDatePromised);
                pickDate(args);

                break;
            case R.id.cbSetReminder:

                break;
            case R.id.signUpBtn:
                addData();
                break;
        }
    }

    private void addData() {
        //add data to db
        getViewData();
    }


    //contacts name and number
    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                // Do something with the phone number
                actvNumber.setText(number);

            }
        }
    }

    public void getViewData() {
        //get values from view
        String name =  actvName.getText().toString();
        String number =  actvNumber.getText().toString();
        String email =  actvName.getText().toString();
        String dateTaken =  actvDateTaken.getText().toString();
        String dateToRepay =  actvDatePromised.getText().toString();
        String remarks =  actvRemarks.getText().toString();
        String amount = actvAmount.getText().toString();
        //get userId long userID =  mUserID;
        if (cbSetReminder.isChecked()){
            //get reminder parameters
            //add to data setreminder database
            //get userid or loanId
        }
        //Loan loan = new Loan(name, number, email, amount, dateTaken);

    }

    //fragment interaction
    public interface OnFragmentInteractionListener {
        // NOTE : We changed the Uri to String.
        void onFragmentInteraction(String title);
    }
}
