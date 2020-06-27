package com.example.wayezi_onetoonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Button SendVerificationCodeButton,VerifyBtn;
    private EditText InputPhoneNumber, InputVerificationCode,InputName;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private Spinner spinner;


    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CurrentUserID = mAuth.getCurrentUser().getUid();

        SendVerificationCodeButton = (Button)findViewById(R.id.send_ver_code_button);
        VerifyBtn = (Button)findViewById(R.id.Verify_button);
        InputPhoneNumber = (EditText)findViewById(R.id.Phone_number_input);
        InputVerificationCode = (EditText)findViewById(R.id.Verification_code_input);
        InputName=(EditText)findViewById(R.id.name_input);
        mAuth = FirebaseAuth.getInstance();
        LoadingBar = new ProgressDialog(this);
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String PhoneNumber = "+" + code + InputPhoneNumber.getText().toString();
                String nameInput=InputName.getText().toString();
                if(TextUtils.isEmpty(nameInput))
                {
                    Toast.makeText(MainActivity.this," Name is Required",Toast.LENGTH_SHORT).show();

                }

                if(TextUtils.isEmpty(PhoneNumber))
                {
                    Toast.makeText(MainActivity.this," Phone Number is Required",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LoadingBar.setTitle("Phone Verification");
                    LoadingBar.setMessage("Please Wait...while we are authenticating your Phone");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            PhoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            MainActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });
        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                String VerificationCode = InputVerificationCode.getText().toString();
                if(TextUtils.isEmpty(VerificationCode))
                {
                    Toast.makeText(MainActivity.this,"Please Enter Verification Code",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    LoadingBar.setTitle("Code Verification");
                    LoadingBar.setMessage("Please wait,while we are verifying your code");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, VerificationCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });




        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                Toast.makeText(MainActivity.this,"Invalid , Please enter correct phone number",Toast.LENGTH_SHORT).show();

                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);

                VerifyBtn.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(MainActivity.this," Code has been sent, Please check and verify",Toast.LENGTH_SHORT).show();


                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                VerifyBtn.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);


            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            LoadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            senduserToHomeActivity();


                        }
                        else
                        {
                            String Message = task.getException().toString();
                            Toast.makeText(MainActivity.this,"Error : " + Message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void senduserToHomeActivity()
    {
        String phoneNumber = InputPhoneNumber.getText().toString();
        String name = InputName.getText().toString();
        Intent HomeIntent = new Intent(MainActivity.this,HomeActivity.class);
        HomeIntent.putExtra("number",phoneNumber);
        HomeIntent.putExtra("name",name);
        startActivity(HomeIntent);
        finish();
    }





}