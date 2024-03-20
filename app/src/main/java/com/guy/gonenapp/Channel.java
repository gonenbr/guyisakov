package com.guy.gonenapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Channel {

    private String title = "";
    private Deque<Float> data = new LinkedList<>();

    public Channel() {
        for (int i = 0; i < 250; i++) {
            data.add(0f);
        }
    }

    public String getTitle() {
        return title;
    }

    public Channel setTitle(String title) {
        this.title = title;
        return this;
    }

    public Deque<Float> getData() {
        return data;
    }
}
