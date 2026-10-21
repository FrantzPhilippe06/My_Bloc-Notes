package com.example.mybloc_notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class EditionActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editContenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_view);

        editNom = findViewById(R.id.editNom);
        editContenu = findViewById(R.id.editContenu);

        MaterialToolbar barre = findViewById(R.id.barre_outils);

        barre.setNavigationIcon(R.drawable.chevron_backward);

        barre.setNavigationOnClickListener(v -> {
            finish();
        });

        barre.setOnMenuItemClickListener( i -> {
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

                setResult(Activity.RESULT_OK, note);

                Toast.makeText(this,"Note enregistrer",Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            return false;
        });
    }
}
