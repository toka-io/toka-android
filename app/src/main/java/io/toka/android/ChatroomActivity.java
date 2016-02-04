package io.toka.android;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatroomActivity extends AppCompatActivity {

    // A bunch of variables so stuff works well
    private Socket ChatroomSocket = IO.socket(URI.create("https://www.toka.io:1337"));
    private boolean Connected = false;
    private SimpleDateFormat userDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
    private SimpleDateFormat jsonDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
    private MediaPlayer MessageSound;
    private String Username;
    private Chatroom Chatroom;
    private String Id;
    private ArrayList<ChatMessage> History;
    private HistoryAdapter HistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        // Set up some basic variables for later
        jsonDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        userDateFormat.setTimeZone(TimeZone.getDefault());

        MessageSound = MediaPlayer.create(this, R.raw.chat);
        MessageSound.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Username = ((Info) this.getApplication()).getUsername();

        // Call the Extra that contains the chatroomId and connect to it
        connectTo(getIntent().getStringExtra("Id"));
    }

    public void connectTo(String id) {
        Id = id;
        // Get the chatroom for easy access
        Chatroom = ((Info) this.getApplication()).getChatroom(Id);

        // Setup the History Adapter
        History = Chatroom.getHistory();
        HistoryAdapter = new HistoryAdapter(this, History);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(HistoryAdapter);

        Button button = (Button) findViewById(R.id.Send);

        // Setup the input for messages
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {
                    EditText input = (EditText) findViewById(R.id.input);

                    sendMessage(input.getText().toString());

                    input.setText("");
                }
            }
        });

        // Connect to Toka
        connect();
    }

    public void close() {
        ChatroomSocket.close();
    }

    public boolean isConnected() {
        return Connected;
    }

    public void sendMessage(String text) {

        JSONObject json = new JSONObject();

        try {
            // Create message to send
            json.put("username", Username);
            json.put("chatroomId", Id);
            json.put("text", text);
            json.put("timestamp", userDateFormat.format(new Date()));

            if (json.getString("text").trim().equals("")) {
                // Skip
            } else {
                // Add message to History than send the message to Toka
                ChatMessage message = new ChatMessage(json.getString("text"), json.getString("username"), json.getString("timestamp"));
                History.add(message);

                ChatroomSocket.emit("sendMessage", json);
            }

        } catch (Exception ignored) {
        }
    }

    public void callUsers() {
        ChatroomSocket.emit("users", Id);
    }

    public void connect() {
        ChatroomSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {
                    JSONObject json = new JSONObject();

                    // Send a join message to Toka
                    json.put("username", Username);
                    json.put("chatroomId", Chatroom.getId());

                    ChatroomSocket.emit("join", json);
                    //ChatroomSocket.emit("users", Id);
                } catch (Exception ignored) {
                }
            }
        });

        ChatroomSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Connected = false;
            }
        });

        ChatroomSocket.on("history", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(args[0].toString());
                            JSONArray jsonHistory = json.getJSONArray("data");

                            for (int i =0; i < jsonHistory.length(); i++) {
                                JSONObject chat = jsonHistory.getJSONObject(i);
                                ChatMessage message = new ChatMessage(chat.getString("text"), chat.getString("username"), userDateFormat.format(jsonDateFormat.parse(chat.getString("timestamp"))));

                                History.add(message);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        });

        ChatroomSocket.on("receiveMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Change incoming message to an object
                            JSONObject json = new JSONObject(args[0].toString());

                            // Add message to the History
                            ChatMessage message = new ChatMessage(json.getString("text"), json.getString("username"), userDateFormat.format(jsonDateFormat.parse(json.getString("timestamp"))));

                            History.add(message);
                            HistoryAdapter.notifyDataSetChanged();

                            // Alert the user
                            MessageSound.start();
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        });

        ChatroomSocket.on("activeViewerCount", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(args[0].toString());

                            TextView activeViewerCount = (TextView) findViewById(R.id.activeViewerCount);

                            activeViewerCount.setText(json.getString(Id));

                        } catch (JSONException ignored) {
                        }
                    }
                });
            }
        });

        ChatroomSocket.on("users", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Log.i("Users", args[0].toString());
            }
        });

        // Connect the Socket and say so!
        ChatroomSocket.connect();
        Connected = true;
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
