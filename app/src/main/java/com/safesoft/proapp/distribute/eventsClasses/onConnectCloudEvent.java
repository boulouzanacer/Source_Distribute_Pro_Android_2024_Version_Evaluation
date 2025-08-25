package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class onConnectCloudEvent {
    private final int code;
    private final boolean is_connect;
    private final String message;

    public onConnectCloudEvent(int code, boolean is_connect, String message) {
        this.code = code;
        this.is_connect = is_connect;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsConnect() {
        return is_connect;
    }

}
