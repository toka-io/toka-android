package io.toka.android;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class Info extends Application {
    private String Username;
    private ArrayList<ChatMessage> ChatMessages = new ArrayList<ChatMessage>();
    private HashMap<String, Chatroom> Chatrooms = new HashMap<String, Chatroom>();

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void addChatMessage(ChatMessage message) {
        ChatMessages.add(message);
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return ChatMessages;
    }

    public Chatroom getChatroom(String id) {
        if (Chatrooms.containsKey(id)) {
            return Chatrooms.get(id);
        } else {
            this.addChatroom(id);
            return Chatrooms.get(id);
        }
    }

    public void addChatroom(String id) {
        if (!Chatrooms.containsKey(id)) {
            Chatrooms.put(id, new Chatroom(id, id));
        }
    }

    public void deleteChatroom(String id) {
        if (Chatrooms.containsKey(id)) {
            Chatrooms.remove(id);
        }
    }
}
