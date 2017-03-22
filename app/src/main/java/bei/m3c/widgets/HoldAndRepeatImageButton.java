package bei.m3c.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import bei.m3c.R;

/**
 * An image button that repeats an action on a given interval.
 */
public class HoldAndRepeatImageButton extends ImageButton {

    public static final int DEFAULT_INTERVAL_MILLIS = 500;

    public HoldAndRepeatImageButton(Context context) {
        this(context, null);
    }

    public HoldAndRepeatImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public HoldAndRepeatImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HoldAndRepeatImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFocusable(true);
    }

    public void setRepeatableAction(Runnable action) {
        this.setRepeatableAction(action, DEFAULT_INTERVAL_MILLIS);
    }

    public void setRepeatableAction(final Runnable action, final int intervalMillis) {
        this.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.post(mAction);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    action.run();
                    mHandler.postDelayed(this, intervalMillis);
                }
            };

        });
    }
}
