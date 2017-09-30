package com.example.artur.bandsapp2;

/**
 * Created by artur on 29.09.2017.
 */

public class AlbumCover {

    String Name, ImageURL;

    public AlbumCover (String name, String imageURL){
        Name=name;
        ImageURL=imageURL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
