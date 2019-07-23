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
    private final String TAG = getClass().getSimpleName();
    private EditText etFirstName, etLastName;
    private boolean isConnected = true;

    private Socket socket;
    {
        try {
            socket = IO.socket(Keys.BASE_URL);
        } catch(URISyntaxException e) {
            Log.e(TAG, "URISyntaxException caught: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socket.on(Keys.RESPONSE, onMessageReceived);
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
            socket.emit(Keys.NAME, data);
        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Emitter.Listener onMessageReceived = args -> runOnUiThread(() -> {
        JSONObject receivedData = (JSONObject) args[0];
        String hello;
        try {
            hello = receivedData.getString(Keys.HELLO);
        } catch(JSONException e) {
            Log.e(TAG, "JSONException caught: " + e.getMessage());
            return;
        }
        Toast.makeText(MainActivity.this, hello, Toast.LENGTH_SHORT).show();
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "turning the socket off.");
        socket.disconnect();
        socket.off();
    }
}