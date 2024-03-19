package ch.zli.aj.notebeam.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ch.zli.aj.notebeam.R;
import ch.zli.aj.notebeam.activity.MainActivity;
import ch.zli.aj.notebeam.model.Note;

/**
 * Implementation of App Widget functionality.
 * @author Aksel Jessen
 * @version 1.0
 * @since 18.03.2024
 */
public class NoteWidget extends AppWidgetProvider {

    public static final String ACTION_UPDATE_NOTE_WIDGET = "action.UPDATE_NOTE_WIDGET";


    /**
     * UI Generation method
     * @param context Active environment
     * @param appWidgetManager Updates AppWidget State
     * @param appWidgetId Id of Widget
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        Note note = getMostRecentNote(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        views.setTextViewText(R.id.appwidget_title, note.title);
        views.setTextViewText(R.id.appwidget_content, note.content);
        views.setTextViewText(R.id.appwidget_timestamp, note.timestamp.toString());

        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    /**
     * Gets the most recently modified note
     * @param context active environment
     * @return the most recently modified note to show in the widget.
     */
    public static Note getMostRecentNote(Context context) {
        File file = new File(context.getFilesDir(), "notes.json");
        Note mostRecentNote = null;

        if (file.exists()) {
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONArray notesArray = new JSONArray(jsonContent.toString());

                mostRecentNote = loopThroughArray(notesArray);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        if (mostRecentNote == null) {
            // Return a default note if no suitable note was found
            return new Note(UUID.randomUUID(), "No notes found", "", "", Timestamp.valueOf(String.valueOf(System.currentTimeMillis())));
        }

        return mostRecentNote;
    }

    /**
     * Loops through JSONArray to retrieve the most recently modified Note
     * @param notesArray Array of Notes in JSON Format
     * @return mostRecentNote the most recently modified Note
     * @throws JSONException Due to accessing JSON formats
     */
    public static Note loopThroughArray(JSONArray notesArray) throws JSONException {
        Date mostRecentDate = null;
        Note mostRecentNote = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        for (int i = 0; i < notesArray.length(); i++) {
            JSONObject noteObject = notesArray.getJSONObject(i);
            String timestampStr = noteObject.getString("timestamp");
            try {
                Date date = dateFormat.parse(timestampStr);
                if (mostRecentDate == null || (date != null && date.after(mostRecentDate))) {
                    mostRecentDate = date;

                    UUID id = UUID.fromString(noteObject.getString("id"));
                    String title = noteObject.getString("title");
                    String author = noteObject.getString("author");
                    String content = noteObject.getString("content");
                    String timestamp = noteObject.getString("timestamp");

                    mostRecentNote = new Note(id, title, author, content, Timestamp.valueOf(timestamp));
                    return mostRecentNote;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return mostRecentNote;
    }

    /**
     * Updates the Widget with possible changes
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     * @param appWidgetManager A {@link AppWidgetManager} object you can call {@link
     *                  AppWidgetManager#updateAppWidget} on.
     * @param appWidgetIds The appWidgetIds for which an update is needed.  Note that this
     *                  may be all of the AppWidget instances for this provider, or just
     *                  a subset of them.
     *
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Unimportant
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     */
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * Unimportant
     * @param context   The {@link android.content.Context Context} in which this receiver is
     *                  running.
     */
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Receives Intents
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_UPDATE_NOTE_WIDGET.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, NoteWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

}