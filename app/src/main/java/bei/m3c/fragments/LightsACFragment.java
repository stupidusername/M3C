package bei.m3c.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import bei.m3c.R;
import bei.m3c.commands.TRCGetStatusCommand;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.PICConnectionHelper;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.models.AC;
import bei.m3c.models.Light;
import bei.m3c.widgets.LightWidget;

/**
 * Lights and AC fragment
 */
public class LightsACFragment extends Fragment {

    public static final int DEFAULT_TEMP = 23;
    public static final int LAYOUT_LARGE_LIGHT_COLUMNS_WITH_AC = 4;
    public static final int LAYOUT_LARGE_LIGHT_COLUMNS = 6;
    public static final int LAYOUT_SMALL_WIDGETS_ROW_BOTTOM_MARGIN_DP = 10;
    public static final int GET_STATUS_DELAY_MILLIS = 5000;

    private List<Light> lights;
    private List<LightWidget> largeLightWidgets;
    private List<LightWidget> smallLightWidgets;
    private boolean updateLights = true;
    private AC ac = new AC();
    // views
    private LinearLayout lightsLayout;
    private LinearLayout acLayout;
    private ImageButton acPowerButton;
    private ImageButton acMinusButton;
    private TextView acTempTextView;
    private ImageButton acPlusButton;
    private TextView acStatusTextView;
    private TextView acModeTextView;

    private int updateIndex = 0;
    private int largeLightColumns = LAYOUT_LARGE_LIGHT_COLUMNS;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(TRCGetStatusCommand.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

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
        if (lights == null) {
            lights = PreferencesHelper.getLights();
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

        // Add master controls if there are more than one large light widget
        if (largeLightWidgets.size() > 1) {
            Light masterLight = new Light(getString(R.string.light_master), Light.TYPE_MASTER);
            largeLightWidgets.add(0, new LightWidget(getContext(), masterLight));
        }

        // Add light widgets
        if (PreferencesHelper.showACControls()) {
            largeLightColumns = LAYOUT_LARGE_LIGHT_COLUMNS_WITH_AC;
        }
        for (LightWidget lightWidget : largeLightWidgets) {
            lightsLayout.addView(lightWidget);
        }
        LinearLayout row = null;
        for (int i = 0; i < smallLightWidgets.size(); i++) {
            if (i % largeLightColumns == 0) {
                row = getRow();
                lightsLayout.addView(row);
            }
            if (row != null) {
                LightWidget lightWidget = smallLightWidgets.get(i);
                row.addView(lightWidget);
            }
        }

        // Register UI listeners
        for (final LightWidget lightWidget : largeLightWidgets) {
            switch (lightWidget.light.type) {
                case Light.TYPE_MASTER:
                    lightWidget.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                updateFromMaster(progress - lightWidget.getValue());
                                lightWidget.setValue(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // Do nothing
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // Do nothing
                        }
                    });
                    break;
                case Light.TYPE_DIMMER:
                    lightWidget.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                lightWidget.setValue(progress);
                                updateMaster();
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // Do nothing
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // Do nothing
                        }
                    });
                    break;
            }
        }
        for (final LightWidget lightWidget : smallLightWidgets) {
            switch (lightWidget.light.type) {
                case Light.TYPE_ON_OFF:
                    lightWidget.powerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lightWidget.toggle();
                            updateMaster();
                        }
                    });
                    break;
            }
        }
        updateMaster();

        // Register events and jobs
        EventBus.getDefault().register(this);
        PICConnectionHelper.sendCommand(new TRCGetStatusCommand(), GET_STATUS_DELAY_MILLIS);
    }

    // Fix for getting the seekbar progress after the fragment is hidden
    @Override
    public void onResume() {
        super.onResume();
        for (LightWidget lightWidget : largeLightWidgets) {
            lightWidget.setValue(lightWidget.getValue());
        }
    }

    public LinearLayout getRow() {
        LinearLayout row = new LinearLayout(getContext());
        int columnWidth = Math.round(getResources().getDimension(R.dimen.lightWidgetOnOffWidth));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(columnWidth * largeLightColumns, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        row.setOrientation(LinearLayout.HORIZONTAL);
        Resources resources = getResources();
        int marginBottom = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LAYOUT_SMALL_WIDGETS_ROW_BOTTOM_MARGIN_DP, resources.getDisplayMetrics()));
        layoutParams.setMargins(0, 0, 0, marginBottom);
        row.setLayoutParams(layoutParams);
        return row;
    }

    private boolean masterExists() {
        return !largeLightWidgets.isEmpty() && largeLightWidgets.get(0).light.type == Light.TYPE_MASTER;
    }

    private int getRealLightsCount() {
        int count = largeLightWidgets.size();
        if (masterExists()) {
            count--;
        }
        return count;
    }

    private boolean lightWidgetIsDeltable(LightWidget lightWidget, int delta) {
        int newValue = lightWidget.getValue() + delta;
        return newValue >= 0 && newValue <= Light.MAX_VALUE;
    }

    private List<LightWidget> getDeltableLightWidgets(int delta) {
        List<LightWidget> deltableLightWidgets = new ArrayList<>();
        for (LightWidget lightWidget : getChildrenLightWidgets()) {
            if (lightWidgetIsDeltable(lightWidget, delta)) {
                deltableLightWidgets.add(lightWidget);
            }
        }
        return deltableLightWidgets;
    }

    private List<LightWidget> getChildrenLightWidgets() {
        List<LightWidget> children = new ArrayList<>();
        int offset = 0;
        if (masterExists()) {
            offset = 1;
        }
        for (LightWidget lightWidget : largeLightWidgets.subList(offset, largeLightWidgets.size())) {
            children.add(lightWidget);
        }
        return children;
    }

    private void updateMaster() {
        if (masterExists()) {
            int realLightsCount = getRealLightsCount();
            int sum = 0;
            // Do not add master widget value
            for (LightWidget lightWidget : largeLightWidgets.subList(1, largeLightWidgets.size())) {
                sum += lightWidget.getValue();
            }
            largeLightWidgets.get(0).setValue(Math.round(sum / realLightsCount));
        }
    }

    private void updateFromMaster(int delta) {
        int realLightsCount = getRealLightsCount();
        int totalDelta = delta * realLightsCount;
        int individualDelta = totalDelta >= 0 ? 1 : -1;
        List<LightWidget> deltableLightWidgets = getDeltableLightWidgets(individualDelta);
        while (!deltableLightWidgets.isEmpty() && totalDelta != 0) {
            while (updateIndex < deltableLightWidgets.size() && totalDelta != 0) {
                LightWidget lightWidget = deltableLightWidgets.get(updateIndex);
                lightWidget.setValue(lightWidget.getValue() + individualDelta);
                totalDelta -= individualDelta;
                int oldSize = deltableLightWidgets.size();
                deltableLightWidgets = getDeltableLightWidgets(individualDelta);
                if (oldSize == deltableLightWidgets.size()) {
                    updateIndex++;
                }
                if (deltableLightWidgets.size() != 0) {
                    updateIndex %= deltableLightWidgets.size();
                }
            }
        }
    }
}
