package com.example.artur.bandsapp2;

import android.content.Intent;
import android.provider.ContactsContract;
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

public class AlbumsSearchDialogActivity extends AppCompatActivity {

    Button addAlbumButton;
    Button submitAlbums;
    int counter;
    HashMap<String,String> albums;

    //Firebase stuff
    DatabaseReference mDatabaseRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_search_dialog);

        addAlbumButton = (Button) findViewById(R.id.addGenresButton);
        submitAlbums = (Button) findViewById(R.id.genresArray);
        albums = new HashMap<>();

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        counter = 1;

        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(AlbumsSearchDialogActivity.this, "Search...", "Add your favourite albums",
                        null, albumsData(), new SearchResultListener<SearchModel>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, SearchModel searchable, int i) {
                        if (albums.containsValue(searchable.getTitle())){
                            Toast.makeText(AlbumsSearchDialogActivity.this, "This album is already added", Toast.LENGTH_SHORT).show();
                            baseSearchDialogCompat.dismiss();
                        }
                        else{
                            Toast.makeText(AlbumsSearchDialogActivity.this, searchable.getTitle(), Toast.LENGTH_SHORT).show();
                            albums.put("Album " + Integer.toString(counter), searchable.getTitle());
                            counter++;
                            baseSearchDialogCompat.dismiss();
                        }
                    }
                }).show();
            }
        });

        submitAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter>3){
                    mDatabaseRef.child("Users").child(userID).child("Albums").setValue(albums);
                    mDatabaseRef.child("Users").child(userID).child("How many albums").setValue(Integer.toString(counter-1));
                    mDatabaseRef.child("Users").child(userID).child("Ready to go").setValue("Yes");
                    Toast.makeText(AlbumsSearchDialogActivity.this, "Albums submitted to your profile!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AlbumsSearchDialogActivity.this, ProfileViewActivity.class));
                }else{
                    Toast.makeText(AlbumsSearchDialogActivity.this, "You need to select at least 3 albums, you selected " + (Integer.toString(counter-1))
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    ArrayList<SearchModel> albumsData(){
        ArrayList<SearchModel> genres = new ArrayList<>();
        genres.add(new SearchModel("Northlane - Discoveries (2013)"));
        genres.add(new SearchModel("Northlane - Singularity (2014)"));
        genres.add(new SearchModel("Northlane - Node (2015)"));
        genres.add(new SearchModel("Northlane - Mesmer (2017)"));
        genres.add(new SearchModel("Casey - Love Is Not Enough (2015)"));
        genres.add(new SearchModel("Being As An Ocean - Waiting For A Morning To Come (2017)"));

        return genres;
    }

}
