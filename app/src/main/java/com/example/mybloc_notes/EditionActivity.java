package com.example.mybloc_notes;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditionActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editContenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_view);

        editNom = findViewById(R.id.editNom);
        editContenu = findViewById(R.id.editContenu);
    }
}
