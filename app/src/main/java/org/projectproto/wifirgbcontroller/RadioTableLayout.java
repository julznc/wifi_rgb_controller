package org.projectproto.wifirgbcontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;


public class RadioTableLayout extends TableLayout implements OnClickListener{

    interface RadioBtnCallback {
        void select(int id);
    }

    private RadioButton activeRadioButton = null;
    private RadioBtnCallback callback = null;

    public RadioTableLayout(Context context) {
        super(context);
    }
    public RadioTableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setCallback(RadioBtnCallback cb ) {
        callback = cb;
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if ( activeRadioButton != null ) {
            activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        activeRadioButton = rb;
        if (null != callback) {
            callback.select(rb.getId());
        }
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow)child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i=0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if ( v instanceof RadioButton ) {
                v.setOnClickListener(this);
            }
        }
    }
}
