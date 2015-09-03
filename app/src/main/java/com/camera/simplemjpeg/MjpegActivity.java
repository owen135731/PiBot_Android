package com.camera.simplemjpeg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URI;

import com.pubnub.api.*;
import org.json.*;
//import org.json.JSONObject;

@SuppressWarnings("ALL")
public class MjpegActivity extends Activity {

    private boolean forwardvalue = false;
    private boolean backwardvalue = false;
    private boolean leftvalue = false;
    private boolean rightvalue = false;

    private static final boolean DEBUG = false;
    private static final String TAG = "MJPEG";

    private MjpegView mv = null;
    String URL;

    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;

    private int width = 640;
    private int height = 480;

    private int ip_ad1 = 192;
    private int ip_ad2 = 168;
    private int ip_ad3 = 2;
    private int ip_ad4 = 1;
    private int ip_port = 80;
    private String ip_command = "?action=stream";

    private boolean suspending = false;

    final Handler handler = new Handler();

    final Pubnub pubnub = new Pubnub("PUBLISHKEY", "SUBSCRIBEKEY");
    String uuid = "YOURUUID";

    final Callback callback = new Callback() {
        public void successCallback(String channel, Object response) {
            System.out.println(response.toString());
        }
        public void errorCallback(String channel, PubnubError error) {
            System.out.println(error.toString());
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pubnub.setUUID(uuid);

        try {
            pubnub.subscribe("gpio-raspberry-control", new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

        setContentView(R.layout.main);

        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getInt("width", width);
        height = preferences.getInt("height", height);
        ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
        ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
        ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
        ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
        ip_port = preferences.getInt("ip_port", ip_port);
        ip_command = preferences.getString("ip_command", ip_command);

        StringBuilder sb = new StringBuilder();
        String s_http = "http://";
        String s_dot = ".";
        String s_colon = ":";
        String s_slash = "/";
        sb.append(s_http);
        sb.append(ip_ad1);
        sb.append(s_dot);
        sb.append(ip_ad2);
        sb.append(s_dot);
        sb.append(ip_ad3);
        sb.append(s_dot);
        sb.append(ip_ad4);
        sb.append(s_colon);
        sb.append(ip_port);
        sb.append(s_slash);
        sb.append(ip_command);
        URL = new String(sb);

        setContentView(R.layout.main);
        mv = (MjpegView) findViewById(R.id.mv);
        if (mv != null) {
            mv.setResolution(width, height);
        }

        setTitle(R.string.title_connecting);
        new DoRead().execute(URL);
    }
/*------------------------------------------------------------------------------------------------*/
    public void forwardclick(View view){
        String message;
        if (!forwardvalue) {
            message = "forward";

        } else {
            message = "stop";

        }
        forwardvalue = !forwardvalue;
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }
    public void backwardclick(View view){
        String message;
        if (!backwardvalue) {
            message = "backward";
        } else {
            message = "stop";
        }
        backwardvalue = !backwardvalue;
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }
    public void leftclick(View view){
        String message;
        if (!leftvalue) {
            message = "left";
        } else {
            message = "stop";
        }
        leftvalue = !leftvalue;
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }
    public void rightclick(View view){
        String message;
        if (!rightvalue) {
            message = "right";
        } else {
            message = "stop";
        }
        rightvalue = !rightvalue;
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }
    public void stopclick(View view){
        String message;
        message = "stop";
        forwardvalue = false;
        backwardvalue = false;
        leftvalue = false;
        rightvalue = false;
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }
    public void killclick(View view){
        String message;
        message = "kill";
        JSONObject request = new JSONObject();
        try {
            request.put("req", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pubnub.publish("gpio-raspberry-control", request, callback);
    }

/*------------------------------------------------------------------------------------------------*/
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
        if (mv != null) {
            if (suspending) {
                new DoRead().execute(URL);
                suspending = false;
            }
        }

    }

    public void onStart() {
        if (DEBUG) Log.d(TAG, "onStart()");
        super.onStart();
    }

    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
        super.onPause();
        if (mv != null) {
            if (mv.isStreaming()) {
                mv.stopPlayback();
                suspending = true;
            }
        }
    }

    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
        super.onStop();
    }

    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");

        if (mv != null) {
            mv.freeCameraMemory();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings_intent = new Intent(MjpegActivity.this, SettingsActivity.class);
                settings_intent.putExtra("width", width);
                settings_intent.putExtra("height", height);
                settings_intent.putExtra("ip_ad1", ip_ad1);
                settings_intent.putExtra("ip_ad2", ip_ad2);
                settings_intent.putExtra("ip_ad3", ip_ad3);
                settings_intent.putExtra("ip_ad4", ip_ad4);
                settings_intent.putExtra("ip_port", ip_port);
                settings_intent.putExtra("ip_command", ip_command);
                startActivityForResult(settings_intent, REQUEST_SETTINGS);
                return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    width = data.getIntExtra("width", width);
                    height = data.getIntExtra("height", height);
                    ip_ad1 = data.getIntExtra("ip_ad1", ip_ad1);
                    ip_ad2 = data.getIntExtra("ip_ad2", ip_ad2);
                    ip_ad3 = data.getIntExtra("ip_ad3", ip_ad3);
                    ip_ad4 = data.getIntExtra("ip_ad4", ip_ad4);
                    ip_port = data.getIntExtra("ip_port", ip_port);
                    ip_command = data.getStringExtra("ip_command");

                    if (mv != null) {
                        mv.setResolution(width, height);
                    }
                    SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("width", width);
                    editor.putInt("height", height);
                    editor.putInt("ip_ad1", ip_ad1);
                    editor.putInt("ip_ad2", ip_ad2);
                    editor.putInt("ip_ad3", ip_ad3);
                    editor.putInt("ip_ad4", ip_ad4);
                    editor.putInt("ip_port", ip_port);
                    editor.putString("ip_command", ip_command);

                    editor.commit();

                    new RestartApp().execute();
                }
                break;
        }
    }

    public void setImageError() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTitle(R.string.title_imageerror);
            }
        });
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
            if (DEBUG) Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if (DEBUG)
                    Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-ClientProtocolException", e);
                }
                //Error connecting to camera
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-IOException", e);
                }
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if (result != null) {
                result.setSkip(1);
                setTitle(R.string.app_name);
            } else {
                setTitle(R.string.title_disconnected);
            }
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }

    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            MjpegActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            startActivity((new Intent(MjpegActivity.this, MjpegActivity.class)));
        }
    }
}
