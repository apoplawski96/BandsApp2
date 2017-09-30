package com.example.artur.bandsapp2;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

import static com.example.artur.bandsapp2.R.id.coverFlow;

public class CoverFlowActivity extends AppCompatActivity {

    //CoverFlow things
    private FeatureCoverFlow coverFlow;
    private CoverAdapter coverAdapter;
    List<AlbumCover> albumList = new ArrayList<>();
    private TextSwitcher mTitle;
    List <String> albumNames;
    List <String> photoUrls;

    //Firebase stuff
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseRef;
    String userID;

    String genres;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_flow);

        genres="asd";

        //Firebase stuff
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        albumNames = new ArrayList<>();
        photoUrls = new ArrayList<>();


        initData();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mTitle=(TextSwitcher)findViewById(R.id.title);
        mTitle.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                LayoutInflater inflater = LayoutInflater.from(CoverFlowActivity.this);
                TextView text = (TextView)inflater.inflate(R.layout.layout_title, null);
                return text;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        mTitle.setInAnimation(in);
        mTitle.setOutAnimation(out);

        //
        coverAdapter = new CoverAdapter(albumList, this);
        coverFlow = (FeatureCoverFlow)findViewById(R.id.coverFlow);
        coverFlow.setAdapter(coverAdapter);

        coverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                mTitle.setText(albumList.get(position).getName());
            }

            @Override
            public void onScrolling() {

            }
        });

    }

    public void initData() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.child("Users").child(userID).child("Albums").getChildren();
                for (DataSnapshot child: children) {
                    i++;
                    String value = child.getValue(String.class);
                    albumNames.add(value);
                    genres = genres + value + ", ";
                }
                Toast.makeText(CoverFlowActivity.this, String.valueOf(albumNames.size()), Toast.LENGTH_SHORT).show();

                Iterable<DataSnapshot> urls = dataSnapshot.child("Albums photos urls").getChildren();
                for (DataSnapshot url: urls){
                    String urlValue = url.getValue(String.class);
                    if (albumNames.contains(url.toString())) photoUrls.add(urlValue);
                }

                for (int counter=0; counter<albumNames.size();counter++){
                    albumList.add(new AlbumCover(albumNames.get(counter), dataSnapshot.child("Albums photos urls").child(albumNames.get(counter)).getValue().toString() ));
                    //albumList.add(new AlbumCover(albumNames.get(counter), photoUrls.get(counter)) );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Toast.makeText(CoverFlowActivity.this, genres, Toast.LENGTH_SHORT).show();

        pd.dismiss();

        //albumList.add(new AlbumCover("1", "https://images.google.com/images/branding/googleg/1x/googleg_standard_color_128dp.png"));

    }

}
