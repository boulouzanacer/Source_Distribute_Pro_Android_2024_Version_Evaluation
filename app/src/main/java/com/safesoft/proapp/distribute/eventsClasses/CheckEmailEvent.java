package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckEmailEvent {
    private final int code;
    private final boolean is_exist;
    private final boolean is_e_sent;
    private final String message;
    private final String generated_number;

    public CheckEmailEvent(int code, boolean is_exist, boolean is_e_sent, String message,  String generated_number) {
        this.code = code;
        this.is_exist = is_exist;
        this.is_e_sent = is_e_sent;
        this.message = message;
        this.generated_number = generated_number;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsExist() {
        return is_exist;
    }

    public boolean getIsESent() {
        return is_e_sent;
    }
    public String getGeneratedNumber() {
        return generated_number;
    }
}
