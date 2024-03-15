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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Struct;

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

    public void save(View view) {
        title = findViewById(R.id.note_title);
        author = findViewById(R.id.note_author);
        content = findViewById(R.id.note_content);

        String noteTitle = String.valueOf(title.getText());
        String noteAuthor = String.valueOf(author.getText());
        String noteContent = String.valueOf(content.getText());
        long timestamp = System.currentTimeMillis();

        Note note = new Note(noteTitle, noteAuthor, noteContent, timestamp);

        saveNotePersistently(note);
    }

    public void delete(View view) {

    }

    public void share(View view) {

    }

    public void saveNotePersistently(Note note) {
        String json = noteToJson(note);
        try {
            File file = new File(this.getFilesDir(), FILE_NAME);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String noteToJson(Note note) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("title", note.title);
            jsonObject.put("author", note.author);
            jsonObject.put("content", note.content);
            jsonObject.put("timestamp", note.timestamp);

            return jsonObject.toString();
        } catch (JsonIOException | JSONException e) {
            e.printStackTrace();
        }

        return "Json could not be created.";
    }
}
