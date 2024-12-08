package com.misnotasapp.model;

public class Task {
    private int id;
    private String task;
    private boolean done;

    public Task(int id, String task, boolean done) {
        this.id = id;
        this.task = task;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
