package ng.com.quickinfo.plom;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyEditText;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Utils.DatabaseUtils;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;
import static ng.com.quickinfo.plom.Utils.Utilities.stringToDate;

public class AddLoanActivity extends AppCompatActivity {

    //for activity
    public static final String EXTRA_REPLY =
            "ng.com.quickinfo.plom.REPLY";
    public static final String LoanUpdateGetAction = "ng.com.quickinfo.plom.add_loan_activity_get_loan";

    //receivers

    AddReceiver myReceiver;
    IntentFilter myFilter;
    //database
    LoanViewModel mLoanViewModel;

    @BindView(R.id.actvName)
    MyEditText actvName;
    @BindView(R.id.actvNumber)
    MyEditText actvNumber;
    @BindView(R.id.actvEmail)
    MyEditText actvEmail;
    @BindView(R.id.actvAmount)
    MyEditText actvAmount;
    @BindView(R.id.actvDateTaken)
    MyEditText actvDateTaken;
    @BindView(R.id.actvDatePromised)
    MyEditText actvDatePromised;
    @BindView(R.id.spRepaymentOption)
    Spinner spRepaymentOption;
    @BindView(R.id.spLoanType)
    Spinner spLoanType;
    @BindView(R.id.cbNotify)
    CheckBox cbNotify;
    @BindView(R.id.signUpBtn)
    MyTextView signUpBtn;
    @BindView(R.id.actvRemarks)
    MyEditText actvRemarks;
    private String TAG = getClass().getSimpleName();

    //contect
    Context mContext;

    //action type
    long loan_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loan_activity);
        ButterKnife.bind(this);

        //contxt
        mContext = getApplicationContext();
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //instantiate receivers
        //create broadcast receivers
        myReceiver = new AddReceiver();
        myFilter = new IntentFilter(LoanUpdateGetAction);

        //set Date Input Mask
        new DateInputMask(actvDatePromised);
        new DateInputMask(actvDateTaken);

        //action type check either add or update
        loan_id = getIntent().getLongExtra("loan_id", -1);

        //setSpinner();//not needed
        if (loan_id != -1) {
            updateAction(loan_id);
        }
    }

    // ****************update Action **************
    private void updateAction(long id) {
        //get loan details
        DatabaseUtils.GetLoanAsyncTask task = new DatabaseUtils.GetLoanAsyncTask(mLoanViewModel,
                LoanUpdateGetAction);
        task.execute(id);
        //set UI according
        //button

    }

    private void updateUI(Intent intent){
        //update UI from addreceiver intent
        String name, number, email, date_taken, date_promised, remarks;
        int amount, loan_type, repayment_option, notify;
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        email= intent.getStringExtra("email");
        amount= intent.getIntExtra("amount",0);
        loan_type= intent.getIntExtra("loan_type", 0);
        date_taken = intent.getStringExtra("date_taken");
        date_promised = intent.getStringExtra("date_promised");
        repayment_option = intent.getIntExtra("repayment_option", 0);
        notify = intent.getIntExtra("notify", 0);
        remarks = intent.getStringExtra("remarks");


        actvName.setText(name);
        actvNumber.setText(number);
        actvEmail.setText(email);
        actvAmount.setText(amount +"");
        spLoanType.setSelection(loan_type);
        actvDateTaken.setText(date_taken);
        actvDatePromised.setText(date_promised);
        spRepaymentOption.setSelection(repayment_option);
        if (notify != 0){
        cbNotify.setChecked(true);}
        actvRemarks.setText(remarks);
        signUpBtn.setText(R.string.action_update);

    }

    private void setInitialDate() {
        //set date to today
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        actvDateTaken.setText(dd + "/" + (mm + 1) + "/" + yy);
        actvDatePromised.setText(dd + "/" + (mm + 1) + "/" + yy);
    }

    //date
    public void pickDate(Bundle args) {
        DialogFragment dateFragment = new DateDialog();
        dateFragment.setArguments(args);
        dateFragment.show(getSupportFragmentManager(), "Date Picker");

    }

    public void setSpinner() {

        spLoanType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                makeToast(mContext, "item selected spinner" + i + l);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//
//

    }




    @OnClick({R.id.actvName, R.id.ivContact, R.id.actvDateTaken, R.id.actvDatePromised, R.id.cbNotify, R.id.signUpBtn})
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
            case R.id.cbNotify:
                break;
            case R.id.signUpBtn:
                getViewData();

                break;
            case R.id.ivContact:
                pickContact(view);
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
                String[] proj2 = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = this.getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                Cursor cursor1 = this.getContentResolver()
                        .query(contactUri, proj2, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int columnName = cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String number = cursor.getString(column);
                String name = cursor.getString(columnName);


                // Do something with the phone number
                actvNumber.setText(number);
                actvName.setText(name);

            }
        }
    }

    public void getViewData() {

        Intent intent = new Intent();
        if (TextUtils.isEmpty(actvName.getText()) ||
                (TextUtils.isEmpty(actvAmount.getText())) ||
                TextUtils.isEmpty(actvDateTaken.getText())) {
            if(TextUtils.isEmpty(actvName.getText())){
                actvName.setError("Most not be empty");
                actvName.hasFocus();
            }else if (TextUtils.isEmpty(actvAmount.getText())){
                actvAmount.setError("Most not be empty");
                actvAmount.hasFocus();
            }
            else{
                actvDateTaken.setError("Most not be empty");
                actvDateTaken.hasFocus();
            }


        }

        else {
            //get values from view
            String name = actvName.getText().toString();
            String number = actvNumber.getText().toString();
            String email = actvEmail.getText().toString();
            Date dateTaken = stringToDate(actvDateTaken.getText().toString());
            Date dateToRepay = stringToDate(actvDatePromised.getText().toString());
            String remarks = actvRemarks.getText().toString();
            int amount = Integer.valueOf(actvAmount.getText().toString());
            int notify = 0;
            if (cbNotify.isChecked()) {
                notify = 1;
            }
            int loan_type = spLoanType.getSelectedItemPosition();

            int repayment_option = spRepaymentOption.getSelectedItemPosition();

            Loan loan = new Loan(name, number,email, amount, dateTaken, dateToRepay, loan_type,
                    remarks, 0, 0, notify, repayment_option, -1);


            log(TAG, "else setRasesult");
            intent.putExtra("name", name);
            intent.putExtra("number", number);
            intent.putExtra("email", email);
            intent.putExtra("amount", amount);
            intent.putExtra("loan_type", loan_type);
            intent.putExtra("date_taken", dateTaken);
            intent.putExtra("date_promised", dateToRepay);
            intent.putExtra("repayment_option", repayment_option);
            intent.putExtra("notify", notify);
            intent.putExtra("remarks", remarks);
            setResult(RESULT_OK, intent);
            finish();


        }
    }


    // *********** Register and unregister receivers
    //register receiver when app resumes and unregister when app pauses
    //register on create then unregister on Destroy
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, myFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregistering using local broadcast manager
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        //null the receivers to prevent ish
        myReceiver = null;
    }

    //******************** AddReceiver ********************
    public class AddReceiver extends BroadcastReceiver {
        //receives loan details for update
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
            log(TAG, "intent received");
            // updateUI();
        }
    }

}

