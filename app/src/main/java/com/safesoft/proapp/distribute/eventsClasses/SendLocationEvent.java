package com.safesoft.proapp.distribute.eventsClasses;

import android.location.Location;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class SendLocationEvent {
    private int responseCode;
    private String status;
    private String message;

    public SendLocationEvent(int responseCode, String status, String message) {
        this.responseCode = responseCode;
        this.status = status;
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
