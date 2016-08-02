package bei.m3c.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bei.m3c.Application;
import bei.m3c.R;

public class Light {

    public static final int MAX_LIGHTS = 6;
    // Light types
    public static final int TYPE_ON_OFF = 0;
    public static final int TYPE_DIMMER = 1;

    public String name;
    public int type;

    public Light(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public static ArrayList<String> getTypeNames() {
        ArrayList<String> typeNames = new ArrayList<>();
        typeNames.add(TYPE_ON_OFF, Application.getInstance().getString(R.string.light_type_on_off));
        typeNames.add(TYPE_DIMMER, Application.getInstance().getString(R.string.light_type_dimmer));
        return typeNames;
    }
}
