package bei.m3c.commands;

import android.util.Log;

public class TPCStartAudioMessageCommand extends BaseCommand {

    public static final String TAG = "TPCAccountInfoCommand";
    public static final byte VALUE = 4;
    public static final String ALARM_TID_NAME = "EventWakeUp";

    public int audioMessageKey;
    public int roomNumber;
    public int suffixCode;

    public TPCStartAudioMessageCommand(int audioMessageKey, int roomNumber, int suffixCode) {
        super(TAG, VALUE, (byte) audioMessageKey, (byte) roomNumber, (byte) suffixCode);
        this.audioMessageKey = audioMessageKey;
        this.roomNumber = roomNumber;
        this.suffixCode = suffixCode;
    }

    public TPCStartAudioMessageCommand(byte[] command) {
        this(command[1], command[2], command[3]);
    }

    /**
     * @return the tid name or null if not found
     */
    public String getTidName() {
        String[] tidNames = {
                "StateDirectInput",
                "StateInput",
                "StateDelayedInput",
                "StateRelease",
                "StateError",
                "EventAuxiliarA",
                "EventAuxiliarB",
                "EventAuxiliarC",
                "EventPhoneHangUp",
                ALARM_TID_NAME,
                "EventBye",
                "EventDelayedBarQuery",
                "EventBarSwitch",
                "EventTVSensor",
                "EventWelcomeSensor",
                "EventChangeSwitch",
                "EventChargeSwitch",
                "EventEndOfTurnA",
                "EventEndOfTurnB",
                "EventEndOfTurnC",
                "EventIntermediateDoorOpen",
                "EventCurtainsOpen",
                "StateInputCurtainsClosed",
                "StateInputCurtainsOpen",
                "EventProjectorCooling",
                "EventKitchenBarSwitch",
                "EventExtraCharge",
                "EventMessageQueued",
                "EventPrepareBarQuery",
                "EventNoFastener",
                "Remise",
                "SecondWakeUp",
                "Manual #1",
                "Manual #2",
                "AttendantCommunication",
                "AttendantCommunicationRoom",
                "HeightExceeded",
                "Manual #3",
                "Manual #4",
                "LightControlAdvance"
        };

        try {
            return tidNames[audioMessageKey];
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Tid name not found", e);
            return null;
        }
    }

    public String getSuffix() {
        String suffix = "";
        String tidName = getTidName();
        if (tidName != null && tidName.equals(ALARM_TID_NAME)) {
            try {
                String[] suffixes = {
                        "M",
                        "T",
                        "N"
                };
                suffix = suffixes[suffixCode];
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "Suffix not found", e);
            }
        }
        return suffix;
    }
}