package bei.m3c.connections;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import bei.m3c.commands.BaseCommand;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.ConnectJob;
import bei.m3c.jobs.SendCommandJob;

public abstract class BaseConnection {

    public static final int KEEPALIVE_DELAY = 10000;

    public static final byte MESSAGE_START = 0x29;
    public static final byte[] MESSAGE_CRC = {0x00, 0x00};
    public static final int END_OF_STREAM = -1;
    public static final int MESSAGE_MAX_TIME_MILLIS = 1000;

    private String address;
    private int port;
    private int commandLenght;
    public String tag;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public boolean isConnected = false;
    private boolean messageInTime;
    private Timer messageTimer;

    public BaseConnection(String address, int port, int commandLenght, String tag) {
        this.address = address;
        this.port = port;
        this.commandLenght = commandLenght;
        this.tag = tag;
        JobManagerHelper.getJobManager().addJobInBackground(new ConnectJob(this));
    }

    public void connect() {
        try {
            Log.v(tag, "Connecting to " + FormatHelper.asAddress(address, port));
            socket = new Socket(address, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            isConnected = true;
            JobManagerHelper.cancelJobs(this.tag);
            JobManagerHelper.getJobManager().addJob(new SendCommandJob(this, getKeepAliveCommand(), KEEPALIVE_DELAY));
            Log.i(tag, "Connected.");
            byte[] message;
            byte readByte = 0;
            while (readByte != END_OF_STREAM) {
                readByte = (byte) inputStream.read();
                if (readByte != MESSAGE_START) {
                    continue;
                }
                int i = 0;
                message = new byte[getMessageLenght()];
                message[i] = readByte;
                i++;
                messageInTime = true;
                messageTimer = new Timer();
                messageTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        messageInTime = false;
                        messageTimer.cancel();
                    }
                }, MESSAGE_MAX_TIME_MILLIS);
                while (i < getMessageLenght() && messageInTime) {
                    readByte = (byte) inputStream.read();
                    if (readByte == END_OF_STREAM) {
                        break;
                    }
                    message[i] = readByte;
                    i++;
                }
                if (i == getMessageLenght()) {
                    messageTimer.cancel();
                    readMessage(message);
                }
            }
        } catch (Exception e) {
            Log.e(tag, "Error during connection.", e);
        }
    }

    public void disconnect() {
        Log.v(tag, "Disconnecting.");
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
            Log.e(tag, "Error during disconnection", e);
        }
        JobManagerHelper.cancelJobs(getKeepAliveCommand().tag);
        JobManagerHelper.getJobManager().addJob(new ConnectJob(this));
    }

    public boolean sendCommand(BaseCommand command) {
        boolean success = false;
        byte[] messageBody = new byte[commandLenght];
        messageBody[0] = command.value;
        for (int i = 0; i < command.params.length; i++) {
            messageBody[i + 1] = command.params[i];
        }
        Log.v(tag, "Sending command: " + FormatHelper.asHexString(messageBody));
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(MESSAGE_START);
            byteArrayOutputStream.write(messageBody);
            byteArrayOutputStream.write(MESSAGE_CRC);
            byte[] message = byteArrayOutputStream.toByteArray();
            outputStream.write(message);
            success = true;
        } catch (Exception e) {
            Log.e(tag, "Error sending command", e);
            disconnect();
        }
        return success;
    }

    public void addCommandJob(BaseCommand command, int interval, boolean retry) {
        // Stop previous command job
        JobManagerHelper.cancelJobsInBackground(command.tag);
        // Start new command job
        JobManagerHelper.getJobManager().addJobInBackground(new SendCommandJob(this, command, interval, SendCommandJob.DELAY, retry));
    }

    public int getMessageLenght() {
        int messageStartLenght = 1;
        return messageStartLenght + commandLenght + MESSAGE_CRC.length;
    }

    public void readMessage(byte[] message) {
        int messageStartLenght = 1;
        byte[] command = Arrays.copyOfRange(message, messageStartLenght, messageStartLenght + commandLenght);
        readCommand(command);
    }

    public abstract void readCommand(byte[] command);

    public abstract BaseCommand getKeepAliveCommand();
}
