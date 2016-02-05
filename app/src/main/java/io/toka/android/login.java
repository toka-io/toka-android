package io.toka.android;

import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class login extends AppCompatActivity {

    @Override
    /*
      Objective:
      Set up StrictMode thread policy
      setContentView Brings up login screen
    */
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // Creates a new thread
        StrictMode.setThreadPolicy(policy); // Initiates new thread


        super.onCreate(savedInstanceState); // Runs original onCreate
        setContentView(R.layout.activity_login); // sets display to activity login



    }

    // Not Working
    /*
    private void getSMS() {

        findViewById(R.id.smsLayout).setVisibility(View.VISIBLE); // Sign in layout becomes visible

        EditText txtphoneNo = (EditText) findViewById(R.id.phoneNo); // Allows input for Username text box
        EditText textmessage = (EditText) findViewById(R.id.message); // Allows input for Username text box

    }
    */

    private void sendSMS() {
        SmsManager manager = SmsManager.getDefault();

        manager.sendTextMessage("+XXXXXXX", null, "Meow", null, null);
    }


    /*
        Objective:
        Presents login error message
    */
    private void requestLogin() {
        findViewById(R.id.signInView).setVisibility(View.VISIBLE); // Sign in layout becomes visible
        findViewById(R.id.progressBar).setVisibility(View.GONE); // Progress bar disappears

        findViewById(R.id.errorMessage).setVisibility(View.VISIBLE); // Error message becomes visible
    }

    /*
        Objective:
        Presents progress bar to main page upon successful login
    */
    public void login(View v) {
        findViewById(R.id.signInView).setVisibility(View.GONE); //Sign in layout disappears
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE); // Progress bar becomes visible

        getUser(); // Starts login process
    }

    /*
        Objective:
        Find username and password input
        Compares username and password to Toka directory
        Then logs in the user
    */
    private void getUser() {
        EditText username = (EditText) findViewById(R.id.Username); // Allows input for Username text box
        EditText password = (EditText) findViewById(R.id.Password); // Allows input for Password text box

        HashMap<String, String> user = new HashMap<String, String>(); // Group Username and Password

        user.put("username", username.getText().toString().toLowerCase()); // Sets username EditText to a string
        user.put("password", password.getText().toString()); // Sets password EditText to a string


        loginAPI User = new Gson().fromJson(performPostCall(user), loginAPI.class);


        if (User.status.equals("200")) { // If user status equals 200 then login was successful
            ((Info) this.getApplication()).setUsername(username.getText().toString().toLowerCase()); // Sets username in the chatroom

            Intent i = new Intent(getApplicationContext(), ChatroomActivity.class); // Creates a new chatroom activity

            startActivity(i); // Starts the chatroom activity

            finish(); // ends login.java
        } else {
            requestLogin(); // presents error message
        }
    }

    /*
        Objective:
        Allows easy transformation to web Json
    */
    private class loginAPI {
        String status; // Response code, http
        String message; // Presents text status
        String sessionId; // Session ID
    }

    /*
        Objective:
        Sends login to server
        Reads server resposne

    */
    private String performPostCall(HashMap<String, String> postDataParams) {
        URL url;
        String response = "";

        try {
            url = new URL("https://www.toka.io/api/login"); //Sets URL to said name

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); // Creates new secure connection through URL
            conn.setReadTimeout(15000); // 15 seconds to read Timeout
            conn.setConnectTimeout(15000); // 15 seconds to connect Timeout
            conn.setRequestMethod("POST"); // Secures data sending
            conn.setDoInput(true); // Allows input application
            conn.setDoOutput(true); // Allows output from server

            OutputStream os = conn.getOutputStream(); // creates output stream
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // creates buffered writer using Output Stream
            writer.write(getPostDataString(postDataParams)); // Takes Hashmap and changes it to a string that is encoded in UTF-8

            writer.flush(); // clears information
            writer.close(); // closes writer
            os.close(); //close output stream
            int responseCode=conn.getResponseCode(); // Determines if information has been sent

            if (responseCode == HttpsURLConnection.HTTP_OK) { // If information was successfully sent
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream())); // Creates Buffer Reader for Input Stream
                while ((line=br.readLine()) != null) { // Construct server response
                    response+=line;
                }
            }



        } catch (Exception ignore) { //Catches error and then ignores it

        }
        return response; // Returns server response
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder(); // Creates string builder
        boolean first = true; //

        /*
            Objective:
            Creates string to be sent to server
        */
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) { // First item in the list do not put in &
                first = false; // Sets first value to false to skip
            } else {
                result.append("&"); // Adds and after the first value
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8")); // Gets Key
            result.append("="); // adds an =
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8")); // Gets Value
        }

        return result.toString(); // Converts result into a string
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_login, menu); // This adds items to the action bar if it is present.
        return true;
    }

    @Override
    /*
        Objective:
         Handle action bar items
    */
    public boolean onOptionsItemSelected(MenuItem item) {

           int id = item.getItemId(); // Sets selected ID to id

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
