package com.example.artur.bandsapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);

        Button basicInfo = (Button) findViewById(R.id.basicInfoEdit);
        Button instruments = (Button) findViewById(R.id.instrumentsEdit);
        Button skills = (Button) findViewById(R.id.skillsEdit);
        Button genres = (Button) findViewById(R.id.genresEdit);
        Button bands = (Button) findViewById(R.id.favouriteBandsEdit);
        Button albums = (Button) findViewById(R.id.favouriteAlbumsEdit);

        TextView returnTextView = (TextView) findViewById(R.id.editProfileReturn);

        basicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, InitializeBasicInfoActivity.class));
            }
        });

        instruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, InstrumentsSearchDialogActivity.class));
            }
        });

        skills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, SkillsSearchDialogActivity.class));
            }
        });

        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, GenresSearchDialogActivity.class));
            }
        });

        bands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, BandsSearchDialogActivity.class));
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, AlbumsSearchDialogActivity.class));
            }
        });

        returnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditMenuActivity.this, ProfileViewActivity.class));
            }
        });

    }
}
