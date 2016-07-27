package bei.m3c.receivers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import bei.m3c.R;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, context.getString(R.string.device_admin_enabled),
                Toast.LENGTH_SHORT).show();
    }
}
