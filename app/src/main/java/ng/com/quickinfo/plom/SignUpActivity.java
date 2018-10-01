package ng.com.quickinfo.plom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signUpBtn)
    Button signUpBtn;
    @BindView(R.id.fullName)
    EditText fullName;
    @BindView(R.id.userEmailId)
    EditText userEmailId;
    @BindView(R.id.mobileNumber)
    EditText mobileNumber;
    @BindView(R.id.location)
    EditText location;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;
    @BindView(R.id.terms_conditions)
    CheckBox termsConditions;
    @BindView(R.id.already_user)
    TextView alreadyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        //set email
        userEmailId.setText(getIntent().getStringExtra("email"));


    }


    @OnClick(R.id.signUpBtn)
    public void onViewClicked() {

        //intent to return back to main Activity
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(fullName.getText())) {
            fullName.setError("Enter Fullname");
            fullName.hasFocus();
        } else {
            String username = fullName.getText().toString();
            replyIntent.putExtra("user", username);
            replyIntent.putExtra("email", userEmailId.getText().toString());

            setResult(RESULT_OK, replyIntent);
            finish();
        }

    }


}
