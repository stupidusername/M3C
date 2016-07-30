package bei.m3c.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import bei.m3c.events.GetBarArticlesEvent;
import bei.m3c.helpers.M3SHelper;
import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;

public class GetBarArticlesJob extends Job {

    public static final String TAG = "GetBarArticlesJob";
    public static final int PRIORITY = 1;

    private BarGroup barGroup;

    public GetBarArticlesJob(BarGroup barGroup) {
        super(new Params(PRIORITY).requireNetwork().singleInstanceBy(TAG).addTags(TAG));
        this.barGroup = barGroup;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        List<BarArticle> barArticles = M3SHelper.getBarArticles(barGroup.id);
        EventBus.getDefault().post(new GetBarArticlesEvent(barArticles));
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
