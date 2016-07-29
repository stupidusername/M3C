package bei.m3c.events;

public class GetInfoEvent<T> {

    public T info;

    public GetInfoEvent(T info) {
        this.info = info;
    }
}
