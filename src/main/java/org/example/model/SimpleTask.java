package org.example.model;

public class SimpleTask {

    private final Long id;
    private final String title;
    private final boolean done;

    public SimpleTask(Long id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }
}
