package com.redheadhammer.friendschat;

import android.util.Log;

public class ModelClass {
    private static final String TAG = "ModelClass";
    private String message;
    private String username;

    public ModelClass() {}

    public ModelClass(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        Log.d(TAG, "setMessage: " + message);
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Log.d(TAG, "setMessage: " + username);
        this.username = username;
    }
}
