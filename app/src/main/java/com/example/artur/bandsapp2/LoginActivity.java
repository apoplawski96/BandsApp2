package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //Widgets
    EditText emailLogin;
    EditText passwordLogin;
    TextView createAccountLogin;
    Button buttonLogin;

    //ProgressBars
    ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    //Others
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //others
        progressDialog = new ProgressDialog(this);

        //Firebase things
        mAuth = FirebaseAuth.getInstance();

        //Widgets
        emailLogin = (EditText) findViewById(R.id.emailLogin);
        passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        createAccountLogin = (TextView) findViewById(R.id.createAccount);
        buttonLogin = (Button) findViewById(R.id.signUpButton);

        // AuthListener things
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            //here goes the most hardcoded fucking thing eva
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Signed in properly", Toast.LENGTH_SHORT).show();
                } else{
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(LoginActivity.this, "User is not signed in", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //Checking if the user is already logged in, if yes, we go to TransitionActivity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), TransitionActivity.class));
        }

        //Go to the RegisterActivity onClickListener
        createAccountLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent (LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });


        //Login onClickListener
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                UserLogin();
            }
        });

    }

    //Giving mAuth listener
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //Removing listener from mAuth
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Simple  toast that reminds user to fill the necessary fields
    public void EmptyFieldToast(){
        Toast.makeText(this, "Please fill all the necessary fields", Toast.LENGTH_SHORT).show();
        return;
    }

    //Login code
    private void UserLogin(){
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        //User errors check, empty email, empty password
        if (TextUtils.isEmpty(email)) EmptyFieldToast();
        if (TextUtils.isEmpty(password)) EmptyFieldToast();

        //If validations are ok
        progressDialog.setMessage("Login in progress...");
        progressDialog.show();

        //Signing in the user by Firebase method
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), TransitionActivity.class));
                        }

                    }
                });

    }

}
