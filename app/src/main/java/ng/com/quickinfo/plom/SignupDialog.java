package ng.com.quickinfo.plom;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.util.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ng.com.quickinfo.plom.Model.User;
import ng.com.quickinfo.plom.Utils.Utilities;
import ng.com.quickinfo.plom.ViewModel.UserViewModel;

import static ng.com.quickinfo.plom.Utils.Utilities.log;


public class SignupDialog extends DialogFragment {
    //context
    Context mContext;

    // Use this instance of the interface to deliver action events
    SignupDialogListener mListener;

    //View Model
    UserViewModel userViewModel;


    String mAction;

    Unbinder unbinder;
    @BindView(R.id.signupemail)
    EditText signupemail;
    @BindView(R.id.signupuser)
    EditText signupuser;
    @BindView(R.id.signuppass)
    EditText signuppass;
    @BindView(R.id.signupconfirmpass)
    EditText signupconfirmpass;
    @BindView(R.id.signupmob)
    EditText signupnumber;
    @BindView(R.id.signupsignup)
    TextView signupsignup;
    @BindView(R.id.signuplogin)
    TextView signuplogin;




    //email
    String mEmail;
    //Override on create
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        //get from args
        Bundle bundle = getArguments();
        mAction = bundle.getString("action");
        if (mAction != "username") {

        }
        View view = inflater.inflate(R.layout.signup_layout, null);
        //butterknife
        builder.setView(view);
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        ButterKnife.bind(this, view);
        //TODO set on text listenenr for confrim password
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        setTextListener();

        return builder.create();
    }

    private void setTextListener() {

        signupconfirmpass.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!s.toString().equals(signuppass.getText().toString())){
                    signupconfirmpass.setError("Passwords do not match");

                }
            }
        });
        signuppass.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                log("signup frag", s + "" );
                String confirmpass = signupconfirmpass.getText().toString();
                String pass = signuppass.getText().toString();
                if( (!confirmpass.isEmpty())  && (!s.toString().equals(confirmpass))){
                    signupconfirmpass.setError("Passwords do not match");

                }
            }
        });
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
                    + " must implement SignupDialogListener");
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    private void attemptGoogleSignUp() {
        User user = new User("", "", mEmail, "");
        mListener.onSignUp(this, user);
    }

    @OnClick({ R.id.signupsignup, R.id.signuplogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signupsignup:
                attemptSignUp();
                break;
            case R.id.signuplogin:
                dismiss();
                break;
        }
    }

    private void attemptSignUp() {

        // Reset errors.
        signupuser.setError(null);
        signuppass.setError(null);
        signupemail.setError(null);
        
        // Store values at the time of the login attempt.
        String user = signupuser.getText().toString();
        String password = signuppass.getText().toString();
        String email = signupemail.getText().toString();
        boolean cancel = false;
        View focusView = null;


        //check useremail availability
        if(!isEmailAvailable(email)){
            log("SignUpDlg", "isemailAvailble");
            signupemail.setError("Email address already exists");
            focusView = signupemail;
            cancel = true;

        }else if (!isUserAvailable(user)){
            log("SignUpDlg", "isuserAvailble");

            signupuser.setError("Username already exists");
            focusView = signupuser;
            cancel = true;

        }
        //check email validity
        if(!TextUtils.isEmpty(email) && !isEmailValid(email)){
            signupemail.setError("Enter Valid Email Address");
            focusView = signupemail;
            cancel = true;


        }




        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            signuppass.setError(getString(R.string.error_invalid_password));
            focusView = signuppass;
            cancel = true;
        }

        // Check for a valid user address.
        if (TextUtils.isEmpty(user)) {
            signupuser.setError(getString(R.string.error_field_required));
            focusView = signupuser;
            cancel = true;
        } // Check for a valid user address.
        else if (TextUtils.isEmpty(password)) {
            signuppass.setError(getString(R.string.error_field_required));
            focusView = signuppass;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            registerUser(user, password);
            //showProgress(true);

        }
    }

    private void registerUser(String mUser, String password) {
        User user = new User(mUser,signupnumber.getText().toString(),
                signupemail.getText().toString(), password);
        mListener.onSignUp(this, user);
    }

    private boolean isEmailAvailable(String email) {
        User user = userViewModel.getUserByEmail(email).getValue();
        if (user!=null) {
            return false;
        }
        return true;
    }

    private boolean isUserAvailable(String name) {
        User user = userViewModel.getUserByName(name).getValue();
        if (user!=null) {
            return false;
        }
        return true;
    }
    private boolean isPasswordValid(String password) {
        return (password.length() > 3) && password.equals(
                signupconfirmpass.getText().toString()) ;
    }

    private boolean isEmailValid(String email) {
        return (email.length() > 3) && email.contains("@") ;
    }
    //*********** interface *************
    public interface SignupDialogListener {
        public void onSignUp(DialogFragment dialog, User user);


    }
}
