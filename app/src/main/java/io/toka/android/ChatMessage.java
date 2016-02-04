package io.toka.android;

public class ChatMessage {
    private String Text;
    private String Username;
    private String Timestamp;
    private String ChatroomName;

    public ChatMessage(String text, String username, String timestamp) {
        Text = text;
        Username = username;
        Timestamp = timestamp;
    }

    public String getText() {
        return Text;
    }

    public String getUsername() {
        return Username;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public String getChatroomName() {
        return ChatroomName;
    }

    public void setChatroomName(String chatroomName) {
        ChatroomName = chatroomName;
    }
}