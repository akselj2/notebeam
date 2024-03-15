package ch.zli.aj.notebeam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;

public class MainActivity extends AppCompatActivity {

    private class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

        private List<Note> noteList;

        public NoteAdapter(List<Note> noteList) {
            this.noteList = noteList;
        }

        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return  new NoteViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            Note note = noteList.get(position);
            holder.titleView.setText(note.title);
            holder.contentView.setText(note.content);
        }

        @Override
        public int getItemCount() {
            return noteList.size();
        }

        private class NoteViewHolder extends RecyclerView.ViewHolder {
            TextView titleView, contentView;

            public NoteViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(android.R.id.text1);
                contentView = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    public FloatingActionButton menuButton, scanButton, createButton;
    private boolean areButtonsVisible;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menuButton = findViewById(R.id.menuButton);
        scanButton = findViewById(R.id.actionButton1);
        createButton = findViewById(R.id.actionButton2);

        recyclerView = findViewById(R.id.recyclerView);

        List<Note> notesList = getJsonFile();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NoteAdapter(notesList));
    }

    public void toggleButtons(View view) {
        areButtonsVisible = !areButtonsVisible;
        scanButton.setVisibility(areButtonsVisible ? View.VISIBLE : View.GONE);
        createButton.setVisibility(areButtonsVisible ? View.VISIBLE : View.GONE);

    }

    public void onScanButtonClick(View view) {
        Intent scanIntent = new Intent(MainActivity.this, QRActivity.class);
        startActivity(scanIntent);
    }

    public void onCreateButtonClick(View view) {
        Intent createIntent = new Intent(MainActivity.this, NoteActivity.class);
        startActivity(createIntent);
    }

    public List<Note> getJsonFile() {

        List<Note> noteList = new ArrayList<>();

        File file = new File(getFilesDir(), "notes.json");

        if(file.exists()) {
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONArray jsonArray = new JSONArray(jsonContent.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Note note = new Note(
                            UUID.fromString(jsonObject.getString("id")),
                            jsonObject.getString("title"),
                            jsonObject.getString("author"),
                            jsonObject.getString("content"),
                            Timestamp.valueOf(jsonObject.getString("timestamp"))
                    );
                    noteList.add(note);
                }
            }catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return noteList;
    }

}