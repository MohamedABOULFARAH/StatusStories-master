package com.rahuljanagouda.statusstories.sample;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id,nom;
    List<Photo> photo = new ArrayList<>();

    public User(String id, String nom,  List<Photo> photo) {
        this.id = id;
        this.nom = nom;
        this.photo = photo;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Photo> getPhoto() {
        return photo;
    }

    public void Photo(List<Photo> photo) {
        this.photo = photo;
    }
}

