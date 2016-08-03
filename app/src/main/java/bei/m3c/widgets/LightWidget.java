package bei.m3c.widgets;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Light;

public class LightWidget extends LinearLayout {

    private Light light;
    private LinearLayout widgetLayout;
    private TextView nameTextView;
    private SeekBar seekBar;
    private ImageButton powerButton;

    public LightWidget(Context context, Light light) {
        super(context);
        this.light = light;
        createView();
    }

    private void createView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // The default view is the on/off one beacause dimmer can fuck up on/off lights
        int layout = R.layout.widget_light_on_off;
        switch (light.type) {
            case Light.TYPE_DIMMER:
            case Light.TYPE_MASTER:
                layout = R.layout.widget_light_dimmer;
                break;
        }
        inflater.inflate(layout, this);

        // Get views
        widgetLayout = (LinearLayout) findViewById(R.id.widget_light_layout);
        nameTextView = (TextView) findViewById(R.id.widget_light_name_textview);
        seekBar = (SeekBar) findViewById(R.id.widget_light_seekbar);
        powerButton = (ImageButton) findViewById(R.id.widget_light_power_button);

        if (nameTextView != null) {
            // Increase text size for master control
            if (light.type == Light.TYPE_MASTER) {
                float size = getContext().getResources().getDimension(R.dimen.textLarge);
                nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            nameTextView.setText(light.name);
        }
        if (seekBar != null) {
            ThemeHelper.setSeekBarTheme(seekBar);
        }
        if (powerButton != null) {
            ThemeHelper.setImageButtonTheme(powerButton);
        }

        // Draw border for on/off light widgets
        if (light.type == Light.TYPE_ON_OFF) {
            ThemeHelper.setColorStateListTheme(widgetLayout);
        }
    }
}
