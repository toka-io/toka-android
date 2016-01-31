package io.toka.android;

import android.app.Application;

public class Info extends Application {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String Username) {
        this.username = Username;
    }
}
