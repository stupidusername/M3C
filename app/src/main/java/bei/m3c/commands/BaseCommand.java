package bei.m3c.commands;

public abstract class BaseCommand {

    public String tag;
    public byte value;
    public byte[] params;

    public BaseCommand(String tag, byte value, byte... params) {
        this.tag = tag;
        this.value = value;
        this.params = params;
    }

    public static byte toByte(boolean value) {
        return (byte) (value ? 1 : 0);
    }
}
