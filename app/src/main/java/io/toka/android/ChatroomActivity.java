package io.toka.android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatroomActivity extends AppCompatActivity {

    Socket socket = IO.socket(URI.create("https://www.toka.io:1337"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        final String username = ((Info) this.getApplication()).getUsername();

        final SimpleDateFormat userDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
        final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
        jsonDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        userDateFormat.setTimeZone(TimeZone.getDefault());

        final MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.chat);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        ListView lv = (ListView) findViewById(R.id.listView);

        final ArrayList<ChatMessage> ChatMessages = ((Info) this.getApplication()).getChatMessages();
        final ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(this, ChatMessages);

        lv.setAdapter(chatMessageAdapter);

        Button button = (Button) findViewById(R.id.Send);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText input = (EditText) findViewById(R.id.input);

                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("chatroomId", "toka");
                    json.put("timestamp", userDateFormat.format(new Date()));
                    json.put("text", input.getText().toString());

                    ChatMessage message = new ChatMessage(json.getString("text"), json.getString("username"), json.getString("timestamp"));
                    ChatMessages.add(message);

                    socket.emit("sendMessage", json);
                    chatMessageAdapter.notifyDataSetChanged();

                    input.setText("");
                } catch (JSONException ignored) {
                }
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("chatroomId", "toka");

                    socket.emit("join", json);
                } catch (JSONException ignored) {
                }

            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        socket.on("history", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        socket.on("receiveMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Message json = new Gson().fromJson(args[0].toString(), Message.class);

                            ChatMessage message = new ChatMessage(json.text, json.username, userDateFormat.format(jsonDateFormat.parse(json.timestamp)));

                            ChatMessages.add(message);

                            chatMessageAdapter.notifyDataSetChanged();
                            mPlayer.start();
                        } catch (ParseException ignored) {
                        }
                    }
                });
            }
        });

        socket.on("activeViewerCount", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        socket.on("users", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        });

        socket.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
