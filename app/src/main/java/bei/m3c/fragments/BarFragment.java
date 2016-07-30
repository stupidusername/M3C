package bei.m3c.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bei.m3c.R;
import bei.m3c.adapters.BarArticleAdapter;
import bei.m3c.adapters.BarGroupAdapter;
import bei.m3c.events.GetBarArticlesEvent;
import bei.m3c.events.GetBarGroupsEvent;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.jobs.GetBarArticlesJob;
import bei.m3c.jobs.GetBarGroupsJob;
import bei.m3c.models.BarGroup;

/**
 * Bar fragment
 */
public class BarFragment extends Fragment {

    // views
    private ListView groupsListView;
    private GridView articlesGridView;
    // adapters
    private BarGroupAdapter barGroupAdapter;
    private BarArticleAdapter barArticleAdapter;

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

        barGroupAdapter = new BarGroupAdapter(getLayoutInflater(savedInstanceState));
        groupsListView.setAdapter(barGroupAdapter);
        groupsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        barArticleAdapter = new BarArticleAdapter(getLayoutInflater(savedInstanceState));
        articlesGridView.setAdapter(barArticleAdapter);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetBarGroupsJob());

        // Set UI click listeners
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadSelectedBarGroupArticles();
            }
        });
    }

    private void selectDefaultBarGroup() {
        // Select default group if no one is selected and the list view is not empty
        int barGroupPosition = groupsListView.getCheckedItemPosition();
        if(barGroupPosition == AdapterView.INVALID_POSITION && groupsListView.getCount() > 0) {
            groupsListView.setItemChecked(0, true);
        }
    }

    private void loadSelectedBarGroupArticles() {
        int barGroupPosition = groupsListView.getCheckedItemPosition();
        if(barGroupPosition != AdapterView.INVALID_POSITION) {
            BarGroup barGroup = barGroupAdapter.getItem(barGroupPosition);
            JobManagerHelper.getJobManager().addJobInBackground(new GetBarArticlesJob(barGroup));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetBarGroupsEvent event) {
        barGroupAdapter.replaceList(event.barGroups);
        selectDefaultBarGroup();
        loadSelectedBarGroupArticles();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetBarArticlesEvent event) {
        barArticleAdapter.replaceList(event.barArticles);
    }
}
