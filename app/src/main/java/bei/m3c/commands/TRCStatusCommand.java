package bei.m3c.commands;

import java.util.Arrays;

import bei.m3c.models.Light;

public class TRCStatusCommand extends BaseCommand {

    public static final String TAG = "TRCStatusCommand";
    public static final byte VALUE = 1;

    public byte[] lightValues;
    public int acState;
    public int acTemp;

    public TRCStatusCommand(byte[] lightValues, int acState, int acTemp) {
        super(TAG, VALUE, buildParams(lightValues, acState, acTemp));
        this.lightValues = lightValues;
        this.acState = acState;
        this.acTemp = acTemp;
    }

    public TRCStatusCommand(byte[] command) {
        this(Arrays.copyOf(command, Light.MAX_LIGHTS), command[Light.MAX_LIGHTS], command[Light.MAX_LIGHTS + 1]);
    }

    private static byte[] buildParams(byte[] lightValues, int acState, int acTemp) {
        byte[] params = new byte[lightValues.length + 2];
        for (int i = 0; i < lightValues.length; i++) {
            params[i] = lightValues[i];
        }
        params[lightValues.length] = (byte) acState;
        params[lightValues.length + 1] = (byte) acTemp;
        return params;
    }
}
