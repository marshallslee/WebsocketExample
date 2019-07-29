package com.marshallslee.websocketexample;

import android.util.Log;

import com.marshallslee.websocketexample.keys.Keys;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketEcho extends WebSocketListener {

    private final String TAG = this.getClass().getSimpleName();
    private static WebSocketEcho mWebSocketEcho = null;
    private static WebSocketInteractor mWebSocketInteractor;

    private WebSocketEcho(){}

    public static WebSocketEcho getInstance(WebSocketInteractor webSocketInteractor){
        if(mWebSocketEcho==null){
            mWebSocketInteractor = webSocketInteractor;
            mWebSocketEcho = new WebSocketEcho();
            mWebSocketEcho.run();
        }
        return mWebSocketEcho;
    }

    private void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(Keys.BASE_URL)
                .build();
        client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocketInteractor.onOpen(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.e(TAG, "MESSAGE: " + text);
        mWebSocketInteractor.onGetMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.e(TAG, "MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.e(TAG, "CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Failure: " + t.getMessage());
        t.printStackTrace();
    }

    public interface WebSocketInteractor{
        void onOpen(WebSocket webSocket);
        void onGetMessage(String message);
    }
}
