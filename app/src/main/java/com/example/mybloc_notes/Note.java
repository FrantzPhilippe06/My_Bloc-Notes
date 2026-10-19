package com.example.mybloc_notes;

public class Note {
    private long id;
    private String nom;
    private String contenu;
    private long dateModification;

    public Note(String nom, String contenu) {
        this.id = 0;
        this.nom = nom;
        this.contenu = contenu;
        this.dateModification = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public long getDateModification() {
        return dateModification;
    }

    public void setDateModification(long dateModification) {
        this.dateModification = dateModification;
    }
}
