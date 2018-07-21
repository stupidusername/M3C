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
import bei.m3c.jobs.SendKeepAliveCommandJob;

public abstract class BaseConnection {

    public static final byte MESSAGE_START = 0x29;
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
            JobManagerHelper.cancelJobsInBackground(this.tag);
            JobManagerHelper.getJobManager().addJobInBackground(new SendKeepAliveCommandJob(this));
            Log.v(tag, "Connected.");
            onConnected();
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
                while (i < getMessageLenght()) {
                    readByte = (byte) inputStream.read();
                    if (readByte == END_OF_STREAM) {
                        break;
                    }
                    message[i] = readByte;
                    i++;
                }
                if (i == getMessageLenght()) {
                    readMessage(message);
                }
            }
            Log.i(tag, "Received end of stream.");
            disconnect(true);
        } catch (Exception e) {
            Log.e(tag, "Error during connection.", e);
            disconnect(true);
        }
    }

    public void disconnect(boolean reconnect) {
        if (isConnected) {
            Log.v(tag, "Disconnecting.");
            JobManagerHelper.cancelJobsInBackground(getKeepAliveCommand().tag);
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
                Log.e(tag, "Error during disconnection.", e);
            }
        }
        if (reconnect) {
            JobManagerHelper.getJobManager().addJobInBackground(new ConnectJob(this, ConnectJob.INTERVAL));
        }
    }

    public synchronized boolean sendCommand(BaseCommand command) {
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
            Log.e(tag, "Error sending command.", e);
            disconnect(true);
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

    public abstract void onConnected();

    public abstract void readCommand(byte[] command);

    public abstract BaseCommand getKeepAliveCommand();
}
