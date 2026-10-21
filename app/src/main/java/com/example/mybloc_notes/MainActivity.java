package com.example.mybloc_notes;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Activity;
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

    private final ActivityResultLauncher<Intent> ouvrirEditeur = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("TEST_SAVE", "Résultat reçu : " + result.getResultCode());
                Log.d("TEST_SAVE", "Données reçues : " + result.getData());

                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {

                    Intent data = result.getData();

                    String nom = data.getStringExtra("nom");
                    String contenu = data.getStringExtra("contenu");

                    if (nom != null && contenu != null) {

                        // Créer une nouvelle note
                        Note nouvelleNote = new Note(nom, contenu);

                        ArrayList<Note> nouvellesNotes = new ArrayList<>(notes);
                        nouvellesNotes.add(0, nouvelleNote);

                        try {
                            // enregistre la nouvelle note
                            notesFileHelper.enregistrerNotes(nouvellesNotes);

                            // Mettre à jour la nouvelle note
                            notes.add(0, nouvelleNote);

                            noteAdapter.notifyItemInserted(0);
                            listeNotes.scrollToPosition(0);

                            // Masquer le message Aucune note
                            texteAucuneNote.setVisibility(View.GONE);

                            Toast.makeText(this, "Note enregistrée..", LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e("NOTE_JSON", "Erreur d'enregistrement..");
                            Toast.makeText(this, "Impossible d'enregistrer la note..", LENGTH_SHORT).show();
                        }

                    }
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
}