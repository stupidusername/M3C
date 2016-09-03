package bei.m3c.models;

import java.util.ArrayList;
import bei.m3c.Application;
import bei.m3c.R;

public class Light {

    public static final int MAX_LIGHTS = 6;
    public static final int MAX_VALUE = 100;
    // Light types
    public static final int TYPE_DIMMER = 0;
    public static final int TYPE_ON_OFF = 1;
    public static final int TYPE_RGB = 2;
    public static final int TYPE_MASTER = 3;

    public String name;
    public int type;
    private byte value;

    public Light(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public void setValue(int value) {
        setValue((byte) value);
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        if(value > MAX_VALUE) {
            value = MAX_VALUE;
        }
        if (type == TYPE_ON_OFF) {
            this.value = value == MAX_VALUE ? MAX_VALUE : (byte) 0;
        } else {
            this.value = value;
        }
    }

    public static ArrayList<String> getTypeNames() {
        ArrayList<String> typeNames = new ArrayList<>();
        typeNames.add(TYPE_ON_OFF, Application.getInstance().getString(R.string.light_type_on_off));
        typeNames.add(TYPE_DIMMER, Application.getInstance().getString(R.string.light_type_dimmer));
        return typeNames;
    }
}
