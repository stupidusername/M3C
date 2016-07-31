package bei.m3c.models;

public class Channel {

    public int id;
    public int number;
    public String title;
    public String logoUrl;

    public Channel(int id, int number, String title, String logoUrl) {
        this.id = id;
        this.number = number;
        this.title = title;
        this.logoUrl = logoUrl;
    }
}
