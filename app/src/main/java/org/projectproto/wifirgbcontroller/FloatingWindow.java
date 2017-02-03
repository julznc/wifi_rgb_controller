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
import android.widget.RelativeLayout;

public class FloatingWindow extends Service{

    private WindowManager wm;
    private RelativeLayout layout;
    private Button btnStop;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        layout = new RelativeLayout(this);
        btnStop = new Button(this);

        ViewGroup.LayoutParams blParams = new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT );
        btnStop.setText("Exit");
        btnStop.setLayoutParams(blParams);

        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(
                                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                                        RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setBackgroundColor(Color.argb(96, 152, 255, 152));
        layout.setLayoutParams(llParams);

        final WindowManager.LayoutParams wlparams = new WindowManager.LayoutParams(
                                                    640, 480,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                    PixelFormat.TRANSLUCENT );
        wlparams.x = 0;
        wlparams.y = 0;
        wlparams.gravity = Gravity.CENTER;

        layout.addView(btnStop);
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
                    break;
                case MotionEvent.ACTION_MOVE:
                    nextparams.x = (int) (x + event.getRawX() - touchedX);
                    nextparams.y = (int) (y + event.getRawY() - touchedY);

                    wm.updateViewLayout(layout, nextparams);
                    break;
                }
                return false;
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(layout);
                stopSelf();
            }
        });
    }
}
