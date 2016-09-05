package com.bubble.aidansliney.bubble;


import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //The rectangle hit area around the bubbles
    Rect viewHitRect = new Rect();
    //stores if we are currently inside on of the bubbles
    boolean insideLeft, insideRight,insideTop,insideBottom;
    public MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        // shrink the bubbles to 0pct so they scale up from then once the button has been pressed
        descale(R.id.bubbleTop);
        descale(R.id.bubbleBottom);
        descale(R.id.bubbleLeft);
        descale(R.id.bubbleRight);

        //the hop button touch
        Button btn = (Button) findViewById(R.id.hop);
        assert btn != null;
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                //if finger does action then take action
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        //inverse of descale. Bring the bubbles back
                        bounceScale(R.id.bubbleTop);
                        bounceScale(R.id.bubbleBottom);
                        bounceScale(R.id.bubbleLeft);
                        bounceScale(R.id.bubbleRight);
                        Log.d("gesture", "Action was DOWN");
                        break;
                    case MotionEvent.ACTION_UP:
                        //Hide the bubbles on finger up
                        descale(R.id.bubbleTop);
                        descale(R.id.bubbleBottom);
                        descale(R.id.bubbleLeft);
                        descale(R.id.bubbleRight);
                        Log.d("gesture", "Action was Up");
                        break;

                    case MotionEvent.ACTION_MOVE:

                        //logging where the touch is
                        Log.d("vHR", "" + viewHitRect);
                        Log.d("rawX", "" + (int) event.getRawX());
                        Log.d("rawY", "" + (int) event.getRawY());

                        // if we enter or leave a bubble do what needs to be done
                        enterLeaveBubble(R.id.bubbleLeft, event, R.raw.jack,insideLeft);
                        enterLeaveBubble(R.id.bubbleRight, event, R.raw.jack,insideRight);
                        enterLeaveBubble(R.id.bubbleBottom, event, R.raw.daft,insideBottom);
                        enterLeaveBubble(R.id.bubbleTop, event, R.raw.chemical,insideTop);
                        break;
                }
                return true;
            }
        });

    }

    /*used when a touch enters or leave the bubbles rec
    * bubble = the bubble
    * even = the finger event (down, up, move)
    * song = the 30 second clip to add to entering the bubble
    * inside = variable to store event e.g entered topbubble
    * */
    void enterLeaveBubble(int bubble, MotionEvent event, int song, boolean inside) {
        //grab the correct bubble
        final Button btn = (Button) findViewById(bubble);
        //get the rectangle position of the bubble
        getHitRect2(btn, viewHitRect);

        //if we were not yet inside the bubble check if we are now
        if (inside == false) {
            if (viewHitRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                // We are in the Matrix. Store what bubble we have entered
                setInsideBubble(bubble, true);
                Log.d("Inside Bubble ", ""+ bubble);
                //animate the bubble to becoming the primary bubble
                primaryBubble(bubble, song);
                btn.bringToFront();
                btn.requestLayout();
            }
        }
        //if we were inside the bubble check if we have now left
        if (inside == true) {
            if (!viewHitRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                // Elvis has left the building
                setInsideBubble(bubble, false);
                Log.d("elvis has left building", "");
                //animate the bubble back to being a secondary bubble
                secondaryBubble(bubble);
            }
        }
    }
    //store if we have entered or left a bubble
    public void setInsideBubble(int bubble, boolean inside){
        switch(bubble) {
            case R.id.bubbleTop:
                insideTop = inside;
                break;
            case R.id.bubbleBottom:
                insideBottom = inside;
                break;
            case R.id.bubbleLeft:
                insideLeft = inside;
                break;
            case R.id.bubbleRight:
                insideRight = inside;
                break;
        }
    }
    // animate back to being a secondary bubble
    public void secondaryBubble(int circle) {
        Button button = (Button) findViewById(circle);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.secondarybubble);
        button.startAnimation(myAnim);
    }

    // make bubble primary
    public void primaryBubble(int circle, int song) {
        Button button = (Button) findViewById(circle);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.primarybubble);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);

        if (mp != null)
            if(mp.isPlaying()) //
                 mp.stop();

        mp = MediaPlayer.create(this, song);
        mp.start();
    }
    //animate in bubbles
    public void bounceScale(int circle) {
        Button button = (Button) findViewById(circle);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);
    }

    public void descale(int circle) {
        if (mp != null)
            if(mp.isPlaying())
                mp.stop();
        Button button = (Button) findViewById(circle);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.descale);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        // BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        // myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);
    }

// this is the shit part of the code. I had to hardcode in values to get rectangles close to circles. I have no idea if that works across all phone densities.
    private static void getHitRect2(View v, Rect rect) {
        rect.left = (int) (v.getLeft() + v.getTranslationX());
        rect.top = (int) (v.getTop() + v.getTranslationY() + 200);
        rect.right = rect.left + v.getWidth();
        rect.bottom = rect.top + v.getHeight() * 2 - 300;
    }
}

