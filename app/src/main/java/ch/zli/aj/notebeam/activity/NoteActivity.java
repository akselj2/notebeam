package ch.zli.aj.notebeam.activity;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;
import ch.zli.aj.notebeam.widget.NoteWidget;

/**
 * Class for Note View (CRUD Functions)
 * @author Aksel Jessen
 * @version 1.0
 * @since 18.03.2024
 */
public class NoteActivity extends AppCompatActivity {

    public Button delete, save, share;

    public static final String FILE_NAME = "notes.json";

    public EditText title, tag, content;

    public UUID noteId = null;

    /**
     * UI Generation method
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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
        title = findViewById(R.id.note_title);
        tag = findViewById(R.id.note_author);
        content = findViewById(R.id.note_content);
        /*
         * Checks whether there is an Intent, to decide if it's an existing Note or a new Note.
         */
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            String idString = intent.getStringExtra("id");
            if (idString != null && !idString.isEmpty()) {
                noteId = UUID.fromString(idString);
                title.setText(intent.getStringExtra("title"));
                tag.setText(intent.getStringExtra("author"));
                content.setText(intent.getStringExtra("content"));
            }
        }
    }

    /**
     * UUID Generation
     * @return UUID for new Note
     */
    public static UUID generateId() {
        return UUID.randomUUID();
    }

    /**
     * Gets text from various TextViews and creates a new Note with them. If noteId is not null, it'll replace the existing one, an update if i may
     * @param view for OnClick declaration in .xml file
     */
    public void save(View view) {
        String noteTitle = title.getText().toString();
        String noteTag = tag.getText().toString();
        String noteContent = content.getText().toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (noteId == null) {
            noteId = generateId();
        }

        Note note = new Note(noteId, noteTitle, noteTag, noteContent, timestamp);

        saveNotePersistently(note);
        //Intent returns to main menu and updates the Widget.
        startActivity(new Intent(this, MainActivity.class));
        updateWidget(this);
    }

    /**
     * Deletes select Note. Similar procedure as the Edit
     * @param view for OnClick declaration in .xml file
     */
    public void delete(View view) {
        File file = new File(getFilesDir(), FILE_NAME);
        JSONArray newNotesArray = new JSONArray();
        if (file.exists()) {
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONArray currentNotesArray = new JSONArray(jsonContent.toString());
                for (int i = 0; i < currentNotesArray.length(); i++) {
                    JSONObject note = currentNotesArray.getJSONObject(i);
                    if (!note.getString("id").equals(noteId.toString())) {
                        newNotesArray.put(note);
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write(newNotesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        updateWidget(this);
    }

    /**
     * Generates a QR Code to share a Note by opening a Dialog.
     * @param view
     */
    public void share(View view) {
        JSONObject object = new JSONObject();
        BitMatrix bitMatrix = null;
        try {
            object.put("id", noteId.toString());
            object.put("title", title.getText().toString());
            object.put("author", tag.getText().toString());
            object.put("content", content.getText().toString());
            object.put("timestamp", new Timestamp(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            bitMatrix = new MultiFormatWriter().encode(object.toString(), BarcodeFormat.QR_CODE, 200, 200);
            showDialog(bitMatrix);
        } catch (WriterException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and shows a dialog with the generated QR Code.
     * @param bitMatrix
     * TODO: Close dialog after 10s and return to Main Screen
     */
    public void showDialog(BitMatrix bitMatrix) throws InterruptedException {
        Dialog dialog = new Dialog(this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.dialog_layout, null);
        dialog.setContentView(dialogView);
        ImageView imageView = dialogView.findViewById(R.id.imageView);
        imageView.setImageBitmap(generateBitmap(bitMatrix));

        dialog.setTitle("QR CODE");
        dialog.setCancelable(true);

        dialog.show();

        wait(5000);
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Generates a Bitmap or Image from the generated QR Code so it can be shown in the Dialog
     * @param matrix BitMatrix from share()
     * @return Bitmap of BitMatrix
     */
    public Bitmap generateBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Reads JSON Files
     * @return JSONArray from JSON File
     */
    public JSONArray readFile() {
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
        return notesArray;
    }

    /**
     * Reads and writes to json file for persistent storage
     * @param note for OnClick declaration in .xml file
     */
    public void saveNotePersistently(Note note) {
        File file = new File(this.getFilesDir(), FILE_NAME);
        JSONArray notesArray = readFile(); //reads existing file
        boolean noteUpdated = false;
        for (int i = 0; i < notesArray.length(); i++) {
            try {
                JSONObject existingNotes = notesArray.getJSONObject(i);
                if (existingNotes.getString("id").equals(note.id.toString())) { //fills data if ID matches.
                    existingNotes.put("title", note.title);
                    existingNotes.put("tag", note.tag);
                    existingNotes.put("content", note.content);
                    existingNotes.put("timestamp", note.timestamp);
                    notesArray.put(i, existingNotes);
                    noteUpdated = true;
                    break;
                }
            } catch (JSONException e) { e.printStackTrace(); }
        }
        //if not updating note
        if (!noteUpdated) { try { notesArray.put(new JSONObject(noteToJson(note))); } catch (JSONException e) { e.printStackTrace(); } }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
            bufferedWriter.write(notesArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a Note object to a JSON Object
     * @param note
     * @return String of a JSONObject
     */
    public static String noteToJson(Note note) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", note.id);
            jsonObject.put("title", note.title);
            jsonObject.put("tag", note.tag);
            jsonObject.put("content", note.content);
            jsonObject.put("timestamp", note.timestamp);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Updates Widget
     * @param context current View
     */
    public void updateWidget(Context context) {
        Intent intent = new Intent(context, NoteWidget.class);
        intent.setAction(NoteWidget.ACTION_UPDATE_NOTE_WIDGET);
        context.sendBroadcast(intent);
    }

}
