package com.example.artur.bandsapp2;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by artur on 21.09.2017.
 */

public class SearchModel implements Searchable {
    private String mTitle;

    public SearchModel (String mTitle){
        this.mTitle = mTitle;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
