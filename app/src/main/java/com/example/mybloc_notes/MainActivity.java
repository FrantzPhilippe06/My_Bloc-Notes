
package com.example.mybloc_notes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listeNotes;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notes;
    private TextView texteAucuneNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FloatingActionButton boutonAjouter = findViewById(R.id.bouton_nouvelle_note);

        boutonAjouter.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    EditionActivity.class
            );
            startActivity(intent);
        });

        DrawerLayout tiroir = findViewById(R.id.tiroir);
        MaterialToolbar barre = findViewById(R.id.barre_outils);
        barre.setNavigationOnClickListener(
                v -> tiroir.openDrawer(GravityCompat.START)
        );

        // Récupérer les éléments du XML
        listeNotes = findViewById(R.id.liste_notes);
        texteAucuneNote = findViewById(R.id.texte_aucune_note);

        // Créer la liste des notes
        notes = new ArrayList<>();

        // Ajouter des notes de test
        notes.add(new Note(
                "Liste d'épicerie",
                "Lait, pain, œufs, pommes et beurre"
        ));

        notes.add(new Note(
                "Cours Android",
                "Réviser Java et RecyclerView"
        ));

        notes.add(new Note(
                "Mes objectifs",
                "Terminer mon projet Android"
        ));

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