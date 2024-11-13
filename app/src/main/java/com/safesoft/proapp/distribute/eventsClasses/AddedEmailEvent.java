package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class AddedEmailEvent {
    private final int code;
    private final boolean is_added;
    private final String message;
    private final String email_cloud;
    private final String pasword_cloud;

    public AddedEmailEvent(int code, boolean is_added, String message, String email_cloud, String pasword_cloud) {
        this.code = code;
        this.is_added = is_added;
        this.message = message;
        this.email_cloud = email_cloud;
        this.pasword_cloud = pasword_cloud;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsAdded() {
        return is_added;
    }
    public String getEmailCloud() {
        return email_cloud;
    }
    public String getPaswordCloud() {
        return pasword_cloud;
    }
}
