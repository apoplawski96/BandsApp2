package com.example.artur.bandsapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class InstrumentsSearchDialogActivity extends AppCompatActivity {

    Button addInstrumentsButton;
    Button submitInstruments;
    int counter;
    HashMap<String,String> instruments;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments_search_dialog);

        addInstrumentsButton = (Button) findViewById(R.id.addInstrumentsButton);
        submitInstruments = (Button) findViewById(R.id.submitInstruments);
        instruments = new HashMap<>();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        counter = 1;

        addInstrumentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(InstrumentsSearchDialogActivity.this, "Search...", "Add your instruments!",
                        null, instrumentsData(), new SearchResultListener<SearchModel>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchModel searchable, int i) {
                        Toast.makeText(InstrumentsSearchDialogActivity.this, searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        instruments.put("Instrument " + Integer.toString(counter), searchable.getTitle());
                        counter++;
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        submitInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabaseRef.child("Siusiaki").setValue(genres);
                mDatabaseRef.child("Users").child(userID).child("Instruments").setValue(instruments);
                mDatabaseRef.child("Users").child(userID).child("How many instruments").setValue(Integer.toString(counter-1));
                Toast.makeText(InstrumentsSearchDialogActivity.this, "Instruments submitted to your profile!", Toast.LENGTH_SHORT).show();
                changeActivity();
            }
        });

    }

    ArrayList<SearchModel> instrumentsData(){
        ArrayList<SearchModel> instruments = new ArrayList<>();
        instruments.add(new SearchModel("Electric guitar"));
        instruments.add(new SearchModel("Acoustic guitar"));
        instruments.add(new SearchModel("Drums"));
        instruments.add(new SearchModel("Bass guitar"));
        instruments.add(new SearchModel("Cello"));
        instruments.add(new SearchModel("Violin"));
        instruments.add(new SearchModel("Harp"));

        return instruments;
    }

    void changeActivity(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ifReady = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                if (ifReady.equals("Yes")){
                    startActivity(new Intent(InstrumentsSearchDialogActivity.this, ProfileViewActivity.class));
                } else{
                    startActivity(new Intent(InstrumentsSearchDialogActivity.this, SkillsSearchDialogActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
