package ch.zli.aj.notebeam.model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Note Model
 * @author Aksel Jessen
 * @version 1.0
 * @since 18.03.2024
 */
public class Note {

    public UUID id;
    public String title;
    public String tag;
    public String content;
    public Timestamp timestamp;

    /**
     * Note Constructor
     * @param id UUID of Note
     * @param title title of Note
     * @param tag a tag for a Note (e.g. Work / Shopping)
     * @param content content of Note
     * @param timestamp time of creation / modification
     */
    public Note(UUID id, String title, String tag, String content, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.content = content;
        this.timestamp = timestamp;
    }

}
