package ch.zli.aj.notebeam.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Note {

    public UUID id;
    public String title;
    public String tag;
    public String content;
    public Timestamp timestamp;

    public Note(UUID id, String title, String tag, String content, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.content = content;
        this.timestamp = timestamp;
    }

}
