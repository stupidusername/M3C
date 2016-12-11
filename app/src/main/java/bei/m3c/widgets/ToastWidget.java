package bei.m3c.widgets;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;

public class ToastWidget extends PopupWindow {

    public static final int DISMISS_DELAY_MILLIS = 5000;

    private Timer timer;
    private Context context;
    private View parentView;
    private LinearLayout linearLayout;
    private TextView textView;

    @SuppressWarnings("deprecation")
    public ToastWidget(Context context, String text, View parentView) {
        super(getView(context), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.parentView = parentView;
        linearLayout = (LinearLayout) getContentView().findViewById(R.id.widget_toast_layout);
        textView = (TextView) getContentView().findViewById(R.id.widget_toast_textview);
        ThemeHelper.setLayoutTheme(linearLayout);
        textView.setText(text);
        setAnimationStyle(android.R.style.Animation_Dialog);
    }

    public void flash() {
        showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                });
            }
        }, DISMISS_DELAY_MILLIS);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        timer.cancel();
    }

    public static View getView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
    }
}
