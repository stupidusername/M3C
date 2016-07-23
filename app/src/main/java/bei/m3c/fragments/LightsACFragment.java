package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import bei.m3c.R;
import bei.m3c.helpers.ThemeHelper;

/**
 * Lights and AC fragment
 */
public class LightsACFragment extends Fragment {

    public static final int DEFAULT_TEMP = 23;

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
    }
}
