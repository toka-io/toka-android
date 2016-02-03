package io.toka.android;
/*
    Preintiates the ability to store and retrieve chat messages
*/
public class ChatMessage {
    private String text;
    private String username;
    private String timestamp;
    private String chatroomName;

    public ChatMessage(String text, String username, String timestamp) {
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
    }

    public String getText() {
        return this.text;
    }

    public String getUsername() {
        return this.username;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getChatroomName() {
        return this.chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }
}