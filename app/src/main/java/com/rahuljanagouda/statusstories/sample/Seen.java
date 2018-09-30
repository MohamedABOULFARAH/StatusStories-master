package com.rahuljanagouda.statusstories.sample;

import java.util.Date;

public class Seen {
    private String userId;
    private Date seenTime;

    public Seen(String userId, Date seenTime) {
        this.userId = userId;
        this.seenTime = seenTime;
    }


    public Seen() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getSeenTime() {
        return seenTime;
    }

    public void setSeenTime(Date seenTime) {
        this.seenTime = seenTime;
    }
}
