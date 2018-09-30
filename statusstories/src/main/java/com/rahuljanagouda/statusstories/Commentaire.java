package com.rahuljanagouda.statusstories;

import java.util.Date;

public class Commentaire {

    private String userId;
    private String commentaire;
    private Date commentaireTime;

    public Commentaire(String userId, String commentaire, Date commentaireTime) {
        this.userId = userId;
        this.commentaire = commentaire;
        this.commentaireTime = commentaireTime;
    }

    public Commentaire() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getCommentaireTime() {
        return commentaireTime;
    }

    public void setCommentaireTime(Date commentaireTime) {
        this.commentaireTime = commentaireTime;
    }
}
