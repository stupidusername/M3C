package bei.m3c.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import bei.m3c.R;
import bei.m3c.adapters.BarArticleAdapter;
import bei.m3c.adapters.BarGroupAdapter;
import bei.m3c.events.GetBarArticlesEvent;
import bei.m3c.events.GetBarGroupsEvent;
import bei.m3c.helpers.FormatHelper;
import bei.m3c.helpers.JobManagerHelper;
import bei.m3c.helpers.ThemeHelper;
import bei.m3c.interfaces.FragmentInterface;
import bei.m3c.jobs.GetBarArticlesJob;
import bei.m3c.jobs.GetBarGroupsJob;
import bei.m3c.models.BarArticle;
import bei.m3c.models.BarGroup;

/**
 * Bar fragment
 */
public class BarFragment extends Fragment implements FragmentInterface {

    public static final int POPUP_MARGIN_DP = 25;
    public static final String POPUP_TITLE_SEPARATOR = " - ";

    // views
    private RelativeLayout activityLayout;
    private ListView groupsListView;
    private ProgressBar groupsListViewLoadingProgressBar;
    private GridView articlesGridView;
    private ProgressBar articlesGridViewLoadingProgressBar;
    private PopupWindow popupWindow;
    private ImageView popupImageView;
    private TextView popupTitleTextView;
    private ScrollView popupDescriptionLayout;
    private TextView popupDescriptionTextView;
    private Button popupCloseButton;
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

    @SuppressWarnings("deprecation")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activityLayout = (RelativeLayout) getActivity().findViewById(R.id.activity_layout);
        groupsListView = (ListView) view.findViewById(R.id.bar_listview);
        groupsListViewLoadingProgressBar = (ProgressBar) view.findViewById(R.id.bar_listview_loading_progress_bar);
        articlesGridView = (GridView) view.findViewById(R.id.bar_gridview);
        articlesGridViewLoadingProgressBar = (ProgressBar) view.findViewById(R.id.bar_gridview_loading_progress_bar);

        groupsListView.setEmptyView(groupsListViewLoadingProgressBar);
        articlesGridView.setEmptyView(articlesGridViewLoadingProgressBar);

        barGroupAdapter = new BarGroupAdapter(getLayoutInflater(savedInstanceState));
        groupsListView.setAdapter(barGroupAdapter);
        groupsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        barArticleAdapter = new BarArticleAdapter(getLayoutInflater(savedInstanceState));
        articlesGridView.setAdapter(barArticleAdapter);

        // Set UI theme
        ThemeHelper.setProgressBarTheme(groupsListViewLoadingProgressBar);
        ThemeHelper.setProgressBarTheme(articlesGridViewLoadingProgressBar);

        // Register events and jobs
        EventBus.getDefault().register(this);
        JobManagerHelper.getJobManager().addJobInBackground(new GetBarGroupsJob());

        // Create popup
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int popupMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POPUP_MARGIN_DP, getResources().getDisplayMetrics()));
        int popupHeight = displayMetrics.heightPixels - popupMargin * 2;
        int popupWidth = popupHeight;
        View popupView = getLayoutInflater(savedInstanceState).inflate(R.layout.popup_bar, null);
        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupImageView = (ImageView) popupView.findViewById(R.id.popup_bar_imageview);
        popupTitleTextView = (TextView) popupView.findViewById(R.id.popup_bar_title);
        popupDescriptionLayout = (ScrollView) popupView.findViewById(R.id.popup_bar_description_layout);
        popupDescriptionTextView = (TextView) popupView.findViewById(R.id.popup_bar_description_textview);
        popupCloseButton = (Button) popupView.findViewById(R.id.popup_bar_close_button);
        ThemeHelper.setButtonTheme(popupCloseButton);

        // Set UI click listeners
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadSelectedBarGroupArticles();
            }
        });
        articlesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BarArticle barArticle = (BarArticle) parent.getItemAtPosition(position);
                showPopup(barArticle);
            }
        });
        popupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void showPopup(BarArticle barArticle) {
        String articleTitle = barArticle.name != null ? barArticle.name.trim() : "";
        String articlePrice = FormatHelper.asCurrency(barArticle.price);
        String articleDescription = barArticle.description != null ? barArticle.description.trim() : "";
        String title = articleTitle + POPUP_TITLE_SEPARATOR + articlePrice;
        popupTitleTextView.setText(title);
        if (articleDescription.isEmpty()) {
            popupDescriptionLayout.setVisibility(View.GONE);
        } else {
            popupDescriptionLayout.setVisibility(View.VISIBLE);
            popupDescriptionTextView.setText(articleDescription);
        }
        Glide.with(getContext()).load(barArticle.pictureUrl).centerCrop()
                .placeholder(R.drawable.bar_article_placeholder).dontAnimate().into(popupImageView);
        popupWindow.showAtLocation(activityLayout, Gravity.CENTER, 0, 0);
    }

    private void selectDefaultBarGroup() {
        // Select default group if no one is selected and the list view is not empty
        int barGroupPosition = groupsListView.getCheckedItemPosition();
        if (barGroupPosition == AdapterView.INVALID_POSITION && groupsListView.getCount() > 0) {
            groupsListView.setItemChecked(0, true);
        }
    }

    private void loadSelectedBarGroupArticles() {
        int barGroupPosition = groupsListView.getCheckedItemPosition();
        if (!barGroupAdapter.isEmpty() && barGroupPosition != AdapterView.INVALID_POSITION) {
            BarGroup barGroup = barGroupAdapter.getItem(barGroupPosition);
            JobManagerHelper.getJobManager().addJobInBackground(new GetBarArticlesJob(barGroup));
        }
        // Clear selection if apadater is empty
        if (barGroupAdapter.isEmpty()) {
            barArticleAdapter.replaceList(new ArrayList<BarArticle>());
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

    @Override
    public void fragmentBecameVisible() {

    }

    @Override
    public void fragmentBecameInvisible() {

    }
}
