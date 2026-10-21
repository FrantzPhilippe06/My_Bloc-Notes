package com.example.mybloc_notes;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NotesFileHelper {
    private final Context context;
    private final String NOM_FICHIER = "notes.json";
    public NotesFileHelper(Context context) {
        this.context = context;
    }
    public void enregistrerNotes(ArrayList<Note> notes)
            throws Exception {

        JSONArray tableau = new JSONArray();

        for (Note note : notes) {

            JSONObject objet = new JSONObject();

            objet.put("id", note.getId());
            objet.put("nom", note.getNom());
            objet.put("contenu", note.getContenu());
            objet.put("dateModification",
                    note.getDateModification());

            tableau.put(objet);
        }

        try (FileOutputStream fichier =  context.openFileOutput(
                             NOM_FICHIER, Context.MODE_PRIVATE)) {

            fichier.write(tableau.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public ArrayList<Note> chargerNotes()
            throws Exception {

        ArrayList<Note> notes = new ArrayList<>();

        if (!context.getFileStreamPath(NOM_FICHIER).exists()) {
            return notes;
        }

        StringBuilder texte = new StringBuilder();

        try (FileInputStream fichier =
                     context.openFileInput(NOM_FICHIER)) {

            byte[] donnees = new byte[4096];
            int nombre;

            while ((nombre = fichier.read(donnees)) != -1) {
                texte.append(new String(
                        donnees, 0, nombre,
                        StandardCharsets.UTF_8));
            }
        }

        JSONArray tableau = new JSONArray(texte.toString());

        for (int i = 0; i < tableau.length(); i++) {

            JSONObject objet = tableau.getJSONObject(i);

            Note note = new Note(
                    objet.getString("nom"),
                    objet.getString("contenu")
            );

            note.setId(objet.getLong("id"));
            note.setDateModification(
                    objet.getLong("dateModification"));

            notes.add(note);
        }
        return notes;
    }
}
