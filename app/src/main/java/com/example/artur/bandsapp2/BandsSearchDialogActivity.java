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

public class BandsSearchDialogActivity extends AppCompatActivity {

    Button addBandsButton;
    Button submitBands;
    int counter;
    HashMap<String,String> bands;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bands_search_dialog);

        addBandsButton = (Button) findViewById(R.id.addBandsButton);
        submitBands = (Button) findViewById(R.id.submitBands);
        bands = new HashMap<>();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        counter = 1;

        addBandsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(BandsSearchDialogActivity.this, "Search...", "Add your favourite bands!",
                        null, bandsData(), new SearchResultListener<SearchModel>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchModel searchable, int i) {
                        Toast.makeText(BandsSearchDialogActivity.this, searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        bands.put("Band " + Integer.toString(counter), searchable.getTitle());
                        counter++;
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        submitBands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter>3){
                    mDatabaseRef.child("Users").child(userID).child("Bands").setValue(bands);
                    mDatabaseRef.child("Users").child(userID).child("How many bands").setValue(Integer.toString(counter-1));
                    Toast.makeText(BandsSearchDialogActivity.this, "Bands submitted to your profile!", Toast.LENGTH_SHORT).show();
                    changeActivity();
                } else{
                    Toast.makeText(BandsSearchDialogActivity.this, "You need to select at least 3 bands, you selected " + (Integer.toString(counter-1))
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    ArrayList<SearchModel> bandsData(){
        ArrayList<SearchModel> bands = new ArrayList<>();
        bands.add(new SearchModel("Northlane"));
        bands.add(new SearchModel("The Contortionist"));
        bands.add(new SearchModel("Meshuggah"));
        bands.add(new SearchModel("Architects"));
        bands.add(new SearchModel("Not Enough"));
        bands.add(new SearchModel("Harmed"));
        bands.add(new SearchModel("Veil Of Maya"));

        return bands;

    }

    void changeActivity(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ifReady = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                if (ifReady.equals("Yes")){
                    startActivity(new Intent(BandsSearchDialogActivity.this, ProfileViewActivity.class));
                } else{
                    startActivity(new Intent(BandsSearchDialogActivity.this, AlbumsSearchDialogActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
