package ch.zli.aj.notebeam.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Note {

    public UUID id;
    public String title;
    public String author;
    public String content;
    public Timestamp timestamp;

    public Note(UUID id, String title, String author, String content, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
