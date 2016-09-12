package bei.m3c.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public abstract class RootHelper {

    private static final String TAG = "RootHelper";

    public static boolean canRunRootCommands() {
        boolean retval = false;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(
                    suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(
                    suProcess.getInputStream());

            if (null != os && null != osRes) {
                // Getting the id of the current user to check if this is root
                os.writeBytes("id\n");
                os.flush();

                @SuppressWarnings("deprecation")
                String currUid = osRes.readLine();
                boolean exitSu;
                if (currUid == null) {
                    retval = false;
                    exitSu = false;
                } else if (currUid.contains("uid=0") == true) {
                    retval = true;
                    exitSu = true;
                } else {
                    retval = false;
                    exitSu = true;
                }

                if (exitSu) {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output
            // stream (os) after su failed, meaning that the device is not
            // rooted

            retval = false;
            Log.e(TAG, "Root access rejected.", e);
        }

        if (!retval) {
            Log.w(TAG, "Root access not enabled.");
        }
        return retval;
    }

    public static boolean execute(String... commands) {
        boolean retval = false;

        try {
            if (null != commands && commands.length > 0) {
                Process suProcess = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(
                        suProcess.getOutputStream());

                // Execute commands that require root access
                for (String currCommand : commands) {
                    Log.d(TAG, "Executing: " + currCommand);
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }

                os.writeBytes("exit\n");
                os.flush();

                try {
                    int suProcessRetval = suProcess.waitFor();
                    if (255 != suProcessRetval) {
                        // Root access granted
                        retval = true;
                    } else {
                        // Root access denied
                        retval = false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error executing root action.", e);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Can't get root access.", e);
        } catch (SecurityException e) {
            Log.w(TAG, "Can't get root access.", e);
        } catch (Exception e) {
            Log.w(TAG, "Error executing internal operation.", e);
        }

        return retval;
    }
}