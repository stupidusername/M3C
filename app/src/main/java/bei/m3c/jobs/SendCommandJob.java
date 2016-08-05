package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import bei.m3c.commands.BaseCommand;
import bei.m3c.connections.BaseConnection;
import bei.m3c.helpers.JobManagerHelper;

public class SendCommandJob extends Job {

    public BaseConnection connection;
    public BaseCommand command;
    public int interval = 0;
    public static final int PRIORITY = 1;
    public static final int DELAY = 0; // delay in millis
    public static final int DEFAULT_INTERVAL = 0;

    public SendCommandJob(BaseConnection connection, BaseCommand command) {
        this(connection, command, DEFAULT_INTERVAL);
    }

    public SendCommandJob(BaseConnection connection, BaseCommand command, int interval) {
        this(connection, command, interval, DELAY);
    }

    public SendCommandJob(BaseConnection connection, BaseCommand command, int interval, int delay) {
        super(new Params(PRIORITY).requireNetwork().setDelayMs(delay).singleInstanceBy(command.tag).addTags(command.tag));
        this.connection = connection;
        this.command = command;
        this.interval = interval;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        connection.sendCommand(command);
        if (interval != DEFAULT_INTERVAL) {
            JobManagerHelper.getJobManager().addJob(new SendCommandJob(connection, command, interval, interval));
        }
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
