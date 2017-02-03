package org.projectproto.wifirgbcontroller;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class FloatingWindow extends Service{

    private WindowManager wm;
    private LinearLayout ll;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(128, 255, 0, 0));
        ll.setLayoutParams(llParams);

        WindowManager.LayoutParams wlparams = new WindowManager.LayoutParams(
                                                    400, 150,
                                                    WindowManager.LayoutParams.TYPE_PHONE,
                                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                    PixelFormat.TRANSLUCENT );
        wlparams.x = 0;
        wlparams.y = 0;
        wlparams.gravity = Gravity.CENTER;

        wm.addView(ll, wlparams);
    }
}
