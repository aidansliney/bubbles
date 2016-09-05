package com.bubble.aidansliney.bubble;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by aidansliney on 04/09/2016.
 */
class CustomButtonView extends Button {

    public CustomButtonView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        Log.d("boom", String.valueOf(action));

        return super.onTouchEvent(event);
    }
}