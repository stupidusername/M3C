package bei.m3c.preferences;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import bei.m3c.R;
import bei.m3c.activities.LightPreferencesActivity;
import bei.m3c.helpers.PreferencesHelper;
import bei.m3c.models.Light;

public class LightPreference extends Preference {

    public static final String PREFIX_NAME = "light_name_";
    public static final String PREFIX_TYPE = "light_type_";

    private int index;
    private boolean editTextHadFocus = false;
    private SharedPreferences.Editor editor;
    // Views
    private EditText editText;
    private Spinner spinner;
    private ImageButton deleteButton;

    public LightPreference(Context context, int index) {
        super(context);
        this.index = index;
        setLayoutResource(R.layout.dialog_light_preference);
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        final View layout = super.onCreateView(parent);

        editor = PreferencesHelper.getSharedPreferences().edit();

        // Find views
        editText = (EditText) layout.findViewById(R.id.light_preference_name_edittext);
        spinner = (Spinner) layout.findViewById(R.id.light_preference_type_spinner);
        deleteButton = (ImageButton) layout.findViewById(R.id.light_preference_delete_button);

        // Populate spinner
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Light.getTypeNames());
        spinner.setAdapter(adapter);

        // Get saved values
        String name = PreferencesHelper.getSharedPreferences().getString(getNameKey(index), getDefaultName());
        int type = PreferencesHelper.getSharedPreferences().getInt(getTypeKey(index), getDefaultType());

        editText.setText(name);
        spinner.setSelection(type);

        // Set UI listeners
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && editTextHadFocus) {
                    saveName();
                    editTextHadFocus = false;
                } else {
                    editTextHadFocus = true;
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                FragmentManager fragmentManager = ((LightPreferencesActivity) getContext()).getFragmentManager();
                LightPreferencesActivity.LightPreferencesFragment fragment =
                        (LightPreferencesActivity.LightPreferencesFragment) fragmentManager.findFragmentById(android.R.id.content);
                fragment.removeLightPreference(index);
            }
        });

        // Save if lights was not saved before
        String savedName = PreferencesHelper.getSharedPreferences().getString(getNameKey(index), null);
        if (savedName == null) {
            save();
        }
        return layout;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDefaultName() {
        String defaultName = getContext().getString(R.string.light_default_name) + " " + (index + 1);
        return defaultName;
    }

    public static int getDefaultType() {
        return Light.TYPE_ON_OFF;
    }

    public static String getNameKey(int index) {
        return PREFIX_NAME + index;
    }

    public static String getTypeKey(int index) {
        return PREFIX_TYPE + index;
    }

    public void save() {
        saveName();
        saveType();
    }

    public void delete() {
        // Remove focus listener because it will be fired when the view gets deleted
        editText.setOnFocusChangeListener(null);
        editor.putString(getNameKey(index), null);
        editor.remove(getTypeKey(index));
        editor.apply();
    }

    private void saveName() {
        editor.putString(getNameKey(index), editText.getText().toString());
        editor.apply();
    }

    private void saveType() {
        editor.putInt(getTypeKey(index), spinner.getSelectedItemPosition());
        editor.apply();
    }
}
