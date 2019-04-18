package bei.m3c.helpers;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public abstract class DownloadHelper {

    public static final String TAG = "DownloadHelper";

    public static void download(String urlString, String path) {
        int count;
        OutputStream output = null;
        URL url = null;
        InputStream input = null;
        try {
            url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();
        } catch (Exception e) {
            Log.e(TAG, "URL error.", e);
        }

        try {
            // download the file
            input = new BufferedInputStream(url.openStream(), 8192);
        } catch (Exception e) {
            Log.e(TAG, "Open stream error.", e);
        }

        try {
            // Output stream
            output = new FileOutputStream(path);
        } catch (Exception e) {
            Log.e(TAG, "FileOutputStream error.", e);
        }

        try {
            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                // writing data to file
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            Log.e(TAG, "Input read error.", e);
        }

        try {
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e(TAG, "Streams closing error.", e);
        }
    }
}
