package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Client;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedClientEvent {

    private final PostData_Client client;

    public SelectedClientEvent(PostData_Client client) {
        this.client = client;
    }

    public PostData_Client getClient() {
        return client;
    }
}
