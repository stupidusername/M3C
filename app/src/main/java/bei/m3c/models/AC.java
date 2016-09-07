package bei.m3c.models;

import android.content.Context;
import android.util.Log;

import bei.m3c.R;
import bei.m3c.activities.MainActivity;

public class AC {

    public static final String TAG = "AC";
    public static final int STATE_OFF = 0;
    public static final int STATE_TURNING_ON = 1;
    public static final int STATE_ON = 2;
    public static final int STATE_TURNING_OFF = 3;
    public static final int STATE_NOT_SET = -1;
    public static final int TEMP_NOT_SET = -1;
    public static final int MAX_TEMP_COOL = 21;
    public static final int MAX_TEMP_AUTO = 25;

    public static final int[] TEMPS = {19, 21, 23, 25, 27, 29};

    private int state = STATE_NOT_SET;
    private int temp = TEMP_NOT_SET;
    private int tempCode = TEMP_NOT_SET;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setTempCode(int tempCode) {
        try {
            this.temp = TEMPS[tempCode];
            this.tempCode = tempCode;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, "Cannot set temperature.", e);
        }
    }

    public int getTempCode() {
        return tempCode;
    }

    public String getStateLabel() {
        Context context = MainActivity.getInstance();
        String label = "";
        switch (state) {
            case STATE_OFF:
                label = context.getString(R.string.ac_status_off);
                break;
            case STATE_TURNING_ON:
                label = context.getString(R.string.ac_status_turning_on);
                break;
            case STATE_ON:
                label = context.getString(R.string.ac_status_on);
                break;
            case STATE_TURNING_OFF:
                label = context.getString(R.string.ac_status_turning_off);
                break;
            default:
                label = context.getString(R.string.no_value);
        }
        return label;
    }

    public String getTempLabel() {
        Context context = MainActivity.getInstance();
        String label = "";
        if (temp != TEMP_NOT_SET) {
            String tempUnit = context.getString(R.string.ac_temp_unit);
            label = temp + " " + tempUnit;
        } else {
            label = context.getString(R.string.no_value);
        }
        return label;
    }

    public String getModeLabel() {
        Context context = MainActivity.getInstance();
        String label = "";
        if (temp == TEMP_NOT_SET) {
            label = context.getString(R.string.no_value);
        } else if (temp <= MAX_TEMP_COOL) {
            label = context.getString(R.string.ac_mode_cool);
        } else if (temp <= MAX_TEMP_AUTO) {
            label = context.getString(R.string.ac_mode_auto);
        } else {
            label = context.getString(R.string.ac_mode_heat);
        }
        return label;
    }

    public void toggle() {
        if (state == STATE_OFF) {
            state = STATE_TURNING_ON;
        } else if (state == STATE_ON) {
            state = STATE_TURNING_OFF;
        }
    }

    public void increaseTemp() {
        if (tempCode != TEMP_NOT_SET && tempCode < TEMPS.length - 1) {
            tempCode++;
            setTempCode(tempCode);
        }
    }

    public void decreaseTemp() {
        if (tempCode != TEMP_NOT_SET && tempCode > 0) {
            tempCode--;
            setTempCode(tempCode);
        }
    }
}
