package com.example.mybloc_notes;

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

        barre.setNavigationIcon(
                androidx.appcompat.R.drawable.abc_ic_ab_back_material
        );

        barre.setNavigationOnClickListener(v -> {
            finish();
        });

        barre.setOnMenuItemClickListener( i -> {
            if (i.getItemId() == R.id.action_enregistrer){
                Toast.makeText(this,"Note enregistrer",Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}
