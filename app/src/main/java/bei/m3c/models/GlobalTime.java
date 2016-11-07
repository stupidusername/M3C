package bei.m3c.models;

/**
 * Kodi global time model
 */
public class GlobalTime {

    public int hours;
    public int minutes;
    public int seconds;
    public int milliseconds;

    public GlobalTime(int timeInMillis) {
        int hours = timeInMillis / (60 * 60 * 1000);
        int remainingMillis = timeInMillis - hours * (60 * 60 * 1000);
        int minutes = remainingMillis / (60 * 1000);
        remainingMillis = remainingMillis - minutes * (60 * 1000);
        int seconds = (int) Math.round(((double) remainingMillis) / 1000);
        remainingMillis = remainingMillis - seconds * (1000);
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = remainingMillis;
    }

    public int toMilliseconds() {
        return ((hours * 60 + minutes) * 60 + seconds) * 1000 + milliseconds;
    }
}
