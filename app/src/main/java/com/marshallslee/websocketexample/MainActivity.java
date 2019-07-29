package com.marshallslee.websocketexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.marshallslee.websocketexample.keys.Keys;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity implements WebSocketEcho.WebSocketInteractor {
    private final String TAG = getClass().getSimpleName();
    private EditText etFirstName, etLastName;
    private WebSocket mWebSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebSocketEcho.getInstance(this);

        Button btnQuery = findViewById(R.id.btnQuery);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                callMessage(firstName, lastName);
            }
        });
    }

    private void callMessage(String firstName, String lastName) {
        mWebSocket.send(firstName);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        mWebSocket = webSocket;
    }

    @Override
    public void onGetMessage(String message) {

    }
}