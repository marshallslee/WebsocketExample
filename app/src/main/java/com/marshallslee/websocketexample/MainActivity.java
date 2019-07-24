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
import io.socket.engineio.client.EngineIOException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private EditText etFirstName, etLastName;

    private Socket socket;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect();

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

    private void connect() {
        try {
            socket = IO.socket(Keys.BASE_URL);
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Keys.RESPONSE, onMessageReceived);
            socket.connect();
        } catch(URISyntaxException e) {
            Log.e(TAG, "URISyntaxException caught: " + e.getMessage());
        }
    }

    private void callMessage() {
        JSONObject data = new JSONObject();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        if(socket.connected()) {
            try {
                data.put(Keys.FIRST_NAME, firstName);
                data.put(Keys.LAST_NAME, lastName);
                socket.emit(Keys.NAME, data);
                Log.e(TAG, "Emitting the data: " + data.toString());
            } catch (JSONException e) {
                Log.e(this.getClass().getSimpleName(), "Exception: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Socket is not connected.");
        }
    }

    private Emitter.Listener onMessageReceived = args -> runOnUiThread(() -> {
        JSONObject receivedData = (JSONObject) args[0];
        String hello;
        try {
            hello = receivedData.getString(Keys.NAME);
            Toast.makeText(MainActivity.this, hello, Toast.LENGTH_SHORT).show();
        } catch(JSONException e) {
            Log.e(TAG, "JSONException caught: " + e.getMessage());
        }
    });

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Websocket is successfully connected.");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EngineIOException e = (EngineIOException) args[0];
                    Log.e(TAG, "Failed to connect  " + e.getMessage());
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Disconnected.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "turning the socket off.");
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
    }
}