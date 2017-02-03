package org.projectproto.wifirgbcontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class FloatingWindow extends Service{

    private WindowManager wm;
    private RelativeLayout layout;
    private Button btnStop;
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
        layout = new RelativeLayout(this);
        btnStop = new Button(this);
        txtInput = new EditText(this);
        btnSetText = new Button(this);

        RelativeLayout.LayoutParams btnStopLayourParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnStop.setText("Exit");
        btnStopLayourParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        btnStopLayourParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        btnStop.setLayoutParams(btnStopLayourParams);

        RelativeLayout.LayoutParams txtInputLayourParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        txtInput.setHint("message");
        txtInputLayourParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        txtInputLayourParams.addRule(RelativeLayout.BELOW, btnStop.getId());
        txtInput.setLayoutParams(txtInputLayourParams);

        RelativeLayout.LayoutParams btnSetTextLayourParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        btnSetText.setText("Set");
        btnSetTextLayourParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //btnSetTextLayourParams.addRule(RelativeLayout.BELOW, txtInput.getId());
        btnSetText.setLayoutParams(btnSetTextLayourParams);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                                        RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setBackgroundColor(Color.argb(96, 152, 255, 152));
        layout.setLayoutParams(layoutParams);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final WindowManager.LayoutParams wlparams = new WindowManager.LayoutParams(
                                                    (size.x * 7) / 10 /* width */,
                                                    (size.y * 3) / 10 /* height */,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                                    PixelFormat.TRANSLUCENT );


        wlparams.gravity = Gravity.CENTER;

        layout.addView(btnStop);
        layout.addView(txtInput);
        layout.addView(btnSetText);
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

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(layout);
                stopSelf();
            }
        });
    }
}
