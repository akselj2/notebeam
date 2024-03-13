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

import ch.zli.aj.notebeam.R;

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

    }

    public void onCreateButtonClick(View view) {
        Intent createIntent = new Intent(MainActivity.this, NoteActivity.class);
        startActivity(createIntent);
    }
}