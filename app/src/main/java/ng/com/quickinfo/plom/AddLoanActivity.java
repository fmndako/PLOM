package ng.com.quickinfo.plom;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customfonts.MyEditText;
import customfonts.MyTextView;
import ng.com.quickinfo.plom.Model.Loan;
import ng.com.quickinfo.plom.Utils.DateInputMask;
import ng.com.quickinfo.plom.ViewModel.LoanViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.dateToString;
import static ng.com.quickinfo.plom.Utils.Utilities.log;
import static ng.com.quickinfo.plom.Utils.Utilities.makeToast;

public class AddLoanActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_add_loan);
        ButterKnife.bind(this);

        //contxt
        mContext = getApplicationContext();
        mLoanViewModel = ViewModelProviders.of(this).get(LoanViewModel.class);

        //set Date Input Mask
        new DateInputMask(actvDatePromised);
        new DateInputMask(actvDateTaken);
        actvDateTaken.setText(dateToString(Calendar.getInstance().getTime()));
        actvDatePromised.setText(dateToString(Calendar.getInstance().getTime()));


        //action type check either add or update
        loan_id = getIntent().getLongExtra("loan_id", -1);


        actvDateTaken.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){

                }
                else{
                    setArgs(R.id.actvDateTaken);
                }
            }
        });

        actvDatePromised.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){

                }
                else{
                    setArgs(R.id.actvDatePromised);
                }
            }
        });
        //setSpinner();//not needed
        if (loan_id != -1) {
            // loan data observer
            mLoanViewModel.getLoan(loan_id).observe(this,
                    new Observer<Loan>()
                    {
                        @Override
                        public void onChanged(@Nullable final Loan loan) {
                            // Update the cached copy of the loans in the adapter.

                            updateUI(loan);
                        }
                    });

        }
    }

    private void updateUI(Loan loan){
        //update UI from addreceiver intent
        actvName.setText(loan.getName());
        actvNumber.setText(loan.getNumber());
        actvEmail.setText(loan.getEmail());
        actvAmount.setText(loan.getAmount()+ "");
        spLoanType.setSelection(loan.getLoanType());
        actvDateTaken.setText(dateToString(loan.getDateTaken()));
        actvDatePromised.setText(dateToString(loan.getDateToRepay()));
        spRepaymentOption.setSelection(loan.getRepaymentOption());
        if (loan.getNotify()!= 0){
        cbNotify.setChecked(true);}
        actvRemarks.setText(loan.getRemarks());
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
                //makeToast(mContext, "item selected spinner" + i + l);

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
                setArgs(R.id.actvDateTaken);
                break;
            case R.id.actvDatePromised:
                setArgs(R.id.actvDatePromised);
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

    private void setArgs(int actvDateTaken) {
        Bundle args1 = new Bundle();
        args1.putInt("key", R.id.actvDateTaken);
        pickDate(args1);
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
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Email.DATA
                };


                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = this.getContentResolver()
                        .query(contactUri, projection,null, null, null);

                // .query(contactUri, projection, null, null, null);

                // Retrieve the phone number from the NUMBER column
                //int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String number = "";
                String name = "";
                String email = "";


                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                // Do something with the phone number
                actvNumber.setText(number);
                actvName.setText(name);
                //actvEmail.setText(email);

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
            else if (TextUtils.isEmpty(actvDateTaken.getText())){
                actvDateTaken.setError("Most not be empty");
                actvDateTaken.hasFocus();
            } else if (TextUtils.isEmpty(actvDatePromised.getText())){
                actvDatePromised.setError("Most not be empty");
                actvDatePromised.hasFocus();
            }


        }

        else {
            //get values from view
            String name = actvName.getText().toString();
            String number = actvNumber.getText().toString();
            String email = actvEmail.getText().toString();
            String dateTaken = actvDateTaken.getText().toString();
            String dateToRepay = actvDatePromised.getText().toString();
            String remarks = actvRemarks.getText().toString();
            int amount = Integer.valueOf(actvAmount.getText().toString());
            int notify = 0;
            if (cbNotify.isChecked()) {
                notify = 1;
            }
            int loan_type = spLoanType.getSelectedItemPosition();

            int repayment_option = spRepaymentOption.getSelectedItemPosition();

//            Loan loan = new Loan(name, number,email, amount, dateTaken, dateToRepay, loan_type,
//                    remarks, 0, 0, notify, repayment_option, -1);
//

            log(TAG, dateTaken + ": " + dateToRepay);
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



    //******************** AddLoanReceiver ********************
    public class AddLoanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


         makeToast(mContext, "intent received");
        }
    }

}

