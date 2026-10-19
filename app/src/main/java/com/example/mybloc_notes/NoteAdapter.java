package com.example.mybloc_notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteAdapter
        extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> ListNotes;
    public NoteAdapter(List<Note> listNotes) {
        this.ListNotes = listNotes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textNom;
        TextView textContenu;
        TextView textDate;

        public NoteViewHolder(View itemView) {
            super(itemView);

            textNom = itemView.findViewById(R.id.textNom);
            textContenu = itemView.findViewById(R.id.textContenu);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }

    // Crée l'affichage d'une note
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);

        return new NoteViewHolder(view);
    }

    // Remplit l'affichage avec les données
    @Override
    public void onBindViewHolder(
            @NonNull NoteViewHolder holder, int position) {

        Note note = ListNotes.get(position);

        holder.textNom.setText(note.getNom());

        String contenu = note.getContenu();

        if (contenu.length() > 30) {
            contenu = contenu.substring(0, 30) + "...";
        }

        holder.textContenu.setText(contenu);

        SimpleDateFormat format =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CANADA_FRENCH);

        String date = format.format(note.getDateModification());

        holder.textDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return ListNotes.size();
    }
}
