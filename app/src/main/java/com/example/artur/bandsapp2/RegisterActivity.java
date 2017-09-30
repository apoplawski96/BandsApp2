package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //Widgets
    EditText emailRegister;
    EditText passwordRegister;
    TextView goBackToLogin;
    Button buttonRegister;

    //Others
    ProgressDialog progressDialog;

    //Firebase stuff
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //Checking if the user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), TransitionActivity.class));
        }

        emailRegister = (EditText) findViewById(R.id.emailLogin);
        passwordRegister = (EditText) findViewById(R.id.passwordLogin);
        goBackToLogin = (TextView) findViewById(R.id.createAccount);
        buttonRegister = (Button) findViewById(R.id.signUpButton);


        //Go back to the LoginActivity
        goBackToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent (RegisterActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        //Button onClick that registers a user
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RegisterUser();
            }
        });

    }

    //Simple  toast that reminds user to fill the necessary fields
    public void EmptyFieldToast(){
        Toast.makeText(this, "Please fill all the necessary fields", Toast.LENGTH_SHORT).show();
        return;
    }

    //Function that does all the register user things
    private void RegisterUser(){
        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();

        //User errors check, empty email, empty password
        if (TextUtils.isEmpty(email)) EmptyFieldToast();
        if (TextUtils.isEmpty(password)) EmptyFieldToast();

        //If validations are ok
        progressDialog.setMessage("Registering you to the BandsApp family...");
        progressDialog.show();

        //Creating account using Firebase method + tracking completion by onCompleteListener
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //User is successfully registered and logged in
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    finish();
                    startActivity(new Intent(getApplicationContext(), TransitionActivity.class));
                } else{
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });


    }

}
