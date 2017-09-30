package com.example.artur.bandsapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import ir.mirrajabi.searchdialog.core.Searchable;

public class GenresSearchDialogActivity extends AppCompatActivity {

    Button addGenresButton;
    Button submitGenres;
    int counter;
    HashMap<String,String> genres;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres_search_dialog);

        addGenresButton = (Button) findViewById(R.id.addGenresButton);
        submitGenres = (Button) findViewById(R.id.genresArray);
        genres = new HashMap<>();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        counter = 1;

        addGenresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(GenresSearchDialogActivity.this, "Search...", "Add your music genres!",
                        null, genresData(), new SearchResultListener<SearchModel>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchModel searchable, int i) {
                        if (genres.containsValue(searchable.getTitle())){
                            Toast.makeText(GenresSearchDialogActivity.this, "This genre is already added", Toast.LENGTH_SHORT).show();
                            baseSearchDialogCompat.dismiss();
                        }
                        else{
                            Toast.makeText(GenresSearchDialogActivity.this, searchable.getTitle(), Toast.LENGTH_SHORT).show();
                            genres.put("Genre " + Integer.toString(counter), searchable.getTitle());
                            counter++;
                            baseSearchDialogCompat.dismiss();
                        }
                    }
                }).show();
            }
        });

        submitGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("Users").child(userID).child("Genres").setValue(genres);
                mDatabaseRef.child("Users").child(userID).child("How many genres").setValue(Integer.toString(counter-1));
                Toast.makeText(GenresSearchDialogActivity.this, "Genres submitted to your profile!", Toast.LENGTH_SHORT).show();
                changeActivity();
            }
        });

    }

    ArrayList<SearchModel> genresData(){
        ArrayList<SearchModel> genres = new ArrayList<>();
        genres.add(new SearchModel("Ambient"));
        genres.add(new SearchModel("Blues"));
        genres.add(new SearchModel("Country"));
        genres.add(new SearchModel("Pop"));
        genres.add(new SearchModel("Metalcore"));
        genres.add(new SearchModel("Djent"));
        genres.add(new SearchModel("Instrumental"));

        return genres;
    }

    void changeActivity(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ifReady = dataSnapshot.child("Users").child(userID).child("Ready to go").getValue().toString();
                if (ifReady.equals("Yes")){
                    startActivity(new Intent(GenresSearchDialogActivity.this, ProfileViewActivity.class));
                } else{
                    startActivity(new Intent(GenresSearchDialogActivity.this, InstrumentsSearchDialogActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
