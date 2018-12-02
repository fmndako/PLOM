package ng.com.quickinfo.plom;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

public class AddLoanActivity extends AppCompatActivity {

    //for activity
    public static final String EXTRA_REPLY =
            "ng.com.quickinfo.plom.REPLY";
    private String TAG = getClass().getSimpleName();

    @BindView(R.id.actvName)
    AutoCompleteTextView actvName;
    @BindView(R.id.actvNumber)
    AutoCompleteTextView actvNumber;
    @BindView(R.id.actvAmount)
    AutoCompleteTextView actvAmount;
    @BindView(R.id.actvDateTaken)
    AutoCompleteTextView actvDateTaken;
    @BindView(R.id.actvDatePromised)
    AutoCompleteTextView actvDatePromised;
    @BindView(R.id.spLoanType)
    Spinner spLoanType;
    @BindView(R.id.actvRemarks)
    AutoCompleteTextView actvRemarks;
    @BindView(R.id.cbSetReminder)
    CheckBox cbSetReminder;
    @BindView(R.id.signUpBtn)
    Button signUpBtn;

    //contect
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loan_fragment_layout);
        ButterKnife.bind(this);

        //contxt
        mContext = getApplicationContext();

        //action type check either add or update
        long loan_id = getIntent().getLongExtra("loan_id", 0);

        if (loan_id != 0){
            updateAction(loan_id);

        }



    }

    // ****************update Action **************
    private void updateAction(long id){
        //TODO get loan details
        Utilities.makeToast(mContext, "updateAction");
        //set UI according
        //button

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
        dateFragment.show(getSupportFragmentManager(), "Date Picker");

    }
    public void setSpinner(Context context) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spLoanType.setAdapter(adapter);


    }


    @OnClick({R.id.actvName, R.id.actvDateTaken, R.id.actvDatePromised, R.id.cbSetReminder, R.id.signUpBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.actvName:
                //picks contact
                //pickContact(view);
                break;
            case R.id.actvDateTaken:
                break;
            case R.id.actvDatePromised:
                break;
            case R.id.cbSetReminder:
                break;
            case R.id.signUpBtn:
                getViewData();
                break;
        }
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
                Cursor cursor = this.getContentResolver()
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
        String loanType = spLoanType.toString();
        //get userId long userID =  mUserID;
        if (cbSetReminder.isChecked()){
            //get reminder parameters
            //add to data setreminder database
            //get userid or loanId
        }

        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(actvName.getText())) {
            actvName.setError("Most not be empty");
            actvName.hasFocus();
            //setResult(RESULT_CANCELED, replyIntent);
            Utilities.log(TAG,loanType + ":" + dateToRepay );
        } else {
            replyIntent.putExtra(EXTRA_REPLY, name);
            replyIntent.putExtra("loanType", remarks.length());
            replyIntent.putExtra("dateToRepay", dateToRepay);
            setResult(RESULT_OK, replyIntent);
            finish();
        }

        //Loan loan = new Loan(name, number, email, amount, dateTaken);

    }

}

