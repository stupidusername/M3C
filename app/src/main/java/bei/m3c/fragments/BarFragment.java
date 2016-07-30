package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.BarArticleAdapter;
import bei.m3c.adapters.BarGroupAdapter;
import bei.m3c.events.GetBarGroupsEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.GetBarGroupsJob;
import bei.m3c.models.BarArticle;

/**
 * Bar fragment
 */
public class BarFragment extends Fragment {

    private BarGroupAdapter barGroupAdapter;
    private ListView groupsListView;
    private GridView articlesGridView;

    @Override
    public void onDestroyView() {
        JobManagerHelper.cancelJobsInBackground(GetBarGroupsJob.TAG);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupsListView = (ListView) view.findViewById(R.id.bar_listview);
        articlesGridView = (GridView) view.findViewById(R.id.bar_gridview);

        // Load bar groups demo data
        barGroupAdapter = new BarGroupAdapter(getLayoutInflater(savedInstanceState));
        groupsListView.setAdapter(barGroupAdapter);
        groupsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetBarGroupsJob());

        // Load bar articles demo data
        ArrayList<BarArticle> barArticles = new ArrayList<>();
        barArticles.add(new BarArticle(1, "Test 1", new BigDecimal(1)));
        barArticles.add(new BarArticle(1, "Test 2", new BigDecimal(2)));
        barArticles.add(new BarArticle(1, "Test 3", new BigDecimal(3)));
        barArticles.add(new BarArticle(1, "Test 4", new BigDecimal(4)));
        barArticles.add(new BarArticle(1, "Test 5", new BigDecimal(5)));
        barArticles.add(new BarArticle(1, "Test 6", new BigDecimal(6)));
        barArticles.add(new BarArticle(1, "Test 7", new BigDecimal(7)));
        barArticles.add(new BarArticle(1, "Test 8", new BigDecimal(8)));
        barArticles.add(new BarArticle(1, "Test 9", new BigDecimal(9)));
        barArticles.add(new BarArticle(1, "Test 10", new BigDecimal(10)));
        barArticles.add(new BarArticle(1, "Test 11", new BigDecimal(11)));
        barArticles.add(new BarArticle(1, "Test 12", new BigDecimal(12)));
        BarArticleAdapter barArticleAdapter = new BarArticleAdapter(getContext(), R.layout.gridview_item, barArticles);
        articlesGridView.setAdapter(barArticleAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetBarGroupsEvent event) {
        barGroupAdapter.replaceList(event.barGroups);
    }
}
