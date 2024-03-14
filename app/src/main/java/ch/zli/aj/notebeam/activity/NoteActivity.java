package ch.zli.aj.notebeam.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;

public class NoteActivity extends AppCompatActivity {

    public Button delete, save, share;

    public EditText title, author, content;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.note), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        delete = findViewById(R.id.delete);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);

    }

    public void save(View view) {
       String noteTitle = String.valueOf(findViewById(R.id.note_title));
       String noteAuthor = String.valueOf(findViewById(R.id.note_author));
       String noteContent = String.valueOf(findViewById(R.id.note_content));
       long timestamp = System.currentTimeMillis();

       Note note = new Note(noteTitle, noteAuthor, noteContent, timestamp);

       saveNotePersistently(note);
    }

    public void delete(View view) {

    }

    public void share(View view) {

    }

    public void saveNotePersistently(Note note, String filePath) {
        String json = noteToJson(note);

        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(json);
            fileWriter.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String noteToJson(Note note) {
        Gson gson = new Gson();
        return gson.toJson(note);
    }
}
