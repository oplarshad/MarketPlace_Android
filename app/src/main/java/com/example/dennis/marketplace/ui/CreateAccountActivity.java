package com.example.dennis.marketplace.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dennis.marketplace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.name;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = CreateAccountActivity.class.getSimpleName();

    private String mName;

    //Now we implement the Binds
    @Bind(R.id.edittextname)EditText editName;
    @Bind(R.id.editemail)EditText editEmail;
    @Bind(R.id.editpass)EditText editPassword;
    @Bind(R.id.editpass2)EditText editPassword2;
    @Bind(R.id.logInActivity)TextView login;
    @Bind(R.id.submit)Button myButton;

    //THis will be for creating the authentication
    private FirebaseAuth mAuth;
    //This is for creating the authentication listener
    private FirebaseAuth.AuthStateListener mAuthListener;
    //INcorporate progress dialog
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Implement the butterknife
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        //Now this will be the method for creating the authentication listener
        createAuthStateListener();

        //Lauch the method for progress dialog
        createAuthProgressDialog();

        //Lets create an onclick listener for text
        login.setOnClickListener(this);

        myButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        if (view == login){
            Intent intent = new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if (view == myButton){
            createNewUser();
        }
    }

    public void createNewUser(){
        final String mName = editName.getText().toString().trim();
        final String name = editName.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editPassword2.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(mName);
        boolean validPassword = isValidPassword(password, confirmPassword);
        if (!validEmail || !validName || !validPassword) return;

        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();
                if (task.isSuccessful()){
                    Log.d(TAG,"Authentication Successful");
                    createFirebaseUserProfile(task.getResult().getUser());
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Authentication Failed", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {

        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName)
                .build();

        user.updateProfile(addProfileName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getSupportActionBar().setTitle("Welcome, " + user.getDisplayName() + "!");
                        }
                    }

                });
    }
    //Now lets incorporate the progress dialog

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Registering you" + name);
        mAuthProgressDialog.setCancelable(false);
    }

    private void createAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

        };
    }
//Now lets check if the email and passwords are valid

    public boolean isValidEmail(String email){
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            editEmail.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            editName.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            editPassword.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            editPassword2.setError("Passwords do not match");
            return false;
        }
        return true;
    }
    //Now these methods will be for on create and on // STOPSHIP: 9/21/17
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


   }

}
