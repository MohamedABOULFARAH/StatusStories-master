package com.rahuljanagouda.statusstories.sample;

import android.support.annotation.NonNull;

/**
 * Created by AkshayeJH on 04/01/18.
 */

public class PhotoId {

    public String photoId;

    public <T extends PhotoId> T withId(@NonNull final String id) {
        this.photoId = id;
        return (T) this;
    }

}
