package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransitionActivity extends AppCompatActivity {

    //Others
    ProgressDialog progressDialog;
    String userID;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    DatabaseReference lastActivityRef;
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        //others
        progressDialog = new ProgressDialog(this);
        //Displayed progressDialog
        progressDialog.setMessage("Wait a second...");
        progressDialog.show();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        lastActivityRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Previous Activity");
        userID = user.getUid();



        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean ifCreated = dataSnapshot.child("Users").hasChild(user.getUid());
                String readyToGo;
                if (ifCreated){
                    readyToGo = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                    //We check if profile is full, if yes, we go to main provileviewactivity
                    if (readyToGo.equals("Yes")){
                        Toast.makeText(TransitionActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileViewActivity.class));
                    } else{
                        Toast.makeText(TransitionActivity.this, "Finish filling your profile info", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), InitializeBasicInfoActivity.class));
                    }
                } else {
                    mDatabaseRef.child("Users").child(userID).child("Email").setValue(user.getEmail());
                    mDatabaseRef.child("Users").child(userID).child("Ready to go").setValue("no");
                    Toast.makeText(TransitionActivity.this, "Create your profile!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent (getApplicationContext(), InitializeBasicInfoActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();

    }



}
