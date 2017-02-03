package org.projectproto.wifirgbcontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatingWindow extends Service{

    private WindowManager wm;
    private LinearLayout ll;
    private Button btnStop;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);
        btnStop = new Button(this);

        ViewGroup.LayoutParams blParams = new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT );
        btnStop.setText("Stop");
        btnStop.setLayoutParams(blParams);

        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(64, 152, 255, 152));
        ll.setLayoutParams(llParams);

        final WindowManager.LayoutParams wlparams = new WindowManager.LayoutParams(
                                                    640, 480,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                    PixelFormat.TRANSLUCENT );
        wlparams.x = 0;
        wlparams.y = 0;
        wlparams.gravity = Gravity.CENTER;

        ll.addView(btnStop);
        wm.addView(ll, wlparams);

        ll.setOnTouchListener(new View.OnTouchListener() {
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
                    break;
                case MotionEvent.ACTION_MOVE:
                    nextparams.x = (int) (x + event.getRawX() - touchedX);
                    nextparams.y = (int) (y + event.getRawY() - touchedY);

                    wm.updateViewLayout(ll, nextparams);
                    break;
                }
                return false;
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(ll);
                stopSelf();
            }
        });
    }
}
