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
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SMS();
    }

    // Not Working
    private void SMS() {

        String[] projection = {Telephony.Sms.Sent.DATE};
        String selectionClause = "";
        String[] selectionArgs = {""};
        String orderBy = Telephony.Sms.Sent.DATE;

        Cursor sms = getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, projection, selectionClause, selectionArgs, orderBy);

        int index = sms.getColumnIndex(Telephony.Sms.Sent.DATE);

        while (sms.moveToNext()) {
            String newWord = sms.getString(index);

            Log.i("Date", newWord);
        }


        sms.close();

    }

    private void requestLogin() {
        findViewById(R.id.signInView).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        findViewById(R.id.errorMessage).setVisibility(View.VISIBLE);
    }

    public void login(View v) {
        findViewById(R.id.signInView).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        getUser();
    }

    private void getUser() {
        EditText username = (EditText) findViewById(R.id.Username);
        EditText password = (EditText) findViewById(R.id.Password);

        HashMap<String, String> user = new HashMap<String, String>();

        user.put("username", username.getText().toString().toLowerCase());
        user.put("password", password.getText().toString());

        loginAPI User = new Gson().fromJson(performPostCall(user), loginAPI.class);
        if (User.status.equals("200")) {
            ((Info) this.getApplication()).setUsername(username.getText().toString());

            Intent i = new Intent(getApplicationContext(), ChatroomActivity.class);
            startActivity(i);
            finish();
        } else {
            requestLogin();
        }
    }

    private class loginAPI {
        String status;
        String message;
        String sessionId;
    }

    private String performPostCall(HashMap<String, String> postDataParams) {
        URL url;
        String response = "";

        try {
            url = new URL("https://www.toka.io/api/login");

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
        } catch (Exception ignore) {
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
