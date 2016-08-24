package bei.m3c;

import android.content.res.Resources;

import java.util.Locale;

import bei.m3c.helpers.PreferencesHelper;

public class Application extends android.app.Application {

    public static final String LANG = "es";

    private static Application instance;

    public Application() {
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setLocale();
        PreferencesHelper.initialize(this);
    }

    private void setLocale() {
        Resources resources = getBaseContext().getResources();
        android.content.res.Configuration newConfig = resources.getConfiguration();
        newConfig.locale = new Locale(LANG);
        super.onConfigurationChanged(newConfig);
        Locale.setDefault(newConfig.locale);
        resources.updateConfiguration(newConfig, getResources().getDisplayMetrics());
    }
}
