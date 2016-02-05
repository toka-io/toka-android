package io.toka.android;

import android.app.Application;

import java.util.ArrayList;

public class Info extends Application {
    private String username;
    private ArrayList<ChatMessage> ChatMessages = new ArrayList<ChatMessage>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String Username) {
        this.username = Username;
    }

    public void addChatMessage(ChatMessage message) {
        this.ChatMessages.add(message);
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return ChatMessages;
    }


}
