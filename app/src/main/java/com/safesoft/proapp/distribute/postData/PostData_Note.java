package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

public class PostData_Note implements Serializable {
    public int id;
    public String content;
    public String date;

    public PostData_Note() {
    }

    public PostData_Note(int id, String content, String date) {
        this.id = id;
        this.content = content;
        this.date = date;
    }
}
