package com.snidd.note_store;

/**
 * Note used as request note exposed outside DB.
 */
public class CreateNoteRequest {

    private String title;
    private String content;


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
