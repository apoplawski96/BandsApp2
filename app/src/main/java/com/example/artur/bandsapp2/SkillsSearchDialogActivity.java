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

public class SkillsSearchDialogActivity extends AppCompatActivity {

    Button addSkillsButton;
    Button submitSkills;
    int counter;
    HashMap<String,String> skills;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_search_dialog);

        addSkillsButton = (Button) findViewById(R.id.addSkillsButton);
        submitSkills = (Button) findViewById(R.id.submitSkills);
        skills = new HashMap<>();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        counter = 1;

        addSkillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(SkillsSearchDialogActivity.this, "Search...", "Add your music genres!",
                        null, skillsData(), new SearchResultListener<SearchModel>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchModel searchable, int i) {
                        Toast.makeText(SkillsSearchDialogActivity.this, searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        skills.put("Skills " + Integer.toString(counter), searchable.getTitle());
                        counter++;
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        submitSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Users").child(userID).child("Skills").setValue(skills);
                mDatabaseRef.child("Users").child(userID).child("How many skills").setValue(Integer.toString(counter-1));
                Toast.makeText(SkillsSearchDialogActivity.this, "Skills submitted to your profile!", Toast.LENGTH_SHORT).show();
                changeActivity();
            }
        });


    }

    ArrayList<SearchModel> skillsData(){
        ArrayList<SearchModel> skills = new ArrayList<>();
        skills.add(new SearchModel("Vocals"));
        skills.add(new SearchModel("Backing vocals"));
        skills.add(new SearchModel("Songwriting"));
        skills.add(new SearchModel("Producing"));
        skills.add(new SearchModel("Lyrics"));
        skills.add(new SearchModel("Mastering"));
        skills.add(new SearchModel("Ghost writing"));

        return skills;
    }

    void changeActivity(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ifReady = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                if (ifReady.equals("Yes")){
                    startActivity(new Intent(SkillsSearchDialogActivity.this, ProfileViewActivity.class));
                } else{
                    startActivity(new Intent(SkillsSearchDialogActivity.this, BandsSearchDialogActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
