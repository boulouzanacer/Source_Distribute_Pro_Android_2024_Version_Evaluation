package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ConnectedCloudEvent {
    private final String email;
    private final String password;

    public ConnectedCloudEvent(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
