package org.projectproto.wifirgbcontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class FloatingWindow extends Service{

    private WindowManager wm;
    private RelativeLayout layout;

    private Button btnExit;
    private Button btnSetText;
    private EditText txtIPaddr;
    private EditText txtInput;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = (RelativeLayout) inflater.inflate(R.layout.controller, null, false);
        layout.setBackgroundColor(Color.argb(96, 152, 255, 152));

        txtIPaddr = (EditText) layout.findViewById(R.id.editIPaddr);
        btnExit = (Button) layout.findViewById(R.id.btnExit);

        txtInput = (EditText) layout.findViewById(R.id.editIntput);
        btnSetText = (Button) layout.findViewById(R.id.btnSetText);

        txtIPaddr.setText("192.168.0.172");
        btnExit.setBackgroundColor(Color.argb(128, 255, 218, 185));
        btnSetText.setBackgroundColor(Color.argb(128, 0, 255, 255));

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final WindowManager.LayoutParams wlparams = new WindowManager.LayoutParams(
                                                    (size.x * 7) / 10 /* width */,
                                                    (size.y * 3) / 10 /* height */,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                                                    PixelFormat.TRANSLUCENT );


        wlparams.gravity = Gravity.CENTER;
        wm.addView(layout, wlparams);

        layout.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams nextparams = wlparams;
            int x, y;
            float touchedX, touchedY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = nextparams.x;
                    y = nextparams.y;
                    touchedX = event.getRawX();
                    touchedY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    nextparams.x = (int) (x + event.getRawX() - touchedX);
                    nextparams.y = (int) (y + event.getRawY() - touchedY);

                    wm.updateViewLayout(layout, nextparams);
                    return true;
                }
                return false;
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(layout);
                stopSelf();
            }
        });

        btnSetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = txtIPaddr.getText().toString();

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("text", txtInput.getText().toString());
                parameters.put("color", "0xaabbcc");

                httpPost("http://" + ip + "/text", parameters);
            }
        });
    }

    private String httpPost(String reqUrl, HashMap<String, String> parameters) {
        class PostTask extends AsyncTask<String, String, String>{

            private HashMap<String, String> _params;

            public PostTask(HashMap<String, String> postParams) {
                super();
                _params = postParams;
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];

                URL url;

                String response = "";

                try {
                    url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(3*1000);
                    conn.setConnectTimeout(3*1000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostData(_params));
                    writer.flush();
                    writer.close();

                    int responseCode = conn.getResponseCode();
                    if (HttpsURLConnection.HTTP_OK == responseCode) {
                        String line;
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        while (null !=(line=br.readLine())) {
                            response+=line;
                        }
                    } else {
                        response="";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getApplicationContext(), "done PostTask", Toast.LENGTH_SHORT).show();
            }

            private String getPostData(HashMap<String, String> params) throws UnsupportedEncodingException {
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
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
        }

        PostTask task = new PostTask(parameters);
        task.execute(reqUrl);

        return "";
    }
}
