package com.marshallslee.websocketexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.marshallslee.websocketexample.keys.Keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName;
    private Socket socket;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = IO.socket("ws://websockettest.marshallslee.com");
        } catch(Exception e) {
            Log.e(this.getClass().getSimpleName(), "Exception: " + e.getMessage());
            e.printStackTrace();
        }

        socket.on("request", onNewMessage);
        socket.connect();

        Button btnQuery = findViewById(R.id.btnQuery);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMessage();
            }
        });
    }

    private void callMessage() {
        JSONObject data = new JSONObject();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        try {
            data.put(Keys.FIRST_NAME, firstName);
            data.put(Keys.LAST_NAME, lastName);
        } catch(JSONException e) {
            Log.e(this.getClass().getSimpleName(), "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String firstName;
                    String lastName;

                    try {
                        firstName = data.getString(Keys.FIRST_NAME);
                        lastName = data.getString(Keys.LAST_NAME);
                        Toast.makeText(MainActivity.this, "First Name: " + firstName + "\nLast Name: " + lastName, Toast.LENGTH_SHORT).show();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}