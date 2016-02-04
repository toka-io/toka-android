package io.toka.android;

import android.content.Context;

import java.util.ArrayList;

public class Chatroom {
    private String Name;
    private String Id;
    private ArrayList<String> Users = new ArrayList<String>();
    private ArrayList<ChatMessage> History = new ArrayList<ChatMessage>();

    public Chatroom(String id, String name) {
        Id = id;
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<ChatMessage> getHistory() {
        return History;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ArrayList<String> getUsers() {
        return Users;
    }

    public void addUser(String user) {
        if (!Users.contains(user)) {
            Users.add(user);
        }
    }

    public void deleteUser(String user) {
        if (Users.contains(user)) {
            Users.remove(Users.indexOf(user));
        }
    }

    public String getNumberOfUsers() {
        return String.valueOf(Users.size());
    }

}
