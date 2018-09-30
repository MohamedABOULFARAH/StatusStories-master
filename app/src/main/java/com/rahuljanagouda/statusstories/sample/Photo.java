package com.rahuljanagouda.statusstories.sample;

import java.util.ArrayList;
import java.util.List;

public class Photo {
    private String id;
    private String url;
    List<Seen> seenList = new ArrayList<>();

    public Photo(String id, String url, List<Seen> seenList) {
        this.id = id;
        this.url = url;
        this.seenList = seenList;
    }

    public Photo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Seen> getSeenList() {
        return seenList;
    }

    public void setSeenList(List<Seen> seenList) {
        this.seenList = seenList;
    }
}
