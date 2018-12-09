package com.rag.postapp.model;

/**
 * Created by Raghavendra Kallubandi on 09/12/18.
 */

import android.net.Uri;


public class Picture {
    String name;
    Uri uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}