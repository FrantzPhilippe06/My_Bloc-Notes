package com.example.mybloc_notes;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listeNotes;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notes;
    private TextView texteAucuneNote;
    private NotesFileHelper notesFileHelper;

    private final ActivityResultLauncher<Intent> ouvrirEditeur =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() != Activity.RESULT_OK
                                || result.getData() == null) {
                            return;
                        }

                        Intent data = result.getData();

                        String nom = data.getStringExtra("nom");
                        String contenu = data.getStringExtra("contenu");

                        if (nom == null || contenu == null) {
                            return;
                        }

                        ArrayList<Note> nouvellesNotes =
                                new ArrayList<>(notes);

                        if (data.hasExtra("noteId")) {

                            // Modifier une note existante
                            long id = data.getLongExtra("noteId", -1);
                            int position = trouverPositionNote(id);

                            if (position == -1) {
                                Toast.makeText(this,
                                        "Note introuvable",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Préparer la note modifiée
                            Note ancienneNote = notes.get(position);
                            Note noteModifiee = new Note(nom, contenu);

                            noteModifiee.setId(ancienneNote.getId());

                            // Remplacer l'ancienne note
                            nouvellesNotes.set(position, noteModifiee);

                        } else {
                            // Créer une nouvelle note
                            Note nouvelleNote = new Note(nom, contenu);

                            // Trouver un identifiant unique
                            long nouvelId = System.currentTimeMillis();

                            // Vérifier les identifiants déjà utilisés
                            boolean existe = true;

                            while (existe) {
                                existe = false;

                                for (Note note : nouvellesNotes) {
                                    if (note.getId() == nouvelId) {
                                        nouvelId++;
                                        existe = true;
                                    }
                                }
                            }
                            nouvelleNote.setId(nouvelId);
                            nouvellesNotes.add(0, nouvelleNote);
                        }

                        try {
                            // Sauvegarder la liste complète
                            notesFileHelper.enregistrerNotes(nouvellesNotes);

                            // Mettre à jour la liste utilisée
                            notes.clear();
                            notes.addAll(nouvellesNotes);

                            // Trier par date de modification
                            notes.sort((a, b) ->
                                    Long.compare(
                                            b.getDateModification(),
                                            a.getDateModification()
                                    )
                            );

                            noteAdapter.notifyDataSetChanged();

                            if (!notes.isEmpty()) {
                                listeNotes.scrollToPosition(0);
                            }

                            texteAucuneNote.setVisibility(
                                    notes.isEmpty() ? View.VISIBLE : View.GONE
                            );

                            Toast.makeText(this,
                                    "Note enregistrée",
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e("NOTES_JSON",
                                    "Erreur d'enregistrement", e);

                            Toast.makeText(this,
                                    "Impossible d'enregistrer la note",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        notesFileHelper = new NotesFileHelper(this);

        FloatingActionButton boutonAjouter = findViewById(R.id.bouton_nouvelle_note);

        boutonAjouter.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    EditionActivity.class
            );
            ouvrirEditeur.launch(intent);
        });

        DrawerLayout tiroir = findViewById(R.id.tiroir);
        MaterialToolbar barre = findViewById(R.id.barre_outils);
        barre.setNavigationOnClickListener(
                v -> tiroir.openDrawer(GravityCompat.START)
        );

        // Récupérer les éléments du XML
        listeNotes = findViewById(R.id.liste_notes);
        texteAucuneNote = findViewById(R.id.texte_aucune_note);

        // Ajouter des notes persistante
        try {
            notes = notesFileHelper.chargerNotes();

            notes.sort((a, b) -> Long.compare(
                    b.getDateModification(),
                    a.getDateModification()
            ));
        } catch (Exception e) {
            Log.e("Notes_JSON", "Erreur de chargement");
            notes = new ArrayList<>();
        }

        // Configurer le RecyclerView
        listeNotes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Créer l'adaptateur
        noteAdapter = new NoteAdapter(notes);

        noteAdapter.setOnClickListener(note -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    EditionActivity.class
            );
            intent.putExtra("noteId", note.getId());
            intent.putExtra("nom", note.getNom());
            intent.putExtra("contenu", note.getContenu());

            ouvrirEditeur.launch(intent);
        });


        noteAdapter.setOnLongClickListener(note -> {

            new AlertDialog.Builder(this)
                    .setTitle("Supprimer la note")
                    .setMessage("Voulez-vous supprimer « "
                            + note.getNom() + " » ?")

                    .setNegativeButton("Annuler", (dialog, which) -> {
                        dialog.dismiss();
                    })

                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        // Préparer la liste sans la note sélectionnée
                        ArrayList<Note> nouvellesNotes =
                                new ArrayList<>(notes);

                        nouvellesNotes.remove(note);

                        try {
                            // Enregistrer la nouvelle liste
                            notesFileHelper.enregistrerNotes(nouvellesNotes);

                            // Actualiser les notes affichées
                            notes.clear();
                            notes.addAll(nouvellesNotes);
                            noteAdapter.notifyDataSetChanged();

                            // Afficher "Aucune note" si la liste est vide
                            if (notes.isEmpty()) {
                                texteAucuneNote.setVisibility(View.VISIBLE);
                            } else {
                                texteAucuneNote.setVisibility(View.GONE);
                            }

                            Toast.makeText(this,
                                    "Note supprimée",
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e("NOTES_JSON",
                                    "Erreur de suppression", e);

                            Toast.makeText(this,
                                    "Impossible de supprimer la note",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })

                    .show();
        });


        // Relier l'adaptateur au RecyclerView
        listeNotes.setAdapter(noteAdapter);

        // Afficher ou masquer le message
        if (notes.isEmpty()) {
            texteAucuneNote.setVisibility(View.VISIBLE);
        } else {
            texteAucuneNote.setVisibility(View.GONE);
        }

        // Gestion des marges système
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return insets;
                });
    }


    private int trouverPositionNote(long id) {

        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}