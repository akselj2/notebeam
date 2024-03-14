package ch.zli.aj.notebeam.model;

import java.sql.Timestamp;

public class Note {

    public String title;
    public String author;
    public String content;
    public long timestamp;

    public Note(String title, String author, String content, long timestamp) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }
}
