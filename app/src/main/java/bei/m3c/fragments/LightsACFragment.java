package bei.m3c.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bei.m3c.R;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.Light;
import bei.m3c.widgets.LightWidget;

/**
 * Lights and AC fragment
 */
public class LightsACFragment extends Fragment {

    public static final int DEFAULT_TEMP = 23;
    public static final int LAYOUT_LARGE_LIGHT_COLUMNS = 4;

    private List<Light> lights;
    private List<LightWidget> largeLightWidgets;
    private List<LightWidget> smallLightWidgets;
    // views
    private LinearLayout lightsLayout;
    private LinearLayout acLayout;
    private ImageButton acPowerButton;
    private ImageButton acMinusButton;
    private TextView acTempTextView;
    private ImageButton acPlusButton;
    private TextView acStatusTextView;
    private TextView acModeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lights_ac, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get lights from preferences
        lights = PreferencesHelper.getLights();
        // Add master controls if there are more than one light
        if (lights.size() > 1) {
            Light masterLight = new Light(getString(R.string.light_master), Light.TYPE_MASTER);
            lights.add(0, masterLight);
        }

        lightsLayout = (LinearLayout) view.findViewById(R.id.lights_layout);
        acLayout = (LinearLayout) view.findViewById(R.id.ac_layout);
        acPowerButton = (ImageButton) view.findViewById(R.id.ac_power_button);
        acMinusButton = (ImageButton) view.findViewById(R.id.ac_minus_button);
        acTempTextView = (TextView) view.findViewById(R.id.ac_temp_textview);
        acPlusButton = (ImageButton) view.findViewById(R.id.ac_plus_button);
        acStatusTextView = (TextView) view.findViewById(R.id.ac_status_textview);
        acModeTextView = (TextView) view.findViewById(R.id.ac_mode_textview);

        acTempTextView.setText(DEFAULT_TEMP + " " + getString(R.string.ac_temp_unit));
        acStatusTextView.setText(" " + getString(R.string.ac_status_off));
        acModeTextView.setText(" " + getString(R.string.ac_mode_auto));

        ThemeHelper.setColorStateListTheme(acLayout);
        ThemeHelper.setImageButtonTheme(acPowerButton);
        ThemeHelper.setImageButtonTheme(acMinusButton);
        ThemeHelper.setImageButtonTheme(acPlusButton);

        // Hide ac controls if not needed
        if (!PreferencesHelper.showACControls()) {
            ((ViewGroup) acLayout.getParent()).removeView(acLayout);
        }

        // Divide lights to be suitable for the table layout
        largeLightWidgets = new ArrayList<>();
        smallLightWidgets = new ArrayList<>();
        for (Light light : lights) {
            // Divide lights to be suitable for the table layout
            if (light.type == Light.TYPE_ON_OFF) {
                smallLightWidgets.add(new LightWidget(getContext(), light));
            } else {
                largeLightWidgets.add(new LightWidget(getContext(), light));
            }
        }
        // Add light widgets
        for (LightWidget lightWidget : largeLightWidgets) {
            lightsLayout.addView(lightWidget);
        }
        LinearLayout row = null;
        for (int i = 0; i < smallLightWidgets.size(); i++) {
            if (i % LAYOUT_LARGE_LIGHT_COLUMNS == 0) {
                row = getRow();
                lightsLayout.addView(row);
            }
            if (row != null) {
                LightWidget lightWidget = smallLightWidgets.get(i);
                row.addView(lightWidget);
            }
        }
    }

    public LinearLayout getRow() {
        LinearLayout row = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Resources resources = getResources();
        int marginBottom = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, resources.getDisplayMetrics()));
        layoutParams.setMargins(0, 0, 0, marginBottom);
        row.setLayoutParams(layoutParams);
        return row;
    }
}
