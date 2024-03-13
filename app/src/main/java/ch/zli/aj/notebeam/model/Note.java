package ch.zli.aj.notebeam.model;

import java.sql.Timestamp;

public class Note {

    public String title;
    public String author;
    public String content;
    public Timestamp timestamp;

    public Note(String title, String author, String content, Timestamp timestamp) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }
}
