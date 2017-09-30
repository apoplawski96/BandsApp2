package com.example.artur.bandsapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by artur on 29.09.2017.
 */

public class CoverAdapter extends BaseAdapter {

    private List<AlbumCover> albumList;
    private Context mContext;

    public CoverAdapter(List<AlbumCover> albumList, Context mContext) {
        this.albumList = albumList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null){
            rowView = LayoutInflater.from(mContext).inflate(R.layout.layout_item, null);
            TextView name = (TextView)rowView.findViewById(R.id.label);
            ImageView albumCover = (ImageView)rowView.findViewById(R.id.albumCover);

            Picasso.with(mContext).load(albumList.get(position).getImageURL()).into(albumCover);
            name.setText(albumList.get(position).getName());
        }
        return rowView;
    }
}
