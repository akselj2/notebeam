package ch.zli.aj.notebeam.activity;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.UUID;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;

public class NoteActivity extends AppCompatActivity {

    public Button delete, save, share;

    public static final String FILE_NAME = "notes.json";

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

    public static UUID generateId() {
        return UUID.randomUUID();
    }

    public void save(View view) {
        title = findViewById(R.id.note_title);
        author = findViewById(R.id.note_author);
        content = findViewById(R.id.note_content);

        UUID id = generateId();
        String noteTitle = String.valueOf(title.getText());
        String noteAuthor = String.valueOf(author.getText());
        String noteContent = String.valueOf(content.getText());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Note note = new Note(id, noteTitle, noteAuthor, noteContent, timestamp);

        saveNotePersistently(note);
    }

    public void delete(View view) {

    }

    public void share(View view) {

    }

    public void saveNotePersistently(Note note) {
        File file = new File(this.getFilesDir(), FILE_NAME);

        JSONArray notesArray = new JSONArray();

        if (file.exists()) {
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonContent.append(line);
                }
                notesArray = new JSONArray(jsonContent.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            notesArray.put(new JSONObject(noteToJson(note)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
            bufferedWriter.write(notesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String noteToJson(Note note) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", note.id);
            jsonObject.put("title", note.title);
            jsonObject.put("author", note.author);
            jsonObject.put("content", note.content);
            jsonObject.put("timestamp", note.timestamp);

            return jsonObject.toString();
        } catch (JsonIOException | JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
