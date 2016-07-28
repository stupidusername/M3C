package bei.m3c.events;

/**
 * Created by nico on 27/07/16.
 */
public class GetInfoEvent<T> {

    public T info;

    public GetInfoEvent(T info) {
        this.info = info;
    }
}
