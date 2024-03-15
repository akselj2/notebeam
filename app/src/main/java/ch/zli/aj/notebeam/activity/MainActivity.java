package ch.zli.aj.notebeam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.model.Note;

public class MainActivity extends AppCompatActivity {

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

    public String getJsonFile() {
        String filename = "notes.json";

        try {
            File file = new File(this.getFilesDir(), filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

            String response = stringBuilder.toString();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "file nto found idk";
    }

    public Note JsonToNote (String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        return new Note(
                jsonObject.get("title").toString(),
                jsonObject.get("author").toString(),
                jsonObject.get("content").toString(),
                Timestamp.valueOf(jsonObject.get("timestamp").toString())
        );
    }

}