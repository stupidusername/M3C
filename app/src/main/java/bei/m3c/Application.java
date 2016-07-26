package bei.m3c;

import bei.m3c.helpers.PreferencesHelper;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesHelper.initialize(this);
    }
}
