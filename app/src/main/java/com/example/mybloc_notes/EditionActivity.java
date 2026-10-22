package com.example.mybloc_notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class EditionActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editContenu;


    private ArrayList<String> historiqueNom = new ArrayList<>();
    private ArrayList<String> historiqueContenu = new ArrayList<>();
    private int positionHistorique = -1;
    private boolean modificationAutomatique = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_view);

        editNom = findViewById(R.id.editNom);
        editContenu = findViewById(R.id.editContenu);

        Intent intent = getIntent();
        if (intent.hasExtra("noteId")){
            String nom = intent.getStringExtra("nom");
            String contenu = intent.getStringExtra("contenu");

            editNom.setText(nom);
            editContenu.setText(contenu);
        }

        enregistrerEtat();
        surveillerModifications();

        MaterialToolbar barre = findViewById(R.id.barre_outils);

        barre.setNavigationIcon(R.drawable.chevron_backward);

        barre.setNavigationOnClickListener(v -> {
            finish();
        });

        barre.setOnMenuItemClickListener( i -> {

            if (i.getItemId() == R.id.action_annuler) {
                annuler();
                return true;
            }

            if (i.getItemId() == R.id.action_refaire) {
                refaire();
                return true;
            }

            if (i.getItemId() == R.id.action_enregistrer){
                //Recupérer le titre et le contenu
                String titre = editNom.getText().toString().trim();
                String contenu =  editContenu.getText().toString().trim();

                if( titre.isEmpty()){
                    editNom.setError("Veuillez entrer un titre ");
                    return true;
                }

                Intent note = new Intent();
                note.putExtra("nom", titre);
                note.putExtra("contenu", contenu);

                if(getIntent().hasExtra("noteId")){
                    long noteId = getIntent().getLongExtra("noteId",0);
                    note.putExtra("noteId", noteId);
                }

                setResult(Activity.RESULT_OK, note);
                finish();
                return true;
            }
            return false;
        });
    }

    private void enregistrerEtat() {

        if (modificationAutomatique) {
            return;
        }

        String nom = editNom.getText().toString();
        String contenu = editContenu.getText().toString();

        // Supprimer les états situés après la position actuelle
        while (historiqueNom.size() > positionHistorique + 1) {
            historiqueNom.remove(historiqueNom.size() - 1);
            historiqueContenu.remove(historiqueContenu.size() - 1);
        }

        // Ajouter le nouvel état
        historiqueNom.add(nom);
        historiqueContenu.add(contenu);

        positionHistorique++;
        android.util.Log.d(
                "Historique",
                "Position: " + positionHistorique
        );
    }


    private void surveillerModifications() {

        TextWatcher surveillance = new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("HISTORIQUE", "TextWatcher détecte une modification");

                enregistrerEtat();
            }
        };

        editNom.addTextChangedListener(surveillance);
        editContenu.addTextChangedListener(surveillance);
    }


    private void annuler() {

        if (positionHistorique > 0) {

            positionHistorique--;

            afficherEtat();
        }
    }


    private void refaire() {

        if (positionHistorique < historiqueNom.size() - 1) {

            positionHistorique++;

            afficherEtat();
        }
    }


    private void afficherEtat() {

        modificationAutomatique = true;

        String nom = historiqueNom.get(positionHistorique);
        String contenu = historiqueContenu.get(positionHistorique);

        editNom.setText(nom);
        editContenu.setText(contenu);

        editNom.setSelection(editNom.length());
        editContenu.setSelection(editContenu.length());

        modificationAutomatique = false;
    }


}
