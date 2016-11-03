package bei.m3c.connections;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.ConnectKodiJob;
import bei.m3c.jobs.SendKodiMethodJob;
import bei.m3c.kodiMethods.BaseMethod;

public class KodiConnection {

    public static final String TAG = "KodiConnection";
    public static final int END_OF_STREAM = -1;

    private String address;
    private int port;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public boolean isConnected = false;

    public KodiConnection(String address, int port) {
        this.address = address;
        this.port = port;
        JobManagerHelper.getJobManager().addJobInBackground(new ConnectKodiJob(this));
    }

    public void connect() {
        try {
            Log.v(TAG, "Connecting to " + FormatHelper.asAddress(address, port));
            socket = new Socket(address, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            isConnected = true;
            JobManagerHelper.cancelJobsInBackground(TAG);
            Log.v(TAG, "Connected.");
            ArrayList<Character> readChars = new ArrayList<>();
            char readChar = 0;
            while (readChar != END_OF_STREAM) {
                readChar = (char) inputStream.read();
                readChars.add(readChar);
                int beginCount = Collections.frequency(readChars, '{');
                int endCount = Collections.frequency(readChars, '}');
                if (endCount > beginCount) {
                    // drop message
                    readChars = new ArrayList<>();
                } else if (endCount == beginCount) {
                    // Read message and get ready for the next one
                    readMessage(readChars);
                    readChars = new ArrayList<>();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during connection.", e);
        }
    }

    public void disconnect(boolean reconnect) {
        Log.v(TAG, "Disconnecting.");
        isConnected = false;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream = null;
                outputStream = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during disconnection", e);
        }
        if (reconnect) {
            JobManagerHelper.getJobManager().addJobInBackground(new ConnectKodiJob(this));
        }
    }

    public boolean sendMethod(BaseMethod method) {
        boolean success = false;
        try {
            String methodString = method.getJsonRPCString();
            Log.d(TAG, "Sending method: " + methodString);
            outputStream.write(methodString.getBytes(Charset.forName("UTF-8")));
            success = true;
        } catch (Exception e) {
            Log.e(TAG, "Error sending command", e);
            disconnect(true);
        }
        return success;
    }

    public void addMethodJob(BaseMethod method, int interval, boolean retry) {
        // Stop previous method job
        JobManagerHelper.cancelJobsInBackground(method.method);
        // Start new method job
        JobManagerHelper.getJobManager().addJobInBackground(new SendKodiMethodJob(this, method, interval, SendKodiMethodJob.DELAY, retry));
    }

    public void readMessage(ArrayList<Character> readChars) {
        String readString = FormatHelper.asString(readChars);
        Log.d(TAG, "Received message: " + readString);
    }
}
