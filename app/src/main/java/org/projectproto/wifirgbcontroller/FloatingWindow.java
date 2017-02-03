package org.projectproto.wifirgbcontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
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

public class FloatingWindow extends Service{

    private WindowManager wm;
    private RelativeLayout layout;
    private Button btnExit;
    private EditText txtInput;
    private Button btnSetText;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = (RelativeLayout) inflater.inflate(R.layout.controller,null);
        layout.setBackgroundColor(Color.argb(96, 152, 255, 152));

        btnExit = (Button) layout.findViewById(R.id.btnExit);
        txtInput = (EditText) layout.findViewById(R.id.editIntput);
        btnSetText = (Button) layout.findViewById(R.id.btnSetText);

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
    }
}
