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

import ch.zli.aj.notebeam.OnNoteListener;
import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;

/**
 * @author Aksel Jessen
 * @version 1.0
 * @since 18.03.2024
 */
public class MainActivity extends AppCompatActivity implements OnNoteListener {

    private class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements OnNoteListener {

        private OnNoteListener onNoteListener;

        private List<Note> noteList;

        /**
         * Constructor for NoteAdapter
         * @param noteList list of all Notes for RecyclerView
         * @param onNoteListener onNoteListener used for edit function
         */
        public NoteAdapter(List<Note> noteList, OnNoteListener onNoteListener) {
            this.noteList = noteList;
            this.onNoteListener = onNoteListener;
        }

        /**
         * Fills the RecyclerView with the Title and Content of each Note.
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType The view type of the new View.
         *
         * @return a new NoteViewHolder with the itemView as a parameter
         */
        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return  new NoteViewHolder(itemView);
        }

        /**
         * Binds the RecyclerView to the onNoteListener
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            Note note = noteList.get(position);
            holder.itemView.setOnClickListener(view -> {
                onNoteClick(note);
            });
            holder.titleView.setText(note.title);
            holder.contentView.setText(note.content);
        }

        /**
         * Getter
         * @return size of noteList
         */
        @Override
        public int getItemCount() {
            return noteList.size();
        }

        /**
         * OnClick method that changes View when clicking on a Note in the Main Screen. (For Edit)
         * @param note Note Object for Intent Extras
         */
        @Override
        public void onNoteClick(Note note) {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("id", note.id.toString());
            intent.putExtra("title", note.title);
            intent.putExtra("author", note.author);
            intent.putExtra("content", note.content);
            intent.putExtra("timestamp", note.timestamp);
            startActivity(intent);
        }


        private class NoteViewHolder extends RecyclerView.ViewHolder {
            TextView titleView, contentView;

            /**
             * Constructor for NoteViewHolder class
             * @param itemView default ID for the textViews in which title and content are set.
             */
            public NoteViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(android.R.id.text1);
                contentView = itemView.findViewById(android.R.id.text2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && onNoteListener != null) {
                            onNoteListener.onNoteClick(noteList.get(position));
                        }
                    }
                });
            }
        }
    }

    public FloatingActionButton menuButton, scanButton, createButton;
    private boolean areButtonsVisible;
    public RecyclerView recyclerView;

    /**
     * UI Generation method. Updated with populateView()
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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

        populateView();
    }

    /**
     * Method for inflating the RecyclerView.
     */
    public void populateView() {
        recyclerView = findViewById(R.id.recyclerView);

        List<Note> notesList = getJsonFile();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NoteAdapter(notesList, this));
    }

    /**
     * Toggles visibility of the FloatingActionButtons in the bottom right of the application (Only in Main Menu). Includes a scan and create Button.
     * @param view current View, is required for onClick methods if referenced by a .xml file
     */
    public void toggleButtons(View view) {
        areButtonsVisible = !areButtonsVisible;
        scanButton.setVisibility(areButtonsVisible ? View.VISIBLE : View.GONE);
        createButton.setVisibility(areButtonsVisible ? View.VISIBLE : View.GONE);

    }

    /**
     * OnClick method for Scan FloatingActionButton, creates Intent to change Views to the QRActivity class
     * @param view current View, required for onClick methods if referenced by a .xml file
     */
    public void onScanButtonClick(View view) {
        Intent scanIntent = new Intent(MainActivity.this, QRActivity.class);
        startActivity(scanIntent);
    }

    /**
     * OnClick method for Create FloatingActionButton, creates Intent to change Views to the NoteActivity class
     * @param view current View, required for onClick methods if referenced by a .xml file
     */
    public void onCreateButtonClick(View view) {
        Intent createIntent = new Intent(MainActivity.this, NoteActivity.class);
        startActivity(createIntent);
    }

    /**
     * Reads the JSON File responsible for storing all Notes written by the user.
     * @return a List of Note Objects
     */
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

    /**
     * If app is suspended or is left in the background, it calls onResume() to repopulate the View in case of any changes made.
     */
    @Override
    protected void onResume() {
        super.onResume();
        populateView();
    }

    /**
     * Needs to be implemented due to MainActivity implementing the OnNoteListener Interface. Can't remove it.
     */
    @Override
    public void onNoteClick(Note note) {
        /*Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("id", note.id);
        intent.putExtra("title", note.title);
        intent.putExtra("author", note.author);
        intent.putExtra("content", note.content);
        intent.putExtra("timestamp", note.timestamp);
        startActivity(intent);*/
    }

}