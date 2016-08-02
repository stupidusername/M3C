package bei.m3c.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bei.m3c.R;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.models.Light;
import bei.m3c.preferences.LightPreference;

public class LightPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LightPreferencesFragment()).commit();
    }

    public static class LightPreferencesFragment extends PreferenceFragment {

        private ImageButton addButton;
        private PreferenceScreen preferenceScreen;
        private List<LightPreference> lightPreferences;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(preferenceScreen);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_light_preferences, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            addLightPreferences();
            addButton = (ImageButton) view.findViewById(R.id.light_preferences_add_button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLight();
                }
            });
        }

        private void addLightPreferences() {
            lightPreferences = new ArrayList<>();
            List<Light> lights = PreferencesHelper.getLights();
            for (int i = 0; i < lights.size(); i++) {
                addLightPreference(i);
            }
        }

        private void addLightPreference(int index) {
            LightPreference preference = new LightPreference(getActivity(), index);
            preferenceScreen.addPreference(preference);
            lightPreferences.add(preference);
        }

        private void addLight() {
            if (lightPreferences.size() < Light.MAX_LIGHTS) {
                addLightPreference(lightPreferences.size());
            } else {
                Toast.makeText(getActivity(), getString(R.string.settings_max_lights_reached),
                        Toast.LENGTH_SHORT).show();
            }
        }

        private void reloadLightPreferences() {
            List<LightPreference> savedLightPreferences = lightPreferences;
            clearSavedLightPreferences();
            for(int i = 0; i < savedLightPreferences.size(); i++) {
                // Re-index lights
                LightPreference preference = savedLightPreferences.get(i);
                preference.setIndex(i);
                preference.save();
            }
            addLightPreferences();
        }

        public void removeLightPreference(int index) {
            LightPreference preference = lightPreferences.get(index);
            preferenceScreen.removePreference(preference);
            lightPreferences.remove(index);
            reloadLightPreferences();
        }

        private void clearSavedLightPreferences() {
            for (int i = 0; i < lightPreferences.size(); i++) {
                LightPreference preference = lightPreferences.get(i);
                preferenceScreen.removePreference(preference);
                preference.delete();
            }
        }
    }
}
