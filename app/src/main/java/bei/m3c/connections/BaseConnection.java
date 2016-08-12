package bei.m3c.connections;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import bei.m3c.commands.BaseCommand;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.ConnectJob;
import bei.m3c.jobs.SendCommandJob;

public abstract class BaseConnection {

    public static final int KEEPALIVE_DELAY = 2500;

    public static final byte[] MESSAGE_START = {0x29};
    public static final byte[] MESSAGE_CRC = {0x00, 0x00};
    public static final int END_OF_STREAM = -1;

    private String address;
    private int port;
    private int commandLenght;
    public String tag;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public boolean isConnected = false;

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
            byte[] message = new byte[getMessageLenght()];
            while (inputStream.read(message, 0, getMessageLenght()) != END_OF_STREAM) {
                readMessage(message);
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

    public void sendCommand(BaseCommand command) {
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
        } catch (Exception e) {
            Log.e(tag, "Error sending command", e);
            disconnect();
        }
    }

    public void addCommandJob(BaseCommand command, int interval) {
        // Stop previous command job
        JobManagerHelper.cancelJobsInBackground(command.tag);
        // Start new command job
        JobManagerHelper.getJobManager().addJobInBackground(new SendCommandJob(this, command, interval));
    }

    public int getMessageLenght() {
        return MESSAGE_START.length + commandLenght + MESSAGE_CRC.length;
    }

    public void readMessage(byte[] message) {
        byte[] command = Arrays.copyOfRange(message, MESSAGE_START.length, MESSAGE_START.length + commandLenght);
        readCommand(command);
    }

    public abstract void readCommand(byte[] command);

    public abstract BaseCommand getKeepAliveCommand();
}
