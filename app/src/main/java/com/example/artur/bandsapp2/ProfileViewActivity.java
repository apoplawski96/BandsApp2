package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewActivity extends AppCompatActivity {

    //Widgets
    Toolbar toolbar;
    ImageView userAvatar;
    TextView usernameDisplay, locationDisplay, genresSelected, instrumentsSelected, skillsSelected;
    Button logoutButton;
    FloatingActionButton editProfileButton;

    //Lists
    List<String> genreList;
    List<String> instrumentsList;
    List<String> skillsList;

    //Firebase stuff
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;
    String userID;

    //Data stuff
    String username, location, genres, instruments, skills;

    //Band and album photos
    ImageView photo1, photo2, photo3;
    ImageView albumPhoto1, albumPhoto2, albumPhoto3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);


        //Widgets
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        userAvatar = (ImageView)findViewById(R.id.userAvatarOnProfileView);
        usernameDisplay = (TextView) findViewById(R.id.username);
        locationDisplay = (TextView) findViewById(R.id.location);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        genresSelected = (TextView) findViewById(R.id.genresSelected);
        instrumentsSelected = (TextView) findViewById(R.id.instrumentsSelected);
        skillsSelected = (TextView) findViewById(R.id.skillsSelected);
        editProfileButton = (FloatingActionButton) findViewById(R.id.editProfileButton);


        //Collections
        genreList = new ArrayList<>();
        instrumentsList = new ArrayList<>();
        skillsList = new ArrayList<>();

        //Others
        genres = "";
        instruments = "";
        skills = "";

        //Firebase stuff
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        //Bandphotos
        photo1 = (ImageView) findViewById(R.id.bandPhoto1);
        photo2 = (ImageView) findViewById(R.id.bandPhoto2);
        photo3 = (ImageView) findViewById(R.id.bandPhoto3);
        albumPhoto1 = (ImageView) findViewById(R.id.album1);
        albumPhoto2 = (ImageView) findViewById(R.id.album2);
        albumPhoto3 = (ImageView) findViewById(R.id.album3);


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileViewActivity.this, EditMenuActivity.class));
            }
        });


        //Reading from database | setting USERNAME and LOCATION
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("Users").child(userID).child("First Name").getValue().toString();
                String lastName = dataSnapshot.child("Users").child(userID).child("Last Name").getValue().toString();
                username = firstName + " " + lastName;
                location = dataSnapshot.child("Users").child(userID).child("City").getValue().toString();
                usernameDisplay.setText(username);
                locationDisplay.setText(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting up albums photos
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String album1, album2, album3;
                album1 = dataSnapshot.child("Users").child(userID).child("Albums").child("Album 1").getValue().toString();
                album2 = dataSnapshot.child("Users").child(userID).child("Albums").child("Album 2").getValue().toString();
                album3 = dataSnapshot.child("Users").child(userID).child("Albums").child("Album 3").getValue().toString();

                String albumUrl1 = dataSnapshot.child("Albums photos urls").child(album1).getValue().toString();
                String albumUrl2 = dataSnapshot.child("Albums photos urls").child(album2).getValue().toString();
                String albumUrl3 = dataSnapshot.child("Albums photos urls").child(album3).getValue().toString();

                Glide.with(ProfileViewActivity.this).load(albumUrl1).into(albumPhoto1);
                Glide.with(ProfileViewActivity.this).load(albumUrl2).into(albumPhoto2);
                Glide.with(ProfileViewActivity.this).load(albumUrl3).into(albumPhoto3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting up band photos
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String band1, band2, band3;
                band1 = dataSnapshot.child("Users").child(userID).child("Bands").child("Band 1").getValue().toString();
                band2 = dataSnapshot.child("Users").child(userID).child("Bands").child("Band 2").getValue().toString();
                band3 = dataSnapshot.child("Users").child(userID).child("Bands").child("Band 3").getValue().toString();

                String albumUrl1 = dataSnapshot.child("Bands photos urls").child(band1).getValue().toString();
                String albumUrl2 = dataSnapshot.child("Bands photos urls").child(band2).getValue().toString();
                String albumUrl3 = dataSnapshot.child("Bands photos urls").child(band3).getValue().toString();

                Glide.with(ProfileViewActivity.this).load(albumUrl1).into(photo1);
                Glide.with(ProfileViewActivity.this).load(albumUrl2).into(photo2);
                Glide.with(ProfileViewActivity.this).load(albumUrl3).into(photo3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Setting up photo
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageURL = dataSnapshot.child("Users").child(user.getUid()).child("Image Info").child("url").getValue().toString();
                Glide.with(ProfileViewActivity.this).load(imageURL).into(userAvatar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Reading from database | setting GENRES list
        mDatabaseRef.child("Users").child(userID).child("Genres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    String value = child.getValue(String.class);
                    genreList.add(value);
                    genres = genres + value + ", ";
                }
                genresSelected.setText(genres.substring(0, genres.length() - 2));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Reading from database | setting INSTRUMENTS list
        mDatabaseRef.child("Users").child(userID).child("Instruments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    String value = child.getValue(String.class);
                    instrumentsList.add(value);
                    instruments = instruments + value + ", ";
                    instrumentsSelected.setText(instruments.substring(0, instruments.length() - 2));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Reading from database | setting SKILLS list
        mDatabaseRef.child("Users").child(userID).child("Skills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    String value = child.getValue(String.class);
                    skillsList.add(value);
                    skills = skills + value + ", ";
                    skillsSelected.setText(skills.substring(0, skills.length() - 2));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ProfileViewActivity.this, LoginActivity.class));
            }
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }
}
